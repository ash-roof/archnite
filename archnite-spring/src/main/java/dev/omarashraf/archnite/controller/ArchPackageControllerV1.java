package dev.omarashraf.archnite.controller;

import dev.omarashraf.archnite.exception.InvalidRequestException;
import dev.omarashraf.archnite.model.ArchPackage;
import dev.omarashraf.archnite.service.ArchPackageService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/arch")
public class ArchPackageControllerV1 {
    private final ArchPackageService archPackageService;

    public ArchPackageControllerV1(ArchPackageService archPackageService) {
        this.archPackageService = archPackageService;
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllArchPackages(@RequestParam(defaultValue = "0") @Min(0) int page,
                                                                  @RequestParam(defaultValue = "10") @Min(5) @Max(100) int size,
                                                                  @RequestParam(defaultValue = "packageName") String sort,
                                                                  @RequestParam(defaultValue = "asc") String order) {

        Map<String, String> allowedSortFields = Map.of(
                "packagename", "packageName",
                "lastupdate", "lastUpdate"
        );
        String normalizedSortField = sort.toLowerCase();

        if (!allowedSortFields.containsKey(normalizedSortField)) {
            throw new InvalidRequestException("Invalid sort field: " + sort + ". Allowed values: " + allowedSortFields.values());
        }

        Sort.Direction direction;
        if (order.equalsIgnoreCase("asc")) {
            direction = Sort.Direction.ASC;
        } else if (order.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        } else {
            throw new InvalidRequestException("Invalid order parameter: " + order + ". Allowed values: [asc, desc]");
        }

        sort = allowedSortFields.get(normalizedSortField);
        Sort sortBy = Sort.by(direction, sort);
        Pageable paging = PageRequest.of(page, size, sortBy);
        Page<ArchPackage> archPackagePage = archPackageService.findAll(paging);
        List<ArchPackage> packages = archPackagePage.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("packages", packages);
        response.put("currentPage", archPackagePage.getNumber());
        response.put("totalItems", archPackagePage.getTotalElements());
        response.put("totalPages", archPackagePage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{packageName}")
    public ResponseEntity<?> getArchPackageByName(@PathVariable String packageName) {
        return ResponseEntity.ok(archPackageService.getArchPackageByPackageName(packageName));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArchPackage>> searchArchPackagesBySimilarity(
            @RequestParam @NotNull @NotEmpty String keyword,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
        return ResponseEntity.ok(archPackageService.searchArchPackagesBySimilarity(keyword, limit));
    }
}
