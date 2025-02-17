package dev.omarashraf.archnite.exception;

import dev.omarashraf.archnite.model.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(PackageNotFoundException.class)
    public ResponseEntity<?> handlePackageNotFoundException(PackageNotFoundException ex) {
        String details;
        Object identifier = ex.getMissingPackageIdentifier();

        if (identifier instanceof String) {
            details = "Package with name: '" + identifier + "' not found";
        } else {
            details = "Package with id: " + identifier + " not found";
        }

        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), details, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        // String field extracts the constraint violation property path by removing the function name
        // preceding the '.', for example "getAll.size" becomes "size"
        Map<String, String> details = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString().substring(violation.getPropertyPath().toString().lastIndexOf(".") + 1),
                        violation -> violation.getMessage(),
                        (existing, replacement) -> existing
                ));

        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), details, "Bad Request");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String parameterName = ex.getName();
        String providedValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        Class<?> requiredType = ex.getRequiredType();

        String details;

        if (requiredType != null && requiredType.isEnum()) {
            Object[] enumConstants = requiredType.getEnumConstants();
            String allowedValues = Arrays.stream(enumConstants)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            details = String.format(
                    "Invalid value '%s' for parameter '%s'. Allowed values are: %s (case-insensitive)",
                    providedValue,
                    parameterName,
                    allowedValues
            );
        } else {
            // Default message for non-enum types
            String expectedType = requiredType != null ?
                    requiredType.getSimpleName() : "unknown";

            details = String.format(
                    "Parameter '%s' must be of type %s. Provided value: '%s'",
                    parameterName,
                    expectedType,
                    providedValue
            );
        }

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                details,
                "Bad Request"
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException ex) {
        String details = "The requested endpoint '" + ex.getResourcePath() + "' does not exist.";
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), details, "Not Found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        String details = "The requested endpoint GET'" + ex.getRequestURL() + "' does not exist.";
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), details, "Not Found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingRequestParameterException(MissingServletRequestParameterException ex) {
        String details = "Required request parameter '" + ex.getParameterName() + "' is not present";
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), details, "Bad Request");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        System.out.println("Exception thrown: " + ex.getClass().getName());
        ex.printStackTrace();

        String details = ex.getMessage();
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), details, "Internal Server Error");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
