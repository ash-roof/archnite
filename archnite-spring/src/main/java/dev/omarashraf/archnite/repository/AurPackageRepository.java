package dev.omarashraf.archnite.repository;

import dev.omarashraf.archnite.model.AurPackage;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AurPackageRepository extends JpaRepository<AurPackage, Integer> {
    @Query(value = """
        SELECT *
        FROM aur_packages
        WHERE package_name ILIKE '%' || :keyword || '%'
        ORDER BY
            CASE WHEN package_name ILIKE :keyword THEN 0 ELSE 1 END,
            CASE WHEN package_name ILIKE :keyword || '%' THEN 0 ELSE 1 END,
            similarity(package_name, :keyword) DESC,
            length(package_name)
        LIMIT :limit;
    """, nativeQuery = true)
    List<AurPackage> searchAurPackagesBySimilarity(@Param("keyword") String keyword, @Param("limit") int limit);

    AurPackage getAurPackageByPackageName(@NotNull String packageName);

    Page<AurPackage> findAll(Pageable paging);
}
