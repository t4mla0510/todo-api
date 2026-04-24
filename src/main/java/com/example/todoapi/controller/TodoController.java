package com.example.todoapi.controller;

import com.example.todoapi.dto.TodoDto;
import com.example.todoapi.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ResponseEntity<TodoDto.PagedResponse> getAllTodos(
            Pageable pageable,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String title) {
        return ResponseEntity.ok(todoService.getAllTodos(pageable, completed, title));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDto.Response> getTodoById(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    @PostMapping
    public ResponseEntity<TodoDto.Response> createTodo(@Valid @RequestBody TodoDto.CreateRequest request) {
        TodoDto.Response todo = todoService.createTodo(request);
        return ResponseEntity.created(URI.create("/api/todos/" + todo.getId())).body(todo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDto.Response> updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody TodoDto.UpdateRequest request) {
        return ResponseEntity.ok(todoService.updateTodo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TodoDto.Response> completeTodo(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.completeTodo(id));
    }
}
