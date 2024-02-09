package by.mrrockka.service;

import by.mrrockka.mapper.GameMapper;
import by.mrrockka.repo.game.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

  private final GameMapper gameMapper;
  private final GameRepository gameRepository;
  private final EntriesService entriesService;
  private final FinalePlacesService finalePlacesService;
  private final PersonService personService;
  private final PrizePoolService prizePoolService;


}
