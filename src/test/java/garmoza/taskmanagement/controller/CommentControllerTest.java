package garmoza.taskmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import garmoza.taskmanagement.dto.comment.CommentCreateDTO;
import garmoza.taskmanagement.dto.comment.CommentResponseDTO;
import garmoza.taskmanagement.security.service.JwtServiceImpl;
import garmoza.taskmanagement.service.CommentService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest({CommentController.class})
@AutoConfigureMockMvc(addFilters = false)
@Import({JwtServiceImpl.class})
@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    private CommentCreateDTO createDTO;
    private CommentResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        createDTO = CommentCreateDTO.builder()
                .body("Comment Body")
                .taskId(2L)
                .build();
        responseDTO = CommentResponseDTO.builder()
                .id(1L)
                .body("Comment Body")
                .build();
    }

    private static final String jsonResponseDTO = """
            {
                "id": 1,
                "body": "Comment Body"
            }
            """;

    @Test
    void postComment() throws Exception {
        given(commentService.createComment(Mockito.any(CommentCreateDTO.class))).willReturn(responseDTO);

        ResultActions response = mockMvc.perform(post("/comments")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO))
        );

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(content().json(jsonResponseDTO));
    }

    @Test
    void postComment_validation_nulls() throws Exception {
        ResultActions response = mockMvc.perform(post("/comments")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        );

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "code": "400 BAD_REQUEST",
                            "message": "Invalid Data",
                            "body": "must not be blank",
                            "taskId": "must be greater than 0"
                        }
                        """));
    }

    @Test
    void getCommentById() throws Exception {
        long id = 1L;
        given(commentService.findCommentById(id)).willReturn(responseDTO);

        ResultActions response = mockMvc.perform(get("/comments/%d".formatted(id))
                .accept(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(jsonResponseDTO));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -2L})
    void getCommentById_validation_params(long id) throws Exception {
        ResultActions response = mockMvc.perform(get("/comments/%d".formatted(id))
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
    void deleteCommentById() throws Exception {
        long id = 1L;
        doNothing().when(commentService).deleteCommentById(id);

        ResultActions response = mockMvc.perform(delete("/comments/%d".formatted(id)));

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -2L})
    void deleteCommentById_validation_params(long id) throws Exception {
        ResultActions response = mockMvc.perform(delete("/comments/%d".formatted(id))
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
}