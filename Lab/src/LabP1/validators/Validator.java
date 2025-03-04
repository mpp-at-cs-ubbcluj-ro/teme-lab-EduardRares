package LabP1.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}