package pl.teksusik.upmine.configuration;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationFactory.class);

    public static <T extends OkaeriConfig> T createConfiguration(Class<T> configurationClass) {
        try {
            return ConfigManager.create(configurationClass, (it) -> {
                it.withConfigurer(new YamlSnakeYamlConfigurer());
                it.withBindFile("configuration.yml");
                it.withRemoveOrphans(true);
                it.saveDefaults();
                it.load(true);
            });
        } catch (OkaeriException exception) {
            LOGGER.error("An error occurred while creating the configuration", exception);
        }

        return null;
    }
}
