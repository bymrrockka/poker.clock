package by.mrrockka.repo.person;

import by.mrrockka.domain.TelegramPerson;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static by.mrrockka.repo.person.TelegramPersonColumnNames.CHAT_ID;
import static by.mrrockka.repo.person.TelegramPersonColumnNames.TELEGRAM;

@Repository
@RequiredArgsConstructor
public class TelegramPersonRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final TelegramPersonEntityRowMapper telegramPersonEntityRowMapper;

  private static final String SAVE_SQL = """
    INSERT INTO chat_persons
      (person_id, chat_id, telegram)
    VALUES
      (:person_id, :chat_id, :telegram);
    """;

  public void save(TelegramPerson person) {
    final var params = new MapSqlParameterSource()
      .addValue(TelegramPersonColumnNames.PERSON_ID, person.getId())
      .addValue(CHAT_ID, person.getChatId())
      .addValue(TELEGRAM, person.getTelegram());
    jdbcTemplate.update(SAVE_SQL, params);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void saveAll(List<TelegramPerson> telegramPersons) {
    telegramPersons.forEach(this::save);
  }

  private static final String FIND_BY_CHAT_ID_AND_TELEGRAMS_SQL = """
      SELECT
        cp.telegram, cp.chat_id, p.id, p.first_name, p.last_name
      FROM
        chat_persons as cp
      JOIN
        person as p on p.id = person_id
      WHERE
        chat_id = :chat_id AND
        telegram IN (:telegram)
    """;

  public List<TelegramPersonEntity> findAllByChatIdAndTelegrams(Long chatId, List<String> telegrams) {
    final var params = new MapSqlParameterSource()
      .addValue(CHAT_ID, chatId)
      .addValue(TELEGRAM, telegrams);

    return jdbcTemplate.query(FIND_BY_CHAT_ID_AND_TELEGRAMS_SQL, params, telegramPersonEntityRowMapper);
  }

  private static final String FIND_BY_TELEGRAM_SQL = """
      SELECT
        cp.telegram, cp.chat_id, p.id, p.first_name, p.last_name
      FROM
        chat_persons as cp
      JOIN
        person as p on p.id = person_id
      WHERE
        chat_id = :chat_id AND
        telegram = :telegram
    """;

  public Optional<TelegramPersonEntity> findByTelegram(Long chatId, String telegram) {
    final var params = new MapSqlParameterSource()
      .addValue(TELEGRAM, telegram)
      .addValue(CHAT_ID, chatId);

    return jdbcTemplate.query(FIND_BY_TELEGRAM_SQL, params, rs -> rs.next()
      ? Optional.ofNullable(telegramPersonEntityRowMapper.mapRow(rs, rs.getRow()))
      : Optional.empty());
  }

  private static final String FIND_ALL_BY_GAME_ID = """
      SELECT
        cp.telegram, cp.chat_id, p.id, p.first_name, p.last_name
      FROM
        entries as e
      JOIN
        person as p on p.id = e.person_id
      JOIN
        chat_persons as cp on cp.person_id = e.person_id
      WHERE
        e.game_id = :game_id
    """;

  public List<TelegramPersonEntity> findAllByGameId(UUID gameId) {
    final var params = new MapSqlParameterSource()
      .addValue(TelegramPersonColumnNames.GAME_ID, gameId);

    return jdbcTemplate.query(FIND_ALL_BY_GAME_ID, params, telegramPersonEntityRowMapper);
  }

  /* todo: move to service
  private static final String FIND_ALL_BY_TELEGRAM_SQL = """
      SELECT
         id, chat_id, telegram, first_name, last_name
      FROM person
      WHERE
        chat_id = :chat_id AND
        telegram IN (:telegram)
    """;

  public List<PersonEntity> findAllByTelegrams(List<String> telegrams, String chatId) {
    final var params = new MapSqlParameterSource()
      .addValue(TELEGRAM, telegrams)
      .addValue(CHAT_ID, chatId);

    return jdbcTemplate.query(FIND_ALL_BY_TELEGRAM_SQL, params, personEntityRowMapper);
  }
*/


}
