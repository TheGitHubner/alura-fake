package br.com.alura.AluraFake.user.dto;

import br.com.alura.AluraFake.course.dto.CourseReportDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class InstructorReportDTO {
    @Getter
    private List<CourseReportDTO> courses;

    @Getter
    private long publishedCoursesCount;
}
