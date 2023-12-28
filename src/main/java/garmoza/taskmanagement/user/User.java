package garmoza.taskmanagement.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import garmoza.taskmanagement.task.Task;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @SequenceGenerator(name = "generator", sequenceName = "user_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @Column(name = "id")
    private long id;
    @Column(name = "email", unique = true)
    private String email;
    @JsonIgnore
    @Column(name = "password")
    private String password;
    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> authorities;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    private Set<Task> createdTasks;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "performer")
    private Set<Task> performedTasks;
}
