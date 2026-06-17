package com.examonline.web;

import com.examonline.domain.ExamPaper;
import com.examonline.domain.PaperQuestion;
import com.examonline.domain.Question;
import com.examonline.repo.ExamPaperRepository;
import com.examonline.repo.PaperQuestionRepository;
import com.examonline.repo.QuestionRepository;
import com.examonline.service.JsonSupport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:paper_visibility;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class PaperVisibilityIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamPaperRepository paperRepository;

    @Autowired
    private PaperQuestionRepository paperQuestionRepository;

    @Autowired
    private JsonSupport jsonSupport;

    @Test
    void studentCanSeeFuturePaperCreatedAndPublishedForAllClassesThroughApi() throws Exception {
        Question question = questionRepository.findAll().get(0);
        String title = "API 全部班级可见性测试";
        Map<String, Object> createPayload = Map.of(
                "title", title,
                "durationMinutes", 60,
                "passScore", 60,
                "startTime", LocalDateTime.now().plusHours(2).toString(),
                "endTime", LocalDateTime.now().plusDays(1).toString(),
                "questionIds", List.of(question.getId()),
                "targetClasses", List.of("全部班级")
        );

        String createResponse = mockMvc.perform(post("/api/teacher/papers/manual")
                        .queryParam("actor", "teacher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.targetClasses").isEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        long paperId = objectMapper.readTree(createResponse).path("data").path("id").asLong();

        mockMvc.perform(post("/api/teacher/papers/{id}/publish", paperId)
                        .queryParam("actor", "teacher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("targetClasses", List.of("全部班级")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.published").value(true))
                .andExpect(jsonPath("$.data.targetClasses").isEmpty());

        String upcomingResponse = mockMvc.perform(get("/api/student/exams")
                        .queryParam("username", "student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        JsonNode exams = objectMapper.readTree(upcomingResponse).path("data");

        assertThat(exams)
                .anySatisfy(exam -> {
                    assertThat(exam.path("id").asLong()).isEqualTo(paperId);
                    assertThat(exam.path("title").asText()).isEqualTo(title);
                });
    }

    @Test
    void studentCanSeeLegacyPaperStoredWithAllClassToken() throws Exception {
        Question question = questionRepository.findAll().get(0);
        ExamPaper paper = new ExamPaper();
        paper.setTitle("旧数据全部班级可见性测试");
        paper.setDurationMinutes(60);
        paper.setPassScore(60);
        paper.setStartTime(LocalDateTime.now().minusMinutes(5));
        paper.setEndTime(LocalDateTime.now().plusDays(1));
        paper.setPublished(true);
        paper.setAutoGenerated(false);
        paper.setTargetClassesJson(jsonSupport.toJson(List.of("全部班级")));
        paper.setTotalScore(question.getScore());
        paper = paperRepository.save(paper);

        PaperQuestion item = new PaperQuestion();
        item.setPaperId(paper.getId());
        item.setQuestionId(question.getId());
        item.setOrderNo(1);
        item.setScore(question.getScore());
        item.setQuestionSnapshotJson(jsonSupport.toJson(jsonSupport.toQuestionView(question)));
        paperQuestionRepository.save(item);

        String upcomingResponse = mockMvc.perform(get("/api/student/exams")
                        .queryParam("username", "student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        JsonNode exams = objectMapper.readTree(upcomingResponse).path("data");
        long paperId = paper.getId();

        assertThat(exams).anySatisfy(exam -> assertThat(exam.path("id").asLong()).isEqualTo(paperId));
    }
}
