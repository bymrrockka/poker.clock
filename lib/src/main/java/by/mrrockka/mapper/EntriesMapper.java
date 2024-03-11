package by.mrrockka.mapper;

import by.mrrockka.domain.entries.Entries;
import by.mrrockka.repo.entries.EntriesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = PersonMapper.class)
public interface EntriesMapper {

  @Mapping(target = "entries", source = "amounts")
  Entries toDomain(EntriesEntity entriesEntity);
}
