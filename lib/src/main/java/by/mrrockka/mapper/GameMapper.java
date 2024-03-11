package by.mrrockka.mapper;

import by.mrrockka.domain.Withdrawals;
import by.mrrockka.domain.entries.Entries;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.domain.summary.TournamentSummary;
import by.mrrockka.repo.game.GameEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper
public interface GameMapper {

  @Mapping(target = "bounty", constant = "0")
  @Mapping(target = "gameType", constant = "TOURNAMENT")
  GameEntity toEntity(TournamentGame game);

  @Mapping(target = "bounty", constant = "0")
  @Mapping(target = "gameType", constant = "CASH")
  GameEntity toEntity(CashGame game);

  @Mapping(target = "tournamentSummary", ignore = true)
  @InheritInverseConfiguration
  TournamentGame toTournament(GameEntity gameEntity, List<Entries> entries);

  @InheritInverseConfiguration
  TournamentGame toTournament(GameEntity gameEntity, List<Entries> entries, TournamentSummary tournamentSummary);

  @InheritInverseConfiguration
  CashGame toCash(GameEntity gameEntity, List<Entries> entries, List<Withdrawals> withdrawals);

}
