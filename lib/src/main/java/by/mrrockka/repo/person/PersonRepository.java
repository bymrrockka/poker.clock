package by.mrrockka.repo.person;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static by.mrrockka.repo.person.PersonColumnNames.*;

@Repository
@RequiredArgsConstructor
public class PersonRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final PersonEntityRowMapper personEntityRowMapper;

  private static final String SAVE_SQL = """
    INSERT INTO person (id, chat_id, telegram, first_name, last_name)
      VALUES (:id, :chat_id, :telegram, :first_name, :last_name)
    """;

  public void save(PersonEntity personEntity) {
    final var params = new MapSqlParameterSource()
      .addValue(ID, personEntity.id())
      .addValue(CHAT_ID, personEntity.chatId())
      .addValue(TELEGRAM, personEntity.telegram())
      .addValue(FIRST_NAME, personEntity.firstname())
      .addValue(LAST_NAME, personEntity.lastname());
    jdbcTemplate.update(SAVE_SQL, params);
  }

  @Transactional
  public void saveAll(List<PersonEntity> personEntities) {
    personEntities.forEach(personEntity -> {
      final var params = new MapSqlParameterSource()
        .addValue(ID, personEntity.id())
        .addValue(CHAT_ID, personEntity.chatId())
        .addValue(TELEGRAM, personEntity.telegram())
        .addValue(FIRST_NAME, personEntity.firstname())
        .addValue(LAST_NAME, personEntity.lastname());
      jdbcTemplate.update(SAVE_SQL, params);
    });
  }

  private static final String FIND_BY_ID_SQL = """
      SELECT
        id, chat_id, telegram, first_name, last_name
      FROM person
      WHERE
        id = :id;
    """;

  public PersonEntity findById(UUID id) {
    final var params = new MapSqlParameterSource()
      .addValue(ID, id);

    return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, params, (rs, rowNum) ->
      PersonEntity.builder()
        .id(UUID.fromString(rs.getString(ID)))
        .chatId(rs.getString(CHAT_ID))
        .telegram(rs.getString(TELEGRAM))
        .firstname(rs.getString(FIRST_NAME))
        .lastname(rs.getString(LAST_NAME))
        .build());
  }

  private static final String FIND_BY_TELEGRAM_SQL = """
      SELECT
        id, chat_id, telegram, first_name, last_name
      FROM person
      WHERE
        chat_id = :chat_id AND
        telegram = :telegram
    """;

  public PersonEntity findByTelegram(String telegram, String chatId) {
    final var params = new MapSqlParameterSource()
      .addValue(TELEGRAM, telegram)
      .addValue(CHAT_ID, chatId);

    return jdbcTemplate.queryForObject(FIND_BY_TELEGRAM_SQL, params, personEntityRowMapper);
  }

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


}
