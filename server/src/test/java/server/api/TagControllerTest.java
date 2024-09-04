package server.api;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import commons.dtos.TagDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;
import server.service.TagService;

public class TagControllerTest {

    private MockMvc mockMvc;
    private long eventId;
    @Mock
    private TagService tagService;
    @InjectMocks
    private TagController tagController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tagController).build();
        eventId = 1L;
    }

    @Test
    public void getAllTagsTest() throws Exception {
        TagDTO tag1 = new TagDTO("Tag 1", "Red", 1L);
        TagDTO tag2 = new TagDTO("Tag 2", "Blue", 2L);
        List<TagDTO> tags = Arrays.asList(tag1, tag2);

        when(tagService.getAllTags(eventId)).thenReturn(tags);

        mockMvc.perform(get("/api/events/{eventId}/tags", eventId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Tag 1"))
                .andExpect(jsonPath("$[0].color").value("Red"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].name").value("Tag 2"))
                .andExpect(jsonPath("$[1].color").value("Blue"))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(tagService, times(1)).getAllTags(eventId);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    public void getTagByIdTest() throws Exception {
        long tagId = 1L;
        TagDTO tag = new TagDTO("Tag 1", "Red", 1L);

        when(tagService.getTagById(tagId)).thenReturn(tag);

        mockMvc.perform(get("/api/events/{eventId}/tags/{tagId}", eventId, tagId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Tag 1"))
                .andExpect(jsonPath("$.color").value("Red"))
                .andExpect(jsonPath("$.id").value(tagId));

        verify(tagService, times(1)).getTagById(tagId);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    public void getTagByIdNotFoundTest() throws Exception {
        long tagId = 1L;

        when(tagService.getTagById(tagId)).thenThrow(new NotFoundException("A tag with id " + tagId + " was not found."));

        mockMvc.perform(get("/api/events/{eventId}/tags/{tagId}", eventId, tagId))
                .andExpect(status().isNotFound());

        verify(tagService, times(1)).getTagById(tagId);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    public void addTagTest() throws Exception {
        new TagDTO("Tag 1", "Red", 0);

        mockMvc.perform(post("/api/events/{eventId}/tags", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tag 1\",\"color\":\"Red\",\"id\":0}"))
                .andExpect(status().isOk());

        verify(tagService, times(1)).createTag(eq(eventId), any(TagDTO.class));
        verifyNoMoreInteractions(tagService);
    }

    @Test
    public void addTagInvalidPayloadTest() throws Exception {
        doThrow(new InvalidPayloadException("The payload provided was not in the correct format.")).when(tagService).createTag(eq(eventId), any(TagDTO.class));

        mockMvc.perform(post("/api/events/{eventId}/tags", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(tagService, times(1)).createTag(eq(eventId), any(TagDTO.class));
        verifyNoMoreInteractions(tagService);
    }

    @Test
    public void addTagNotFoundTest() throws Exception {
        new TagDTO("Tag 1", "Red", 1L);

        doThrow(new NotFoundException("An event with the id " + eventId + " was not found.")).when(tagService).createTag(eq(eventId), any(TagDTO.class));

        mockMvc.perform(post("/api/events/{eventId}/tags", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tag 1\", \"color\":\"Red\"}"))
                .andExpect(status().isNotFound());

        verify(tagService, times(1)).createTag(eq(eventId), any(TagDTO.class));
        verifyNoMoreInteractions(tagService);
    }

    @Test
    public void updateTagTest() throws Exception {
        long tagId = 1L;

        mockMvc.perform(put("/api/events/{eventId}/tags/{tagId}", eventId, tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Tag\",\"color\":\"Red\"}"))
                .andExpect(status().isOk());

        verify(tagService, times(1)).editTag(eq(eventId), eq(tagId), any(TagDTO.class));
        verifyNoMoreInteractions(tagService);
    }

    @Test
    public void updateTagInvalidPayloadTest() throws Exception {
        long tagId = 1L;

        doThrow(new InvalidPayloadException("Invalid payload."))
                .when(tagService).editTag(eq(eventId), eq(tagId), any(TagDTO.class));

        mockMvc.perform(put("/api/events/{eventId}/tags/{tagId}", eventId, tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"color\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(tagService, times(1)).editTag(eq(eventId), eq(tagId), any(TagDTO.class));
        verifyNoMoreInteractions(tagService);
    }

    @Test
    public void updateTagNotFoundTest() throws Exception {
        long tagId = 1L;

        doThrow(new NotFoundException("A tag with the id " + tagId + " was not found.")).when(tagService).editTag(eq(eventId), eq(tagId), any(TagDTO.class));

        mockMvc.perform(put("/api/events/{eventId}/tags/{tagId}", eventId, tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Nonexistent Tag\"}"))
                .andExpect(status().isNotFound());

        verify(tagService, times(1)).editTag(eq(eventId), eq(tagId), any(TagDTO.class));
        verifyNoMoreInteractions(tagService);
    }

    @Test
    public void deleteTagTest() throws Exception {
        long tagId = 1L;

        mockMvc.perform(delete("/api/events/{eventId}/tags/{tagId}", eventId, tagId))
                .andExpect(status().isOk());

        verify(tagService, times(1)).deleteTag(eventId, tagId);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    public void deleteTagNotFoundTest() throws Exception {
        long tagId = 1L;

        doThrow(new NotFoundException("A tag with the id " + tagId + " was not found.")).when(tagService).deleteTag(eventId, tagId);

        mockMvc.perform(delete("/api/events/{eventId}/tags/{tagId}", eventId, tagId))
                .andExpect(status().isNotFound());

        verify(tagService, times(1)).deleteTag(eventId, tagId);
        verifyNoMoreInteractions(tagService);
    }
}

