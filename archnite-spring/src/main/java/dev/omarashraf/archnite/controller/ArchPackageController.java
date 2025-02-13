package dev.omarashraf.archnite.controller;

import dev.omarashraf.archnite.model.ArchPackage;
import dev.omarashraf.archnite.service.ArchPackageService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("packages/arch")
@Validated
public class ArchPackageController {
    private final ArchPackageService archPackageService;

    public ArchPackageController(ArchPackageService archPackageService) {
        this.archPackageService = archPackageService;
    }

    @GetMapping("/{packageName}")
    public ResponseEntity<?> findPackageByName(@PathVariable String packageName, @RequestParam Boolean isAur) {
        return ResponseEntity.ok(archPackageService.findArchPackageByPackageName(packageName, isAur));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArchPackage>> searchPackagesByName(@RequestParam String keyword,
                                                                  @RequestParam int limit,
                                                                  @RequestParam Boolean isAur) {
        return ResponseEntity.ok(archPackageService.searchPackagesByName(keyword, limit, isAur));
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAll(@RequestParam(defaultValue = "0") @Min(0) int page,
                                                      @RequestParam(defaultValue = "50") @Min(5) @Max(150) int size,
                                                      @RequestParam(required = false) Boolean isAur) {

        Pageable paging = PageRequest.of(page, size);
        Page<ArchPackage> archPackagePage = archPackageService.getAll(paging, isAur);
        List<ArchPackage> packages = archPackagePage.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("packages", packages);
        response.put("currentPage", archPackagePage.getNumber());
        response.put("totalItems", archPackagePage.getTotalElements());
        response.put("totalPages", archPackagePage.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
