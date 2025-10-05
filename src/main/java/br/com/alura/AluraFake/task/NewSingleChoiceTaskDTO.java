package br.com.alura.AluraFake.task;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NewSingleChoiceTaskDTO extends NewTaskBaseDTO {
    @Valid
    @NotNull(message = "As opções precisam ser informadas")
    @Size(min = 2, max = 5, message = "A atividade precisa ter entre 2 e 5 alternativas")
    private List<NewTaskOptionDTO> options;
}
