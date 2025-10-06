package br.com.alura.AluraFake.course.repository;

import br.com.alura.AluraFake.course.enums.Status;
import br.com.alura.AluraFake.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long>{
    List<Course> findAllByInstructor_Id(Long instructorId);

    long countByInstructor_IdAndStatus(Long instructorId, Status status);
}
