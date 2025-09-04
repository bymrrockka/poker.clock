package by.mrrockka.validation;

import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.exception.BusinessException;
import by.mrrockka.validation.mentions.InsufficientMentionsSizeSpecifiedException;
import by.mrrockka.validation.mentions.PersonMentionsValidator;
import by.mrrockka.validation.mentions.PlayerHasNoNicknameException;
import eu.vendeli.tgbot.types.msg.EntityType;
import lombok.Builder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


class PersonMentionsValidatorTest {

  private final PersonMentionsValidator mentionsValidator = new PersonMentionsValidator();

  @Builder
  private record PersonsMessageArgument(MessageMetadata metadata, Class<? extends BusinessException> exception) {}

  private static Stream<Arguments> mentions() {
    return Stream.of(
      Arguments.of(
        PersonsMessageArgument.builder()
          .metadata(MessageMetadataCreator.domain())
          .exception(InsufficientMentionsSizeSpecifiedException.class)
          .build()),

      Arguments.of(
        PersonsMessageArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(builder -> builder
              .metadataEntities(List.of(
                MessageEntityCreator.domainMention("@mrrockka")
              ))))
          .exception(InsufficientMentionsSizeSpecifiedException.class)
          .build()),

      Arguments.of(
        PersonsMessageArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(builder -> builder
              .metadataEntities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@miscusi"),
                MessageEntityCreator.domainEntity(entityBuilder -> entityBuilder
                  .text("Agnes Timiano")
                  .type(EntityType.TextMention)
                )
              ))))
          .exception(PlayerHasNoNicknameException.class)
          .build())
    );
  }

  @ParameterizedTest
  @MethodSource("mentions")
  void givenMessageMeta_whenNoPlayers_thenThrowException(final PersonsMessageArgument argument) {
    assertThatThrownBy(() -> mentionsValidator.validateMessageMentions(argument.metadata(), 2))
      .isInstanceOf(argument.exception());
  }

}