package com.examonline.web;

import com.examonline.common.ApiResponse;
import com.examonline.domain.PaperQuestion;
import com.examonline.domain.Question;
import com.examonline.service.ExamService;
import com.examonline.service.PaperService;
import com.examonline.service.QuestionService;
import com.examonline.web.dto.BasicDtos;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    private final QuestionService questionService;
    private final PaperService paperService;
    private final ExamService examService;

    public TeacherController(QuestionService questionService, PaperService paperService, ExamService examService) {
        this.questionService = questionService;
        this.paperService = paperService;
        this.examService = examService;
    }

    @GetMapping("/questions")
    public ApiResponse<List<BasicDtos.QuestionView>> questions() {
        return ApiResponse.ok(questionService.views());
    }

    @PostMapping("/questions")
    public ApiResponse<Question> saveQuestion(@RequestBody BasicDtos.QuestionRequest request, @RequestParam String actor) {
        return ApiResponse.ok(questionService.save(request, actor));
    }

    @PutMapping("/questions/{id}")
    public ApiResponse<Question> updateQuestion(@PathVariable Long id, @RequestBody BasicDtos.QuestionRequest request, @RequestParam String actor) {
        BasicDtos.QuestionRequest payload = new BasicDtos.QuestionRequest(
                id,
                request.title(),
                request.type(),
                request.subject(),
                request.courseCode(),
                request.courseName(),
                request.chapter(),
                request.section(),
                request.knowledgeModule(),
                request.knowledgePoint(),
                request.difficulty(),
                request.cognitiveLevel(),
                request.source(),
                request.tags(),
                request.options(),
                request.correctAnswer(),
                request.score(),
                request.estimatedMinutes(),
                request.explanation(),
                request.creatorUsername()
        );
        return ApiResponse.ok(questionService.save(payload, actor));
    }

    @DeleteMapping("/questions/{id}")
    public ApiResponse<Void> deleteQuestion(@PathVariable Long id, @RequestParam String actor) {
        questionService.delete(id, actor);
        return ApiResponse.ok(null);
    }

    @PostMapping(value = "/questions/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BasicDtos.ImportResult> importQuestions(@RequestPart("file") MultipartFile file, @RequestParam String actor) throws Exception {
        try (var in = file.getInputStream()) {
            return ApiResponse.ok(questionService.importExcel(in, actor));
        }
    }

    @GetMapping("/questions/template")
    public ResponseEntity<byte[]> template() throws Exception {
        try (Workbook workbook = questionService.templateWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=question-template.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        }
    }

    @GetMapping("/papers")
    public ApiResponse<List<BasicDtos.ExamSummary>> papers() {
        return ApiResponse.ok(paperService.list());
    }

    @PostMapping("/papers/manual")
    public ApiResponse<BasicDtos.ExamSummary> createManual(@RequestBody BasicDtos.PaperManualRequest request, @RequestParam String actor) {
        return ApiResponse.ok(paperService.createManual(request, actor));
    }

    @PostMapping("/papers/auto")
    public ApiResponse<BasicDtos.ExamSummary> createAuto(@RequestBody BasicDtos.PaperAutoRequest request, @RequestParam String actor) {
        return ApiResponse.ok(paperService.createAuto(request, actor));
    }

    @GetMapping("/papers/{id}")
    public ApiResponse<BasicDtos.ExamSummary> paper(@PathVariable Long id) {
        return ApiResponse.ok(paperService.get(id));
    }

    @GetMapping("/papers/{id}/items")
    public ApiResponse<List<PaperQuestion>> items(@PathVariable Long id) {
        return ApiResponse.ok(paperService.items(id));
    }

    @PostMapping("/papers/{id}/publish")
    public ApiResponse<BasicDtos.ExamSummary> publish(@PathVariable Long id, @RequestBody BasicDtos.PublishExamRequest request, @RequestParam String actor) {
        return ApiResponse.ok(paperService.publish(id, request.targetClasses(), actor));
    }

    @DeleteMapping("/papers/{id}")
    public ApiResponse<Void> deletePaper(@PathVariable Long id, @RequestParam String actor) {
        paperService.delete(id, actor);
        return ApiResponse.ok(null);
    }

    @GetMapping("/exams/{paperId}/analytics")
    public ApiResponse<BasicDtos.AnalyticsView> analytics(@PathVariable Long paperId) {
        return ApiResponse.ok(examService.analytics(paperId));
    }

    @GetMapping("/exams/{paperId}/monitor")
    public ApiResponse<BasicDtos.ExamMonitorView> monitor(@PathVariable Long paperId) {
        return ApiResponse.ok(examService.monitor(paperId));
    }

    @GetMapping("/attempts/{attemptId}")
    public ApiResponse<BasicDtos.AttemptView> attempt(@PathVariable Long attemptId) {
        return ApiResponse.ok(examService.viewAttempt(attemptId));
    }

    @PostMapping("/attempts/{attemptId}/review")
    public ApiResponse<BasicDtos.AttemptView> review(@PathVariable Long attemptId, @RequestBody BasicDtos.ReviewAnswerRequest request, @RequestParam String actor) {
        return ApiResponse.ok(examService.reviewAnswer(attemptId, request, actor));
    }

    @PostMapping("/attempts/{attemptId}/extend")
    public ApiResponse<BasicDtos.AttemptView> extend(@PathVariable Long attemptId, @RequestBody BasicDtos.ExtendAttemptRequest request, @RequestParam String actor) {
        return ApiResponse.ok(examService.extendAttempt(attemptId, request, actor));
    }

    @GetMapping("/exams/{paperId}/export")
    public ResponseEntity<byte[]> export(@PathVariable Long paperId) {
        byte[] bytes = examService.exportScores(paperId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exam-scores.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}
