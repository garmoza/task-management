package garmoza.taskmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import garmoza.taskmanagement.dto.task.TaskCreateDTO;
import garmoza.taskmanagement.dto.task.TaskPatchDTO;
import garmoza.taskmanagement.dto.task.TaskPutDTO;
import garmoza.taskmanagement.dto.task.TaskResponseDTO;
import garmoza.taskmanagement.entity.TaskPriority;
import garmoza.taskmanagement.entity.TaskStatus;
import garmoza.taskmanagement.security.service.JwtServiceImpl;
import garmoza.taskmanagement.service.TaskService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest({TaskController.class})
@AutoConfigureMockMvc(addFilters = false) // disables security
@Import({JwtServiceImpl.class})
@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private TaskCreateDTO createDTO;
    private TaskResponseDTO responseDTO;
    private TaskPutDTO putDTO;
    private TaskPatchDTO patchDTO;

    @BeforeEach
    void setUp() {
        createDTO = TaskCreateDTO.builder()
                .title("Task title")
                .description("Task description")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.LOW)
                .authorId(2L)
                .performerId(3L)
                .build();

        responseDTO = TaskResponseDTO.builder()
                .id(1L)
                .title("Task title")
                .description("Task description")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.LOW)
                .build();

        putDTO = TaskPutDTO.builder()
                .id(1L)
                .title("Updated title")
                .description("Updated description")
                .status(TaskStatus.COMPLETED)
                .priority(TaskPriority.HIGH)
                .authorId(2L)
                .performerId(3L)
                .build();

        patchDTO = new TaskPatchDTO();
        patchDTO.setTitle("Patched title");
        patchDTO.setDescription("Patched description");
        patchDTO.setStatus(TaskStatus.IN_PROGRESS);
        patchDTO.setPriority(TaskPriority.MEDIUM);
        patchDTO.setAuthorId(5L);
        patchDTO.setPerformerId(6L);
    }

    private static final String jsonResponseDTO = """
            {
                "id": 1,
                "title": "Task title",
                "description": "Task description",
                "status": "PENDING",
                "priority": "LOW"
            }
            """;

    @Test
    void getAllTasks() throws Exception {
        Long authorId = 1L;
        Long performerId = 2L;
        var pageable = PageRequest.of(0, 20);
        var responseDto = List.of(responseDTO);
        given(taskService.findAllTasks(authorId, performerId, pageable)).willReturn(responseDto);

        ResultActions response = mockMvc.perform(get("/tasks?authorId=1&performerId=2&page=0&size=20")
                .accept(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(responseDto.size())));
    }

    @Test
    void getAllTasks_validation_nulls() throws Exception {
        var pageable = PageRequest.of(0, 20);
        var responseDto = List.of(responseDTO);
        given(taskService.findAllTasks(null, null, pageable)).willReturn(responseDto);

        ResultActions response = mockMvc.perform(get("/tasks?page=0&size=20")
                .accept(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(responseDto.size())));
    }

    @Test
    void getAllTasks_validation_params() throws Exception {
        ResultActions response = mockMvc.perform(get("/tasks?authorId=0&performerId=-1")
                .accept(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "code": "400 BAD_REQUEST",
                            "message": "Invalid Data",
                            "authorId": "must be greater than 0",
                            "performerId": "must be greater than 0"
                        }
                        """));
    }

    @Test
    void postTask() throws Exception {
        given(taskService.createTask(Mockito.any(TaskCreateDTO.class))).willReturn(responseDTO);

        ResultActions response = mockMvc.perform(post("/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO))
        );

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(content().json(jsonResponseDTO));
    }

    @Test
    void postTask_validation_nulls() throws Exception {
        ResultActions response = mockMvc.perform(post("/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        );

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "code": "400 BAD_REQUEST",
                            "message": "Invalid Data",
                            "title": "must not be blank",
                            "description": "must not be null",
                            "status": "must not be null",
                            "priority": "must not be null",
                            "authorId": "must be greater than 0"
                        }
                        """));
    }

    @Test
    void putTask() throws Exception {
        given(taskService.putTask(Mockito.any(TaskPutDTO.class))).willReturn(responseDTO);

        ResultActions response = mockMvc.perform(put("/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(putDTO))
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(jsonResponseDTO));
    }

    @Test
    void putTask_validation_nulls() throws Exception {
        ResultActions response = mockMvc.perform(put("/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        );

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "code": "400 BAD_REQUEST",
                            "message": "Invalid Data",
                            "id": "must be greater than 0",
                            "title": "must not be blank",
                            "description": "must not be null",
                            "status": "must not be null",
                            "priority": "must not be null",
                            "authorId": "must be greater than 0"
                        }
                        """));
    }

    @Test
    void getTaskById() throws Exception {
        long id = 1L;
        given(taskService.findTaskById(id)).willReturn(responseDTO);

        ResultActions response = mockMvc.perform(get("/tasks/%d".formatted(id))
                .accept(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(jsonResponseDTO));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -2L})
    void getTaskById_validation_params(long id) throws Exception {
        ResultActions response = mockMvc.perform(get("/tasks/%d".formatted(id))
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "code": "400 BAD_REQUEST",
                            "message": "Invalid Data",
                            "id": "must be greater than 0"
                        }
                        """));
    }

    @Test
    void deleteTaskById() throws Exception {
        long id = 1L;
        doNothing().when(taskService).deleteTaskById(id);

        ResultActions response = mockMvc.perform(delete("/tasks/%d".formatted(id)));

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -2L})
    void deleteTaskById_validation_params(long id) throws Exception {
        ResultActions response = mockMvc.perform(delete("/tasks/%d".formatted(id))
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "code": "400 BAD_REQUEST",
                            "message": "Invalid Data",
                            "id": "must be greater than 0"
                        }
                        """));
    }

    @Test
    void patchTaskById() throws Exception {
        long id = 1L;
        given(taskService.patchTaskById(eq(id), Mockito.any(TaskPatchDTO.class))).willReturn(responseDTO);

        ResultActions response = mockMvc.perform(patch("/tasks/%d".formatted(id))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDTO))
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(jsonResponseDTO));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -2L})
    void patchTaskById_validation_params(long id) throws Exception {
        ResultActions response = mockMvc.perform(patch("/tasks/%d".formatted(id))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDTO))
        );

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "code": "400 BAD_REQUEST",
                            "message": "Invalid Data",
                            "id": "must be greater than 0"
                        }
                        """));
    }

    @Test
    void patchTaskById_validation_withoutBody() throws Exception {
        long id = 1L;
        given(taskService.patchTaskById(eq(id), Mockito.any(TaskPatchDTO.class))).willReturn(responseDTO);

        ResultActions response = mockMvc.perform(patch("/tasks/%d".formatted(id))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(jsonResponseDTO));
    }
}