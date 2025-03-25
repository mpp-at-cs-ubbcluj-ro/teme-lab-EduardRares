package ro.mpp2025.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}