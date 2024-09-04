package commons.messages;

import java.util.List;

import commons.dtos.TagDTO;

public record TagsMessage(List<TagDTO> tags) {
}
