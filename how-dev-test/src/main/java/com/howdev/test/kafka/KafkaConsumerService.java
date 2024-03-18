package com.howdev.test.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

/**
 * KafkaConsumerService class
 * 一些原理知识：
 * （1）org.springframework.kafka.listener.KafkaMessageListenerContainer.ListenerConsumer#run
 * 项目启动后这个run方法会不停从kafka服务器broke拉取（poll）消息回来消费
 *  其中，this.pollAndInvoke()包含核心逻辑：
 *  1）this.doPoll(); ## this.consumer.poll(this.pollTimeout); 拉取消息
 *  2）this.invokeListener(records); ## this.invokeRecordListener(records); ## this.doInvokeWithRecords(records);
 *     ## this.doInvokeRecordListener(record, iterator); ##  this.invokeOnMessage(record); 执行处理消息逻辑
 *  3）this.invokeOnMessage(record); ##  this.doInvokeOnMessage(record); 最终会调用onMessage方法，
 *  其中第二个参数就是Acknowledgment，有可能是null，看设置的auto.commit值决定。
 *
 *  当我们设置了手动提交，也就是MANUAL或者MANUAL_IMMEDIATE，是通过调用ack.acknowledge()方法，MANUAL_IMMEDIATE模式直接提交offset，MANUAL
 *  模式则先把要提交的offset放到map中，然后返回。
 *  再看回最上面的run方法，其中processCommits()就是从map中拿出要提交的offset，然后批量提交。也就是在下一次poll之前做了提交，
 *  相当于处理完了上一次poll回来的所有消息后，然后再一起提交
 *
 *
 *
 * @author haozhifeng
 * @date 2024/03/18
 */
@Service
public class KafkaConsumerService {

    // 单个ConsumerRecord<?, ?>对象接收
    @KafkaListener(topics = "hao-test-topic", groupId = "hao-group-1")
    public void listen(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        System.out.printf("consume message success: topic = %s, offset = %d, value = %s, key = %s%n", record.topic(), record.offset(), record.value(), record.key());
        ack.acknowledge();
    }

    //多个ConsumerRecord<?, ?>对象接收
//    @KafkaListener(topics = "hao-test-topic", groupId = "hao-KafkaMessageListenerContainergroup-1")
//    public void listen(ConsumerRecords<?, ?> msg, Acknowledgment ack) {
//        Iterator<? extends ConsumerRecord<?, ?>> iterator = msg.iterator();
//        while (iterator.hasNext()){
//            ConsumerRecord<?, ?> record = iterator.next();
//            System.out.printf("consume message success: topic = %s, offset = %d, value = %s, key = %s%n", record.topic(), record.offset(), record.value(), record.key());
//        }
//        ack.acknowledge();
//    }
}
