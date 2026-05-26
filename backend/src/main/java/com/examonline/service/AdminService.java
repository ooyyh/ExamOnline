package com.examonline.service;

import com.examonline.common.BadRequestException;
import com.examonline.common.NotFoundException;
import com.examonline.domain.AppUser;
import com.examonline.domain.ExamPaper;
import com.examonline.domain.SchoolClass;
import com.examonline.repo.AppUserRepository;
import com.examonline.repo.ExamAttemptRepository;
import com.examonline.repo.ExamPaperRepository;
import com.examonline.repo.OperationLogRepository;
import com.examonline.repo.QuestionRepository;
import com.examonline.repo.SchoolClassRepository;
import com.examonline.web.dto.BasicDtos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {
    private final AppUserRepository userRepository;
    private final SchoolClassRepository classRepository;
    private final QuestionRepository questionRepository;
    private final ExamPaperRepository paperRepository;
    private final ExamAttemptRepository attemptRepository;
    private final OperationLogRepository logRepository;
    private final LogService logService;

    public AdminService(AppUserRepository userRepository,
                        SchoolClassRepository classRepository,
                        QuestionRepository questionRepository,
    ExamPaperRepository paperRepository,
    ExamAttemptRepository attemptRepository,
    OperationLogRepository logRepository,
    LogService logService) {
        this.userRepository = userRepository;
        this.classRepository = classRepository;
        this.questionRepository = questionRepository;
        this.paperRepository = paperRepository;
        this.attemptRepository = attemptRepository;
        this.logRepository = logRepository;
        this.logService = logService;
    }

    public BasicDtos.StatsView stats() {
        long userCount = userRepository.count();
        long teacherCount = userRepository.findAll().stream().filter(u -> "TEACHER".equalsIgnoreCase(u.getRole())).count();
        long studentCount = userRepository.findAll().stream().filter(u -> "STUDENT".equalsIgnoreCase(u.getRole())).count();
        return new BasicDtos.StatsView(
                userCount,
                teacherCount,
                studentCount,
                classRepository.count(),
                questionRepository.count(),
                paperRepository.count(),
                attemptRepository.count(),
                attemptRepository.findAll().stream().filter(a -> "IN_PROGRESS".equals(a.getStatus())).count()
        );
    }

    public List<SchoolClass> listClasses() {
        return classRepository.findAll();
    }

    @Transactional
    public SchoolClass saveClass(SchoolClass request, String actor) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("班级名称不能为空");
        }
        boolean existed = request.getId() != null ? classRepository.existsById(request.getId()) : classRepository.findByName(request.getName()).isPresent();
        SchoolClass entity = request.getId() != null ? classRepository.findById(request.getId()).orElseGet(SchoolClass::new) : classRepository.findByName(request.getName()).orElseGet(SchoolClass::new);
        entity.setName(request.getName());
        entity.setDepartment(request.getDepartment());
        entity.setMajor(request.getMajor());
        entity.setStudentCount(request.getStudentCount() == null ? 0 : request.getStudentCount());
        SchoolClass saved = classRepository.save(entity);
        logService.record(actor, existed ? "UPDATE" : "CREATE", "CLASS", String.valueOf(saved.getId()), saved.getName());
        return saved;
    }

    public void deleteClass(Long id, String actor) {
        SchoolClass entity = classRepository.findById(id).orElseThrow(() -> new NotFoundException("班级不存在"));
        classRepository.delete(entity);
        logService.record(actor, "DELETE", "CLASS", String.valueOf(id), entity.getName());
    }

    public List<com.examonline.domain.OperationLog> logs() {
        return logRepository.findTop100ByOrderByCreatedAtDesc();
    }
}
