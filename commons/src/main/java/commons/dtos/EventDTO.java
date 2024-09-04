package commons.dtos;

import java.util.Calendar;
import java.util.Set;

public record EventDTO(long id, String title, String inviteCode, Calendar createdOn, Calendar updatedOn,
                       Set<Long> participantIds) {
}
