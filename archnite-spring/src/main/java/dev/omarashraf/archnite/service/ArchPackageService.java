package dev.omarashraf.archnite.service;

import dev.omarashraf.archnite.model.ArchPackage;
import dev.omarashraf.archnite.repository.ArchPackageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArchPackageService {
    private final ArchPackageRepository archPackageRepository;

    public ArchPackageService(ArchPackageRepository archPackageRepository) {
        this.archPackageRepository = archPackageRepository;
    }

    public Optional<ArchPackage> findArchPackageByPackageName(String packageName, Boolean isAur) {
        return archPackageRepository.findArchPackageByPackageNameAndIsAur(packageName, isAur);
    }

    public List<ArchPackage> searchPackagesByName(String keyword, int limit, Boolean isAur) {
        keyword = keyword.replace(" ", "-");
        return archPackageRepository.searchPackagesByName(keyword, limit, isAur);
    }

    public Page<ArchPackage> getAll(Pageable paging, Boolean isAur) {
        if (isAur == null) {
            return archPackageRepository.findAll(paging);
        }
        return archPackageRepository.getAllByIsAur(paging, isAur);
    }
}
