package dev.omarashraf.archnite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.omarashraf.archnite.controller.ArchPackageController;
import dev.omarashraf.archnite.exception.GlobalExceptionHandler;
import dev.omarashraf.archnite.exception.PackageNotFoundException;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ArchPackageControllerTest {

    @Mock
    private ArchPackageService archPackageService;

    @InjectMocks
    private ArchPackageController archPackageController;

    private MockMvc mockMvc;
    private final String requestMapping = "/packages/arch";
    private final String fixedDateValue = "2025-02-10T22:00:00Z";
    private final OffsetDateTime fixedDate = OffsetDateTime.parse(fixedDateValue);

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
    void GetPackageByName_WhenPackageExists_WithoutIsAurQueryParam_ReturnsPackage() throws Exception {
        String correctPackageName = "testpkg";
        ArchPackage archPackage = createTestPackage(1, correctPackageName, false);
        when(archPackageService.findArchPackageByPackageName(correctPackageName, null))
                .thenReturn(Optional.of(archPackage));

        assertPackageJson(mockMvc.perform(get(requestMapping + "/name/testpkg")), archPackage);
    }

    @Test
    void GetPackageByName_WhenPackageExists_WithIsAurQueryParam_ReturnsPackage() throws Exception {
        String correctPackageName = "testpkg";
        ArchPackage archPackage = createTestPackage(1, correctPackageName, false);
        when(archPackageService.findArchPackageByPackageName(correctPackageName, false))
                .thenReturn(Optional.of(archPackage));

        assertPackageJson(mockMvc.perform(get(requestMapping + "/name/testpkg?isAur=false")), archPackage);
    }

    @Test
    void GetPackageByName_WhenIsAurIsInvalid_Returns400() throws Exception {
        String packageName = "testpkg";

        mockMvc.perform(get(requestMapping + "/name/" + packageName + "?isAur=invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void GetPackageByName_WithIsAurQueryParam_ReturnsCorrectAurPackage() throws Exception {
        String packageName = "testpkg";

        ArchPackage aurPackage = createTestPackage(1, packageName, true);
        ArchPackage officialPackage = createTestPackage(2, packageName, false);

        when(archPackageService.findArchPackageByPackageName(packageName, true))
                .thenReturn(Optional.of(aurPackage));
        when(archPackageService.findArchPackageByPackageName(packageName, false))
                .thenReturn(Optional.of(officialPackage));

        assertPackageJson(mockMvc.perform(get(requestMapping + "/name/" + packageName + "?isAur=true")), aurPackage);
        assertPackageJson(mockMvc.perform(get(requestMapping + "/name/" + packageName + "?isAur=false")), officialPackage);
    }

    @Test
    void GetPackageByName_WhenPackageDoesNotExist_WithoutIsAurQueryParam_Returns404() throws Exception {
        String missingPackageName = "missing";
        String errorMessage = "Package Not Found";
        String errorDetails = "Package with name: '" + missingPackageName + "' not found";

        when(archPackageService.findArchPackageByPackageName(missingPackageName, null))
                .thenThrow(new PackageNotFoundException(errorMessage, missingPackageName));

        assertNotFoundJson(mockMvc.perform(get(requestMapping + "/name/missing")),
                errorDetails,
                errorMessage);
    }

    @Test
    void GetPackageByName_WhenPackageDoesNotExist_WithIsAurQueryParam_Returns404() throws Exception {
        String missingPackageName = "missing";
        String errorMessage = "Package Not Found";
        String errorDetails = "Package with name: '" + missingPackageName + "' not found";

        when(archPackageService.findArchPackageByPackageName(missingPackageName, false))
                .thenThrow(new PackageNotFoundException(errorMessage, missingPackageName));

        assertNotFoundJson(mockMvc.perform(get(requestMapping + "/name/missing?isAur=false")),
                errorDetails,
                errorMessage);
    }

    @Test
    void GetPackageByName_WhenPackageNameIsMissing_Returns404() throws Exception {
        mockMvc.perform(get(requestMapping + "/name/"))
                .andExpect(status().isNotFound());
    }

    @Test
    void GetPackageByName_WithDifferentCase_ReturnsPackage() throws Exception {
        String packageName = "TestPkg";
        ArchPackage archPackage = createTestPackage(1, "testpkg", false);

        when(archPackageService.findArchPackageByPackageName(packageName, false))
                .thenReturn(Optional.of(archPackage));

        assertPackageJson(mockMvc.perform(get(requestMapping + "/name/TestPkg?isAur=false")), archPackage);
    }

    private void assertPackageJson(ResultActions result, ArchPackage archPackage) throws Exception {
        result.andExpect(jsonPath("$.id").value(archPackage.getId()))
                .andExpect(jsonPath("$.packageName").value(archPackage.getPackageName()))
                .andExpect(jsonPath("$.architecture").value(archPackage.getArchitecture()))
                .andExpect(jsonPath("$.description").value(archPackage.getDescription()))
                .andExpect(jsonPath("$.lastUpdate").value(fixedDateValue))
                .andExpect(jsonPath("$.url").value(archPackage.getUrl()))
                .andExpect(jsonPath("$.aur").value(archPackage.isAur()));
    }

    private void assertNotFoundJson(ResultActions result, String errorDetails, String errorMessage) throws Exception {
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details").value(errorDetails))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    private ArchPackage createTestPackage(int id, String name, boolean isAur) {
        return new ArchPackage(id, "any", name, "A test package.",
                fixedDate,
                "https://test.com",
                isAur);
    }
}
