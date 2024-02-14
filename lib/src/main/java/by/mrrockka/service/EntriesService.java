package by.mrrockka.service;

import by.mrrockka.repo.entries.EntriesEntity;
import by.mrrockka.repo.entries.EntriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


//todo: add int tests
@Service
@RequiredArgsConstructor
public class EntriesService {

  private final EntriesRepository entriesRepository;

  public void storeEntry(UUID gameId, UUID personId, BigDecimal amount) {
    entriesRepository.save(gameId, personId, amount);
  }

  public List<EntriesEntity> retrieveEntries(UUID gameId) {
    return entriesRepository.findAllByGameId(gameId);
  }

}
