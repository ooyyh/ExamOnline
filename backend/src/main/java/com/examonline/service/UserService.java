package com.examonline.service;

import com.examonline.common.BadRequestException;
import com.examonline.common.NotFoundException;
import com.examonline.domain.AppUser;
import com.examonline.repo.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final AppUserRepository repository;
    private final LogService logService;

    public UserService(AppUserRepository repository, LogService logService) {
        this.repository = repository;
        this.logService = logService;
    }

    public AppUser login(String username, String password) {
        AppUser user = repository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("账号或密码错误"));
        if (!Boolean.TRUE.equals(user.getEnabled()) || !user.getPassword().equals(password)) {
            throw new BadRequestException("账号或密码错误");
        }
        return user;
    }

    public AppUser me(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new NotFoundException("用户不存在"));
    }

    public List<AppUser> list() {
        return repository.findAll();
    }

    @Transactional
    public AppUser save(AppUser request, String actor) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new BadRequestException("用户名不能为空");
        }
        boolean existed = request.getId() != null ? repository.existsById(request.getId()) : repository.findByUsername(request.getUsername()).isPresent();
        AppUser user = request.getId() != null ? repository.findById(request.getId()).orElseGet(AppUser::new) : repository.findByUsername(request.getUsername()).orElseGet(AppUser::new);
        if (user.getId() != null && (request.getPassword() == null || request.getPassword().isBlank())) {
            request.setPassword(user.getPassword());
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("密码不能为空");
        }
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        user.setRealName(request.getRealName());
        user.setStudentNo(request.getStudentNo());
        user.setEmployeeNo(request.getEmployeeNo());
        user.setClassName(request.getClassName());
        user.setDepartment(request.getDepartment());
        user.setMajor(request.getMajor());
        user.setEnabled(request.getEnabled() == null || request.getEnabled());
        AppUser saved = repository.save(user);
        logService.record(actor, existed ? "UPDATE" : "CREATE", "USER", String.valueOf(saved.getId()), saved.getUsername());
        return saved;
    }

    public void delete(Long id, String actor) {
        AppUser user = repository.findById(id).orElseThrow(() -> new NotFoundException("用户不存在"));
        repository.delete(user);
        logService.record(actor, "DELETE", "USER", String.valueOf(id), user.getUsername());
    }

    @Transactional
    public AppUser changePassword(String username, String oldPassword, String newPassword) {
        AppUser user = repository.findByUsername(username).orElseThrow(() -> new NotFoundException("用户不存在"));
        if (!user.getPassword().equals(oldPassword)) {
            throw new BadRequestException("旧密码错误");
        }
        user.setPassword(newPassword);
        return repository.save(user);
    }
}
