package pl.teksusik.upmine.monitor.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.heartbeat.repository.HeartbeatRepository;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.MonitorType;
import pl.teksusik.upmine.monitor.http.HttpMonitor;
import pl.teksusik.upmine.monitor.ping.PingMonitor;
import pl.teksusik.upmine.storage.SQLStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SQLMonitorRepository implements MonitorRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLMonitorRepository.class);

    private final SQLStorage storage;
    private final HeartbeatRepository heartbeatRepository;

    public SQLMonitorRepository(SQLStorage storage, HeartbeatRepository heartbeatRepository) {
        this.storage = storage;
        this.heartbeatRepository = heartbeatRepository;
    }

    private static final String CREATE_MONITOR = """
            CREATE TABLE IF NOT EXISTS `monitor` (
              `uuid` varchar(36) NOT NULL PRIMARY KEY,
              `name` varchar(50) DEFAULT NULL,
              `type` varchar(20) NOT NULL,
              `creationDate` timestamp NULL DEFAULT NULL,
              `checkInterval` bigint DEFAULT NULL,
              `httpUrl` varchar(50) DEFAULT NULL,
              `httpAcceptedCodes` varchar(255) DEFAULT NULL,
              `pingAddress` varchar(50) DEFAULT NULL
            );
            """;

    private static final String COUNT_MONITORS = """
            SELECT COUNT(*) FROM monitor;
            """;

    private static final String INSERT_MONITOR = """
            INSERT INTO monitor (uuid, name, type, creationDate, checkInterval, httpUrl, httpAcceptedCodes, pingAddress)
            VALUES (?, ? ,?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY
            UPDATE name = VALUES(name), type = VALUES(type), creationDate = VALUES(creationDate), checkInterval = VALUES(checkInterval), httpUrl = VALUES(httpUrl), httpAcceptedCodes = VALUES(httpAcceptedCodes), pingAddress = VALUES(pingAddress);
            """;
    private static final String INSERT_HEARTBEAT = """
            INSERT INTO heartbeat (uuid, status, message, creationDate, monitor_uuid) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE uuid = uuid;
            """;

    private static final String SELECT_MONITOR = """
            SELECT m.uuid, m.name, m.type, m.creationDate, m.checkInterval, m.httpUrl, m.httpAcceptedCodes, m.pingAddress
            FROM monitor AS m
            WHERE m.uuid = ?;
            """;

    private static final String SELECT_ALL_MONITORS = """
            SELECT m.uuid, m.name, m.type, m.creationDate, m.checkInterval, m.httpUrl, m.httpAcceptedCodes, m.pingAddress
            FROM monitor AS m;
            """;

    private static final String DELETE_MONITOR = """
            DELETE FROM monitor WHERE uuid = ?
            """;

    @Override
    public void createTablesIfNotExists() {
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement monitorStatement = connection.prepareStatement(CREATE_MONITOR)) {
            monitorStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while creating tables", exception);
        }
    }

    @Override
    public long count() {
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_MONITORS);
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
    public Monitor save(Monitor monitor) {
        try (Connection connection = this.storage.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement monitorStatement = connection.prepareStatement(INSERT_MONITOR);
                 PreparedStatement heartbeatStatement = connection.prepareStatement(INSERT_HEARTBEAT)) {
                monitorStatement.setString(1, monitor.getUuid().toString());
                monitorStatement.setString(2, monitor.getName());
                monitorStatement.setString(3, monitor.getType().toString());
                monitorStatement.setTimestamp(4, Timestamp.from(monitor.getCreationDate()));

                monitorStatement.setLong(5, monitor.getCheckInterval().toSeconds());

                if (monitor instanceof HttpMonitor httpMonitor) {
                    monitorStatement.setString(6, httpMonitor.getHttpUrl());
                    monitorStatement.setString(7, httpMonitor.getHttpAcceptedCodes().stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(";")));
                    monitorStatement.setNull(8, Types.VARCHAR);
                } else if (monitor instanceof PingMonitor pingMonitor) {
                    monitorStatement.setNull(6, Types.VARCHAR);
                    monitorStatement.setNull(7, Types.VARCHAR);
                    monitorStatement.setString(8, pingMonitor.getPingAddress());
                }
                monitorStatement.executeUpdate();

                for (Heartbeat heartbeat : monitor.getHeartbeats()) {
                    heartbeatStatement.setString(1, heartbeat.getUuid().toString());
                    heartbeatStatement.setString(2, heartbeat.getStatus().toString());
                    heartbeatStatement.setString(3, heartbeat.getMessage());
                    heartbeatStatement.setTimestamp(4, Timestamp.from(heartbeat.getCreationDate()));
                    heartbeatStatement.setString(5, monitor.getUuid().toString());
                    heartbeatStatement.addBatch();
                }
                heartbeatStatement.executeBatch();

                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("An error occurred while saving monitor", exception);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while saving monitor", exception);
        }
        return monitor;
    }

    @Override
    public Optional<Monitor> findByUuid(UUID uuid) {
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement selectMonitor = connection.prepareStatement(SELECT_MONITOR)) {
            selectMonitor.setString(1, uuid.toString());

            try (ResultSet monitorResult = selectMonitor.executeQuery()) {
                if (monitorResult.next()) {
                    String name = monitorResult.getString("name");
                    MonitorType type = MonitorType.valueOf(monitorResult.getString("type"));
                    Instant creationDate = monitorResult.getTimestamp("creationDate").toInstant();
                    Duration checkInterval = Duration.ofSeconds(monitorResult.getLong("checkInterval"));

                    String httpUrl = monitorResult.getString("httpUrl");
                    String httpAcceptedCodes = monitorResult.getString("httpAcceptedCodes");
                    List<Integer> httpAcceptedCodesList = List.of();
                    if (httpAcceptedCodes != null) {
                        httpAcceptedCodesList = Arrays.stream(httpAcceptedCodes
                                        .split(";"))
                                .map(Integer::parseInt)
                                .toList();
                    }

                    String pingAddress = monitorResult.getString("pingAddress");

                    List<Heartbeat> heartbeats = this.heartbeatRepository.findByMonitorUuid(uuid);

                    if (type == MonitorType.HTTP) {
                        HttpMonitor httpMonitor = new HttpMonitor(uuid, name, type, creationDate, checkInterval, httpUrl, httpAcceptedCodesList);
                        httpMonitor.setHeartbeats(heartbeats);
                        return Optional.of(httpMonitor);
                    } else if (type == MonitorType.PING) {
                        PingMonitor pingMonitor = new PingMonitor(uuid, name, type, creationDate, checkInterval, pingAddress);
                        pingMonitor.setHeartbeats(heartbeats);
                        return Optional.of(pingMonitor);
                    }
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while finding monitor", exception);
        }
        return Optional.empty();
    }

    @Override
    public List<Monitor> findAll() {
        Map<UUID, Monitor> monitors = new HashMap<>();
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement selectMonitors = connection.prepareStatement(SELECT_ALL_MONITORS)) {

            try (ResultSet resultSet = selectMonitors.executeQuery()) {
                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));

                    List<Heartbeat> heartbeats = this.heartbeatRepository.findByMonitorUuid(uuid);

                    Monitor monitor = monitors.computeIfAbsent(uuid, mapper -> {
                        try {
                            String name = resultSet.getString("name");
                            MonitorType type = MonitorType.valueOf(resultSet.getString("type"));
                            Instant creationDate = resultSet.getTimestamp("creationDate").toInstant();
                            Duration checkInterval = Duration.ofSeconds(resultSet.getLong("checkInterval"));

                            if (type == MonitorType.HTTP) {
                                String httpUrl = resultSet.getString("httpUrl");
                                List<Integer> httpAcceptedCodes = Arrays.stream(resultSet.getString("httpAcceptedCodes")
                                                .split(";"))
                                        .map(Integer::parseInt)
                                        .toList();

                                return new HttpMonitor(uuid, name, type, creationDate, checkInterval, httpUrl, httpAcceptedCodes);
                            } else if (type == MonitorType.PING) {
                                String pingAddress = resultSet.getString("pingAddress");

                                return new PingMonitor(uuid, name, type, creationDate, checkInterval, pingAddress);
                            }

                        } catch (SQLException exception) {
                            LOGGER.error("An error occurred while finding monitor", exception);
                        }

                        return null;
                    });

                    if (monitor == null) {
                        continue;
                    }

                    monitor.setHeartbeats(heartbeats);
                }
            }
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while finding monitor", exception);
        }
        return monitors.values()
                .stream()
                .toList();
    }

    @Override
    public boolean deleteByUuid(UUID uuid) {
        try (Connection connection = this.storage.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_MONITOR)) {
            statement.setString(1, uuid.toString());

            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while deleting monitor", exception);
        }
        return false;
    }
}
