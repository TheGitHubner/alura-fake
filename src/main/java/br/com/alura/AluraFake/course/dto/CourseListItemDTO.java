package br.com.alura.AluraFake.course.dto;

import br.com.alura.AluraFake.course.enums.Status;
import br.com.alura.AluraFake.course.model.Course;
import lombok.Getter;

import java.io.Serializable;

public class CourseListItemDTO implements Serializable {

    private Long id;

    @Getter
    private String title;

    @Getter
    private String description;

    @Getter
    private Status status;

    public CourseListItemDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.status = course.getStatus();
    }
}
