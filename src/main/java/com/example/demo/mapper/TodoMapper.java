package com.example.demo.mapper;

import com.example.demo.model.Todo;
import java.util.List;

public interface TodoMapper {
    List<Todo> findAll();

    Todo findById(Long id);

    int insert(Todo todo);

    int update(Todo todo);

    int deleteById(Long id);
}
