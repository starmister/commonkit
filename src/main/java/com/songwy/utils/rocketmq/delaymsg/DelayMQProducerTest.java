package com.songwy.utils.rocketmq.delaymsg;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DelayMQProducerTest {
    public static void main(String[] args) throws MQClientException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("delay_test_group");
        producer.setSendMsgTimeout(60000);
        producer.setRetryTimesWhenSendFailed(3);
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.start();
        try {
            for (int i = 0; i < 3; i++) {
                Message msg = new Message("Topic_Delay_Test",// topic
                        "Tag_Delay",// tag
                        (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "Topic_Delay_Test" + i).getBytes()// body
                );
                msg.setDelayTimeLevel(2); // 设置延迟级别为2 也就是 5s
                SendResult sendResult = producer.send(msg);
                System.out.println(sendResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.shutdown();
    }

}
