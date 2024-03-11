package by.mrrockka.domain.withdrawals;

import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

//todo: fix entries and decide weather or not to add person
public record Withdrawals(@NonNull List<BigDecimal> withdrawals) {
}
