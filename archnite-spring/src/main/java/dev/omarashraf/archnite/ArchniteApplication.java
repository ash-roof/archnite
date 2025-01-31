package dev.omarashraf.archnite;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ArchniteApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().directory("../").load();

        setEnvVar(dotenv, "SPRING_DATASOURCE_URL");
        setEnvVar(dotenv, "SPRING_DATASOURCE_USERNAME");
        setEnvVar(dotenv, "SPRING_DATASOURCE_PASSWORD");
        SpringApplication.run(ArchniteApplication.class, args);
    }

    private static void setEnvVar(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        if (value != null) {
            System.setProperty(key, value);
        }
    }
}
