package by.mrrockka.service;

import by.mrrockka.repo.entries.EntriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntriesService {

  private final EntriesRepository entriesRepository;

  public void storeEntry(UUID gameId, UUID personId, BigDecimal amount, LocalDateTime createdAt) {
    entriesRepository.save(gameId, personId, amount, createdAt);
  }

}
