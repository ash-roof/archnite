package dev.omarashraf.archnite.config;

import dev.omarashraf.archnite.enums.SortField;
import dev.omarashraf.archnite.util.CaseInsensitiveEnumConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        List<Class<? extends Enum>> enums = List.of(SortField.class, Sort.Direction.class);
        enums.forEach(enumClass -> registry.addConverter(String.class, enumClass,
                new CaseInsensitiveEnumConverter<>(enumClass)));
    }
}
