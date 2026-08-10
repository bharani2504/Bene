package com.example.bene.util;

import com.example.bene.security.Jwt;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Map;

@Component
public class ServiceUtil {

    @Autowired
    private static Jwt jwt;

    private static ObjectMapper objectMapper = null;

    public static HttpServletRequest getServletRequest() {
        HttpServletRequest request = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes attributes) {
            request = attributes.getRequest();
        }
        return request;
    }

    public static String getUserCrn(Object req) {

        if (req instanceof HttpServletRequest request) {
            String header = request.getHeader("Authorization");
            String token = header.substring(7);
            String userCrn = jwt.extractUserCrn(token);
            return userCrn;
        }

        return null;
    }

    public static ObjectMapper getObjectMapper() {

        if (objectMapper == null) {
            objectMapper = new ObjectMapper();

            SimpleModule clobModule = new SimpleModule();
            clobModule.addSerializer(Clob.class, new JsonSerializer<Clob>() {
                @Override
                public void serialize(Clob value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    try {
                        // Extract text content from the CLOB
                        gen.writeString(value.getSubString(1, (int) value.length()));
                    } catch (SQLException e) {
                        throw new IOException("Failed to serialize CLOB to String", e);
                    }
                }
            });

            objectMapper.registerModule(clobModule);
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }

        return objectMapper;
    }

    public static Map<String, Object> convertPojoToMap(Object pojo) throws Exception {
        ObjectMapper objectMapper = getObjectMapper();
        return objectMapper.convertValue(pojo, new TypeReference<Map<String, Object>>() {
        });
    }


    public static <T> String convertPojoToJson(Object source) {
        var objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(source);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T> T convertJsonToPojo(String json, Class<T> targetClass) {
        try {
            ObjectMapper objectMapper = getObjectMapper();
            return objectMapper.readValue(json, targetClass);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

}
