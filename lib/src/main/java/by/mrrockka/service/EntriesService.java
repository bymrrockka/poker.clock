package by.mrrockka.service;

import by.mrrockka.repo.entries.EntriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EntriesService {

  private final EntriesRepository entriesRepository;

}
