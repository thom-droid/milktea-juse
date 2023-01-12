package com.example.juse.security.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonJsonSerializerConfig {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Bean
    public com.fasterxml.jackson.databind.Module jsonSerializer(){

        SimpleModule module = new SimpleModule();

        // serializer
        module.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm:ss").format(value));
            }
        });

        module.addSerializer(LocalDate.class, new LocalDateSerializer(FORMATTER));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(FORMATTER));


        // deser
        module.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                return LocalDateTime.parse(p.getValueAsString(), DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm:ss"));
            }
        });

        return module;
    }

}

