package br.com.alura.AluraFake.user;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseReportDTO;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.exception.FieldValidationException;
import br.com.alura.AluraFake.exception.NotFoundException;
import br.com.alura.AluraFake.task.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;

    public UserService(UserRepository userRepository,
                       CourseRepository courseRepository,
                       TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public void createUser(NewUserDTO newUser) {
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new FieldValidationException("email", "Email já cadastrado no sistema");
        }
        userRepository.save(newUser.toModel());
    }

    @Transactional
    public List<UserListItemDTO> listAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserListItemDTO::new)
                .toList();
    }

    @Transactional
    public InstructorReportDTO getInstructorReport(Long instructorId) {
        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new NotFoundException("instructorId", "Usuário não encontrado: " + instructorId));

        if (!user.isInstructor()) {
            throw new FieldValidationException("role", "Usuário não é um instrutor");
        }

        List<Course> instructorCourses = courseRepository.findAllByInstructor_Id(instructorId);
        List<CourseReportDTO> courses = getInstructorCoursesReportList(instructorCourses);
        long publishedCoursesCount = getInstructorPublishedCoursesCount(courses);

        return new InstructorReportDTO(courses, publishedCoursesCount);
    }

    private List<CourseReportDTO> getInstructorCoursesReportList(List<Course> instructorCourses) {
        return instructorCourses.stream()
                .map(c -> new CourseReportDTO(
                        c.getId(),
                        c.getTitle(),
                        c.getStatus(),
                        c.getPublishedAt(),
                        taskRepository.countByCourseId(c.getId())
                ))
                .toList();
    }

    private static long getInstructorPublishedCoursesCount(List<CourseReportDTO> courses) {
        return courses.stream()
                .filter(i -> i.getStatus() == Status.PUBLISHED)
                .count();
    }
}

