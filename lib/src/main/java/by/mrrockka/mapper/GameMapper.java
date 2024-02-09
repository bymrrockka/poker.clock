package by.mrrockka.mapper;

import by.mrrockka.domain.Bounty;
import by.mrrockka.domain.Player;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.summary.GameSummary;
import by.mrrockka.repo.game.GameEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface GameMapper {

  @Mapping(target = "bounty", source = "bountyAmount")
  GameEntity toEntity(Game game);

  @Mapping(target = "bounties", ignore = true)
  @Mapping(target = "gameSummary", ignore = true)
  @InheritInverseConfiguration
  Game toDomain(GameEntity gameEntity, List<Player> players);

  @Mapping(target = "bounties", ignore = true)
  @InheritInverseConfiguration
  Game toDomain(GameEntity gameEntity, List<Player> players, GameSummary gameSummary);

  @InheritInverseConfiguration
  Game toDomain(GameEntity gameEntity, List<Player> players, GameSummary gameSummary, List<Bounty> bounties);
}
