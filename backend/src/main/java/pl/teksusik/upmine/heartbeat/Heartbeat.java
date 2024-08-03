package pl.teksusik.upmine.heartbeat;

import java.time.Instant;
import java.util.UUID;

public class Heartbeat {
    private final UUID uuid;

    private Status status;
    private String message;
    private Instant creationDate;

    public Heartbeat(UUID uuid) {
        this.uuid = uuid;
    }

    public Heartbeat(UUID uuid, Status status, String message, Instant creationDate) {
        this.uuid = uuid;
        this.status = status;
        this.message = message;
        this.creationDate = creationDate;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }
}
