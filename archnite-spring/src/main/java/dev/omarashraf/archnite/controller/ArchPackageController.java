package dev.omarashraf.archnite.controller;

import dev.omarashraf.archnite.enums.SortField;
import dev.omarashraf.archnite.exception.PackageNotFoundException;
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

    @GetMapping("/name/{packageName}")
    public ResponseEntity<?> getPackageByName(@PathVariable String packageName, @RequestParam(required = false) Boolean isAur) {
        ArchPackage archPackage = archPackageService.findArchPackageByPackageName(packageName, isAur)
                .orElseThrow(() -> new PackageNotFoundException("Package Not Found", packageName));
        return ResponseEntity.ok(archPackage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPackageById(@PathVariable Integer id) {
        ArchPackage archPackage = archPackageService.findArchPackageById(id)
                .orElseThrow(() -> new PackageNotFoundException("Package Not Found", id));
        return ResponseEntity.ok(archPackage);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArchPackage>> searchPackagesByName(@RequestParam @NotEmpty @NotNull String keyword,
                                                                  @RequestParam(defaultValue = "10", required = false)
                                                                  @Min(1) @Max(50) Integer limit,
                                                                  @RequestParam(required = false) Boolean isAur) {
        return ResponseEntity.ok(archPackageService.searchPackagesByName(keyword, limit, isAur));
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAll(@RequestParam(defaultValue = "0", required = false) @Min(0) Integer page,
                                                      @RequestParam(defaultValue = "50", required = false)
                                                      @Min(5) @Max(150) Integer size,
                                                      @RequestParam(required = false) Boolean aur,
                                                      @RequestParam(required = false, defaultValue = "ASC")
                                                          Sort.Direction order,
                                                      @RequestParam(required = false, defaultValue = "PACKAGENAME")
                                                          SortField sort) {

        String correctedSort;
        if (String.valueOf(sort).equalsIgnoreCase("packageName")) {
            correctedSort = "packageName";
        } else {
            correctedSort = "lastUpdate";
        }

        if (String.valueOf(order).equalsIgnoreCase("asc")) {
            order = Sort.Direction.ASC;
        } else {
            order = Sort.Direction.DESC;
        }

        Sort sortBy = Sort.by(order, correctedSort);
        Pageable paging = PageRequest.of(page, size, sortBy);
        Page<ArchPackage> archPackagePage = archPackageService.getAll(paging, aur);
        List<ArchPackage> packages = archPackagePage.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("packages", packages);
        response.put("currentPage", archPackagePage.getNumber());
        response.put("totalItems", archPackagePage.getTotalElements());
        response.put("totalPages", archPackagePage.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
