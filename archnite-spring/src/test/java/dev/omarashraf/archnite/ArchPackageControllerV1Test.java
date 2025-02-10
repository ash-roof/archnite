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
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
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
                .setValidator(new LocalValidatorFactoryBean())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void getArchPackageByName_WhenArchPackageExists_ReturnsPackage() throws Exception {
        OffsetDateTime fixedTime = OffsetDateTime.parse("2025-02-10T22:00:00Z");
        String correctPackageName = "testpkg";
        ArchPackage archPackage = new ArchPackage(1,
                "any",
                correctPackageName,
                "A test package.",
                fixedTime,
                "https://test.com"
        );
        when(archPackageService.getArchPackageByPackageName(correctPackageName)).thenReturn(archPackage);

        mockMvc.perform(get(requestMapping + "/" + correctPackageName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.packageName").value(correctPackageName))
                .andExpect(jsonPath("$.architecture").value("any"))
                .andExpect(jsonPath("$.description").value("A test package."))
                .andExpect(jsonPath("$.lastUpdate").value("2025-02-10T22:00:00Z"))
                .andExpect(jsonPath("$.url").value("https://test.com"));

        verify(archPackageService).getArchPackageByPackageName(correctPackageName);
    }

    @Test
    void getArchPackageByName_WhenArchPackageDoesNotExist_Returns404() throws Exception {
        String nonExistentAPackageName = "doesntexist";
        String exceptionMessage = "Package not found: " + nonExistentAPackageName;

        when(archPackageService.getArchPackageByPackageName(nonExistentAPackageName))
                .thenThrow(new ResourceNotFoundException(exceptionMessage));

        mockMvc.perform(get(requestMapping + "/" + nonExistentAPackageName))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value(exceptionMessage))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(archPackageService).getArchPackageByPackageName(nonExistentAPackageName);
    }

    @Test
    void searchArchPackages_ValidKeywordAndLimit_ReturnsPackages() throws Exception {
        String keyword = "test";
        String examplePkgName = "testpkg1";
        int limit = 5;
        List<ArchPackage> mockPackages = List.of(
                new ArchPackage(1,
                        "any",
                        examplePkgName,
                        "A test package.",
                        OffsetDateTime.now(),
                        "https://test.com"
                )
        );

        when(archPackageService.searchArchPackagesBySimilarity(keyword, limit))
                .thenReturn(mockPackages);

        mockMvc.perform(get(requestMapping + "/search")
                .param("keyword", keyword)
                .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].packageName").value(examplePkgName));

        verify(archPackageService).searchArchPackagesBySimilarity(keyword, limit);
    }

    @Test
    void searchArchPackages_ValidKeywordWithDefaultLimit_ReturnsPackages() throws Exception {
        String keyword = "test";
        String examplePkgName = "testpkg1";
        int defaultLimit = 10;
        List<ArchPackage> mockPackages = List.of(
                new ArchPackage(1,
                        "any",
                        examplePkgName,
                        "A test package.",
                        OffsetDateTime.now(),
                        "https://test.com"
                )
        );

        when(archPackageService.searchArchPackagesBySimilarity(keyword, defaultLimit))
                .thenReturn(mockPackages);

        mockMvc.perform(get(requestMapping + "/search")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].packageName").value(examplePkgName));

        verify(archPackageService).searchArchPackagesBySimilarity(keyword, defaultLimit);
    }

    @Test
    void searchArchPackages_EmptyKeyword_Returns400() throws Exception {
        mockMvc.perform(get(requestMapping + "/search")
                .param("keyword", "")
                .param("limit", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("keyword: must not be empty"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // TODO: Make this return a 400 not a 500
    @Test
    void searchArchPackages_LimitBelowMin_Returns400() throws Exception {
        mockMvc.perform(get("/search")
                        .param("keyword", "test")
                        .param("limit", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("limit: must be greater than or equal to 1"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // TODO: Make this return a 400 not a 500
    @Test
    void searchArchPackages_LimitAboveMax_Returns400() throws Exception {
        mockMvc.perform(get("/search")
                        .param("keyword", "test")
                        .param("limit", "51"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("limit must be between 1 and 50"));
    }
}
