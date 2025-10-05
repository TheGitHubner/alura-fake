package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Task")
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Getter
    private Type taskType;

    @Column(length = 255, nullable = false)
    @Getter
    private String statement;

    @Column(nullable = false)
    @Getter
    private Integer taskOrder;

    @ManyToOne(optional = false)
    private Course course;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private List<TaskOption> options = new ArrayList<>();

    public Task(Course course, Type type, String statement, Integer order) {
        Assert.notNull(course, "O curso não pode ser nulo");
        Assert.hasText(statement, "O enunciado da atividade deve ser informado ");
        Assert.isTrue(statement.length() >= 4 && statement.length() <= 255, "O enunciado da atividade deve ter entre 4 e 255 caracteres");
        Assert.isTrue(order > 0, "A ordem deve ser positiva");

        this.course = course;
        this.taskType = type;
        this.statement = statement;
        this.taskOrder = order;
    }

    public void addTaskOption(TaskOption option) {
        Assert.isTrue(!Type.OPEN_TEXT.equals(this.taskType), "Atividades de resposta aberta não podem ter opções");
        Assert.notNull(option, "A opção não pode ser nula");
        option.setTask(this);
        this.options.add(option);
    }
}
