package pl.teksusik.upmine.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Variable;

public class ApplicationConfiguration extends OkaeriConfig {
    private StorageConfiguration storageConfiguration = new StorageConfiguration();

    public class StorageConfiguration extends OkaeriConfig {
        @Variable("STORAGE_URL")
        private String jdbcUrl = "jdbc:mariadb://127.0.0.1:3306/upmine?user=upmine&password=upmine";

        public String getJdbcUrl() {
            return jdbcUrl;
        }
    }
}
