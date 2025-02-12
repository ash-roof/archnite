package dev.omarashraf.archnite.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "arch_packages")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ArchPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "arch_packages_id_gen")
    @SequenceGenerator(name = "arch_packages_id_gen", sequenceName = "arch_packages_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 10)
    @Column(name = "architecture", length = 10)
    private String architecture;

    @Column(name = "package_name", nullable = false, length = Integer.MAX_VALUE)
    private String packageName;

    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "last_update", nullable = false)
    private OffsetDateTime lastUpdate;

    @Column(name = "url", length = Integer.MAX_VALUE)
    private String url;
}
