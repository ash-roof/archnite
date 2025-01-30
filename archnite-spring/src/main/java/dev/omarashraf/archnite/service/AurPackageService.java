package dev.omarashraf.archnite.service;

import dev.omarashraf.archnite.model.AurPackage;
import dev.omarashraf.archnite.repository.AurPackageRepository;
import org.springframework.stereotype.Service;

@Service
public class AurPackageService {
    private final AurPackageRepository aurPackageRepository;

    public AurPackageService(AurPackageRepository aurPackageRepository) {
        this.aurPackageRepository = aurPackageRepository;
    }

    public Iterable<AurPackage> findAll() {
        return aurPackageRepository.findAll();
    }

    public Iterable<AurPackage> searchArchPackagesBySimilarity(String keyword, int numResults) {
        return aurPackageRepository.searchAurPackagesBySimilarity(keyword, numResults);
    }
}
