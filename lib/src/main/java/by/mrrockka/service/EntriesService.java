package by.mrrockka.service;

import by.mrrockka.repo.entries.EntriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntriesService {

  private final EntriesRepository entriesRepository;

  public void storeEntry(final UUID gameId, final UUID personId, final BigDecimal amount, final Instant createdAt) {
    entriesRepository.save(gameId, personId, amount, createdAt);
  }

  public void storeBatch(final UUID gameId, final List<UUID> personIds, final BigDecimal amount,
                         final Instant createdAt) {
    entriesRepository.saveAll(gameId, personIds, amount, createdAt);
  }

}
