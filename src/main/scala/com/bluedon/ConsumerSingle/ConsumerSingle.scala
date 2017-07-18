package main.scala.com.bluedon.ConsumerSingle

import java.util
import java.util.{Arrays, Properties}

import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, KafkaConsumer}

/**
  * Created by huoguang on 2017/6/14.
  */
object ConsumerSingle extends App{
  val kafkaConGroup: List[KafkaConsumer[String, String]] = createKafkaConsumerGroup(10, "huoguang")

  val consumerGroups: List[ConsumerRunnable] = createConGroup(kafkaConGroup)

  consumerGroups.foreach(x => {
    new Thread(x).start()
  })

  def createConGroup(in:List[KafkaConsumer[String, String]]): List[ConsumerRunnable] = {
    in.map(x => {
      new ConsumerRunnable(x)
    })
  }
  def createKafkaConsumerGroup(num: Int, topic:String) = {
    var list:List[KafkaConsumer[String, String]] = List()
    val props = getProps()
    for (i <- 0 to num-1) {
      val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
      consumer.subscribe(Arrays.asList(topic))
      list = list :+ consumer
    }
    list
  }

  def getProps(): Properties = {
    val brokerList = "172.16.12.26:9092"
    val groupId = "group1"
    val props = new Properties()
    props.put("bootstrap.servers", brokerList)
    props.put("group.id", groupId)
    props.put("enable.auto.commit", "true")
    props.put("auto.commit.interval.ms", "1000")
    props.put("session.timeout.ms", "30000")
    //  props.put("partition.assignment.strategy", "roundrobin")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props
  }
}




class ConsumerRunnable(in:KafkaConsumer[String, String]) extends Runnable {
  override def run() = {
    while (true) {
      val records = in.poll(100)
      val a = records.iterator()
      while (a.hasNext) {
        val record: ConsumerRecord[String, String] = a.next()
          println(Thread.currentThread.getName + " partition " + record.partition + " the message with offset: "
            + record.offset + s"message is: " + record.value())
      }
    }
  }
}
