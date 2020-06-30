package com.example.demo.dao;

import com.example.demo.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("postgres")
public class PersonDataAccessService implements PersonDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insertPerson(UUID id, Person person) {
        final String query = "INSERT INTO person VALUES (?, ?)";
        jdbcTemplate.update(query, id, person.getName());
        return 1;
    }

    @Override
    public List<Person> selectAllPeople() {
        final String query = "SELECT id, name FROM person";
        return jdbcTemplate.query(query, (resultSet, i) -> {
            UUID id = UUID.fromString(resultSet.getString("id"));
            String name = resultSet.getString("name");
            return new Person(id, name);
        });
    }

    @Override
    public Optional<Person> selectPersonById(UUID id) {
        final String query = "SELECT id, name FROM person WHERE id = ?";

        Person person = jdbcTemplate.queryForObject(
                query,
                new Object[]{id},
                (resultSet, i) -> {
                    UUID personId = UUID.fromString(resultSet.getString("id"));
                    String name = resultSet.getString("name");
                    return new Person(personId, name);
                });

        return Optional.ofNullable(person);
    }

    @Override
    public int deletePersonById(UUID id) {
        Optional<Person> personMaybe = selectPersonById(id);
        if (personMaybe.isEmpty()) {
            return 0;
        }

        String query = "DELETE FROM person WHERE id = ?";
        jdbcTemplate.update(query, id);
        return 1;
    }

    @Override
    public int updatePersonById(UUID id, Person person) {
        Optional<Person> personMaybe = selectPersonById(id);
        if (personMaybe.isEmpty()) {
            return 0;
        }

        String query = "UPDATE person SET name = ? WHERE id = ?";
        jdbcTemplate.update(query, person.getName(), id);
        return 1;
    }
}
