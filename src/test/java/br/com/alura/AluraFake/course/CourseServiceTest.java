package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.dto.CourseListItemDTO;
import br.com.alura.AluraFake.course.dto.NewCourseDTO;
import br.com.alura.AluraFake.course.enums.Status;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.course.service.CourseService;
import br.com.alura.AluraFake.exception.FieldValidationException;
import br.com.alura.AluraFake.exception.NotFoundException;
import br.com.alura.AluraFake.task.enums.Type;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.model.User;
import br.com.alura.AluraFake.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;

    @Mock private
    UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CourseService courseService;

    private final Long COURSE_ID = 1L;

    private User getInstructor() {
        return new User("Instrutor", "inst@alura.com", Role.INSTRUCTOR);
    }

    private Task getTask(Course course, String statement, int order) {
        return new Task(course, null, statement, order);
    }

    private NewCourseDTO newCourseDTO(String title, String description, String emailInstructor) {
        NewCourseDTO dto = new NewCourseDTO();
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setEmailInstructor(emailInstructor);
        return dto;
    }

    @Test
    void createCourse__should_persist_when_instructor_is_valid() {
        var dto = newCourseDTO("Curso X", "Descrição ok", "inst@alura.com");
        when(userRepository.findByEmail("inst@alura.com")).thenReturn(Optional.of(getInstructor()));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        courseService.createCourse(dto);

        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse__should_throw_when_user_is_not_instructor() {
        var dto = newCourseDTO("Curso Java", "Descrição Java", "curso@java.com");
        var aluno = new User("Aluno", "aluno@alura.com", Role.STUDENT);
        when(userRepository.findByEmail("curso@java.com")).thenReturn(Optional.of(aluno));

        assertThatThrownBy(() -> courseService.createCourse(dto))
                .isInstanceOf(FieldValidationException.class)
                .hasMessageContaining("Usuário não é um instrutor");
    }

    @Test
    void listAll__should_put_entities_in_dto() {
        var inst = getInstructor();
        Course c1 = new Course("Curso 1", "Desc 1", inst);
        Course c2 = new Course("Curso 2", "Desc 2", inst);
        ReflectionTestUtils.setField(c1, "id", 1L);
        ReflectionTestUtils.setField(c2, "id", 2L);

        when(courseRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CourseListItemDTO> list = courseService.listAllCourses();

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getId()).isEqualTo(1L);
        assertThat(list.get(0).getTitle()).isEqualTo("Curso 1");
        assertThat(list.get(1).getId()).isEqualTo(2L);
    }

    @Test
    void publishCourse__should_throw_when_course_not_found() {
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.publishCourse(COURSE_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Curso não encontrado");
    }

    @Test
    void publishCourse__should_throw_when_status_is_not_building() {
        var inst = getInstructor();
        Course course = new Course("Curso", "Desc", inst);
        ReflectionTestUtils.setField(course, "id", COURSE_ID);
        course.setStatus(Status.PUBLISHED);

        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> courseService.publishCourse(COURSE_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("status BUILDING");
    }

    @Test
    void publish__should_publish_when_valid() {
        var instructor = getInstructor();
        Course course = new Course("Curso", "Teste", instructor);
        ReflectionTestUtils.setField(course, "id", COURSE_ID);
        course.setStatus(Status.BUILDING);

        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
        when(taskRepository.countByCourse_IdAndTaskType(COURSE_ID, Type.OPEN_TEXT)).thenReturn(1L);
        when(taskRepository.countByCourse_IdAndTaskType(COURSE_ID, Type.SINGLE_CHOICE)).thenReturn(1L);
        when(taskRepository.countByCourse_IdAndTaskType(COURSE_ID, Type.MULTIPLE_CHOICE)).thenReturn(1L);

        List<Task> taskList = Arrays.asList(
                getTask(course, "atividade 1", 1),
                getTask(course, "atividade 2", 2),
                getTask(course, "atividade 3", 3)
        );

        when(taskRepository.findAllByCourse_IdOrderByTaskOrderAsc(COURSE_ID))
                .thenReturn(taskList);

        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        courseService.publishCourse(COURSE_ID);

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository, atLeastOnce()).save(captor.capture());
        Course saved = captor.getValue();

        assertThat(saved.getStatus()).isEqualTo(Status.PUBLISHED);
    }
}
