package by.mrrockka.mapper.person;

import by.mrrockka.domain.Person;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.PersonMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {PersonMapper.class})
public interface TelegramPersonMapper {

  Person map(TelegramPerson telegramPerson);

  List<Person> map(List<TelegramPerson> telegramPerson);
}
