package com.viettel.ems.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class KafkaTopicConfig {

    @Value(value = "${kafka.bootstrap-address}")
    private String bootstrapAddress;

    private final long operationTimeout = 10;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topicEvent() {
        log.info("Creating notification topic");
        return TopicBuilder.name("notification").partitions(10).compact().build();
    }

    @Bean
    public NewTopic topicFault() {
        return TopicBuilder.name("fault").partitions(10).compact().build();
    }

    private void addTopicsIfNeeded(AdminClient adminClient, Collection<NewTopic> topics) {
        if (topics.size() > 0) {
            Map<String, NewTopic> topicNameToTopic = new HashMap<>();
            topics.forEach(t -> topicNameToTopic.compute(t.name(), (k, v) -> t));
            var topicInfo = adminClient.describeTopics(
                topics.stream().map(NewTopic::name).collect(Collectors.toList()));
            List<NewTopic> topicsToAdd = new ArrayList<>();
            Map<String, NewPartitions> topicsToModify = checkPartitions(topicNameToTopic, topicInfo, topicsToAdd);
            if (topicsToAdd.size() > 0) {
                addTopics(adminClient, topicsToAdd);
            }
            // if (topicsToModify.size() > 0) {
            //     modifyTopics(adminClient, topicsToModify);
            // }
        }
    }

    private void addTopics(AdminClient adminClient, List<NewTopic> topicsToAdd) {
        try {
            adminClient.createTopics(topicsToAdd).all().get();
        } catch (Exception e) {
            log.error("Error adding topics:", e);
        }
    }

    private Map<String, NewPartitions> checkPartitions(
        Map<String, NewTopic> topicNameToTopic, DescribeTopicsResult topicInfo, List<NewTopic> topicsToAdd
    ) {
        Map<String, NewPartitions> topicsToModify = new HashMap<>();
        topicInfo.values().forEach((n, f) -> {
            NewTopic topic = topicNameToTopic.get(n);
            try {
                TopicDescription topicDescription = f.get(this.operationTimeout, TimeUnit.SECONDS);
                if (topic.numPartitions() < topicDescription.partitions().size()) {
                    log.info("Topic '{}' exists but has a different partition count: {} not {}", n,
                        topicDescription.partitions().size(), topic.numPartitions());
                } else if (topic.numPartitions() > topicDescription.partitions().size()) {
                    log.info("Topic '{}' exists but has a different partition count: {} not {}, increasing "
                        + "if the broker supports it", n, topicDescription.partitions().size(), topic.numPartitions());
                    topicsToModify.put(n, NewPartitions.increaseTo(topic.numPartitions()));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (TimeoutException e) {
                throw new KafkaException("Timed out waiting to get existing topics", e);
            } catch (ExecutionException e) {
                topicsToAdd.add(topic);
            }
        });
        return topicsToModify;
    }
}
