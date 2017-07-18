package main.scala.com.bluedon.ConsumerSingle

import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}

/**
  * Created by huoguang on 2017/6/14.
  */
object ComsumerSimple extends App{

  val props = new Properties()
  props.put("bootstrap.servers", "172.16.12.26:9092")
  props.put("group.id", "test")
  props.put("enable.auto.commit", "true")
  props.put("auto.commit.interval.ms", "1000")
  props.put("session.timeout.ms", "30000")
  //  props.put("partition.assignment.strategy", "roundrobin")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

  val consumer = new KafkaConsumer[String, String](props)
  consumer.subscribe(util.Arrays.asList("single"))

  while (true) {
    val records: ConsumerRecords[String, String] = consumer.poll(100)
    val a = records.iterator()
    while (a.hasNext) {
      val str = a.next()
      println(s" offset is: ${str.offset()} !!, partition is: ${str.partition()} !!, value is: ${str.value()} !!")
    }

  }

}
