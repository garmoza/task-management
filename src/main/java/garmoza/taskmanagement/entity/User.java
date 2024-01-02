package garmoza.taskmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
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
    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> authorities = new HashSet<>();
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    private Set<Task> createdTasks = new HashSet<>();
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "performer")
    private Set<Task> performedTasks = new HashSet<>();
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    private Set<Comment> writtenComments = new HashSet<>();
}
