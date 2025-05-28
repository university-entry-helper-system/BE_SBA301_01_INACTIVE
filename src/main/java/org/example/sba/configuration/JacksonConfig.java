package org.example.sba.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.sba.util.EnumDeserializer;
import org.example.sba.util.Gender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Register JavaTimeModule for LocalDate support
        mapper.registerModule(new JavaTimeModule());
        // Optionally, disable writing dates as timestamps to ensure ISO-8601 format
        mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // Register custom deserializer for enums if needed
        com.fasterxml.jackson.databind.module.SimpleModule module = new com.fasterxml.jackson.databind.module.SimpleModule();
        module.addDeserializer(Gender.class, new EnumDeserializer<>(Gender.class));
        // Add other enum deserializers if needed
        // module.addDeserializer(AccountStatus.class, new EnumDeserializer<>(AccountStatus.class));
        mapper.registerModule(module);
        return mapper;
    }
}