package dev.omarashraf.archnite;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ArchniteApplication {

    public static void main(String[] args) {
        // Load .env file from the root of the repository
        Dotenv dotenv = Dotenv.configure()
                .directory("../")
                .ignoreIfMissing()
                .load();

        String datasourceUrl;
        if (Boolean.parseBoolean(dotenv.get("DOCKER_ENV"))) {
            datasourceUrl = dotenv.get("SPRING_DATASOURCE_URL_DOCKER");
        } else {
            datasourceUrl = dotenv.get("SPRING_DATASOURCE_URL_LOCAL");
        }

        System.setProperty("spring.datasource.url", datasourceUrl);
        System.setProperty("spring.datasource.username", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.setProperty("spring.datasource.password", dotenv.get("SPRING_DATASOURCE_PASSWORD"));

        SpringApplication.run(ArchniteApplication.class, args);
    }
}
