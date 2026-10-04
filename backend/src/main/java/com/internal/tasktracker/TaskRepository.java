package com.internal.tasktracker;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // The search predicate is grouped in parentheses so that "archived = FALSE" and the
    // status filter apply to BOTH the title and description matches (AND binds tighter than OR).
    // ESCAPE '\' lets the caller treat % and _ in user input as literal characters.
    // id is a tie-breaker so page boundaries are stable. Paging comes from the Pageable,
    // so the DB does the LIMIT/OFFSET and the count instead of loading every row.
    @Query(value = "SELECT * FROM tasks "
                 + "WHERE archived = FALSE "
                 + "AND (LOWER(title) LIKE :term ESCAPE '\\' OR LOWER(description) LIKE :term ESCAPE '\\') "
                 + "AND (:status IS NULL OR status = :status) "
                 + "ORDER BY created_at DESC, id DESC",
           countQuery = "SELECT COUNT(*) FROM tasks "
                 + "WHERE archived = FALSE "
                 + "AND (LOWER(title) LIKE :term ESCAPE '\\' OR LOWER(description) LIKE :term ESCAPE '\\') "
                 + "AND (:status IS NULL OR status = :status)",
           nativeQuery = true)
    Page<Task> searchTasks(@Param("term") String term, @Param("status") String status, Pageable pageable);
}
