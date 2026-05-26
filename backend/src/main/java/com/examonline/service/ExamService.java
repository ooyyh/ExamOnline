package com.examonline.service;

import com.examonline.common.BadRequestException;
import com.examonline.common.NotFoundException;
import com.examonline.domain.AppUser;
import com.examonline.domain.AttemptAnswer;
import com.examonline.domain.ExamAttempt;
import com.examonline.domain.ExamPaper;
import com.examonline.domain.PaperQuestion;
import com.examonline.repo.AppUserRepository;
import com.examonline.repo.AttemptAnswerRepository;
import com.examonline.repo.ExamAttemptRepository;
import com.examonline.repo.ExamPaperRepository;
import com.examonline.repo.PaperQuestionRepository;
import com.examonline.repo.QuestionRepository;
import com.examonline.web.dto.BasicDtos;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ExamService {
    private static final List<String> SUBJECTIVE_TYPES = List.of("SHORT_ANSWER", "CODING");

    private final ExamPaperRepository paperRepository;
    private final PaperQuestionRepository paperQuestionRepository;
    private final ExamAttemptRepository attemptRepository;
    private final AttemptAnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AppUserRepository userRepository;
    private final JsonSupport jsonSupport;
    private final LogService logService;
    private final int switchLimit;

    public ExamService(ExamPaperRepository paperRepository,
                       PaperQuestionRepository paperQuestionRepository,
                       ExamAttemptRepository attemptRepository,
                       AttemptAnswerRepository answerRepository,
                       QuestionRepository questionRepository,
                       AppUserRepository userRepository,
                       JsonSupport jsonSupport,
                       LogService logService,
                       @Value("${app.exam.auto-submit-switch-limit:3}") int switchLimit) {
        this.paperRepository = paperRepository;
        this.paperQuestionRepository = paperQuestionRepository;
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.jsonSupport = jsonSupport;
        this.logService = logService;
        this.switchLimit = switchLimit;
    }

    public List<BasicDtos.ExamSummary> upcoming(String username) {
        AppUser student = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("用户不存在"));
        List<String> classNames = new ArrayList<>();
        if (student.getClassName() != null) {
            classNames.add(student.getClassName());
        }
        LocalDateTime now = LocalDateTime.now();
        return paperRepository.findByPublishedTrueOrderByStartTimeDesc().stream()
                .filter(paper -> isInExamWindow(paper, now))
                .filter(paper -> canJoinPaper(paper, classNames))
                .map(paper -> jsonSupport.toExamSummary(paper, jsonSupport.readStringList(paper.getTargetClassesJson())))
                .toList();
    }

    @Transactional
    public BasicDtos.StartExamResponse start(Long paperId, String username) {
        AppUser student = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("用户不存在"));
        ExamPaper paper = paperRepository.findById(paperId).orElseThrow(() -> new NotFoundException("试卷不存在"));
        if (!Boolean.TRUE.equals(paper.getPublished())) {
            throw new BadRequestException("试卷未发布");
        }
        LocalDateTime now = LocalDateTime.now();
        if (!isInExamWindow(paper, now)) {
            throw new BadRequestException(paper.getStartTime() != null && now.isBefore(paper.getStartTime()) ? "考试尚未开始" : "考试已结束");
        }
        if (!canJoinPaper(paper, student.getClassName() == null ? List.of() : List.of(student.getClassName()))) {
            throw new BadRequestException("当前账号不在本次考试范围内");
        }

        ExamAttempt attempt = attemptRepository.findByPaperIdAndStudentUsername(paperId, username).orElseGet(ExamAttempt::new);
        if (attempt.getId() != null && !"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new BadRequestException("该考试已完成，不能重复进入");
        }
        if (attempt.getId() == null) {
            attempt.setPaperId(paperId);
            attempt.setPaperTitle(paper.getTitle());
            attempt.setStudentUsername(username);
            attempt.setClassName(student.getClassName() == null ? "-" : student.getClassName());
            attempt.setStatus("IN_PROGRESS");
            attempt.setStartTime(now);
            attempt.setScore(0);
            attempt.setSuspicious(false);
            attempt.setSwitchCount(0);
            attempt.setAutoSubmitLimit(switchLimit);
            attempt.setDeadlineTime(now.plusMinutes(Math.max(1, paper.getDurationMinutes())));
            attempt = attemptRepository.save(attempt);
        } else if (attempt.getDeadlineTime() == null) {
            LocalDateTime base = attempt.getStartTime() == null ? now : attempt.getStartTime();
            attempt.setDeadlineTime(base.plusMinutes(Math.max(1, paper.getDurationMinutes())));
            attempt = attemptRepository.save(attempt);
        }

        List<BasicDtos.QuestionView> questions = paperQuestionRepository.findByPaperIdOrderByOrderNoAsc(paperId).stream()
                .map(item -> jsonSupport.fromQuestionViewJson(item.getQuestionSnapshotJson()))
                .toList();
        logService.record(username, "START_EXAM", "ATTEMPT", String.valueOf(attempt.getId()), paper.getTitle());
        return new BasicDtos.StartExamResponse(
                attempt.getId(),
                paperId,
                paper.getTitle(),
                paper.getDurationMinutes(),
                switchLimit,
                paper.getTotalScore(),
                attempt.getDeadlineTime(),
                remainingSeconds(attempt),
                questions
        );
    }

    @Transactional
    public void saveAnswer(Long attemptId, BasicDtos.SaveAnswerRequest request) {
        ExamAttempt attempt = attemptRepository.findById(attemptId).orElseThrow(() -> new NotFoundException("答卷不存在"));
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new BadRequestException("答卷已提交");
        }
        if (attempt.getDeadlineTime() != null && LocalDateTime.now().isAfter(attempt.getDeadlineTime())) {
            submit(attemptId, true);
            throw new BadRequestException("考试已超时，系统已自动交卷");
        }
        AttemptAnswer answer = answerRepository.findByAttemptIdAndQuestionId(attemptId, request.questionId()).orElseGet(AttemptAnswer::new);
        answer.setAttemptId(attemptId);
        answer.setQuestionId(request.questionId());
        answer.setAnswerText(request.answerText() == null ? "" : request.answerText());
        answerRepository.save(answer);
    }

    @Transactional
    public BasicDtos.AttemptView recordSwitch(Long attemptId) {
        ExamAttempt attempt = attemptRepository.findById(attemptId).orElseThrow(() -> new NotFoundException("答卷不存在"));
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            return viewAttempt(attemptId);
        }
        attempt.setSwitchCount(attempt.getSwitchCount() + 1);
        if (attempt.getSwitchCount() >= attempt.getAutoSubmitLimit()) {
            attempt.setSuspicious(true);
            attemptRepository.save(attempt);
            return submit(attemptId, true);
        }
        attemptRepository.save(attempt);
        return viewAttempt(attemptId);
    }

    @Transactional
    public BasicDtos.AttemptView submit(Long attemptId, boolean auto) {
        ExamAttempt attempt = attemptRepository.findById(attemptId).orElseThrow(() -> new NotFoundException("答卷不存在"));
        if (!"IN_PROGRESS".equals(attempt.getStatus()) && !"AUTO_SUBMITTED".equals(attempt.getStatus())) {
            return viewAttempt(attemptId);
        }
        List<PaperQuestion> paperQuestions = paperQuestionRepository.findByPaperIdOrderByOrderNoAsc(attempt.getPaperId());
        int totalScore = 0;
        boolean needsReview = false;
        for (PaperQuestion item : paperQuestions) {
            BasicDtos.QuestionView question = jsonSupport.fromQuestionViewJson(item.getQuestionSnapshotJson());
            AttemptAnswer answer = answerRepository.findByAttemptIdAndQuestionId(attemptId, item.getQuestionId()).orElseGet(AttemptAnswer::new);
            String answerText = answer.getAnswerText() == null ? "" : answer.getAnswerText().trim();
            answer.setAttemptId(attemptId);
            answer.setQuestionId(item.getQuestionId());
            if (isSubjective(question.type())) {
                answer.setCorrect(false);
                answer.setScore(0);
                answer.setReviewed(false);
                needsReview = true;
            } else {
                boolean correct = isCorrectAnswer(answerText, question.correctAnswer());
                int score = correct ? item.getScore() : 0;
                answer.setCorrect(correct);
                answer.setScore(score);
                answer.setReviewed(true);
                totalScore += score;
            }
            answerRepository.save(answer);
        }
        attempt.setScore(totalScore);
        attempt.setSubmitTime(LocalDateTime.now());
        if (isFastSubmit(attempt)) {
            attempt.setSuspicious(true);
        }
        attempt.setStatus(auto ? "AUTO_SUBMITTED" : needsReview ? "REVIEW_PENDING" : "SUBMITTED");
        attemptRepository.save(attempt);
        logService.record(attempt.getStudentUsername(), auto ? "AUTO_SUBMIT" : "SUBMIT", "ATTEMPT", String.valueOf(attemptId), attempt.getPaperTitle());
        return viewAttempt(attemptId);
    }

    @Transactional
    public BasicDtos.AttemptView reviewAnswer(Long attemptId, BasicDtos.ReviewAnswerRequest request, String actor) {
        ExamAttempt attempt = attemptRepository.findById(attemptId).orElseThrow(() -> new NotFoundException("答卷不存在"));
        if ("IN_PROGRESS".equals(attempt.getStatus())) {
            throw new BadRequestException("考试进行中，暂不能阅卷");
        }
        PaperQuestion paperQuestion = paperQuestionRepository.findByPaperIdOrderByOrderNoAsc(attempt.getPaperId()).stream()
                .filter(item -> Objects.equals(item.getQuestionId(), request.questionId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("试卷题目不存在"));
        AttemptAnswer answer = answerRepository.findByAttemptIdAndQuestionId(attemptId, request.questionId()).orElseGet(AttemptAnswer::new);
        int score = Math.max(0, Math.min(request.score(), paperQuestion.getScore()));
        answer.setAttemptId(attemptId);
        answer.setQuestionId(request.questionId());
        answer.setScore(score);
        answer.setCorrect(request.correct() == null ? score > 0 : request.correct());
        answer.setReviewed(true);
        answerRepository.save(answer);

        recalculateScore(attempt);
        if (!hasPendingReview(attempt.getId())) {
            attempt.setStatus("REVIEWED");
        }
        attemptRepository.save(attempt);
        logService.record(actor, "REVIEW", "ATTEMPT", String.valueOf(attemptId), "questionId=" + request.questionId() + ", score=" + score);
        return viewAttempt(attemptId);
    }

    @Transactional
    public BasicDtos.AttemptView extendAttempt(Long attemptId, BasicDtos.ExtendAttemptRequest request, String actor) {
        ExamAttempt attempt = attemptRepository.findById(attemptId).orElseThrow(() -> new NotFoundException("答卷不存在"));
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new BadRequestException("只能为进行中的考试延长时间");
        }
        int minutes = request.minutes() == null ? 10 : Math.max(1, request.minutes());
        LocalDateTime base = attempt.getDeadlineTime() == null ? LocalDateTime.now() : attempt.getDeadlineTime();
        attempt.setDeadlineTime(base.plusMinutes(minutes));
        attemptRepository.save(attempt);
        logService.record(actor, "EXTEND_TIME", "ATTEMPT", String.valueOf(attemptId), "minutes=" + minutes);
        return viewAttempt(attemptId);
    }

    public List<BasicDtos.AttemptView> history(String username) {
        return attemptRepository.findByStudentUsernameOrderByCreatedAtDesc(username).stream()
                .map(a -> viewAttempt(a.getId()))
                .toList();
    }

    public BasicDtos.AttemptView viewAttempt(Long attemptId) {
        ExamAttempt attempt = attemptRepository.findById(attemptId).orElseThrow(() -> new NotFoundException("答卷不存在"));
        ExamPaper paper = paperRepository.findById(attempt.getPaperId()).orElse(null);
        List<PaperQuestion> paperQuestions = paperQuestionRepository.findByPaperIdOrderByOrderNoAsc(attempt.getPaperId());
        Map<Long, AttemptAnswer> answerMap = answerRepository.findByAttemptId(attemptId).stream()
                .collect(Collectors.toMap(AttemptAnswer::getQuestionId, answer -> answer, (left, right) -> left));
        List<BasicDtos.AnswerView> answers = paperQuestions.stream()
                .map(item -> toAnswerView(item, answerMap.get(item.getQuestionId())))
                .toList();
        int totalScore = paper == null ? paperQuestions.stream().mapToInt(PaperQuestion::getScore).sum() : paper.getTotalScore();
        int passScore = paper == null ? 60 : paper.getPassScore();
        return new BasicDtos.AttemptView(
                attempt.getId(),
                attempt.getPaperId(),
                attempt.getPaperTitle(),
                attempt.getStudentUsername(),
                attempt.getClassName(),
                attempt.getStatus(),
                attempt.getScore(),
                totalScore,
                attempt.getScore() != null && attempt.getScore() >= passScore,
                attempt.getSuspicious(),
                attempt.getSwitchCount(),
                attempt.getStartTime(),
                attempt.getSubmitTime(),
                attempt.getDeadlineTime(),
                durationSeconds(attempt),
                remainingSeconds(attempt),
                hasPendingReview(attempt.getId()),
                answers
        );
    }

    public BasicDtos.ExamMonitorView monitor(Long paperId) {
        ExamPaper paper = paperRepository.findById(paperId).orElseThrow(() -> new NotFoundException("试卷不存在"));
        List<ExamAttempt> attempts = attemptRepository.findAll().stream()
                .filter(a -> Objects.equals(a.getPaperId(), paperId))
                .sorted(Comparator.comparing(ExamAttempt::getCreatedAt).reversed())
                .toList();
        List<BasicDtos.MonitorAttemptView> rows = attempts.stream()
                .map(a -> new BasicDtos.MonitorAttemptView(
                        a.getId(),
                        a.getStudentUsername(),
                        a.getClassName(),
                        a.getStatus(),
                        a.getScore(),
                        paper.getTotalScore(),
                        a.getSuspicious(),
                        a.getSwitchCount(),
                        a.getStartTime(),
                        a.getSubmitTime(),
                        a.getDeadlineTime(),
                        remainingSeconds(a),
                        hasPendingReview(a.getId())
                ))
                .toList();
        return new BasicDtos.ExamMonitorView(
                paperId,
                paper.getTitle(),
                attempts.size(),
                attempts.stream().filter(a -> "IN_PROGRESS".equals(a.getStatus())).count(),
                attempts.stream().filter(this::isCompleted).count(),
                attempts.stream().filter(a -> Boolean.TRUE.equals(a.getSuspicious())).count(),
                rows.stream().filter(BasicDtos.MonitorAttemptView::needsReview).count(),
                rows
        );
    }

    public List<BasicDtos.WrongBookView> wrongBook(String username) {
        Map<Long, Integer> wrongCounts = new LinkedHashMap<>();
        for (ExamAttempt attempt : attemptRepository.findByStudentUsernameOrderByCreatedAtDesc(username)) {
            if (!isCompleted(attempt)) {
                continue;
            }
            for (AttemptAnswer answer : answerRepository.findByAttemptId(attempt.getId())) {
                if (Boolean.TRUE.equals(answer.getReviewed()) && Boolean.FALSE.equals(answer.getCorrect())) {
                    wrongCounts.merge(answer.getQuestionId(), 1, Integer::sum);
                }
            }
        }
        if (wrongCounts.isEmpty()) {
            return List.of();
        }
        return wrongCounts.entrySet().stream()
                .map(entry -> questionRepository.findById(entry.getKey())
                        .map(q -> new BasicDtos.WrongBookView(
                                q.getId(),
                                q.getTitle(),
                                q.getType(),
                                q.getSubject(),
                                q.getCourseCode(),
                                q.getCourseName(),
                                q.getChapter(),
                                q.getSection(),
                                q.getKnowledgeModule(),
                                q.getKnowledgePoint(),
                                q.getDifficulty(),
                                q.getCognitiveLevel(),
                                q.getSource(),
                                jsonSupport.readStringList(q.getTagsJson()),
                                jsonSupport.readStringList(q.getOptionsJson()),
                                q.getCorrectAnswer(),
                                entry.getValue()
                        ))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    public BasicDtos.StudentStatsView studentStats(String username) {
        List<ExamAttempt> attempts = attemptRepository.findByStudentUsernameOrderByCreatedAtDesc(username).stream()
                .filter(this::isCompleted)
                .toList();
        List<BasicDtos.ScorePoint> trend = attempts.stream()
                .sorted(Comparator.comparing(ExamAttempt::getCreatedAt))
                .map(a -> new BasicDtos.ScorePoint(a.getPaperTitle(), a.getScore()))
                .toList();
        double average = attempts.isEmpty() ? 0 : attempts.stream().mapToInt(a -> a.getScore() == null ? 0 : a.getScore()).average().orElse(0);
        long suspiciousCount = attempts.stream().filter(a -> Boolean.TRUE.equals(a.getSuspicious())).count();
        return new BasicDtos.StudentStatsView(trend, attempts.size(), average, suspiciousCount, knowledgePointsForAttempts(attempts));
    }

    public BasicDtos.AnalyticsView analytics(Long paperId) {
        ExamPaper paper = paperRepository.findById(paperId).orElseThrow(() -> new NotFoundException("试卷不存在"));
        List<ExamAttempt> attempts = attemptRepository.findAll().stream()
                .filter(a -> Objects.equals(a.getPaperId(), paperId))
                .filter(this::isCompleted)
                .toList();
        if (attempts.isEmpty()) {
            return new BasicDtos.AnalyticsView(0d, 0, 0, 0d, 0, 0, 0, List.of(), List.of(), List.of());
        }
        double average = attempts.stream().mapToInt(a -> a.getScore() == null ? 0 : a.getScore()).average().orElse(0);
        int max = attempts.stream().mapToInt(a -> a.getScore() == null ? 0 : a.getScore()).max().orElse(0);
        int min = attempts.stream().mapToInt(a -> a.getScore() == null ? 0 : a.getScore()).min().orElse(0);
        double passRate = attempts.stream().filter(a -> a.getScore() != null && a.getScore() >= paper.getPassScore()).count() * 1.0 / attempts.size();
        return new BasicDtos.AnalyticsView(
                average,
                max,
                min,
                passRate,
                attempts.size(),
                attempts.stream().filter(a -> hasPendingReview(a.getId())).count(),
                attempts.stream().filter(a -> Boolean.TRUE.equals(a.getSuspicious())).count(),
                questionRates(paperId, attempts),
                classScores(attempts, paper.getPassScore()),
                knowledgePointsForAttempts(attempts)
        );
    }

    public byte[] exportScores(Long paperId) {
        List<ExamAttempt> attempts = attemptRepository.findAll().stream()
                .filter(a -> Objects.equals(a.getPaperId(), paperId))
                .filter(this::isCompleted)
                .toList();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("scores");
            Row header = sheet.createRow(0);
            String[] columns = {"studentUsername", "className", "score", "status", "needsReview", "suspicious", "switchCount", "startTime", "submitTime", "deadlineTime"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }
            int rowIndex = 1;
            for (ExamAttempt attempt : attempts) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(attempt.getStudentUsername());
                row.createCell(1).setCellValue(attempt.getClassName());
                row.createCell(2).setCellValue(attempt.getScore() == null ? 0 : attempt.getScore());
                row.createCell(3).setCellValue(attempt.getStatus());
                row.createCell(4).setCellValue(hasPendingReview(attempt.getId()));
                row.createCell(5).setCellValue(Boolean.TRUE.equals(attempt.getSuspicious()));
                row.createCell(6).setCellValue(attempt.getSwitchCount() == null ? 0 : attempt.getSwitchCount());
                row.createCell(7).setCellValue(String.valueOf(attempt.getStartTime()));
                row.createCell(8).setCellValue(String.valueOf(attempt.getSubmitTime()));
                row.createCell(9).setCellValue(String.valueOf(attempt.getDeadlineTime()));
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("导出失败", e);
        }
    }

    private BasicDtos.AnswerView toAnswerView(PaperQuestion item, AttemptAnswer answer) {
        BasicDtos.QuestionView question = jsonSupport.fromQuestionViewJson(item.getQuestionSnapshotJson());
        return new BasicDtos.AnswerView(
                item.getQuestionId(),
                question.title(),
                question.type(),
                question.subject(),
                question.courseCode(),
                question.courseName(),
                question.chapter(),
                question.section(),
                question.knowledgeModule(),
                question.knowledgePoint(),
                question.difficulty(),
                question.cognitiveLevel(),
                question.source(),
                question.tags(),
                question.correctAnswer(),
                question.explanation(),
                answer == null ? "" : answer.getAnswerText(),
                answer != null && answer.getCorrect(),
                answer == null ? 0 : answer.getScore(),
                item.getScore(),
                answer != null && answer.getReviewed()
        );
    }

    private List<BasicDtos.QuestionRate> questionRates(Long paperId, List<ExamAttempt> attempts) {
        Map<Long, Long> correctCounts = new HashMap<>();
        Map<Long, Long> totalCounts = new HashMap<>();
        for (ExamAttempt attempt : attempts) {
            for (AttemptAnswer answer : answerRepository.findByAttemptId(attempt.getId())) {
                if (!Boolean.TRUE.equals(answer.getReviewed())) {
                    continue;
                }
                totalCounts.merge(answer.getQuestionId(), 1L, Long::sum);
                if (Boolean.TRUE.equals(answer.getCorrect())) {
                    correctCounts.merge(answer.getQuestionId(), 1L, Long::sum);
                }
            }
        }
        List<BasicDtos.QuestionRate> rates = new ArrayList<>();
        for (PaperQuestion item : paperQuestionRepository.findByPaperIdOrderByOrderNoAsc(paperId)) {
            BasicDtos.QuestionView question = jsonSupport.fromQuestionViewJson(item.getQuestionSnapshotJson());
            long total = totalCounts.getOrDefault(item.getQuestionId(), 0L);
            long correct = correctCounts.getOrDefault(item.getQuestionId(), 0L);
            double rate = total == 0 ? 0 : correct * 1.0 / total;
            rates.add(new BasicDtos.QuestionRate(
                    item.getQuestionId(),
                    question.title(),
                    question.subject(),
                    question.courseName(),
                    question.chapter(),
                    question.knowledgeModule(),
                    question.knowledgePoint(),
                    rate,
                    total,
                    correct
            ));
        }
        return rates;
    }

    private List<BasicDtos.ClassScoreView> classScores(List<ExamAttempt> attempts, int passScore) {
        return attempts.stream()
                .collect(Collectors.groupingBy(a -> a.getClassName() == null ? "-" : a.getClassName(), LinkedHashMap::new, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<ExamAttempt> rows = entry.getValue();
                    double average = rows.stream().mapToInt(a -> a.getScore() == null ? 0 : a.getScore()).average().orElse(0);
                    double passRate = rows.stream().filter(a -> a.getScore() != null && a.getScore() >= passScore).count() * 1.0 / rows.size();
                    return new BasicDtos.ClassScoreView(entry.getKey(), average, rows.size(), passRate);
                })
                .toList();
    }

    private List<BasicDtos.KnowledgePointView> knowledgePointsForAttempts(List<ExamAttempt> attempts) {
        Map<String, Long> totalCounts = new LinkedHashMap<>();
        Map<String, Long> correctCounts = new LinkedHashMap<>();
        for (ExamAttempt attempt : attempts) {
            Map<Long, AttemptAnswer> answerMap = answerRepository.findByAttemptId(attempt.getId()).stream()
                    .filter(answer -> Boolean.TRUE.equals(answer.getReviewed()))
                    .collect(Collectors.toMap(AttemptAnswer::getQuestionId, answer -> answer, (left, right) -> left));
            for (PaperQuestion item : paperQuestionRepository.findByPaperIdOrderByOrderNoAsc(attempt.getPaperId())) {
                AttemptAnswer answer = answerMap.get(item.getQuestionId());
                if (answer == null) {
                    continue;
                }
                BasicDtos.QuestionView question = jsonSupport.fromQuestionViewJson(item.getQuestionSnapshotJson());
                String key = question.knowledgePoint() == null || question.knowledgePoint().isBlank() ? "未分类" : question.knowledgePoint();
                totalCounts.merge(key, 1L, Long::sum);
                if (Boolean.TRUE.equals(answer.getCorrect())) {
                    correctCounts.merge(key, 1L, Long::sum);
                }
            }
        }
        return totalCounts.entrySet().stream()
                .map(entry -> {
                    long total = entry.getValue();
                    long correct = correctCounts.getOrDefault(entry.getKey(), 0L);
                    return new BasicDtos.KnowledgePointView(entry.getKey(), total == 0 ? 0 : correct * 1.0 / total, total);
                })
                .toList();
    }

    private void recalculateScore(ExamAttempt attempt) {
        int totalScore = answerRepository.findByAttemptId(attempt.getId()).stream()
                .mapToInt(answer -> answer.getScore() == null ? 0 : answer.getScore())
                .sum();
        attempt.setScore(totalScore);
    }

    private boolean hasPendingReview(Long attemptId) {
        return answerRepository.findByAttemptId(attemptId).stream()
                .anyMatch(answer -> !Boolean.TRUE.equals(answer.getReviewed()));
    }

    private boolean isInExamWindow(ExamPaper paper, LocalDateTime now) {
        boolean afterStart = paper.getStartTime() == null || !now.isBefore(paper.getStartTime());
        boolean beforeEnd = paper.getEndTime() == null || !now.isAfter(paper.getEndTime());
        return afterStart && beforeEnd;
    }

    private boolean canJoinPaper(ExamPaper paper, List<String> classNames) {
        List<String> targets = jsonSupport.readStringList(paper.getTargetClassesJson());
        return targets.isEmpty() || classNames.stream().anyMatch(targets::contains);
    }

    private boolean isCompleted(ExamAttempt attempt) {
        return !"IN_PROGRESS".equals(attempt.getStatus());
    }

    private boolean isSubjective(String type) {
        return SUBJECTIVE_TYPES.contains(type);
    }

    private boolean isCorrectAnswer(String submitted, String correct) {
        return normalize(submitted).equalsIgnoreCase(normalize(correct));
    }

    private boolean isFastSubmit(ExamAttempt attempt) {
        if (attempt.getStartTime() == null || attempt.getSubmitTime() == null || attempt.getDeadlineTime() == null) {
            return false;
        }
        long used = Duration.between(attempt.getStartTime(), attempt.getSubmitTime()).getSeconds();
        long planned = Duration.between(attempt.getStartTime(), attempt.getDeadlineTime()).getSeconds();
        return planned > 0 && used < Math.max(60, planned / 10);
    }

    private long durationSeconds(ExamAttempt attempt) {
        if (attempt.getStartTime() == null) {
            return 0;
        }
        LocalDateTime end = attempt.getSubmitTime() == null ? LocalDateTime.now() : attempt.getSubmitTime();
        return Math.max(0, Duration.between(attempt.getStartTime(), end).getSeconds());
    }

    private long remainingSeconds(ExamAttempt attempt) {
        if (attempt.getDeadlineTime() == null || !"IN_PROGRESS".equals(attempt.getStatus())) {
            return 0;
        }
        return Math.max(0, Duration.between(LocalDateTime.now(), attempt.getDeadlineTime()).getSeconds());
    }

    private String normalize(String value) {
        return value == null ? "" : value.replaceAll("[\\s,，、;；|]+", "").trim();
    }
}
