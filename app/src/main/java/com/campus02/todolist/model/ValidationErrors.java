package com.campus02.todolist.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValidationErrors {
    private final List<ValidationError> errors;

    public ValidationErrors(List<ValidationError> errors) {
        this.errors = errors;
    }

    public ValidationErrors(String singleError) {
        ValidationError unexpectedError = new ValidationError();
        unexpectedError.property = "";
        unexpectedError.message = singleError;

        errors = new ArrayList<>();
        errors.add(unexpectedError);
    }

    public Stream<ValidationError> allErrors() { return errors.stream(); }

    public Stream<ValidationError> errorsWithProperties() { return errors.stream().filter(error -> !error.property.equals("")); }

    public Stream<ValidationError> errorsWithoutProperties() { return errors.stream().filter(error -> error.property.equals("")); }

    public String allErrorsAsString() { return asString(allErrors()); }
    public String errorsWithPropertiesAsString() { return asString(errorsWithProperties()); }
    public String errorsWithoutPropertiesAsString() { return asString(errorsWithoutProperties()); }

    private static String asString(Stream<ValidationError> errors) {
        return errors.map(error -> error.message).collect(Collectors.joining("\n"));
    }
}
