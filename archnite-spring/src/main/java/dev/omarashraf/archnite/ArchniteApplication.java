package dev.omarashraf.archnite;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ArchniteApplication {

    public static void main(String[] args) {
        // Load .env file from the root of the repository
        Dotenv dotenv = Dotenv.configure()
                .directory("../")
                .ignoreIfMissing()
                .load();

        String datasourceUrl = Boolean.parseBoolean(dotenv.get("DOCKER_COMPOSE_ENV"))
                ? "jdbc:postgresql://postgres:" + dotenv.get("POSTGRES_PORT") + "/" + dotenv.get("POSTGRES_DB")
                : "jdbc:postgresql://localhost:" + dotenv.get("POSTGRES_PORT") + "/" + dotenv.get("POSTGRES_DB");

        System.setProperty("spring.datasource.url", datasourceUrl);
        System.setProperty("spring.datasource.username", dotenv.get("POSTGRES_USER"));
        System.setProperty("spring.datasource.password", dotenv.get("POSTGRES_PASSWORD"));

        SpringApplication.run(ArchniteApplication.class, args);
    }
}
