package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Getter
    private String title;

    @Getter
    private String description;

    @ManyToOne
    private User instructor;

    @Enumerated(EnumType.STRING)
    @Setter
    @Getter
    private Status status;

    private LocalDateTime publishedAt;

    @Deprecated
    public Course(){}

    public Course(String title, String description, User instructor) {
        Assert.isTrue(instructor.isInstructor(), "Usuário deve ser um instrutor");
        this.title = title;
        this.instructor = instructor;
        this.description = description;
        this.status = Status.BUILDING;
    }

    public void publishCourse(LocalDateTime now) {
        Assert.notNull(now, "Data de publicação deve ser informada");
        if (this.status != Status.BUILDING) {
            throw new IllegalStateException("Curso deve estar com status BUILDING para poder publicat");
        }
        this.status = Status.PUBLISHED;
        this.publishedAt = now;
    }
}
