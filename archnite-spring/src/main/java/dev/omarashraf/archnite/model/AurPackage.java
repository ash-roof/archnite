package dev.omarashraf.archnite.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "aur_packages")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AurPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aur_packages_id_gen")
    @SequenceGenerator(name = "aur_packages_id_gen", sequenceName = "aur_packages_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "package_name", nullable = false, length = Integer.MAX_VALUE)
    private String packageName;

    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "last_update", nullable = false)
    private OffsetDateTime lastUpdate;

    @Column(name = "url", length = Integer.MAX_VALUE)
    private String url;
}
