package by.mrrockka.service;

import by.mrrockka.domain.entries.Entries;
import by.mrrockka.mapper.EntriesMapper;
import by.mrrockka.repo.entries.EntriesRepository;
import lombok.NonNull;
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
  private final EntriesMapper entriesMapper;

  public void storeEntry(final UUID gameId, final UUID personId, final BigDecimal amount, final Instant createdAt) {
    entriesRepository.save(gameId, personId, amount, createdAt);
  }

  public void storeBatch(final UUID gameId, final List<UUID> personIds, final BigDecimal amount,
                         final Instant createdAt) {
    entriesRepository.saveAll(gameId, personIds, amount, createdAt);
  }

  public List<Entries> getAllForGame(@NonNull final UUID gameId) {
    final var entries = entriesRepository.findAllByGameId(gameId);

    return entries.stream()
      .map(entriesMapper::toDomain)
      .toList();
  }


}
