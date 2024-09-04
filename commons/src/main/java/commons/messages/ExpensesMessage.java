package commons.messages;

import java.util.List;

import commons.dtos.ExpenseDTO;

public record ExpensesMessage(List<ExpenseDTO> expenses) {
}
