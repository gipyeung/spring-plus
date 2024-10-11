package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    private final JPAQueryFactory queryFactory;

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN FETCH t.user u " +  // user와의 관계를 페치 조인
            "WHERE (:weather IS NULL OR t.weather = :weather) AND " +
            "(:startDate IS NULL OR t.modifiedAt >= :startDate) AND " +
            "(:endDate IS NULL OR t.modifiedAt <= :endDate) " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(@Param("weather") String weather,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     Pageable pageable);



    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        Todo result = queryFactory.selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()  // N+1 문제 방지
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

}
