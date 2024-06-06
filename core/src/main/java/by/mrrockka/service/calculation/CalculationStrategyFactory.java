package by.mrrockka.service.calculation;

import by.mrrockka.domain.game.Game;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CalculationStrategyFactory {

  private final ApplicationContext applicationContext;

  public CalculationStrategy getStrategy(@NonNull final Game game) {
    final var beanName = StringUtils.uncapitalize(Pattern.compile(Game.class.getSimpleName())
                                                    .matcher(game.getClass().getSimpleName())
                                                    .replaceFirst(CalculationStrategy.class.getSimpleName()));

    try {
      return applicationContext.getBean(beanName, CalculationStrategy.class);
    } catch (final NoSuchBeanDefinitionException beanDefinitionException) {
      throw new NoStrategyFoundToCalculateGameException(StringUtils.capitalize(beanName), game);
    }
  }

}
