package dev.omarashraf.archnite.repository;

import dev.omarashraf.archnite.model.AurPackage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AurPackageRepository extends CrudRepository<AurPackage, Integer> {
    @Query(value = """
        SELECT *
        FROM aur_packages
        WHERE package_name ILIKE CONCAT('%', :keyword, '%')
        ORDER BY similarity(package_name, :keyword) DESC
        LIMIT :numResults
    """, nativeQuery = true)
    List<AurPackage> searchAurPackagesBySimilarity(@Param("keyword") String keyword, @Param("numResults") int numResults);
}
