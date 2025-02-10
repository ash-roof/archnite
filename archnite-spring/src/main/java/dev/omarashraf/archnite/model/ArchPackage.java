package dev.omarashraf.archnite.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

@Entity
@Table(name = "arch_packages")
public class ArchPackage {

    public ArchPackage() {
    }

    public ArchPackage(Integer id, String architecture, String packageName, String description, OffsetDateTime lastUpdate, String url) {
        this.id = id;
        this.architecture = architecture;
        this.packageName = packageName;
        this.description = description;
        this.lastUpdate = lastUpdate;
        this.url = url;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "arch_packages_id_gen")
    @SequenceGenerator(name = "arch_packages_id_gen", sequenceName = "arch_packages_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 10)
    @Column(name = "architecture", length = 10)
    private String architecture;

    @NotNull
    @Column(name = "package_name", nullable = false, length = Integer.MAX_VALUE)
    private String packageName;

    @NotNull
    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "last_update", nullable = false)
    private OffsetDateTime lastUpdate;

    @Column(name = "url", length = Integer.MAX_VALUE)
    private String url;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(OffsetDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "{Name: " + packageName
                + "\nArchitecture: " + architecture
                + "\nDescription: " + description
                + "\nLast Update: " + lastUpdate
                + "\nURL: " + url + "}";
    }
}