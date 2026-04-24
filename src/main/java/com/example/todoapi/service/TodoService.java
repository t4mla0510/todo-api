package com.example.todoapi.service;

import com.example.todoapi.dto.TodoDto;
import com.example.todoapi.dto.TodoDto.PagedResponse;
import com.example.todoapi.dto.TodoDto.Response;
import com.example.todoapi.model.Todo;
import com.example.todoapi.model.User;
import com.example.todoapi.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public PagedResponse getAllTodos(Pageable pageable, Boolean completed, String title) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Page<Todo> page;
        if (completed == null && title == null) {
            page = todoRepository.findByUser(user, pageable);
        } else {
            page = todoRepository.findByUserWithFilters(user, title, completed, pageable);
        }

        PagedResponse response = new PagedResponse();
        response.setData(page.getContent().stream()
                .map(this::convertToDto)
                .toList());
        response.setPage(page.getNumber());
        response.setLimit(page.getSize());
        response.setTotal(page.getTotalElements());
        return response;
    }

    public Response getTodoById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found"));

        if (!todo.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized");
        }

        return convertToDto(todo);
    }

    @Transactional
    public Response createTodo(TodoDto.CreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Todo todo = Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .build();

        todoRepository.save(todo);
        return convertToDto(todo);
    }

    @Transactional
    public Response updateTodo(Long id, TodoDto.UpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found"));

        if (!todo.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized");
        }

        if (request.getTitle() != null) {
            todo.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            todo.setDescription(request.getDescription());
        }
        if (request.getCompleted() != null) {
            todo.setCompleted(request.getCompleted());
        }

        return convertToDto(todo);
    }

    @Transactional
    public void deleteTodo(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found"));

        if (!todo.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized");
        }

        todoRepository.delete(todo);
    }

    @Transactional
    public Response completeTodo(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found"));

        if (!todo.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized");
        }

        todo.setCompleted(true);
        return convertToDto(todo);
    }

    private Response convertToDto(Todo todo) {
        Response response = new Response();
        response.setId(todo.getId());
        response.setTitle(todo.getTitle());
        response.setDescription(todo.getDescription());
        response.setCompleted(todo.isCompleted());
        response.setCreatedAt(todo.getCreatedAt());
        response.setUpdatedAt(todo.getUpdatedAt());
        return response;
    }
}
