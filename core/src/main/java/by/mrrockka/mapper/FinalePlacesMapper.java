package by.mrrockka.mapper;

import by.mrrockka.domain.finaleplaces.FinalPlace;
import by.mrrockka.domain.finaleplaces.FinalePlaces;
import by.mrrockka.repo.finalplaces.FinalePlacesEntity;
import by.mrrockka.repo.person.PersonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.*;
import java.util.stream.Collectors;

@Mapper
public interface FinalePlacesMapper {

  PersonMapper PERSON_MAPPER = Mappers.getMapper(PersonMapper.class);

  @Mapping(target = "places", source = "domain.finalPlaces", qualifiedByName = "finalPlaceListToPlacesMap")
  FinalePlacesEntity toEntity(UUID gameId, FinalePlaces domain);


  @Mapping(target = "finalPlaces", source = "places", qualifiedByName = "placesMapToFinalPlaceList")
  FinalePlaces toDomain(FinalePlacesEntity entity);

  @Named("placesMapToFinalPlaceList")
  default List<FinalPlace> placesMapToFinalPlaceList(final Map<Integer, PersonEntity> places) {
    return places.entrySet()
      .stream()
      .map(entry -> new FinalPlace(entry.getKey(), PERSON_MAPPER.toDomain(entry.getValue())))
      .sorted(Comparator.comparingLong(FinalPlace::position))
      .toList();
  }

  @Named("finalPlaceListToPlacesMap")
  default Map<Integer, PersonEntity> finalPlaceListToPlacesMap(final List<FinalPlace> finalPlaces) {
    return finalPlaces.stream()
      .map(finalPlace ->
             new AbstractMap.SimpleEntry<>(finalPlace.position(), PERSON_MAPPER.toEntity(finalPlace.person())))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
