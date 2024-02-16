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

  PersonMapper personMapper = Mappers.getMapper(PersonMapper.class);

  @Mapping(target = "places", source = "domain.finalPlaces", qualifiedByName = "finalPlaceListToPlacesMap")
  FinalePlacesEntity toEntity(UUID gameId, FinalePlaces domain);


  @Mapping(target = "finalPlaces", source = "places", qualifiedByName = "placesMapToFinalPlaceList")
  FinalePlaces toDomain(FinalePlacesEntity entity);

  @Named("placesMapToFinalPlaceList")
  default List<FinalPlace> placesMapToFinalPlaceList(Map<Integer, PersonEntity> places) {
    return places.entrySet()
      .stream()
      .map(entry -> new FinalPlace(entry.getKey(), personMapper.toDomain(entry.getValue())))
      .sorted(Comparator.comparingLong(FinalPlace::position))
      .toList();
  }

  @Named("finalPlaceListToPlacesMap")
  default Map<Integer, PersonEntity> finalPlaceListToPlacesMap(List<FinalPlace> finalPlaces) {
    return finalPlaces.stream()
      .map(finalPlace ->
             new AbstractMap.SimpleEntry<>(finalPlace.position(), personMapper.toEntity(finalPlace.person(), null)))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
