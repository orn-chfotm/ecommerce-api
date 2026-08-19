package com.build.ecommerce.domain.address.dto.jackson;

import com.build.ecommerce.domain.address.enums.AddressType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AddressTypeDeserializer extends JsonDeserializer<AddressType> {

    @Override
    public AddressType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.isBlank()) {
            return null;
        }
        for (AddressType type : AddressType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
