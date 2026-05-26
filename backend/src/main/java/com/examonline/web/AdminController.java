package com.examonline.web;

import com.examonline.common.ApiResponse;
import com.examonline.domain.AppUser;
import com.examonline.domain.OperationLog;
import com.examonline.domain.SchoolClass;
import com.examonline.service.AdminService;
import com.examonline.service.UserService;
import com.examonline.web.dto.BasicDtos;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    private final AdminService adminService;

    public AdminController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ApiResponse<List<AppUser>> users() {
        return ApiResponse.ok(userService.list());
    }

    @PostMapping("/users")
    public ApiResponse<AppUser> saveUser(@RequestBody AppUser user, @RequestParam String actor) {
        return ApiResponse.ok(userService.save(user, actor));
    }

    @PutMapping("/users/{id}")
    public ApiResponse<AppUser> updateUser(@PathVariable Long id, @RequestBody AppUser user, @RequestParam String actor) {
        user.setId(id);
        return ApiResponse.ok(userService.save(user, actor));
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id, @RequestParam String actor) {
        userService.delete(id, actor);
        return ApiResponse.ok(null);
    }

    @GetMapping("/classes")
    public ApiResponse<List<SchoolClass>> classes() {
        return ApiResponse.ok(adminService.listClasses());
    }

    @PostMapping("/classes")
    public ApiResponse<SchoolClass> saveClass(@RequestBody SchoolClass schoolClass, @RequestParam String actor) {
        return ApiResponse.ok(adminService.saveClass(schoolClass, actor));
    }

    @PutMapping("/classes/{id}")
    public ApiResponse<SchoolClass> updateClass(@PathVariable Long id, @RequestBody SchoolClass schoolClass, @RequestParam String actor) {
        schoolClass.setId(id);
        return ApiResponse.ok(adminService.saveClass(schoolClass, actor));
    }

    @DeleteMapping("/classes/{id}")
    public ApiResponse<Void> deleteClass(@PathVariable Long id, @RequestParam String actor) {
        adminService.deleteClass(id, actor);
        return ApiResponse.ok(null);
    }

    @GetMapping("/stats")
    public ApiResponse<BasicDtos.StatsView> stats() {
        return ApiResponse.ok(adminService.stats());
    }

    @GetMapping("/logs")
    public ApiResponse<List<OperationLog>> logs() {
        return ApiResponse.ok(adminService.logs());
    }
}
