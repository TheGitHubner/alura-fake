package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.enums.Status;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.exception.FieldValidationException;
import br.com.alura.AluraFake.exception.NotFoundException;
import br.com.alura.AluraFake.task.dto.NewMultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.NewTaskOptionDTO;
import br.com.alura.AluraFake.task.enums.Type;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.task.model.TaskOption;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.task.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private TaskService taskService;

    private final Long COURSE_ID = 10L;

    private NewOpenTextTaskDTO newOpenTextDTO(String statement, Integer order) {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(COURSE_ID);
        dto.setStatement(statement);
        dto.setOrder(order);
        return dto;
    }

    private NewSingleChoiceTaskDTO newSingleChoiceDTO(String statement, Integer order, List<NewTaskOptionDTO> options) {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(COURSE_ID);
        dto.setStatement(statement);
        dto.setOrder(order);
        dto.setOptions(options);
        return dto;
    }

    private NewMultipleChoiceTaskDTO newMultipleChoiceDTO(String statement, Integer order, List<NewTaskOptionDTO> options) {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO();
        dto.setCourseId(COURSE_ID);
        dto.setStatement(statement);
        dto.setOrder(order);
        dto.setOptions(options);
        return dto;
    }

    private NewTaskOptionDTO getNewTaskOption(String text, Boolean isCorrect) {
        NewTaskOptionDTO dto = new NewTaskOptionDTO();
        dto.setOption(text);
        dto.setIsCorrect(isCorrect);
        return dto;
    }

    private Course mockCourse(Status status) {
        Course course = mock(Course.class);
        lenient().when(course.getId()).thenReturn(COURSE_ID);
        when(course.getStatus()).thenReturn(status);
        return course;
    }

    @Test
    void createOpenTextTask__should_persist_when_valid() {
        Course course = mockCourse(Status.BUILDING);
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourse_IdAndStatementIgnoreCase(eq(COURSE_ID), anyString())).thenReturn(false);
        when(taskRepository.countByCourseId(COURSE_ID)).thenReturn(0L);
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        NewOpenTextTaskDTO dto = newOpenTextDTO("  Enunciado open text  ", 1);
        taskService.createOpenTextTask(dto);

        verify(taskRepository).save(captor.capture());
        Task toSave = captor.getValue();

        assertThat(toSave.getStatement()).isEqualTo("Enunciado open text");
        assertThat(toSave.getTaskOrder()).isEqualTo(1);
        assertThat(toSave.getTaskType()).isEqualTo(Type.OPEN_TEXT);
        assertThat(toSave.getOptions()).isEmpty();

        verify(taskRepository, never()).shiftOrdersFrom(anyLong(), anyInt());
    }

    @Test
    void createOpenTextTask__should_call_shift_when_inserting_in_middle() {
        Course course = mockCourse(Status.BUILDING);
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourse_IdAndStatementIgnoreCase(eq(COURSE_ID), anyString())).thenReturn(false);
        when(taskRepository.countByCourseId(COURSE_ID)).thenReturn(3L);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        NewOpenTextTaskDTO dto = newOpenTextDTO("Enunciado", 2);
        taskService.createOpenTextTask(dto);

        verify(taskRepository).shiftOrdersFrom(COURSE_ID, 2);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createOpenTextTask__should_throw_when_course_not_found() {
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.empty());

        NewOpenTextTaskDTO dto = newOpenTextDTO("Enunciado", 1);
        assertThatThrownBy(() -> taskService.createOpenTextTask(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Curso não encontrado");
    }

    @Test
    void createOpenTextTask__should_throw_when_course_not_building() {
        Course course = mockCourse(Status.PUBLISHED);
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));

        NewOpenTextTaskDTO dto = newOpenTextDTO("Enunciado", 1);

        assertThatThrownBy(() -> taskService.createOpenTextTask(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Curso deve estar com status BUILDING");
    }

    @Test
    void createOpenTextTask__should_throw_when_statement_already_exists() {
        Course course = mockCourse(Status.BUILDING);
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourse_IdAndStatementIgnoreCase(eq(COURSE_ID), anyString())).thenReturn(true);

        NewOpenTextTaskDTO dto = newOpenTextDTO("Enunciado", 1);

        assertThatThrownBy(() -> taskService.createOpenTextTask(dto))
                .isInstanceOf(FieldValidationException.class)
                .hasMessageContaining("Já existe uma atividade com o mesmo enunciado");
    }

    @Test
    void createOpenTextTask__should_throw_when_order_has_gap() {
        Course course = mockCourse(Status.BUILDING);
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourse_IdAndStatementIgnoreCase(eq(COURSE_ID), anyString())).thenReturn(false);
        when(taskRepository.countByCourseId(COURSE_ID)).thenReturn(1L);

        NewOpenTextTaskDTO dto = newOpenTextDTO("Enunciado", 5);
        assertThatThrownBy(() -> taskService.createOpenTextTask(dto))
                .isInstanceOf(FieldValidationException.class)
                .hasMessageContaining("A ordem das atividade deve ser sequencial e não podem haver brechas entre elas");
    }

    @Test
    void createSingleChoiceTask__should_throw_when_not_exactly_one_correct() {
        Course course = mockCourse(Status.BUILDING);
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourse_IdAndStatementIgnoreCase(eq(COURSE_ID), anyString())).thenReturn(false);
        when(taskRepository.countByCourseId(COURSE_ID)).thenReturn(0L);

        List<NewTaskOptionDTO> options = Arrays.asList(
                getNewTaskOption("A", false),
                getNewTaskOption("B", false)
        );
        NewSingleChoiceTaskDTO dto = newSingleChoiceDTO("Pergunta?", 1, options);

        assertThatThrownBy(() -> taskService.createSingleChoiceTask(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("devem ter exatamente 1 alternativa correta");
    }

    @Test
    void createSingleChoiceTask__should_throw_when_options_out_of_range() {
        Course course = mockCourse(Status.BUILDING);
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourse_IdAndStatementIgnoreCase(eq(COURSE_ID), anyString())).thenReturn(false);
        when(taskRepository.countByCourseId(COURSE_ID)).thenReturn(0L);

        NewSingleChoiceTaskDTO dtoFew = newSingleChoiceDTO("Q?", 1, List.of(getNewTaskOption("A", true)));
        assertThatThrownBy(() -> taskService.createSingleChoiceTask(dtoFew))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("única devem ter entre 2 e 5");

        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO =
                newSingleChoiceDTO("Teste Single Choice", 1,
                    Arrays.asList(getNewTaskOption("alternativa 1", true),
                                  getNewTaskOption("alternativa 2", false),
                                  getNewTaskOption("alternativa 3", false),
                                  getNewTaskOption("alternativa 4", false),
                                  getNewTaskOption("alternativa 5", false),
                                  getNewTaskOption("alternativa 6", false))
                );
        assertThatThrownBy(() -> taskService.createSingleChoiceTask(newSingleChoiceTaskDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("única devem ter entre 2 e 5");
    }

    @Test
    void createSingleChoiceTask__should_save_task() {
        Course course = mockCourse(Status.BUILDING);
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourse_IdAndStatementIgnoreCase(eq(COURSE_ID), anyString())).thenReturn(false);
        when(taskRepository.countByCourseId(COURSE_ID)).thenReturn(0L);

        NewSingleChoiceTaskDTO newSingleChoiceTaskDTO =
                newSingleChoiceDTO("Teste Single Choice", 1,
                        Arrays.asList(getNewTaskOption("alternativa 1", true),
                                getNewTaskOption("alternativa 2", false),
                                getNewTaskOption("alternativa 3", false),
                                getNewTaskOption("alternativa 4", false),
                                getNewTaskOption("alternativa 5", false))
                );

        taskService.createSingleChoiceTask(newSingleChoiceTaskDTO);

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createMultipleChoiceTask__should_persist_with_at_least_2_correct_and_1_incorrect_and_between_3_and_5_options() {
        Course course = mockCourse(Status.BUILDING);
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourse_IdAndStatementIgnoreCase(eq(COURSE_ID), anyString())).thenReturn(false);
        when(taskRepository.countByCourseId(COURSE_ID)).thenReturn(1L);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        List<NewTaskOptionDTO> options = Arrays.asList(
                getNewTaskOption("option A", true),
                getNewTaskOption("option B", true),
                getNewTaskOption("option C", false)
        );
        NewMultipleChoiceTaskDTO dto = newMultipleChoiceDTO("Selecione as corretas", 1, options);

        taskService.createMultipleChoiceTask(dto);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task toSave = captor.getValue();

        assertThat(toSave.getTaskType()).isEqualTo(Type.MULTIPLE_CHOICE);
        assertThat(toSave.getOptions()).hasSize(3);
        assertThat(toSave.getOptions().stream().filter(TaskOption::isCorrect).count()).isGreaterThanOrEqualTo(2);
        assertThat(toSave.getOptions().stream().filter(o -> !o.isCorrect()).count()).isGreaterThanOrEqualTo(1);

        verify(taskRepository).shiftOrdersFrom(COURSE_ID, 1);
    }

    @Test
    void createMultipleChoiceTask__should_throw_when_options_out_of_range_or_duplicated_or_equal_to_statement() {
        Course course = mockCourse(Status.BUILDING);
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourse_IdAndStatementIgnoreCase(eq(COURSE_ID), anyString())).thenReturn(false);
        when(taskRepository.countByCourseId(COURSE_ID)).thenReturn(0L);

        NewMultipleChoiceTaskDTO dtoFew = newMultipleChoiceDTO("Q?", 1,
                Arrays.asList(getNewTaskOption("A", true), getNewTaskOption("B", true)));
        assertThatThrownBy(() -> taskService.createMultipleChoiceTask(dtoFew))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("múltipla escolha devem ter entre 3 e 5");

        NewMultipleChoiceTaskDTO dtoDup = newMultipleChoiceDTO("Pergunta", 1,
                Arrays.asList(getNewTaskOption(" A ", true), getNewTaskOption("a", true), getNewTaskOption("B", false)));
        assertThatThrownBy(() -> taskService.createMultipleChoiceTask(dtoDup))
                .isInstanceOf(FieldValidationException.class)
                .hasMessageContaining("não podem se repetir");

        NewMultipleChoiceTaskDTO dtoEq = newMultipleChoiceDTO("Pergunta", 1,
                Arrays.asList(getNewTaskOption("pergunta", true), getNewTaskOption("Outra", true), getNewTaskOption("Terceira", false)));
        assertThatThrownBy(() -> taskService.createMultipleChoiceTask(dtoEq))
                .isInstanceOf(FieldValidationException.class)
                .hasMessageContaining("não pode ser igual ao enunciado");
    }
}
