package com.viettel.ems.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.viettel.ems.scheduler.ScriptConfig;

import java.io.IOException;
import java.util.List;

public class ScheduleConfigSerializer extends StdSerializer<ScriptConfig> {

    public ScheduleConfigSerializer() {
        this(null);
    }

    protected ScheduleConfigSerializer(Class<ScriptConfig> t) {
        super(t);
    }

    @Override
    public void serialize(ScriptConfig value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("device_type", value.getDeviceType());
        gen.writeBooleanField("stop_on_error", value.isStopOnError());

        gen.writeArrayFieldStart("list_ne");
        writeList(gen, value.getNeIpList(), "ne_ip");
        gen.writeEndArray();

        gen.writeArrayFieldStart("list_command");
        writeList(gen, value.getCommandList(), "command");
        gen.writeEndArray();

        gen.writeEndObject();
    }

    private void writeList(JsonGenerator gen, List<String> list, String name) throws IOException {
        for (var s : list) {
            gen.writeStartObject();
            gen.writeStringField(name, s);
            gen.writeEndObject();
        }
    }
}
