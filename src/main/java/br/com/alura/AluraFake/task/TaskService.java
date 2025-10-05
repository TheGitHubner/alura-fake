package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;

    public TaskService(TaskRepository taskRepository,
                       CourseRepository courseRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Task createOpenTextTask(NewOpenTextTaskDTO newOpenTextTaskDTO) {
        Course course = genericValidationsCanCreateTaskAndGetCourse (
                newOpenTextTaskDTO.getCourseId(),
                newOpenTextTaskDTO.getStatement(),
                newOpenTextTaskDTO.getOrder()
        );

        Task task = new Task(course, Type.OPEN_TEXT, newOpenTextTaskDTO.getStatement().trim(), newOpenTextTaskDTO.getOrder());
        return taskRepository.save(task);
    }

    @Transactional
    public Task createSingleChoiceTask(NewSingleChoiceTaskDTO newSingleChoiceTaskDTO) {
        Course course = genericValidationsCanCreateTaskAndGetCourse(
                newSingleChoiceTaskDTO.getCourseId(),
                newSingleChoiceTaskDTO.getStatement(),
                newSingleChoiceTaskDTO.getOrder()
        );
        validateOptionsForSingleChoiceTask(newSingleChoiceTaskDTO.getStatement(), newSingleChoiceTaskDTO.getOptions());

        Task task = new Task(course, Type.SINGLE_CHOICE, newSingleChoiceTaskDTO.getStatement().trim(), newSingleChoiceTaskDTO.getOrder());

        newSingleChoiceTaskDTO
                .getOptions()
                .forEach(o -> {
            TaskOption option = new TaskOption(o.getOption().trim(), Boolean.TRUE.equals(o.getIsCorrect()));
            task.addTaskOption(option);
        });

        return taskRepository.save(task);
    }

    @Transactional
    public Task createMultipleChoiceTask(NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO) {
        Course course = genericValidationsCanCreateTaskAndGetCourse(
                newMultipleChoiceTaskDTO.getCourseId(),
                newMultipleChoiceTaskDTO.getStatement(),
                newMultipleChoiceTaskDTO.getOrder()
        );
        validateOptionsForMultipleChoiceTask(newMultipleChoiceTaskDTO.getStatement(), newMultipleChoiceTaskDTO.getOptions());

        Task task = new Task(course, Type.MULTIPLE_CHOICE, newMultipleChoiceTaskDTO.getStatement().trim(), newMultipleChoiceTaskDTO.getOrder());

        newMultipleChoiceTaskDTO
                .getOptions()
                .forEach(o -> {
            TaskOption option = new TaskOption(o.getOption().trim(), Boolean.TRUE.equals(o.getIsCorrect()));
            task.addTaskOption(option);
        });

        return taskRepository.save(task);
    }

    private Course genericValidationsCanCreateTaskAndGetCourse(Long courseId, String statement, Integer order) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("Curso não encontrado: " + courseId));

        validateCourseIsBuilding(course);
        validateStatementAlreadyExists(course.getId(), statement);
        validateOrderAndShift(course.getId(), order);

        return course;
    }

    private void validateCourseIsBuilding(Course course) {
        if (!course.getStatus().equals(Status.BUILDING)) {
            throw new IllegalStateException("Curso deve estar com status BUILDING para cadastrar atividades");
        }
    }

    private void validateStatementAlreadyExists(Long courseId, String statement) {
        if (taskRepository.existsByCourse_IdAndStatementIgnoreCase(courseId, statement.trim())) {
            throw new IllegalArgumentException("Já existe uma atividade com o mesmo enunciado neste curso");
        }
    }

    private void validateOrderAndShift(Long courseId, Integer order) {
        Long count = taskRepository.countByCourseId(courseId);
        if (order > count + 1) {
            throw new IllegalArgumentException("Não podem haver brechas entre as ordens das atividade");
        }
        if (order <= count) {
            taskRepository.shiftOrdersFrom(courseId, order);
        }
    }

    private void validateOptionsForSingleChoiceTask(String statement, List<NewTaskOptionDTO> options) {
        validateQuantityChoicesSize(options, 2, 5, "Atividades de alterantiva única devem ter entre 2 e 5 alternativas");
        validateNoDuplicatesAndNotEqualStatement(statement, options);

        long correctOptionQuantity = options.
                stream().
                filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                .count();
        if (correctOptionQuantity != 1) {
            throw new IllegalArgumentException("Atividades de alternativa única devem ter exatamente 1 alternativa correta");
        }
    }

    private void validateOptionsForMultipleChoiceTask(String statement, List<NewTaskOptionDTO> options) {
        validateQuantityChoicesSize(options, 3, 5, "Atividades de múltipla escolha devem ter entre 3 e 5 alternativas");
        validateNoDuplicatesAndNotEqualStatement(statement, options);

        long correctOptionQuantity = options
                .stream()
                .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                .count();
        long incorrectOptionQuantity = options.size() - correctOptionQuantity;

        if (correctOptionQuantity < 2 || incorrectOptionQuantity < 1) {
            throw new IllegalArgumentException("Atividades de múltipla escolha deve ter ao menos 2 alternativas corretas e 1 incorreta");
        }
    }

    private void validateQuantityChoicesSize(List<NewTaskOptionDTO> list, int min, int max, String message) {
        if (list == null || list.size() < min || list.size() > max) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateNoDuplicatesAndNotEqualStatement(String statement, List<NewTaskOptionDTO> options) {
        String commingStatement = prepareForComparison(statement);

        List<String> statements = new ArrayList<>();
        for (NewTaskOptionDTO o : options) {
            Assert.isTrue(StringUtils.hasText(o.getOption()), "Opção não pode ser vazia");
            String actualStatements = prepareForComparison(o.getOption());

            if (actualStatements.equals(commingStatement)) {
                throw new IllegalArgumentException("A alternativa não pode ser igual ao enunciado da atividade");
            }
            if (statements.contains(actualStatements)) {
                throw new IllegalArgumentException("As alternativas não podem se repetir");
            }
            statements.add(actualStatements);
        }
    }

    private String prepareForComparison(String statement) {
        return Objects.isNull(statement)
                ? ""
                : statement.trim().toLowerCase();
    }
}
