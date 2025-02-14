package dev.omarashraf.archnite.exception;

import lombok.Getter;

public class PackageNotFoundException extends RuntimeException {
  @Getter
  Object missingPackageIdentifier;
    public PackageNotFoundException(String message, Object missingPackageIdentifier) {
        super(message);
        this.missingPackageIdentifier = missingPackageIdentifier;
    }
}
