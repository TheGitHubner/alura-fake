package br.com.alura.AluraFake.task.repository;

import br.com.alura.AluraFake.task.model.TaskOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskOptionRepository extends JpaRepository<TaskOption, Long>{
}
