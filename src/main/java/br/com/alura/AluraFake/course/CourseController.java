package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.user.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService){
        this.courseService = courseService;
    }

    @Transactional
    @PostMapping("/course/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse) {
        courseService.createCourse(newCourse);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/course/all")
    public ResponseEntity<List<CourseListItemDTO>> createCourse() {
        return ResponseEntity.ok(courseService.listAllCourses());
    }

    @PostMapping("/course/{id}/publish")
    public ResponseEntity createCourse(@PathVariable("id") Long id) {
        courseService.publishCourse(id);
        return ResponseEntity.ok().build();
    }

}
