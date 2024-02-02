package by.mrrockka.repo;

import by.mrrockka.repo.entities.PersonEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PersonRepository {

  private static final String ID = "id";
  private static final String CHAT_ID = "chat_id";
  private static final String TELEGRAM = "telegram";
  private static final String FIRST_NAME = "first_name";
  private static final String LAST_NAME = "last_name";

  private final NamedParameterJdbcTemplate jdbcTemplate;

  private static final String SAVE_SQL = """
    INSERT INTO person (id, chat_id, telegram, first_name, last_name)
      VALUES (:id, :chatId, :telegram, :firstName, :lastName)
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
        id = :id
        AND chat_id = :chat_id;
    """;

  public PersonEntity findById(UUID id, String chatId) {
    final var params = new MapSqlParameterSource()
      .addValue(ID, id)
      .addValue(CHAT_ID, chatId);

    return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, params, (rs, rowNum) ->
      PersonEntity.builder()
                  .id(UUID.fromString(rs.getString(ID)))
                  .chatId(rs.getString(CHAT_ID))
                  .telegram(rs.getString(TELEGRAM))
                  .firstname(rs.getString(FIRST_NAME))
                  .lastname(rs.getString(LAST_NAME))
                  .build());
  }

  private static final String FIND_ALL_BY_TELEGRAM_SQL = """
      SELECT
        id, chat_id, telegram, first_name, last_name
      FROM person
      WHERE
        chat_id = :chat_id AND
        telegram IN (:telegram)
    """;

  public List<PersonEntity> findAllByIds(List<String> telegrams, String chatId) {
    final var params = new MapSqlParameterSource()
      .addValue(TELEGRAM, telegrams.stream().reduce((telegram1, telegram2) -> telegram1 + "," + telegram2))
      .addValue(CHAT_ID, chatId);

    return jdbcTemplate.query(FIND_ALL_BY_TELEGRAM_SQL, params, this::rowMapper);
  }

  private PersonEntity rowMapper(ResultSet rs, int rowNum) throws SQLException {
    return PersonEntity.builder()
                       .id(UUID.fromString(rs.getString(ID)))
                       .chatId(rs.getString(CHAT_ID))
                       .telegram(rs.getString(TELEGRAM))
                       .firstname(rs.getString(FIRST_NAME))
                       .lastname(rs.getString(LAST_NAME))
                       .build();
  }

}
