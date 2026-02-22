package com.bilibili.service.util;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author: Dk.L
 * @date: 2023/3/24
 * @Description: TODO
 */
public class RocketMQUtil {

    //同步发送消息
    public static void syncSendMsg(DefaultMQProducer producer, Message msg) throws Exception{
        SendResult result = producer.send(msg);
        System.out.println(result);
    }

    // 异步发送消息
    public static void asyncSendMsg(DefaultMQProducer producer, Message msg) throws Exception {
        int messageCount = 1;
        CountDownLatch2 countDownLatch = new CountDownLatch2(messageCount);
        for(int i = 0; i < messageCount; i++){
            producer.send(msg, new SendCallback(){

                @Override
                public void onSuccess(SendResult sendResult) {
                    countDownLatch.countDown();
                    Logger logger = LoggerFactory.getLogger(RocketMQUtil.class);
                    logger.info("异步发送消息成功，消息id:" + sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable throwable) {
                    countDownLatch.countDown();
                    System.out.println("发送消息时发生了异常" + throwable);
                    throwable.printStackTrace();
                }
            });
        }
        countDownLatch.await(5, TimeUnit.SECONDS);
    }

}
