# Upmine

A self-hosted uptime monitoring and alerting system built with Java. Upmine continuously checks the availability of various services and sends notifications when status changes occur.

## Features

- **Multiple Monitor Types**
  - **HTTP** - Check HTTP endpoint availability with custom accepted status codes
  - **Ping** - ICMP ping checks to verify host reachability
  - **Docker** - Monitor Docker container status on registered hosts
  - **Push** - Receive availability status via webhook pushes from external systems

- **Notification System**
  - Discord webhook integration
  - Alerts only on status changes (reduces noise)
  - Configurable notification settings per monitor

- **Heartbeat Tracking**
  - Records availability status with timestamps
  - Maintains history of all checks

- **Docker Host Management**
  - Register and manage multiple Docker hosts
  - Monitor containers across different hosts

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Web Framework | Javalin |
| Database | MariaDB with HikariCP |
| Scheduling | Quartz |
| Container Management | Docker Java |

## Prerequisites

- Java 17 or higher
- MariaDB 10.5+
- Docker (optional, for Docker monitoring and test environment)

## Getting Started

### 1. Start MariaDB

Using Docker Compose (for development/testing):

```bash
cd environment/test
docker-compose up -d mariadb
```

This creates a MariaDB container with:
- Database: `upmine`
- User: `upmine`
- Password: `upmine`
- Port: `3306`

### 2. Configure Application

Create a `configuration.yml` file in the `backend` directory:

```yaml
storage-configuration:
  storage-url: jdbc:mariadb://127.0.0.1:3306/upmine?user=upmine&password=upmine

web-configuration:
  web-hostname: 127.0.0.1
  web-port: 8080
```

Alternatively, use environment variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `STORAGE_URL` | `jdbc:mariadb://127.0.0.1:3306/upmine?user=upmine&password=upmine` | MariaDB JDBC connection string |
| `WEB_HOSTNAME` | `127.0.0.1` | Hostname the web server binds to |
| `WEB_PORT` | `8080` | Port the web server listens on |

### 3. Build and Run

```bash
cd backend
./gradlew build
./gradlew run
```

The application will automatically initialize database tables on first run.

## Project Structure

```
Upmine/
├── backend/                   # Java backend application
│   └── src/main/java/         # Source code
├── environment/
│   └── test/                  # Test environment (Docker Compose)
└── README.md
```

## Database Schema

The application automatically creates the following tables:

- `docker_hosts` - Registered Docker host configurations
- `monitors` - Monitor definitions and metadata
- `heartbeats` - Availability check results and history
- `notification_settings` - Notification channel configurations
- `monitor_notification_settings` - Junction table linking monitors to notifications

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
