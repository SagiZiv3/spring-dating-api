package iob.data.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;

@Converter
public class MapToStringConverter implements AttributeConverter<Map<String, Object>, String> {
    private final ObjectMapper objectMapper;

    public MapToStringConverter() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String convertToDatabaseColumn(Map<String, Object> jsonFromEntity) {
        try {
            return this.objectMapper.writeValueAsString(jsonFromEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String stringFromDb) {
        try {
            return this.objectMapper.readValue(stringFromDb, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}