package br.com.alura.AluraFake.task.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class NewTaskOptionDTO {
    @NotNull(message = "A descrição da opção deve ser informada")
    @Length(min = 4, max = 80, message = "A descrição da opção deve conter entre 4 e 80 caracteres")
    private String option;

    @NotNull(message = "É preciso informar se a opção está correta")
    private Boolean isCorrect;
}
