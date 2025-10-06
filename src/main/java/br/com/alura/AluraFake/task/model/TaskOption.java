package br.com.alura.AluraFake.task.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

@Entity
@Table(name = "TaskOption")
@NoArgsConstructor
public class TaskOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 80, nullable = false)
    @Getter
    private String optionDescription;

    @Column(nullable = false)
    @Getter
    private boolean isCorrect;

    @ManyToOne(optional = false)
    @Setter
    @Getter
    private Task task;

    public TaskOption(String optionDescription, boolean isOptionCorrect) {
        Assert.notNull(optionDescription, "A descrição da alternativa não pode ser nulo");
        Assert.isTrue(optionDescription.length() >= 4 && optionDescription.length() <= 80, "O texto da opção deve ter entre 4 e 80 caracteres");
        this.optionDescription = optionDescription;
        this.isCorrect = isOptionCorrect;
    }
}
