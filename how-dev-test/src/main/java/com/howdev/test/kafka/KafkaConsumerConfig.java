package com.howdev.test.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * KafkaConsumerConfig class
 *
 * @author haozhifeng
 * @date 2024/03/18
 */
// @Service
public class KafkaConsumerConfig {

    @KafkaListener(containerFactory = "kafkaListenerContainerFactory1", topics = "test-topic", groupId = "group1")
    public void consumerServer1(ConsumerRecord<String, String> record) {
        System.out.printf("topic = %s, offset = %d, value = %s, key = %s%n", record.topic(), record.offset(), record.value(), record.key());
    }

    @KafkaListener(containerFactory = "kafkaListenerContainerFactory2", topics = "test-topic", groupId = "group1")
    public void consumerServer2(ConsumerRecord<String, String> record) {
        System.out.printf("topic = %s, offset = %d, value = %s, key = %s%n", record.topic(), record.offset(), record.value(), record.key());
    }
}
