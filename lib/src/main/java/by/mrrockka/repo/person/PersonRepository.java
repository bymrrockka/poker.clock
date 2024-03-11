package by.mrrockka.repo.person;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
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
    INSERT INTO person (id, first_name, last_name, nick_name)
      VALUES (:id, :first_name, :last_name, :nick_name)
    """;

  public void save(final PersonEntity personEntity) {
    final var params = new MapSqlParameterSource()
      .addValue(ID, personEntity.getId())
      .addValue(FIRST_NAME, personEntity.getFirstname())
      .addValue(LAST_NAME, personEntity.getLastname())
      .addValue(NICK_NAME, personEntity.getNickname());
    jdbcTemplate.update(SAVE_SQL, params);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void saveAll(final List<PersonEntity> personEntities) {
    personEntities.forEach(this::save);
  }

  private static final String FIND_BY_ID_SQL = """
      SELECT
        id, first_name, last_name, nick_name
      FROM person
      WHERE
        id = :id;
    """;

  public PersonEntity findById(final UUID id) {
    final var params = new MapSqlParameterSource()
      .addValue(ID, id);

    return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, params, personEntityRowMapper);
  }

  private static final String FIND_ALL_BY_IDS_SQL = """
      SELECT
        id, first_name, last_name, nick_name
      FROM person
      WHERE
        id IN (:id)
    """;

  public List<PersonEntity> findAllByIds(final List<UUID> ids) {
    final var params = new MapSqlParameterSource()
      .addValue(ID, ids);

    return jdbcTemplate.query(FIND_ALL_BY_IDS_SQL, params, personEntityRowMapper);
  }


}
