package pl.teksusik.upmine.docker.host;

import java.util.UUID;

public class DockerHost {
    private final UUID uuid;
    private String name;

    private String address;

    public DockerHost(UUID uuid) {
        this.uuid = uuid;
    }

    public DockerHost(UUID uuid, String name, String address) {
        this.uuid = uuid;
        this.name = name;
        this.address = address;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
