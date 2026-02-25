package com.bilibili.service.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bilibili.domain.UserFollowing;
import com.bilibili.domain.UserMoment;
import com.bilibili.domain.constant.UserMomentsConstant;
import com.bilibili.service.UserFollowingService;
import com.bilibili.service.WebSocketService;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.lettuce.core.pubsub.PubSubOutput.Type.subscribe;

@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserFollowingService userFollowingService;

    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_MOMENTS);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

    @Bean("momentsConsumer")
    /**
     * 消费用户动态
     */
    public DefaultMQPushConsumer momentsConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_MOMENTS);
        consumer.setNamesrvAddr(nameServerAddr);
        consumer.subscribe(UserMomentsConstant.TOPIC_MOMENTS, "*");
        // 监听器处理消息逻辑
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            MessageExt msg = msgs.get(0);
            if (msg == null) {
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            String bodyStr = new String(msg.getBody());
            // 新动态实体类 userMoment
            UserMoment userMoment = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), UserMoment.class);
            // 查找所有订阅了该新动态的用户（即粉丝）
            Long userId = userMoment.getUserId();
            List<UserFollowing> fanList = userFollowingService.getUserFans(userId);
            // 遍历粉丝，push消息给所有关联的粉丝（redis）
            for (UserFollowing userFan : fanList) {
                // 粉丝Id作为key值
                String key = "subscribed-" + userFan.getUserId();
                // 用户可以订阅很多其他的用户动态，用List来存
                String subscribedListStr = redisTemplate.opsForValue().get(key);
                List<UserMoment> subscribedList;
                if (StringUtil.isNullOrEmpty(subscribedListStr)) {
                    subscribedList = new ArrayList<>();
                } else {
                    // 解析字符串为subscribedList
                    subscribedList = JSONArray.parseArray(subscribedListStr, UserMoment.class);
                }
                // 将新动态添加到每个粉丝的订阅动态列表中
                subscribedList.add(userMoment);
                redisTemplate.opsForValue().set(key, JSONObject.toJSONString(subscribedList));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();
        return consumer;
    }

    // 弹幕提供者
    @Bean("danmusProducer")
    public DefaultMQProducer danmusProducer() throws MQClientException {
        // 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_DANMUS);
        // 设置NameServer的地址
        producer.setNamesrvAddr(nameServerAddr);
        // 启动Producer实例
        producer.start();
        return producer;
    }

    // 弹幕消费者
    @Bean("danmusConsumer")
    public DefaultMQPushConsumer danmusConsumer() throws MQClientException {
        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_DANMUS);
        // 设置NameServer地址
        consumer.setNamesrvAddr(nameServerAddr);
        // 订阅一个或多个Topic，以及Tag来过滤需要消费的消息
        consumer.subscribe(UserMomentsConstant.TOPIC_DANMUS, "*");
        // 注册回调实现类来处理从broker拉去回来的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt msg = msgs.get(0);
                byte[] msgByte = msg.getBody();
                String bodyStr = new String(msgByte);
                JSONObject jsonObject = JSONObject.parseObject(bodyStr);
                String sessionId = jsonObject.getString("sessionId");
                String message = jsonObject.getString("message");
                // 获取会话
                WebSocketService webSocketService = WebSocketService.WEBSOCKET_MAP.get(sessionId);
                if(webSocketService.getSession().isOpen()){
                    try {
                        webSocketService.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // 标记该消息已经被成功消费
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        });
        // 启动消费者实例
        consumer.start();
        return consumer;
    }
}
