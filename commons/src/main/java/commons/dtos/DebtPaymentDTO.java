package commons.dtos;

import java.util.Calendar;

public record DebtPaymentDTO(long id, long from, long to, double amount, Calendar date) {
}
