package dev.omarashraf.archnite;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ArchniteApplication {

    public static void main(String[] args) {
        // use .env.local in case of running outside of docker
        if (System.getenv("DOCKER_ENV") == null) {
            Dotenv dotenv = Dotenv.configure().directory("../").filename(".env.local").load();
            setEnvVar(dotenv, "SPRING_DATASOURCE_URL");
            setEnvVar(dotenv, "SPRING_DATASOURCE_USERNAME");
            setEnvVar(dotenv, "SPRING_DATASOURCE_PASSWORD");
        }
        SpringApplication.run(ArchniteApplication.class, args);
    }

    private static void setEnvVar(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        if (value != null && System.getenv(key) == null) {
            System.setProperty(key, value);
        }
    }
}
