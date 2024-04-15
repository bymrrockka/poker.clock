package by.mrrockka.mapper;

import by.mrrockka.domain.Bounty;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.collection.PersonWithdrawals;
import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.domain.summary.finale.FinaleSummary;
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

  @Mapping(target = "bounty", source = "bountyAmount")
  @Mapping(target = "gameType", constant = "BOUNTY")
  GameEntity toEntity(BountyGame game);

  @InheritInverseConfiguration
  TournamentGame toTournament(GameEntity gameEntity, List<PersonEntries> entries, FinaleSummary finaleSummary);

  @InheritInverseConfiguration
  CashGame toCash(GameEntity gameEntity, List<PersonEntries> entries, List<PersonWithdrawals> withdrawals);

  @Mapping(target = "bountyAmount", source = "gameEntity.bounty")
  @InheritInverseConfiguration
  BountyGame toBounty(GameEntity gameEntity, List<PersonEntries> entries, List<Bounty> bountyList,
                      FinaleSummary finaleSummary);

}
