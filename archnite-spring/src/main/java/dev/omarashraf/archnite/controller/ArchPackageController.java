package dev.omarashraf.archnite.controller;

import dev.omarashraf.archnite.model.ArchPackage;
import dev.omarashraf.archnite.service.ArchPackageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/arch")
public class ArchPackageController {
    private final ArchPackageService archPackageService;

    public ArchPackageController(ArchPackageService archPackageService) {
        this.archPackageService = archPackageService;
    }

    @GetMapping("/search")
    public Iterable<ArchPackage> searchArchPackagesBySimilarity(@RequestParam String keyword,
                                                                @RequestParam(defaultValue = "10") int results) {
        keyword = keyword.replace(" ", "-");
        return archPackageService.searchArchPackagesBySimilarity(keyword, results);
    }
}
