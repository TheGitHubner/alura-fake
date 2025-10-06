package br.com.alura.AluraFake.course;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
public class CourseReportDTO {
    @Getter
    private Long id;

    @Getter
    private String title;

    @Getter
    private Status status;

    @Getter
    private LocalDateTime publishedAt;

    @Getter
    private long taskCount;
}
