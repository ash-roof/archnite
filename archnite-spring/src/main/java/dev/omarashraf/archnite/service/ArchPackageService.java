package dev.omarashraf.archnite.service;

import dev.omarashraf.archnite.model.ArchPackage;
import dev.omarashraf.archnite.repository.ArchPackageRepository;
import org.springframework.stereotype.Service;

@Service
public class ArchPackageService {
    private final ArchPackageRepository archPackageRepository;

    public ArchPackageService(ArchPackageRepository archPackageRepository) {
        this.archPackageRepository = archPackageRepository;
    }

    public Iterable<ArchPackage> findAll() {
        return archPackageRepository.findAll();
    }

    public Iterable<ArchPackage> searchArchPackagesBySimilarity(String keyword, int numResults) {
        return archPackageRepository.searchArchPackagesBySimilarity(keyword, numResults);
    }
}
