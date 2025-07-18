package by.mrrockka.mapper;

import by.mrrockka.service.help.CommandDescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;
import java.util.Map;

@Mapper
@Deprecated(forRemoval = true)
public interface BotCommandMapper {

  @Mapping(source = "commandDescription.description", target = "description")
  @Mapping(target = "command", expression = "java(\"/\" + command)")
  BotCommand mapToApi(final String command, final CommandDescription commandDescription);

  default List<BotCommand> mapToApi(final Map<String, CommandDescription> descriptions) {
    return descriptions.entrySet().stream()
      .filter(entry -> entry.getValue().enabled())
      .map(entry -> mapToApi(entry.getKey(), entry.getValue()))
      .toList();
  }

}
