package model.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.sql.Date;
import java.time.LocalDate;

@Converter(autoApply = true)
public class SQLiteLocalDateConverter implements AttributeConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseColumn(LocalDate attribute) {
        // Convert LocalDate to SQLite-compatible format (ISO-8601 string format)
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public LocalDate convertToEntityAttribute(String dbData) {
        // Convert SQLite string back to LocalDate
        return dbData == null ? null : LocalDate.parse(dbData);
    }
}