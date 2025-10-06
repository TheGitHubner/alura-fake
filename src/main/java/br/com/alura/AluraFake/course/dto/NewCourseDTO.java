package br.com.alura.AluraFake.course.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
public class NewCourseDTO {

    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String title;

    @NotNull
    @NotBlank
    @Length(min = 4, max = 255)
    @Getter
    @Setter
    private String description;

    @NotNull
    @NotBlank
    @Email
    @Getter
    @Setter
    private String emailInstructor;
}
