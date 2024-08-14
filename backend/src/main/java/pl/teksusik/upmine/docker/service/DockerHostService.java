package pl.teksusik.upmine.docker.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import pl.teksusik.upmine.docker.DockerHost;
import pl.teksusik.upmine.docker.dto.DockerHostDto;
import pl.teksusik.upmine.docker.repository.DockerHostRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DockerHostService {
    private final Map<UUID, DockerClient> clients = new HashMap<>();

    private final DockerHostRepository dockerHostRepository;

    public DockerHostService(DockerHostRepository dockerHostRepository) {
        this.dockerHostRepository = dockerHostRepository;
    }

    public long count() {
        return this.dockerHostRepository.count();
    }

    public DockerHost save(DockerHost dockerHost) {
        return this.dockerHostRepository.save(dockerHost);
    }

    public Optional<DockerHost> findByUuid(UUID uuid) {
        return this.dockerHostRepository.findByUuid(uuid);
    }

    public List<DockerHost> findAll() {
        return this.dockerHostRepository.findAll();
    }

    public boolean deleteByUuid(UUID uuid) {
        return this.dockerHostRepository.deleteByUuid(uuid);
    }

    public Optional<DockerHost> createDockerHost(DockerHostDto dockerHostDto) {
        UUID uuid = UUID.randomUUID();
        String name = dockerHostDto.getName();
        String address = dockerHostDto.getAddress();

        DockerHost dockerHost = new DockerHost(uuid, name, address);
        DockerHost createdDockerHost = this.dockerHostRepository.save(dockerHost);
        this.register(dockerHost);
        return Optional.of(createdDockerHost);
    }

    public Optional<DockerHost> updateDockerHost(UUID uuid, DockerHostDto dockerHostDto) {
        Optional<DockerHost> dockerHostOptional = this.dockerHostRepository.findByUuid(uuid);
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

        DockerHost updatedDockerHost = this.dockerHostRepository.save(dockerHost);
        this.register(dockerHost);
        return Optional.of(updatedDockerHost);
    }

    public void registerExistingHosts() {
        this.dockerHostRepository.findAll()
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
