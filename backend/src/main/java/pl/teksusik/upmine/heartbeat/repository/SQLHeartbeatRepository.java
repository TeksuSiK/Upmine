package pl.teksusik.upmine.heartbeat.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.heartbeat.Status;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.storage.SQLStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SQLHeartbeatRepository implements HeartbeatRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLHeartbeatRepository.class);

    private final SQLStorage storage;

    public SQLHeartbeatRepository(SQLStorage storage) {
        this.storage = storage;
    }

    private static final String CREATE_HEARTBEAT = """
            CREATE TABLE IF NOT EXISTS `heartbeat` (
              `uuid` varchar(36) NOT NULL PRIMARY KEY,
              `status` varchar(20) DEFAULT NULL,
              `message` varchar(255) DEFAULT NULL,
              `creationDate` timestamp NULL DEFAULT NULL,
              `monitor_uuid` varchar(36) NOT NULL,
              FOREIGN KEY (`monitor_uuid`)
              REFERENCES `monitor`(`uuid`)
              ON UPDATE CASCADE
              ON DELETE CASCADE
            );
            """;

    private static final String COUNT_HEARTBEATS = """
            SELECT COUNT(*) FROM monitor;
            """;

    private static final String SELECT_HEARTBEAT = """
            SELECT h.uuid, h.status, h.message, h.creationDate
            FROM heartbeat AS h
            WHERE h.uuid = ?;
            """;

    private static final String SELECT_ALL_HEARTBEATS = """
            SELECT h.uuid, h.status, h.message, h.creationDate
            FROM heartbeat AS h
            WHERE h.uuid = ?;
            """;

    private static final String SELECT_HEARTBEATS_BY_MONITOR = """
            SELECT h.uuid, h.status, h.message, h.creationDate
            FROM heartbeat AS h
            WHERE h.monitor_uuid = ?
            ORDER BY h.creationDate DESC;
            """;

    private static final String DELETE_HEARTBEAT = """
            DELETE FROM monitor WHERE uuid = ?
            """;

    @Override
    public void createTablesIfNotExists() {
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement heartbeatStatement = connection.prepareStatement(CREATE_HEARTBEAT)) {
            heartbeatStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while creating tables", exception);
        }
    }

    @Override
    public long count() {
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_HEARTBEATS);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while counting monitors", exception);
        }
        return 0;
    }

    @Override
    public Heartbeat save(Heartbeat heartbeat) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Heartbeat> findByUuid(UUID uuid) {
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_HEARTBEAT)) {
            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Status status = Status.valueOf(resultSet.getString("status"));
                    String message = resultSet.getString("message");
                    Instant creationDate = resultSet.getTimestamp("creationDate").toInstant();
                    Heartbeat heartbeat = new Heartbeat(uuid, status, message, creationDate);
                    return Optional.of(heartbeat);
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while finding heartbeat", exception);
        }
        return Optional.empty();
    }

    @Override
    public List<Heartbeat> findByMonitorUuid(UUID monitorUuid) {
        List<Heartbeat> heartbeats = new ArrayList<>();
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_HEARTBEATS_BY_MONITOR)) {
            statement.setString(1, monitorUuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    Status status = Status.valueOf(resultSet.getString("status"));
                    String message = resultSet.getString("message");
                    Instant creationDate = resultSet.getTimestamp("creationDate").toInstant();
                    Heartbeat heartbeat = new Heartbeat(uuid,  status, message, creationDate);
                    heartbeats.add(heartbeat);
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while finding heartbeats", exception);
        }
        return heartbeats;
    }

    @Override
    public List<Heartbeat> findByMonitor(Monitor monitor) {
        return this.findByMonitorUuid(monitor.getUuid());
    }

    @Override
    public List<Heartbeat> findAll() {
        List<Heartbeat> heartbeats = new ArrayList<>();
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_HEARTBEATS);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                Status status = Status.valueOf(resultSet.getString("status"));
                String message = resultSet.getString("message");
                Instant creationDate = resultSet.getTimestamp("creationDate").toInstant();
                Heartbeat heartbeat = new Heartbeat(uuid,  status, message, creationDate);
                heartbeats.add(heartbeat);
            }
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while finding heartbeats", exception);
        }
        return heartbeats;
    }

    @Override
    public boolean deleteByUuid(UUID uuid) {
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_HEARTBEAT)) {
            statement.setString(1, uuid.toString());

            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while deleting heartbeat", exception);
        }
        return false;
    }
}
