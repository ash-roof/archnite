package dev.omarashraf.archnite.model;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;

import java.time.Instant;

public class Package {
    @Id
    private long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String architecture;
    @NotEmpty
    private String description;
    @NotEmpty
    private String version;
    @NotEmpty
    private Instant lastUpdate;

    public Package() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
