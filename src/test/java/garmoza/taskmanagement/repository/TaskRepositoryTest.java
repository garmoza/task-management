package garmoza.taskmanagement.repository;

import garmoza.taskmanagement.entity.Task;
import garmoza.taskmanagement.entity.TaskPriority;
import garmoza.taskmanagement.entity.TaskStatus;
import garmoza.taskmanagement.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    private User author1, author2;
    private User performer1, performer2;
    private Task task1, task2, task3;


    @BeforeEach
    void setUp() {
        author1 = User.builder()
                .id(1L)
                .email("author1@mail.com")
                .password("author1")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();
        author2 = User.builder()
                .id(2L)
                .email("author2@mail.com")
                .password("author3")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();

        performer1 = User.builder()
                .email("performer1@mail.com")
                .password("performer1")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();
        performer2 = User.builder()
                .email("performer2@mail.com")
                .password("performer2")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();

        task1 = Task.builder()
                .title("Task title 1")
                .description("Task description 1")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.LOW)
                .author(author1)
                .performer(performer1)
                .build();
        task2 = Task.builder()
                .title("Task title 2")
                .description("Task description 2")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.LOW)
                .author(author1)
                .performer(performer2)
                .build();
        task3 = Task.builder()
                .title("Task title 3")
                .description("Task description 3")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.LOW)
                .author(author2)
                .performer(performer1)
                .build();

        userRepository.save(author1);
        userRepository.save(author2);
        userRepository.save(performer1);
        userRepository.save(performer2);

        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);
    }

    @Test
    void findAllByAuthor_Id() {
        var pageable = PageRequest.of(0, 20);
        Page<Task> page = taskRepository.findAllByAuthor_Id(author1.getId(), pageable);

        List<Task> list = page.toList();
        assertThat(list).hasSize(2);
        assertTrue(list.contains(task1));
        assertTrue(list.contains(task2));
    }

    @Test
    void findAllByPerformer_Id() {
        var pageable = PageRequest.of(0, 20);
        Page<Task> page = taskRepository.findAllByPerformer_Id(performer1.getId(), pageable);

        List<Task> list = page.toList();
        assertThat(list).hasSize(2);
        assertTrue(list.contains(task1));
        assertTrue(list.contains(task3));
    }

    @Test
    void findAllByAuthor_IdAndPerformer_Id() {
        var pageable = PageRequest.of(0, 20);
        Page<Task> page = taskRepository.findAllByAuthor_IdAndPerformer_Id(author1.getId(), performer1.getId(), pageable);

        List<Task> list = page.toList();
        assertThat(list).hasSize(1);
        assertTrue(list.contains(task1));
    }
}