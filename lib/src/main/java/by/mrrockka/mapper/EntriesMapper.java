package by.mrrockka.mapper;

import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.repo.entries.EntriesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = PersonMapper.class)
public interface EntriesMapper {

  @Mapping(target = "entries", source = "amounts")
  PersonEntries toDomain(EntriesEntity entriesEntity);
}
