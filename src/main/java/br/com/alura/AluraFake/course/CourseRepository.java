package br.com.alura.AluraFake.course;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long>{
    List<Course> findAllByInstructor_Id(Long instructorId);

    long countByInstructor_IdAndStatus(Long instructorId, Status status);
}
