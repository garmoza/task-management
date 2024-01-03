package garmoza.taskmanagement.repository;

import garmoza.taskmanagement.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findAllByAuthor_Id(long authorId, Pageable pageable);

    Page<Task> findAllByPerformer_Id(long performerId, Pageable pageable);

    Page<Task> findAllByAuthor_IdAndPerformer_Id(long authorId, long performerId, Pageable pageable);
}
