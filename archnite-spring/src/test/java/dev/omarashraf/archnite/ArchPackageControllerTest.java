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
import org.springframework.data.domain.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
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
    void getPackageByName_WhenPackageExists_WithoutIsAurQueryParam_ReturnsPackage() throws Exception {
        String correctPackageName = "testpkg";
        ArchPackage archPackage = createTestPackage(1, correctPackageName, false);
        when(archPackageService.findArchPackageByPackageName(correctPackageName, null))
                .thenReturn(Optional.of(archPackage));

        assertPackageJson(mockMvc.perform(get(requestMapping + "/name/testpkg")), archPackage);
    }

    @Test
    void getPackageByName_WhenPackageExists_WithIsAurQueryParam_ReturnsPackage() throws Exception {
        String correctPackageName = "testpkg";
        ArchPackage archPackage = createTestPackage(1, correctPackageName, false);
        when(archPackageService.findArchPackageByPackageName(correctPackageName, false))
                .thenReturn(Optional.of(archPackage));

        assertPackageJson(mockMvc.perform(get(requestMapping + "/name/testpkg")
                .param("aur", "false")), archPackage);
    }

    @Test
    void getPackageByName_WhenIsAurIsInvalid_Returns400() throws Exception {
        String packageName = "testpkg";

        mockMvc.perform(get(requestMapping + "/name/" + packageName)
                        .param("aur", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getPackageByName_WithIsAurQueryParam_ReturnsCorrectAurPackage() throws Exception {
        String packageName = "testpkg";

        ArchPackage aurPackage = createTestPackage(1, packageName, true);
        ArchPackage officialPackage = createTestPackage(2, packageName, false);

        when(archPackageService.findArchPackageByPackageName(packageName, true))
                .thenReturn(Optional.of(aurPackage));
        when(archPackageService.findArchPackageByPackageName(packageName, false))
                .thenReturn(Optional.of(officialPackage));

        assertPackageJson(mockMvc.perform(get(requestMapping + "/name/" + packageName)
                .param("aur", "true")), aurPackage);
        assertPackageJson(mockMvc.perform(get(requestMapping + "/name/" + packageName)
                .param("aur", "false")), officialPackage);
    }

    @Test
    void getPackageByName_WhenPackageDoesNotExist_WithoutIsAurQueryParam_Returns404() throws Exception {
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
    void getPackageByName_WhenPackageDoesNotExist_WithIsAurQueryParam_Returns404() throws Exception {
        String missingPackageName = "missing";
        String errorMessage = "Package Not Found";
        String errorDetails = "Package with name: '" + missingPackageName + "' not found";

        when(archPackageService.findArchPackageByPackageName(missingPackageName, false))
                .thenThrow(new PackageNotFoundException(errorMessage, missingPackageName));

        assertNotFoundJson(mockMvc.perform(get(requestMapping + "/name/missing")
                        .param("aur", "false")),
                errorDetails,
                errorMessage);
    }

    @Test
    void getPackageByName_WhenPackageNameIsMissing_Returns404() throws Exception {
        mockMvc.perform(get(requestMapping + "/name/"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPackageByName_WithDifferentCase_ReturnsPackage() throws Exception {
        String packageName = "TestPkg";
        ArchPackage archPackage = createTestPackage(1, "testpkg", false);

        when(archPackageService.findArchPackageByPackageName(packageName, false))
                .thenReturn(Optional.of(archPackage));

        assertPackageJson(mockMvc.perform(get(requestMapping + "/name/TestPkg")
                .param("aur", "false")), archPackage);
    }

    @Test
    void getPackageById_ReturnsCorrectPackage() throws Exception {
        String packageName = "testPkg";
        int packageId = 1;
        ArchPackage archPackage = createTestPackage(packageId, packageName, false);

        when(archPackageService.findArchPackageById(1)).thenReturn(Optional.of(archPackage));

        assertPackageJson(mockMvc.perform(get(requestMapping + "/" + packageId)), archPackage);
    }

    @Test
    void getPackageById_WithNonExistentId_Returns404() throws Exception {
        int missingId = 1;
        String errorMessage = "Package Not Found";
        String errorDetails = "Package with id: " + missingId + " not found";

        when(archPackageService.findArchPackageById(missingId))
                .thenThrow(new PackageNotFoundException(errorMessage, missingId));

        assertNotFoundJson(mockMvc.perform(get(requestMapping + "/" + missingId)), errorDetails, errorMessage);
    }

    @Test
    void getPackageById_WithInvalidIdType_Returns400() throws Exception {
        String invalidId = "invalid";

        mockMvc.perform(get(requestMapping + "/" + invalidId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void searchPackagesByName_WithValidKeyword_ReturnsPackages() throws Exception {
        String keyword = "test";
        List<ArchPackage> packages = List.of(
                createTestPackage(1, "test-pkg", false),
                createTestPackage(2, "test-pkg2", true)
        );

        when(archPackageService.searchPackagesByName("test", 10, null))
                .thenReturn(packages);

        mockMvc.perform(get(requestMapping + "/search")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].packageName").value("test-pkg"))
                .andExpect(jsonPath("$[1].packageName").value("test-pkg2"));
    }

    @Test
    void searchPackagesByName_WithAllParameters_ReturnsPackages() throws Exception {
        String keyword = "test";
        int limit = 5;
        boolean isAur = true;
        List<ArchPackage> packages = List.of(createTestPackage(1, "test-pkg", true));

        when(archPackageService.searchPackagesByName("test", limit, isAur))
                .thenReturn(packages);

        mockMvc.perform(get(requestMapping + "/search")
                        .param("keyword", keyword)
                        .param("limit", String.valueOf(limit))
                        .param("aur", String.valueOf(isAur)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].aur").value(true));
    }

    @Test
    void searchPackagesByName_WithMinLimit_ReturnsPackages() throws Exception {
        String keyword = "test";
        int limit = 1;

        when(archPackageService.searchPackagesByName("test", limit, null))
                .thenReturn(List.of(createTestPackage(1, "test-pkg", false)));

        mockMvc.perform(get(requestMapping + "/search")
                        .param("keyword", keyword)
                        .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchPackagesByName_WithMaxLimit_ReturnsPackages() throws Exception {
        String keyword = "test";
        int limit = 50;
        when(archPackageService.searchPackagesByName("test", limit, null))
                .thenReturn(List.of(createTestPackage(1, "test-pkg", false)));

        mockMvc.perform(get(requestMapping + "/search")
                        .param("keyword", keyword)
                        .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchPackagesByName_WithIsAurFalse_ReturnsOfficialPackages() throws Exception {
        boolean isAur = false;
        when(archPackageService.searchPackagesByName(anyString(), anyInt(), eq(isAur)))
                .thenReturn(List.of(createTestPackage(1, "test-pkg", false)));

        mockMvc.perform(get(requestMapping + "/search")
                        .param("keyword", "test")
                        .param("aur", String.valueOf(isAur)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].aur").value(false));
    }

    @Test
    void searchPackagesByName_MissingKeyword_Returns400() throws Exception {
        String errorMessage = "Bad Request";
        String errorDetails = "Required request parameter 'keyword' is not present";

        mockMvc.perform(get(requestMapping + "/search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.details").value(errorDetails))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void searchPackagesByName_InvalidIsAurParam_Returns400() throws Exception {
        mockMvc.perform(get(requestMapping + "/search")
                        .param("keyword", "test")
                        .param("aur", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    void searchPackagesByName_NoPackagesFound_ReturnsEmptyList() throws Exception {
        when(archPackageService.searchPackagesByName(anyString(), anyInt(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get(requestMapping + "/search")
                        .param("keyword", "nonexistent"))
                .andExpect(status().isOk()) .andExpect(jsonPath("$", empty()));
    }

    @Test
    void getAll_WithDefaultParameters_ReturnsPaginatedResults() throws Exception {
        List<ArchPackage> packages = List.of(
                createTestPackage(1, "package1", false),
                createTestPackage(2, "package2", true)
        );
        Page<ArchPackage> page = new PageImpl<>(packages);
        when(archPackageService.getAll(any(Pageable.class), eq(null)))
                .thenReturn(page);

        mockMvc.perform(get(requestMapping))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packages", hasSize(2)))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(archPackageService).getAll(
                PageRequest.of(0, 50, Sort.by(Sort.Direction.ASC, "packageName")),
                null
        );
    }


    @Test
    void getAll_WithCustomPagination_ReturnsCorrectPage() throws Exception {
        Pageable pageable = PageRequest.of(2, 20, Sort.by(Sort.Direction.DESC, "lastUpdate"));
        Page<ArchPackage> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(archPackageService.getAll(any(Pageable.class), eq(false)))
                .thenReturn(page);

        mockMvc.perform(get(requestMapping)
                        .param("page", "2")
                        .param("size", "20")
                        .param("aur", "false")
                        .param("order", "DESC")
                        .param("sort", "LASTUPDATE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(2))
                .andExpect(jsonPath("$.totalItems").value(0));

        verify(archPackageService).getAll(pageable, false);
    }

    @Test
    void getAll_WithAurFilterTrue_ReturnsAurPackages() throws Exception {
        ArchPackage aurPackage = createTestPackage(1, "aur-pkg", true);
        Page<ArchPackage> page = new PageImpl<>(List.of(aurPackage));
        when(archPackageService.getAll(any(Pageable.class), eq(true)))
                .thenReturn(page);

        mockMvc.perform(get(requestMapping)
                        .param("aur", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packages[0].aur").value(true));
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
