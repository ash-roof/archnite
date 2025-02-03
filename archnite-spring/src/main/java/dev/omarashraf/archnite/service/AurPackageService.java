package dev.omarashraf.archnite.service;

import dev.omarashraf.archnite.model.AurPackage;
import dev.omarashraf.archnite.repository.AurPackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AurPackageService {
    private final AurPackageRepository aurPackageRepository;

    public AurPackageService(AurPackageRepository aurPackageRepository) {
        this.aurPackageRepository = aurPackageRepository;
    }

    public List<AurPackage> searchArchPackagesBySimilarity(String keyword, int limit) {
        return aurPackageRepository.searchAurPackagesBySimilarity(keyword, limit);
    }
}
