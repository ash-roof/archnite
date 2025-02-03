package dev.omarashraf.archnite.controller;

import dev.omarashraf.archnite.model.AurPackage;
import dev.omarashraf.archnite.service.AurPackageService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/aur")
public class AurPackageController {
    private final AurPackageService aurPackageService;

    public AurPackageController(AurPackageService aurPackageService) {
        this.aurPackageService = aurPackageService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<AurPackage>> searchAurPackagesBySimilarity(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
        keyword = keyword.replace(" ", "-");
        return ResponseEntity.ok(aurPackageService.searchArchPackagesBySimilarity(keyword, limit));
    }
}
