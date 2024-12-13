package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM users");
            query.executeUpdate();
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var email = "email@email.email";
        var name = "name";
        var password = "password";
        var user = new User(email, name, password);
        var isSaved = sql2oUserRepository.save(user).isPresent();
        var savedUser = sql2oUserRepository.findByEmailAndPassword(email, password);
        assertThat(isSaved).isTrue();
        assertThat(savedUser.get()).isEqualTo(user);
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        var email = "email@email.email";
        var password = "password";
        var savedUser = sql2oUserRepository.findByEmailAndPassword(email, password);
        assertThat(savedUser).isEmpty();
    }

    @Test
    public void whenSaveExistingThenNotSave() {
        var email = "email@email.email";
        var name = "name";
        var password = "password";
        var user = new User(email, name, password);
        sql2oUserRepository.save(user);
        var isSavedExisting = sql2oUserRepository.save(user).isPresent();
        assertThat(isSavedExisting).isFalse();
    }
}