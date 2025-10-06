package br.com.alura.AluraFake.task.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class NewTaskBaseDTO {
    @NotNull(message = "O ID do curso deve ser informado")
    private Long courseId;

    @NotNull(message = "O enunciado deve ser informado")
    @Length(min = 4, max = 255, message = "O enunciado deve ter entre 4 e 255 caracteres")
    private String statement;

    @NotNull(message = "A ordem deve ser informada")
    @Min(value = 1, message = "A ordem deve ser um número inteiro positivo")
    private Integer order;
}
