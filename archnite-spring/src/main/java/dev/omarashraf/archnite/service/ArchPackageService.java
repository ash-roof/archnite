package dev.omarashraf.archnite.service;

import dev.omarashraf.archnite.exception.ResourceNotFoundException;
import dev.omarashraf.archnite.model.ArchPackage;
import dev.omarashraf.archnite.repository.ArchPackageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArchPackageService {
    private final ArchPackageRepository archPackageRepository;

    public ArchPackageService(ArchPackageRepository archPackageRepository) {
        this.archPackageRepository = archPackageRepository;
    }

    public ArchPackage getArchPackageByPackageName(String packageName) {
        ArchPackage archPackage = archPackageRepository.getArchPackageByPackageName(packageName);
        if (archPackage == null) {
            throw new ResourceNotFoundException("Package not found: " + packageName);
        }
        return archPackage;
    }

    public List<ArchPackage> searchArchPackagesBySimilarity(String keyword, int limit) {
        keyword = keyword.replace(" ", "-");
        return archPackageRepository.searchArchPackagesBySimilarity(keyword, limit);
    }

    public Page<ArchPackage> findAll(Pageable paging) {
        return archPackageRepository.findAll(paging);
    }
}
