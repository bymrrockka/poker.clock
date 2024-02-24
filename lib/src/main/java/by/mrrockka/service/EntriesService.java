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

  public void storeEntry(UUID gameId, UUID personId, BigDecimal amount, Instant createdAt) {
    entriesRepository.save(gameId, personId, amount, createdAt);
  }

  public void storeBatch(UUID gameId, List<UUID> personIds, BigDecimal amount, Instant createdAt) {
    entriesRepository.saveAll(gameId, personIds, amount, createdAt);
  }

}
