package com.example.todoapi.repository;

import com.example.todoapi.model.Todo;
import com.example.todoapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findByUser(User user, Pageable pageable);

    @Query("SELECT t FROM Todo t WHERE t.user = :user " +
           "AND (:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
           "AND (:completed IS NULL OR t.completed = :completed)")
    Page<Todo> findByUserWithFilters(
            @Param("user") User user,
            @Param("title") String title,
            @Param("completed") Boolean completed,
            Pageable pageable
    );
}