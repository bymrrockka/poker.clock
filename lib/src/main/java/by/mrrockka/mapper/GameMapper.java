package by.mrrockka.mapper;

import by.mrrockka.domain.Player;
import by.mrrockka.domain.entries.Entries;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.domain.summary.TournamentGameSummary;
import by.mrrockka.repo.game.GameEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


//todo: add mappings
@Mapper
public interface GameMapper {

  @Mapping(target = "bounty", constant = "0")
  GameEntity toEntity(TournamentGame game);

  //  @Mapping(target = "bountyTransactions", ignore = true)
  @Mapping(target = "tournamentGameSummary", ignore = true)
  @InheritInverseConfiguration
  TournamentGame toDomainWithPlayers(GameEntity gameEntity, List<Player> players);

  //  @Mapping(target = "bountyTransactions", ignore = true)
  @Mapping(target = "tournamentGameSummary", ignore = true)
  @Mapping(target = "players", ignore = true)
  @InheritInverseConfiguration
  TournamentGame toDomain(GameEntity gameEntity, List<Entries> players);

  //  @Mapping(target = "bountyTransactions", ignore = true)
  @InheritInverseConfiguration
  TournamentGame toDomainWithPlayers(GameEntity gameEntity, List<Player> players,
                                     TournamentGameSummary tournamentGameSummary);

  @InheritInverseConfiguration
  TournamentGame toDomain(GameEntity gameEntity, List<Entries> entries, TournamentGameSummary tournamentGameSummary);

//  @InheritInverseConfiguration
//  Game toDomain(GameEntity gameEntity, List<Player> players, TournamentGameSummary tournamentGameSummary, List<Bounty> bountyTransactions);
}
