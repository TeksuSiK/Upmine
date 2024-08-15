package pl.teksusik.upmine.docker.host.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import pl.teksusik.upmine.docker.host.DockerHost;
import pl.teksusik.upmine.docker.host.dto.DockerHostDto;
import pl.teksusik.upmine.storage.Repository;
import pl.teksusik.upmine.web.CrudService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DockerHostService extends CrudService<DockerHost, DockerHostDto> {
    private final Map<UUID, DockerClient> clients = new HashMap<>();

    public DockerHostService(Repository<DockerHost> repository) {
        super(repository);
    }

    @Override
    public Optional<DockerHost> create(DockerHostDto dockerHostDto) {
        UUID uuid = UUID.randomUUID();
        String name = dockerHostDto.getName();
        String address = dockerHostDto.getAddress();

        DockerHost dockerHost = new DockerHost(uuid, name, address);
        DockerHost createdDockerHost = this.repository.save(dockerHost);
        this.register(dockerHost);
        return Optional.of(createdDockerHost);
    }

    @Override
    public Optional<DockerHost> update(UUID uuid, DockerHostDto dockerHostDto) {
        Optional<DockerHost> dockerHostOptional = this.repository.findByUuid(uuid);
        if (dockerHostOptional.isEmpty()) {
            return Optional.empty();
        }

        String newName = dockerHostDto.getName();
        String newAddress = dockerHostDto.getAddress();

        DockerHost dockerHost = dockerHostOptional.get();

        if (newName != null && !newName.equals(dockerHostDto.getName())) {
            dockerHost.setName(newName);
        }

        if (newAddress != null && !newAddress.equals(dockerHostDto.getAddress())) {
            dockerHost.setAddress(newAddress);
        }

        DockerHost updatedDockerHost = this.repository.save(dockerHost);
        this.register(dockerHost);
        return Optional.of(updatedDockerHost);
    }

    public void registerExistingHosts() {
        this.repository.findAll()
                .forEach(this::register);
    }

    public void register(DockerHost host) {
        DockerClientConfig clientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(host.getAddress())
                .build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(clientConfig.getDockerHost())
                .build();
        DockerClient client = DockerClientImpl.getInstance(clientConfig, httpClient);
        this.clients.put(host.getUuid(), client);
    }

    public DockerClient getClient(DockerHost dockerHost) {
        return this.clients.get(dockerHost.getUuid());
    }
}
