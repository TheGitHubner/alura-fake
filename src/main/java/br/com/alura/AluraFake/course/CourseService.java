package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.exception.FieldValidationException;
import br.com.alura.AluraFake.exception.NotFoundException;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public CourseService(CourseRepository courseRepository,
                         UserRepository userRepository,
                         TaskRepository taskRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public void createCourse(NewCourseDTO newCourse) {
        if (!StringUtils.hasText(newCourse.getEmailInstructor())) {
            throw new FieldValidationException("emailInstructor", "Email do instrutor é obrigatório");
        }

        User instructor = userRepository.findByEmail(newCourse.getEmailInstructor())
                .filter(User::isInstructor)
                .orElseThrow(() ->
                        new FieldValidationException("emailInstructor", "Usuário não é um instrutor"));

        Course course = new Course(newCourse.getTitle(), newCourse.getDescription(), instructor);
        courseRepository.save(course);
    }

    @Transactional
    public List<CourseListItemDTO> listAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseListItemDTO::new)
                .toList();
    }

    @Transactional
    public void publishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("courseId", "Curso não encontrado: " + courseId));

        validateCourseStatus(course);
        validateHasOneTaskOfEachType(course.getId());
        validateOrderSequence(course.getId());

        course.publishCourse(LocalDateTime.now());
        courseRepository.save(course);
    }

    private void validateCourseStatus(Course course) {
        if (!course.getStatus().equals(Status.BUILDING)) {
            throw new IllegalStateException("Curso precisa estar com status BUILDING para publicar");
        }
    }

    private void validateHasOneTaskOfEachType(Long courseId) {
        for (Type taskType : Arrays.asList(Type.OPEN_TEXT, Type.SINGLE_CHOICE, Type.MULTIPLE_CHOICE)) {
            long count = taskRepository.countByCourse_IdAndTaskType(courseId, taskType);
            if (count == 0) {
                throw new IllegalStateException("Curso precisa conter ao menos uma atividade do tipo: " + taskType);
            }
        }
    }

    private void validateOrderSequence(Long courseId) {
        List<Task> tasks = taskRepository.findAllByCourse_IdOrderByTaskOrderAsc(courseId);
        if (tasks.isEmpty()) {
            throw new IllegalStateException("Curso deve conter atividades para poder ser publicado");
        }

        int expectedTaskOrder = 1;
        for (Task task : tasks) {
            Integer actualTaskOrder = task.getTaskOrder();
            if (Objects.isNull(actualTaskOrder) || actualTaskOrder != expectedTaskOrder) {
                throw new IllegalStateException("As atividades desse curso não se encontram cadastradas com a ordem correta, verifique!");
            }
            expectedTaskOrder++;
        }
    }
}
