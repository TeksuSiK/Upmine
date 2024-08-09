package pl.teksusik.upmine.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Variable;

public class ApplicationConfiguration extends OkaeriConfig {
    private StorageConfiguration storageConfiguration = new StorageConfiguration();
    private WebConfiguration webConfiguration = new WebConfiguration();

    public class StorageConfiguration extends OkaeriConfig {
        @Variable("STORAGE_URL")
        private String jdbcUrl = "jdbc:mariadb://127.0.0.1:3306/upmine?user=upmine&password=upmine";

        public String getJdbcUrl() {
            return jdbcUrl;
        }
    }

    public class WebConfiguration extends OkaeriConfig {
        @Variable("WEB_HOSTNAME")
        private String hostname = "127.0.0.1";
        @Variable("WEB_PORT")
        private int port = 8080;

        public String getHostname() {
            return hostname;
        }

        public int getPort() {
            return port;
        }
    }

    public StorageConfiguration getStorageConfiguration() {
        return storageConfiguration;
    }

    public WebConfiguration getWebConfiguration() {
        return webConfiguration;
    }
}
