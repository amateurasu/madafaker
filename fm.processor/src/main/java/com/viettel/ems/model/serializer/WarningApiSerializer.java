package com.viettel.ems.model.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class WarningApiSerializer extends StdSerializer<Object> {

    public WarningApiSerializer() {
        this(null);
    }

    protected WarningApiSerializer(Class<Object> t) {
        super(t);
    }

    @Override
    public void serialize(
        Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider
    ) throws IOException {

    }
}
