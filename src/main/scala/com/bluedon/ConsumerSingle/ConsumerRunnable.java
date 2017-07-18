//package main.scala.com.bluedon.ConsumerSingle;
//
///**
// * Created by huoguang on 2017/6/14.
// */
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//
//import java.util.Arrays;
//import java.util.Properties;
//
///**
// * Created by huoguang on 2017/6/13.
// */
//public class ConsumerRunnable implements Runnable {
//    private final KafkaConsumer<String, String> consumer;
//
//    public ConsumerRunnable(String brokerList, String groupId, String topic) {
//        Properties props = new Properties();
//        props.put("bootstrap.servers", brokerList);
//        props.put("group.id", groupId);
//        props.put("enable.auto.commit", "true");
//        props.put("auto.commit.interval.ms", "1000");
//        props.put("session.timeout.ms", "30000");
//        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        this.consumer = new KafkaConsumer<String, String>(props);
//        consumer.subscribe(Arrays.asList(topic));   // 本例使用分区副本自动分配策略
//
//    }
//
//    public void run() {
//        while (true) {
//            System.out.println("Have a try c!!!");
//            ConsumerRecords<String, String> records = consumer.poll(200);
//            for (ConsumerRecord<String, String> record : records) {
//
//                System.out.println(Thread.currentThread().getName()
//                        + " consumed " + record.partition()
//                        + "th message with offset: " + record.offset());
//            }
//        }
//    }
//}
