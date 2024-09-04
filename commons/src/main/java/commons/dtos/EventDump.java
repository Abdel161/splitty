package commons.dtos;

import java.util.List;

public record EventDump(String name, String inviteCode, List<ExpenseDTO> expenses, List<ParticipantDTO> participants, List<TagDTO> tags) {
}