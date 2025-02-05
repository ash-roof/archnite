package dev.omarashraf.archnite.service;

import dev.omarashraf.archnite.exception.ResourceNotFoundException;
import dev.omarashraf.archnite.model.AurPackage;
import dev.omarashraf.archnite.repository.AurPackageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AurPackageService {
    private final AurPackageRepository aurPackageRepository;

    public AurPackageService(AurPackageRepository aurPackageRepository) {
        this.aurPackageRepository = aurPackageRepository;
    }

    public AurPackage getAurPackageByPackageName(String packageName) {
        AurPackage aurPackage = aurPackageRepository.getAurPackageByPackageName(packageName);
        if (aurPackage == null) {
            throw new ResourceNotFoundException("Package not found: " + packageName);
        }
        return aurPackage;
    }

    public List<AurPackage> searchArchPackagesBySimilarity(String keyword, int limit) {
        keyword = keyword.replace(" ", "-");
        return aurPackageRepository.searchAurPackagesBySimilarity(keyword, limit);
    }

    public Page<AurPackage> findAll(Pageable paging) {
        return aurPackageRepository.findAll(paging);
    }
}
