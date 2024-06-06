package by.mrrockka.service;

import by.mrrockka.creator.GameCreator;
import by.mrrockka.creator.MoneyTransferCreator;
import by.mrrockka.creator.PayoutCreator;
import by.mrrockka.creator.PersonCreator;
import by.mrrockka.mapper.MoneyTransferMapper;
import by.mrrockka.repo.moneytransfer.MoneyTransferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoneyTransferServiceTest {

  @Mock
  private MoneyTransferRepository moneyTransferRepository;
  @Mock
  private MoneyTransferMapper moneyTransferMapper;
  @InjectMocks
  private MoneyTransferService moneyTransferService;

  @Test
  void givenGameAndPayouts_whenStoreAllExecuted_thenShouldSaveMappedEntities() {
    final var game = GameCreator.tournament();
    final var actual = List.of(PayoutCreator.payout());
    final var expected = List.of(MoneyTransferCreator.entity());

    when(moneyTransferMapper.map(game.getId(), actual)).thenReturn(expected);

    moneyTransferService.storeBatch(game, actual);

    verify(moneyTransferRepository).saveAll(eq(expected), any(Instant.class));
  }

  @Test
  void givenPersonAndAssociatedTransfers_whenGetByPersonExecuted_thenShouldReturnMoneyTransferForPerson() {
    final var person = PersonCreator.domain();
    final var entity = MoneyTransferCreator.entity();
    final var domain = MoneyTransferCreator.domain();
    final var expected = List.of(entity);

    when(moneyTransferRepository.getForPerson(person.getId())).thenReturn(expected);
    when(moneyTransferMapper.map(entity)).thenReturn(domain);

    moneyTransferService.getForPerson(person);

    verifyNoMoreInteractions(moneyTransferRepository, moneyTransferMapper);
  }

  @Test
  void givenPersonButNoAssociatedTransfers_whenGetByPersonExecuted_thenShouldReturnEmptyList() {
    final var person = PersonCreator.domain();

    when(moneyTransferRepository.getForPerson(person.getId())).thenReturn(Collections.emptyList());

    moneyTransferService.getForPerson(person);

    verifyNoInteractions(moneyTransferMapper);
    verifyNoMoreInteractions(moneyTransferRepository);
  }


}