package by.mrrockka.mapper.person;

import by.mrrockka.domain.Person;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.PersonMapper;
import by.mrrockka.repo.person.TelegramPersonEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {PersonMapper.class})
public interface TelegramPersonMapper {

  Person mapToPerson(TelegramPerson telegramPerson);

  List<Person> mapToPersons(List<TelegramPerson> telegramPerson);

  TelegramPerson mapToTelegram(TelegramPersonEntity entity);

  List<TelegramPerson> mapToTelegrams(List<TelegramPersonEntity> entities);
}
