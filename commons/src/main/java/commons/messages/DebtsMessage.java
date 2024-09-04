package commons.messages;

import java.util.List;

import commons.Debt;

public record DebtsMessage(List<Debt> debts) {
}
