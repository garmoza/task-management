package garmoza.taskmanagement.dto.task;

import garmoza.taskmanagement.dto.PatchDTO;
import garmoza.taskmanagement.entity.TaskPriority;
import garmoza.taskmanagement.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class TaskPatchDTO extends PatchDTO {
    @NotBlank
    private String title;
    @NotNull
    private String description;
    @NotNull
    private TaskStatus status;
    @NotNull
    private TaskPriority priority;
    @Positive
    private long authorId;
    private Long performerId;

    public void setTitle(String title) {
        this.title = title;
        addPatchedAttr("title");
    }

    public void setDescription(String description) {
        this.description = description;
        addPatchedAttr("description");
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        addPatchedAttr("status");
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
        addPatchedAttr("priority");
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
        addPatchedAttr("authorId");
    }

    public void setPerformerId(Long performerId) {
        this.performerId = performerId;
        addPatchedAttr("performerId");
    }
}
