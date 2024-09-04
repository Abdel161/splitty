package commons.dtos;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Set;

public record ExpenseDTO(BigDecimal amountInEUR, String currency, Calendar date, String purpose, Calendar updatedOn,
                         Calendar createdOn, long id, long payerId, Set<Long> returnerIds, long tagId, boolean isDebt) {
}
