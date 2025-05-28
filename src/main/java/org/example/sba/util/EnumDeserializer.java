package org.example.sba.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class EnumDeserializer<T extends Enum<T>> extends StdDeserializer<T> {

    private final Class<T> enumClass;

    // Default constructor for Jackson
    protected EnumDeserializer() {
        super((Class<T>) null);
        this.enumClass = null; // or throw an exception if required
    }

    public EnumDeserializer(Class<T> enumClass) {
        super(enumClass);
        this.enumClass = enumClass;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (enumClass == null) {
            throw new IOException("Enum class not provided for deserialization");
        }
        String value = p.getText().toUpperCase();
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid value for enum " + enumClass.getSimpleName() + ": " + value, e);
        }
    }
}