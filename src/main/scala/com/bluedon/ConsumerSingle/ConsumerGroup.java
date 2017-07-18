//package main.scala.com.bluedon.ConsumerSingle;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by huoguang on 2017/6/14.
// */
//public class ConsumerGroup {
//
//    private List<ConsumerRunnable> consumers;
//    public ConsumerGroup(int consumerNum, String groupID, String topic, String brokerList) {
//        consumers = new ArrayList<ConsumerRunnable>(consumerNum);
//        for (int i = 0; i < consumerNum; i++) {
//            ConsumerRunnable consumerThread = new ConsumerRunnable(brokerList, groupID, topic);
//            consumers.add(consumerThread);
//        }
//    }
//
//    public void execute() {
//        for (ConsumerRunnable task:consumers) {
//            new Thread(task).start();
//        }
//    }
//}
