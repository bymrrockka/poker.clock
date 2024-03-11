package by.mrrockka.mapper.game;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramGame;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.repo.game.TelegramGameEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = MessageMetadataMapper.class)
public interface TelegramGameMapper {

  TelegramGame<Game> toGame(Game game, TelegramGameEntity messageMetadata);

  TelegramGame<TournamentGame> toTournament(TournamentGame game, TelegramGameEntity messageMetadata);

  TelegramGame<CashGame> toCash(CashGame game, TelegramGameEntity messageMetadata);

  @Mapping(source = "game.id", target = "gameId")
  @Mapping(source = "messageMetadata.id", target = "messageId")
  @Mapping(source = "messageMetadata.chatId", target = "chatId")
  @Mapping(source = "messageMetadata.createdAt", target = "createdAt")
  TelegramGameEntity toEntity(Game game, MessageMetadata messageMetadata);
}
