package com.howdev.test.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Arrays;

/**
 * KafkaConfig class
 * 在实际使用中，我们可能会要求消费者从不同的集群进行消费，或者采用不同的反序列化方式（该配置要和生产者的序列化方式一样）。
 * 对此，我们可以根据配置创建不同ConsumerFactory对象和ConcurrentkafkaListenerConfigFactory对象。
 * （1）配置文件
 *      spring.kafka.consumer.bootstrap-servers1=192.168.30.14:9092,192.168.30.15:9092
 *      spring.kafka.consumer.bootstrap-servers2=192.168.31.14:9092,192.168.32.15:9092
 * （2）配置类
 *      在指定了不同的集群地址后，我们需要为消费者配置对应的factory。 具体如下
 *
 *  同样地，针对生产者，我们也可以配置生成不同的KafkaTemplate对象进行消息发送。
 * @author haozhifeng
 * @date 2024/03/18
 */
//@Configuration
public class KafkaConfig {
    @Value(value = "${spring.kafka.consumer.bootstrap-servers1}")
    private String brokerServers1;
    @Value(value = "${spring.kafka.consumer.bootstrap-servers2}")
    private String brokerServers2;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, String> consumerFactory1() {
        KafkaProperties.Consumer consumerProperties = kafkaProperties.getConsumer();
        consumerProperties.setBootstrapServers(Arrays.asList(brokerServers1.split(",")));
        return new DefaultKafkaConsumerFactory<>(consumerProperties.buildProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory kafkaListenerContainerFactory1(ConsumerFactory<String, String> consumerFactory1) {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory1);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory2() {
        KafkaProperties.Consumer consumerProperties = kafkaProperties.getConsumer();
        consumerProperties.setBootstrapServers(Arrays.asList(brokerServers2.split(",")));
        return new DefaultKafkaConsumerFactory<>(consumerProperties.buildProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory kafkaListenerContainerFactory2(ConsumerFactory<String, String> consumerFactory2) {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory2);
        return factory;
    }
}
