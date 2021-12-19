package iob.data.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;

@Converter
public class MapToStringConverter implements AttributeConverter<Map<String, Object>, String> {
    private final ObjectMapper jackson;

    public MapToStringConverter() {
        this.jackson = new ObjectMapper();
    }

    @Override
    public String convertToDatabaseColumn(Map<String, Object> jsonFromEntity) {
        try {
            return this.jackson
                    .writeValueAsString(jsonFromEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String stringFromDb) {
        try {
            return this.jackson.readValue(stringFromDb, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}