package com.examonline.service;

import com.examonline.common.BadRequestException;
import com.examonline.common.NotFoundException;
import com.examonline.domain.Question;
import com.examonline.repo.QuestionRepository;
import com.examonline.web.dto.BasicDtos;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository repository;
    private final JsonSupport jsonSupport;
    private final LogService logService;

    public QuestionService(QuestionRepository repository, JsonSupport jsonSupport, LogService logService) {
        this.repository = repository;
        this.jsonSupport = jsonSupport;
        this.logService = logService;
    }

    public List<Question> list() {
        return repository.findAll();
    }

    public Question get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("题目不存在"));
    }

    @Transactional
    public Question save(BasicDtos.QuestionRequest request, String actor) {
        if (request.title() == null || request.title().isBlank()) {
            throw new BadRequestException("题目标题不能为空");
        }
        boolean existed = request.id() != null && repository.existsById(request.id());
        Question question = request.id() != null ? repository.findById(request.id()).orElseGet(Question::new) : new Question();
        question.setTitle(request.title());
        question.setType(defaultText(request.type(), "SINGLE_CHOICE"));
        question.setSubject(defaultText(request.subject(), "综合"));
        question.setCourseCode(clean(request.courseCode()));
        question.setCourseName(clean(request.courseName()));
        question.setChapter(clean(request.chapter()));
        question.setSection(clean(request.section()));
        question.setKnowledgeModule(clean(request.knowledgeModule()));
        question.setKnowledgePoint(defaultText(request.knowledgePoint(), "未分类"));
        question.setDifficulty(defaultText(request.difficulty(), "EASY"));
        question.setCognitiveLevel(clean(request.cognitiveLevel()));
        question.setSource(clean(request.source()));
        question.setTagsJson(jsonSupport.toJson(request.tags() == null ? List.of() : request.tags()));
        question.setOptionsJson(jsonSupport.toJson(request.options() == null ? List.of() : request.options()));
        question.setCorrectAnswer(defaultText(request.correctAnswer(), ""));
        question.setScore(request.score() == null ? 5 : request.score());
        question.setEstimatedMinutes(request.estimatedMinutes());
        question.setExplanation(request.explanation());
        question.setCreatorUsername(request.creatorUsername() == null ? actor : request.creatorUsername());
        Question saved = repository.save(question);
        logService.record(actor, existed ? "UPDATE" : "CREATE", "QUESTION", String.valueOf(saved.getId()), saved.getTitle());
        return saved;
    }

    public void delete(Long id, String actor) {
        Question question = repository.findById(id).orElseThrow(() -> new NotFoundException("题目不存在"));
        repository.delete(question);
        logService.record(actor, "DELETE", "QUESTION", String.valueOf(id), question.getTitle());
    }

    public BasicDtos.ImportResult importExcel(InputStream inputStream, String actor) {
        int inserted = 0;
        int skipped = 0;
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String title = cellString(row.getCell(0));
                if (title == null || title.isBlank()) {
                    skipped++;
                    continue;
                }
                save(buildImportRequest(row, title), actor);
                inserted++;
            }
        } catch (Exception e) {
            throw new IllegalStateException("导入失败", e);
        }
        return new BasicDtos.ImportResult(inserted, skipped);
    }

    public Workbook templateWorkbook() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("questions");
        Row header = sheet.createRow(0);
        String[] titles = {
                "title",
                "type",
                "subject",
                "courseCode",
                "courseName",
                "chapter",
                "section",
                "knowledgeModule",
                "knowledgePoint",
                "difficulty",
                "cognitiveLevel",
                "source",
                "tags",
                "options",
                "correctAnswer",
                "score",
                "estimatedMinutes",
                "explanation",
                "creatorUsername"
        };
        for (int i = 0; i < titles.length; i++) {
            header.createCell(i).setCellValue(titles[i]);
        }
        return workbook;
    }

    public List<BasicDtos.QuestionView> views() {
        return repository.findAll().stream().map(jsonSupport::toQuestionView).toList();
    }

    private BasicDtos.QuestionRequest buildImportRequest(Row row, String title) {
        boolean extended = hasValue(row, 10) || hasValue(row, 11) || hasValue(row, 12) || hasValue(row, 13) || hasValue(row, 14) || hasValue(row, 15);
        if (!extended) {
            return new BasicDtos.QuestionRequest(
                    null,
                    title,
                    cellString(row.getCell(1)),
                    cellString(row.getCell(2)),
                    null,
                    cellString(row.getCell(2)),
                    null,
                    null,
                    null,
                    cellString(row.getCell(3)),
                    cellString(row.getCell(4)),
                    null,
                    null,
                    List.of(),
                    parseList(cellString(row.getCell(5))),
                    cellString(row.getCell(6)),
                    cellInt(row.getCell(7), 5),
                    null,
                    cellString(row.getCell(8)),
                    cellString(row.getCell(9))
            );
        }
        return new BasicDtos.QuestionRequest(
                null,
                title,
                cellString(row.getCell(1)),
                cellString(row.getCell(2)),
                cellString(row.getCell(3)),
                cellString(row.getCell(4)),
                cellString(row.getCell(5)),
                cellString(row.getCell(6)),
                cellString(row.getCell(7)),
                cellString(row.getCell(8)),
                cellString(row.getCell(9)),
                cellString(row.getCell(10)),
                cellString(row.getCell(11)),
                parseList(cellString(row.getCell(12))),
                parseList(cellString(row.getCell(13))),
                cellString(row.getCell(14)),
                cellInt(row.getCell(15), 5),
                cellInt(row.getCell(16), 3),
                cellString(row.getCell(17)),
                cellString(row.getCell(18))
        );
    }

    private List<String> parseList(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        if (raw.startsWith("[")) {
            return jsonSupport.readStringList(raw);
        }
        String[] parts = raw.split("\\|");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            if (part != null && !part.isBlank()) {
                result.add(part.trim());
            }
        }
        return result;
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private boolean hasValue(Row row, int cellIndex) {
        String value = cellString(row.getCell(cellIndex));
        return value != null && !value.isBlank();
    }

    private String cellString(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }

    private Integer cellInt(Cell cell, int defaultValue) {
        String value = cellString(cell);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
