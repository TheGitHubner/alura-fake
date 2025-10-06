package br.com.alura.AluraFake.user;

import br.com.alura.AluraFake.course.enums.Status;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.exception.FieldValidationException;
import br.com.alura.AluraFake.exception.NotFoundException;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.user.dto.InstructorReportDTO;
import br.com.alura.AluraFake.user.dto.NewUserDTO;
import br.com.alura.AluraFake.user.dto.UserListItemDTO;
import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.model.User;
import br.com.alura.AluraFake.user.repository.UserRepository;
import br.com.alura.AluraFake.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private UserService userService;

    private NewUserDTO newUserDTO(String name, String email, Role role) {
        NewUserDTO dto = new NewUserDTO();
        dto.setName(name);
        dto.setEmail(email);
        dto.setRole(role);
        return dto;
    }

    private User getInstructor() {
        return new User("Inst", "inst@alura.com", Role.INSTRUCTOR);
    }

    private User getStudent() {
        return new User("Aluno", "aluno@alura.com", Role.STUDENT);
    }

    @Test
    void createUser__should_throw_when_email_already_exists() {
        var dto = newUserDTO("Ana", "ana@alura.com", Role.STUDENT);
        when(userRepository.existsByEmail("ana@alura.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(FieldValidationException.class)
                .hasMessageContaining("Email já cadastrado no sistema");
    }

    @Test
    void createUser__should_save_when_valid() {
        var dto = newUserDTO("Ana", "ana@alura.com", Role.STUDENT);
        when(userRepository.existsByEmail("ana@alura.com")).thenReturn(false);

        userService.createUser(dto);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void listAllUsers__should_return_dtos() {
        var u1 = new User("Caio", "caio@alura.com", Role.STUDENT);
        var u2 = new User("Paulo", "paulo@alura.com", Role.INSTRUCTOR);
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<UserListItemDTO> list = userService.listAllUsers();

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getName()).isEqualTo("Caio");
        assertThat(list.get(1).getRole()).isEqualTo(Role.INSTRUCTOR);
    }

    @Test
    void getInstructorCoursesReport__should_throw_404_when_user_not_found() {
        when(userRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getInstructorReport(7L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    void getInstructorCoursesReport__should_throw_400_when_user_is_not_instructor() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(getStudent()));

        assertThatThrownBy(() -> userService.getInstructorReport(7L))
                .isInstanceOf(FieldValidationException.class)
                .hasMessageContaining("Usuário não é um instrutor");
    }

    @Test
    void getInstructorCoursesReport__should_return_empty_list_when_instructor_has_no_courses() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(getInstructor()));
        when(courseRepository.findAllByInstructor_Id(7L)).thenReturn(List.of());

        InstructorReportDTO report = userService.getInstructorReport(7L);

        assertThat(report.getCourses()).isEmpty();
        assertThat(report.getPublishedCoursesCount()).isZero();
    }

    @Test
    void getInstructorCoursesReport__should_return_courses_with_activities_count_and_total_published() {
        var inst = getInstructor();
        when(userRepository.findById(7L)).thenReturn(Optional.of(inst));

        Course c1 = new Course("Spring Boot", "Desc", inst);
        Course c2 = new Course("Java Avançado", "Desc", inst);
        ReflectionTestUtils.setField(c1, "id", 10L);
        ReflectionTestUtils.setField(c2, "id", 11L);
        c1.setStatus(Status.PUBLISHED);
        c2.setStatus(Status.BUILDING);
        ReflectionTestUtils.setField(c1, "publishedAt", LocalDateTime.now());

        when(courseRepository.findAllByInstructor_Id(7L)).thenReturn(List.of(c1, c2));
        when(taskRepository.countByCourseId(10L)).thenReturn(5L);
        when(taskRepository.countByCourseId(11L)).thenReturn(2L);

        InstructorReportDTO report = userService.getInstructorReport(7L);

        assertThat(report.getCourses()).hasSize(2);
        assertThat(report.getCourses().get(0).getId()).isEqualTo(10L);
        assertThat(report.getCourses().get(0).getTaskCount()).isEqualTo(5L);
        assertThat(report.getCourses().get(1).getTaskCount()).isEqualTo(2L);
        assertThat(report.getPublishedCoursesCount()).isEqualTo(1);
    }
}
