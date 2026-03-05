package com.example.demo.service;

import com.example.demo.mapper.TodoMapper;
import com.example.demo.model.Todo;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoMapper todoMapper;

    public List<Todo> findAll() {
        return todoMapper.findAll();
    }

    public Optional<Todo> findById(Long id) {
        return Optional.ofNullable(todoMapper.findById(id));
    }

    public void create(Todo todo) {
        if (todo.getCompleted() == null) {
            todo.setCompleted(false);
        }
        todoMapper.insert(todo);
    }

    public boolean update(Todo todo) {
        return todoMapper.update(todo) > 0;
    }

    public boolean deleteById(Long id) {
        return todoMapper.deleteById(id) > 0;
    }

    public boolean toggleCompleted(Long id) {
        Optional<Todo> optionalTodo = findById(id);
        if (optionalTodo.isEmpty()) {
            return false;
        }

        Todo todo = optionalTodo.get();
        todo.setCompleted(!Boolean.TRUE.equals(todo.getCompleted()));
        return update(todo);
    }
}
