package dev.omarashraf.archnite;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ArchniteApplicationTests {

    @BeforeAll
    static void loadEnvVariables() {
        // Load .env file from the root of the repository
        Dotenv dotenv = Dotenv.configure()
                .directory("../")
                .ignoreIfMissing()
                .load();

        String datasourceUrl = Boolean.parseBoolean(dotenv.get("DOCKER_COMPOSE_ENV"))
                ? dotenv.get("SPRING_DATASOURCE_URL_DOCKER")
                : dotenv.get("SPRING_DATASOURCE_URL_LOCAL");

        System.setProperty("spring.datasource.url", datasourceUrl);
        System.setProperty("spring.datasource.username", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.setProperty("spring.datasource.password", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
    }

    @Test
    void contextLoads() {
    }

}
