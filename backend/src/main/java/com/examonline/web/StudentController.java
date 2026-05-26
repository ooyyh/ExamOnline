package com.examonline.web;

import com.examonline.common.ApiResponse;
import com.examonline.service.ExamService;
import com.examonline.web.dto.BasicDtos;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final ExamService examService;

    public StudentController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/exams")
    public ApiResponse<List<BasicDtos.ExamSummary>> upcoming(@RequestParam String username) {
        return ApiResponse.ok(examService.upcoming(username));
    }

    @PostMapping("/exams/{paperId}/start")
    public ApiResponse<BasicDtos.StartExamResponse> start(@PathVariable Long paperId, @RequestParam String username) {
        return ApiResponse.ok(examService.start(paperId, username));
    }

    @PostMapping("/attempts/{attemptId}/answer")
    public ApiResponse<Void> saveAnswer(@PathVariable Long attemptId, @RequestBody BasicDtos.SaveAnswerRequest request) {
        examService.saveAnswer(attemptId, request);
        return ApiResponse.ok(null);
    }

    @PostMapping("/attempts/{attemptId}/switch")
    public ApiResponse<BasicDtos.AttemptView> recordSwitch(@PathVariable Long attemptId) {
        return ApiResponse.ok(examService.recordSwitch(attemptId));
    }

    @PostMapping("/attempts/{attemptId}/submit")
    public ApiResponse<BasicDtos.AttemptView> submit(@PathVariable Long attemptId) {
        return ApiResponse.ok(examService.submit(attemptId, false));
    }

    @GetMapping("/attempts/{attemptId}")
    public ApiResponse<BasicDtos.AttemptView> viewAttempt(@PathVariable Long attemptId) {
        return ApiResponse.ok(examService.viewAttempt(attemptId));
    }

    @GetMapping("/history")
    public ApiResponse<List<BasicDtos.AttemptView>> history(@RequestParam String username) {
        return ApiResponse.ok(examService.history(username));
    }

    @GetMapping("/wrong-book")
    public ApiResponse<List<BasicDtos.WrongBookView>> wrongBook(@RequestParam String username) {
        return ApiResponse.ok(examService.wrongBook(username));
    }

    @GetMapping("/stats")
    public ApiResponse<BasicDtos.StudentStatsView> stats(@RequestParam String username) {
        return ApiResponse.ok(examService.studentStats(username));
    }
}

