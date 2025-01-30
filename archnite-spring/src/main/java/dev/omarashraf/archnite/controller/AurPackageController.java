package dev.omarashraf.archnite.controller;

import dev.omarashraf.archnite.model.AurPackage;
import dev.omarashraf.archnite.service.AurPackageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aur")
public class AurPackageController {
    private final AurPackageService aurPackageService;

    public AurPackageController(AurPackageService aurPackageService) {
        this.aurPackageService = aurPackageService;
    }

    @GetMapping
    public Iterable<AurPackage> findAll() {
        return aurPackageService.findAll();
    }

    @GetMapping("/search")
    public Iterable<AurPackage> searchAurPackagesBySimilarity(@RequestParam String keyword,
                                                              @RequestParam(defaultValue = "10") int results ) {
        keyword = keyword.replace(" ", "-");
        return aurPackageService.searchArchPackagesBySimilarity(keyword, results);
    }
}
