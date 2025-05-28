package org.example.sba.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CustomDateDeserializer extends JsonDeserializer<Date> {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String date = p.getText();
        try {
            return new Date(formatter.parse(date).getTime());
        } catch (ParseException e) {
            throw new IOException("Invalid date format: " + date, e);
        }
    }
}
