package by.mrrockka.validation.collection;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class CollectionsValidator {

  public void validateMapIsNotEmpty(final Map map, final String entity) {
    if (map.isEmpty()) {
      throw new EntityCouldNotBeEmptyException(entity);
    }
  }

  public void validateCollectionIsNotEmpty(final Collection collection, final String entity) {
    if (collection.isEmpty()) {
      throw new EntityCouldNotBeEmptyException(entity);
    }
  }
}
