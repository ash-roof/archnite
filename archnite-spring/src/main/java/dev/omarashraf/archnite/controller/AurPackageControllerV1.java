package dev.omarashraf.archnite.controller;

import dev.omarashraf.archnite.model.AurPackage;
import dev.omarashraf.archnite.service.AurPackageService;
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
@RequestMapping("/api/v1/aur")
public class AurPackageControllerV1 {
    private final AurPackageService aurPackageService;

    public AurPackageControllerV1(AurPackageService aurPackageService) {
        this.aurPackageService = aurPackageService;
    }

    @GetMapping("/{packageName}")
    public ResponseEntity<?> getAurPackageByName(@PathVariable String packageName) {
        AurPackage aurPackage = aurPackageService.getAurPackageByPackageName(packageName);
        if (aurPackage == null) {
            Map<String, String> errorResponse = Map.of(
                    "timestamp", String.valueOf(Instant.now()),
                    "status", "404",
                    "error", "Not Found",
                    "path", "/api/v1/aur/" + packageName
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(errorResponse);
        }
        return ResponseEntity.ok(aurPackage);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AurPackage>> searchAurPackagesBySimilarity(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
        keyword = keyword.replace(" ", "-");
        return ResponseEntity.ok(aurPackageService.searchArchPackagesBySimilarity(keyword, limit));
    }
}
