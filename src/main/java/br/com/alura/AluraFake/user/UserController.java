package br.com.alura.AluraFake.user;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/new")
    public ResponseEntity newStudent(@RequestBody @Valid NewUserDTO newUser) {
        userService.createUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/user/all")
    public ResponseEntity<List<UserListItemDTO>> listAllUsers() {
        return ResponseEntity.ok(userService.listAllUsers());
    }

    @GetMapping("/instructor/{id}/courses")
    public ResponseEntity<InstructorReportDTO> reportByInstructor(@PathVariable("id") Long instructorId) {
        return ResponseEntity.ok(userService.getInstructorReport(instructorId));
    }
}
