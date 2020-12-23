package com.viettel.ems.kafka;

import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.onap.ves.DCAE;
import org.onap.ves.EventKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap-address}")
    private String bootstrapAddress;

    @Value("${kafka.group-id}")
    private String groupId;

    @Value("${kafka.schema-register}")
    private String schemaRegister;

    @Bean
    public ConsumerFactory<EventKey.Key, DCAE.Event> consumerFactory() {
        //@formatter:off
        return new DefaultKafkaConsumerFactory<>(Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
            ConsumerConfig.GROUP_ID_CONFIG, groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class,
            "schema.registry.url", schemaRegister
        ));
        //@formatter:on
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<EventKey.Key, DCAE.Event> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<EventKey.Key, DCAE.Event>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<EventKey.Key, DCAE.Event> filterKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<EventKey.Key, DCAE.Event>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
