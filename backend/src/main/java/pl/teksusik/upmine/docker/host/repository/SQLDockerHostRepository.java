package pl.teksusik.upmine.docker.host.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.teksusik.upmine.docker.host.DockerHost;
import pl.teksusik.upmine.storage.SQLStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SQLDockerHostRepository implements DockerHostRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLDockerHostRepository.class);

    private final SQLStorage storage;

    public SQLDockerHostRepository(SQLStorage storage) {
        this.storage = storage;
    }

    private static final String CREATE_DOCKER_HOST = """
            CREATE TABLE IF NOT EXISTS `docker_host` (
              `uuid` VARCHAR(36) NOT NULL PRIMARY KEY,
              `name` VARCHAR(50) NOT NULL,
              `host` VARCHAR(50) NOT NULL
            );
            """;

    private static final String COUNT_DOCKER_HOSTS = """
            SELECT COUNT(*) FROM docker_host;
            """;

    private static final String INSERT_DOCKER_HOST = """
            INSERT INTO docker_host (uuid, name, host) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name), host = VALUES(host);
            """;

    private static final String SELECT_DOCKER_HOST = """
            SELECT uuid, name, host FROM docker_host WHERE uuid = ?;
            """;

    private static final String SELECT_ALL_DOCKER_HOSTS = """
            SELECT uuid, name, host FROM docker_host;
            """;

    private static final String DELETE_DOCKER_HOST = """
            DELETE FROM docker_host WHERE uuid = ?;
            """;

    @Override
    public void createTablesIfNotExists() {
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement dockerHostStatement = connection.prepareStatement(CREATE_DOCKER_HOST)) {
            dockerHostStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while creating tables", exception);
        }
    }

    @Override
    public long count() {
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_DOCKER_HOSTS);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while counting docker hosts", exception);
        }
        return 0;
    }

    @Override
    public DockerHost save(DockerHost dockerHost) {
        try (Connection connection = this.storage.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT_DOCKER_HOST)) {
            statement.setString(1, dockerHost.getUuid().toString());
            statement.setString(2, dockerHost.getName());
            statement.setString(3, dockerHost.getAddress());
            statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while saving docker host", exception);
        }
        return dockerHost;
    }

    @Override
    public Optional<DockerHost> findByUuid(UUID uuid) {
        try (Connection connection = this.storage.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_DOCKER_HOST)) {
            statement.setString(1, uuid.toString());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String name = result.getString("name");
                    String host = result.getString("host");

                    return Optional.of(new DockerHost(uuid, name, host));
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while finding docker host", exception);
        }
        return Optional.empty();
    }

    @Override
    public List<DockerHost> findAll() {
        List<DockerHost> dockerHosts = new ArrayList<>();
        try (Connection connection = this.storage.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL_DOCKER_HOSTS);
            ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                UUID uuid = UUID.fromString(result.getString("uuid"));
                String name = result.getString("name");
                String host = result.getString("host");
                DockerHost dockerHost = new DockerHost(uuid, name, host);
                dockerHosts.add(dockerHost);
            }
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while finding docker hosts", exception);
        }
        return dockerHosts;
    }

    @Override
    public boolean deleteByUuid(UUID uuid) {
        try (Connection connection = this.storage.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE_DOCKER_HOST)) {
            statement.setString(1, uuid.toString());

            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while deleting docker host", exception);
        }
        return false;
    }
}
