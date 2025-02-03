package dev.omarashraf.archnite.controller;

import dev.omarashraf.archnite.model.ArchPackage;
import dev.omarashraf.archnite.service.ArchPackageService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/arch")
public class ArchPackageController {
    private final ArchPackageService archPackageService;

    public ArchPackageController(ArchPackageService archPackageService) {
        this.archPackageService = archPackageService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArchPackage>> searchArchPackagesBySimilarity(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
        keyword = keyword.replace(" ", "-");
        return ResponseEntity.ok(archPackageService.searchArchPackagesBySimilarity(keyword, limit));
    }
}
