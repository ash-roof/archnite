package dev.omarashraf.archnite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.omarashraf.archnite.controller.ArchPackageControllerV1;
import dev.omarashraf.archnite.exception.GlobalExceptionHandler;
import dev.omarashraf.archnite.exception.ResourceNotFoundException;
import dev.omarashraf.archnite.model.ArchPackage;
import dev.omarashraf.archnite.service.ArchPackageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ArchPackageControllerV1Test {

    @Mock
    private ArchPackageService archPackageService;

    @InjectMocks
    private ArchPackageControllerV1 archPackageController;

    private MockMvc mockMvc;

    private final String requestMapping = "/api/v1/arch";

    @BeforeEach
    void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(archPackageController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void getArchPackageByName_WhenArchPackageExists_ReturnsPackage() throws Exception {
        OffsetDateTime fixedTime = OffsetDateTime.parse("2025-02-10T22:00:00Z");
        ArchPackage archPackage = new ArchPackage(1,
                "any",
                "testpkg",
                "A test package.",
                fixedTime,
                "https://test.com"
        );
        when(archPackageService.getArchPackageByPackageName("testpkg")).thenReturn(archPackage);

        mockMvc.perform(get(requestMapping + "/testpkg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.packageName").value("testpkg"))
                .andExpect(jsonPath("$.architecture").value("any"))
                .andExpect(jsonPath("$.description").value("A test package."))
                .andExpect(jsonPath("$.lastUpdate").value("2025-02-10T22:00:00Z"))
                .andExpect(jsonPath("$.url").value("https://test.com"));
    }

    @Test
    void getArchPackageByName_WhenArchPackageDoesNotExist_Returns404() throws Exception {
        String nonExistentAPackageName = "doesntexist";
        String exceptionMessage = "Package not found: " + nonExistentAPackageName;

        when(archPackageService.getArchPackageByPackageName(nonExistentAPackageName))
                .thenThrow(new ResourceNotFoundException(exceptionMessage));

        mockMvc.perform(get(requestMapping + "/" + nonExistentAPackageName))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value(exceptionMessage))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
