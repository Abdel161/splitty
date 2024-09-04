package commons.messages;

import java.util.List;

import commons.dtos.ParticipantDTO;

public record ParticipantsMessage(List<ParticipantDTO> participants) {
}
