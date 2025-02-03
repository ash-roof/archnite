package dev.omarashraf.archnite.controller;

import dev.omarashraf.archnite.model.ArchPackage;
import dev.omarashraf.archnite.service.ArchPackageService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/arch")
public class ArchPackageControllerV1 {
    private final ArchPackageService archPackageService;

    public ArchPackageControllerV1(ArchPackageService archPackageService) {
        this.archPackageService = archPackageService;
    }

    @GetMapping("/{packageName}")
    public ResponseEntity<?> getAurPackageByName(@PathVariable String packageName) {
        ArchPackage archPackage = archPackageService.getArchPackageByPackageName(packageName);
        if (archPackage == null) {
            Map<String, String> errorResponse = Map.of(
                    "timestamp", String.valueOf(Instant.now()),
                    "status", "404",
                    "error", "Not Found",
                    "path", "/api/v1/arch/" + packageName
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(errorResponse);
        }
        return ResponseEntity.ok(archPackage);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArchPackage>> searchArchPackagesBySimilarity(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
        keyword = keyword.replace(" ", "-");
        return ResponseEntity.ok(archPackageService.searchArchPackagesBySimilarity(keyword, limit));
    }
}
