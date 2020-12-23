package com.viettel.ems.kafka;

import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import lombok.Data;
import org.onap.ves.DCAE;
import org.onap.ves.EventKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Data
@Configuration
public class KafkaProducerConfig {

    @Value(value = "${kafka.bootstrap-address}")
    private String bootstrapAddress;

    @Value(value = "${kafka.schema-register}")
    private String schemaRegisterAddress;

    @Bean
    public ProducerFactory<EventKey.Key, DCAE.Event> producerFactory() {
        // @formatter:off
        return new DefaultKafkaProducerFactory<>(Map.of(
            BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
            KEY_SERIALIZER_CLASS_CONFIG, KafkaProtobufSerializer.class,
            VALUE_SERIALIZER_CLASS_CONFIG, KafkaProtobufSerializer.class,
            "schema.registry.url", schemaRegisterAddress
        ));
        // @formatter:on
    }

    @Bean
    public KafkaTemplate<EventKey.Key, DCAE.Event> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
