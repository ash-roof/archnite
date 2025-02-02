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

//        // Load .env file from the root of the repository
//        Dotenv dotenv = Dotenv.configure()
//                .directory("../")
//                .ignoreIfMissing()
//                .load();
//
//        // Debug: Print environment variables
//        System.out.println("SPRING_DATASOURCE_URL_LOCAL: " + dotenv.get("SPRING_DATASOURCE_URL_LOCAL"));
//        System.out.println("SPRING_DATASOURCE_URL_DOCKER: " + dotenv.get("SPRING_DATASOURCE_URL_DOCKER"));
//        System.out.println("SPRING_DATASOURCE_USERNAME: " + dotenv.get("SPRING_DATASOURCE_USERNAME"));
//        System.out.println("SPRING_DATASOURCE_PASSWORD: " + dotenv.get("SPRING_DATASOURCE_PASSWORD"));
//
//        // Set environment variables
//        System.setProperty("spring.datasource.url", dotenv.get("SPRING_DATASOURCE_URL_DOCKER"));
//        System.setProperty("spring.datasource.username", dotenv.get("SPRING_DATASOURCE_USERNAME"));
//        System.setProperty("spring.datasource.password", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
//
//        SpringApplication.run(ArchniteApplication.class, args);
}
