package br.com.alura.AluraFake.task;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/new/opentext")
    public ResponseEntity newOpenTextExercise(@Valid @RequestBody NewOpenTextTaskDTO newOpenTextTaskDTO) {
        taskService.createOpenTextTask(newOpenTextTaskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/new/singlechoice")
    public ResponseEntity newSingleChoice(@Valid @RequestBody NewSingleChoiceTaskDTO newSingleChoiceTaskDTO) {
        taskService.createSingleChoiceTask(newSingleChoiceTaskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/new/multiplechoice")
    public ResponseEntity newMultipleChoice(@Valid @RequestBody NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO) {
        taskService.createMultipleChoiceTask(newMultipleChoiceTaskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}