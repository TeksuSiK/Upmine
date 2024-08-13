package pl.teksusik.upmine.notification.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.teksusik.upmine.notification.NotificationSettings;
import pl.teksusik.upmine.notification.NotificationType;
import pl.teksusik.upmine.storage.SQLStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SQLNotificationRepository implements NotificationRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLNotificationRepository.class);

    private final SQLStorage storage;

    public SQLNotificationRepository(SQLStorage storage) {
        this.storage = storage;
    }

    private static final String CREATE_NOTIFICATION_SETTINGS = """
            CREATE TABLE IF NOT EXISTS `notification_settings` (
                `uuid` varchar(36) NOT NULL PRIMARY KEY,
                `name` varchar(50) DEFAULT NULL,
                `type` varchar(30) NOT NULL
            );
            """;

    private static final String COUNT_NOTIFICATION_SETTINGS = """
            SELECT COUNT(*) FROM notification_settings;
            """;

    private static final String INSERT_NOTIFICATION_SETTINGS = """
            INSERT INTO notification_settings (uuid, name, type)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY
            UPDATE name = VALUES(name), type = VALUES(type);
            """;

    private static final String SELECT_NOTIFICATION_SETTINGS = """
            SELECT ns.uuid, ns.name, ns.type FROM notification_settings AS ns WHERE ns.uuid = ?;
            """;

    private static final String SELECT_ALL_NOTIFICATION_SETTINGS = """
            SELECT ns.uuid, ns.name, ns.type FROM notification_settings AS ns;
            """;

    private static final String SELECT_NOTIFICATION_SETTINGS_BY_MONITOR = """
            SELECT ns.uuid, ns.name, ns.type
            FROM notification_settings AS ns
            JOIN monitor_notifications AS mn ON mn.notification_settings_uuid = ns.uuid
            WHERE mn.monitor_uuid = ?;
            """;

    private static final String DELETE_NOTIFICATION_SETTINGS = """
            DELETE FROM notification_settings WHERE uuid = ?;
            """;

    @Override
    public void createTablesIfNotExists() {
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement notificationSettingsStatement = connection.prepareStatement(CREATE_NOTIFICATION_SETTINGS)) {
            notificationSettingsStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while creating tables", exception);
        }
    }

    @Override
    public long count() {
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_NOTIFICATION_SETTINGS);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while counting notification settings", exception);
        }
        return 0;
    }

    @Override
    public NotificationSettings save(NotificationSettings settings) {
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_NOTIFICATION_SETTINGS)) {
            statement.setString(1, settings.getUuid().toString());
            statement.setString(2, settings.getName());
            statement.setString(3, settings.getType().toString());
            statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while saving notification settings", exception);
        }
        return settings;
    }

    @Override
    public Optional<NotificationSettings> findByUuid(UUID uuid) {
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_NOTIFICATION_SETTINGS)) {
            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    NotificationType type = NotificationType.valueOf(resultSet.getString("type"));
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while finding notification settings", exception);
        }
        return Optional.empty();
    }

    @Override
    public List<NotificationSettings> findAll() {
        List<NotificationSettings> notificationSettings = new ArrayList<>();
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_NOTIFICATION_SETTINGS);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String name = resultSet.getString("name");
                NotificationType type = NotificationType.valueOf(resultSet.getString("type"));
            }
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while finding notification settings", exception);
        }
        return notificationSettings;
    }

    @Override
    public List<NotificationSettings> findByMonitorUuid(UUID monitorUuid) {
        List<NotificationSettings> notificationSettings = new ArrayList<>();
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_NOTIFICATION_SETTINGS_BY_MONITOR)) {
            statement.setString(1, monitorUuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    String name = resultSet.getString("name");
                    NotificationType type = NotificationType.valueOf(resultSet.getString("type"));
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while finding notification settings", exception);
        }
        return notificationSettings;
    }

    @Override
    public boolean deleteByUuid(UUID uuid) {
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_NOTIFICATION_SETTINGS)) {
            statement.setString(1, uuid.toString());

            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while deleting notification settings", exception);
        }
        return false;
    }
}
