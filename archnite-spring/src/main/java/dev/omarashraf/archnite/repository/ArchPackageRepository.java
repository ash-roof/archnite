package dev.omarashraf.archnite.repository;

import dev.omarashraf.archnite.model.ArchPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArchPackageRepository extends JpaRepository<ArchPackage, Integer> {
    @Query(value = """
        SELECT *
        FROM packages
        WHERE package_name ILIKE '%' || :keyword || '%'
        AND (:isAur IS NULL OR is_aur = :isAur)
        ORDER BY
            CASE WHEN package_name ILIKE :keyword THEN 0 ELSE 1 END,
            CASE WHEN package_name ILIKE :keyword || '%' THEN 0 ELSE 1 END,
            similarity(package_name, :keyword) DESC,
            length(package_name)
        LIMIT :limit;
""", nativeQuery = true)
    List<ArchPackage> searchPackagesByName(
            @Param("keyword") String keyword,
            @Param("limit") int limit,
            @Param("isAur") Boolean isAur);

    Optional<ArchPackage> findArchPackageByPackageNameAndIsAur(String packageName, Boolean isAur);

    Page<ArchPackage> getAllByIsAur(Pageable pageable, Boolean isAur);
}
