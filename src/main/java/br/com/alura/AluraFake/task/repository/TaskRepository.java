package br.com.alura.AluraFake.task.repository;

import br.com.alura.AluraFake.task.enums.Type;
import br.com.alura.AluraFake.task.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>{

    boolean existsByCourse_IdAndStatementIgnoreCase(Long courseId, String statement);

    long countByCourseId(Long courseId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE Task 
               SET taskOrder = taskOrder + 1 
             WHERE course_id = :courseId 
               AND taskOrder >= :startOrder
             ORDER BY taskOrder DESC  
            """, nativeQuery = true)
    int shiftOrdersFrom(@Param("courseId") Long courseId, @Param("startOrder") Integer startOrder);

    long countByCourse_IdAndTaskType(Long courseId, Type type);

    List<Task> findAllByCourse_IdOrderByTaskOrderAsc(Long courseId);
}
