package dev.omarashraf.archnite.service;

import dev.omarashraf.archnite.model.ArchPackage;
import dev.omarashraf.archnite.repository.ArchPackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArchPackageService {
    private final ArchPackageRepository archPackageRepository;

    public ArchPackageService(ArchPackageRepository archPackageRepository) {
        this.archPackageRepository = archPackageRepository;
    }

    public List<ArchPackage> searchArchPackagesBySimilarity(String keyword, int limit) {
        return archPackageRepository.searchArchPackagesBySimilarity(keyword, limit);
    }
}
