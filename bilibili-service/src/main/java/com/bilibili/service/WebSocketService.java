package com.bilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.bilibili.domain.Danmu;
import com.bilibili.domain.constant.UserMomentsConstant;
import com.bilibili.service.util.RocketMQUtil;
import com.bilibili.service.util.TokenUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: Dk.L
 * @date: 2023/3/31
 * @Description: WebSocketService 类
 */
@Service
// 被下边的注解标注，就说明是一个和WebSocket相关的服务类了,里边的路径值随便定义，访问的时候是用自定义的路径即可
@ServerEndpoint("/imserver/{token}")
public class WebSocketService {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // AtomicInteger java提供的一个原子操作（为了保证线程安全）的相关类
    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    // ConcurrentHashMap 是一个线程安全的Map
    // 每个客户端链接都会生成一个WebSocketService，所以WebSocketService是一个多例模式，多例模式下是用@Autowire注入的话会产生问题
    public static final ConcurrentHashMap<String, WebSocketService> WEBSOCKET_MAP = new ConcurrentHashMap<>();

    // Session 是服务端和客户端的一个会话,WebSocket里边包含一个Session
    private Session session;

    private String sessionId;

    private Long userId;

    private static ApplicationContext APPLICATION_CONTEXT;

    // 添加一个全局上下文变量
    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketService.APPLICATION_CONTEXT = applicationContext;
    }

    // WebSocket 提供的一个注解，当调用成功时，就调用@OnOpen注解表示的方法
    @OnOpen
    public void openConnection(Session session, @PathParam("token") String token) {
        try{
            this.userId = TokenUtil.verifyToken(token);
        }catch (Exception ignored){}
        RedisTemplate<String, String> redisTemplate = (RedisTemplate) WebSocketService.APPLICATION_CONTEXT.getBean("redisTemplate");
        redisTemplate.opsForValue().get("aaa");
        this.sessionId = session.getId();
        this.session = session;
        // 已存在连接
        if (WEBSOCKET_MAP.containsKey(sessionId)) {
            WEBSOCKET_MAP.remove(sessionId);
            // this 代表当前的WebSocketService
            WEBSOCKET_MAP.put(sessionId, this);
        } else {
            WEBSOCKET_MAP.put(sessionId, this);
            ONLINE_COUNT.getAndIncrement();
        }
        // ONLINE_COUNT.get() 会返回一个原始的int类型
        logger.info("用户连接成功：" + sessionId + "，当前在线人数为：" + ONLINE_COUNT.get());
        try {
            this.sendMessage("0");
        } catch (Exception e) {
            logger.error("连接异常");
        }
    }

    @OnClose
    public void closeConnection() {
        if (WEBSOCKET_MAP.containsKey(sessionId)) {
            WEBSOCKET_MAP.remove(sessionId);
            ONLINE_COUNT.getAndDecrement();
        }
        logger.info("用户退出：" + sessionId + "当前在线人数为：" + ONLINE_COUNT.get());
    }

    @OnMessage
    public void onMessage(String message) {
        logger.info("用户信息：" + sessionId + "，报文：" + message);
        // 群发消息
        try{
            for (Map.Entry<String, WebSocketService> entry : WEBSOCKET_MAP.entrySet()) {
                WebSocketService webSocketService = entry.getValue();
               // 获取弹幕生产者
                DefaultMQProducer danmusProducer = (DefaultMQProducer) APPLICATION_CONTEXT.getBean("danmusProducer");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", message);
                jsonObject.put("sessionId", webSocketService.getSessionId());
                // 构建消息对象
                Message msg = new Message(UserMomentsConstant.TOPIC_DANMUS, jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
                // 异步发送消息
                RocketMQUtil.asyncSendMsg(danmusProducer, msg);
            }
            if (this.userId != null){
                // 保存弹幕信息到数据库
                Danmu danmu = JSONObject.parseObject(message, Danmu.class);
                danmu.setUserId(userId);
                danmu.setCreateTime(new Date());
                DanmuService danmuService = (DanmuService) APPLICATION_CONTEXT.getBean("danmuService");
                // 异步请求数据库
                danmuService.asyncAddDanmu(danmu);
                // 保存弹幕到 redis
                danmuService.addDanmusToRedis(danmu);
            }
        } catch (Exception e){
            logger.error("弹幕接收出现问题");
            e.printStackTrace();
        }

    }

    @OnError
    public void onError(Throwable error) {
    }

    public void sendMessage(String message) throws IOException {
        // 调用会话现成的getBasicRemote()方法获得Basic实体类，在调用发送信息的方法
        this.session.getBasicRemote().sendText(message);
    }

    // 指定时间间隔，例如5秒
    @Scheduled(fixedRate = 5000)
    private void noticeOnlineCount() throws IOException{
        for (Map.Entry<String, WebSocketService> entry : WebSocketService.WEBSOCKET_MAP.entrySet()) {
            WebSocketService webSocketService = entry.getValue();
            if (webSocketService.session.isOpen()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("onlineCount", ONLINE_COUNT.get());
                jsonObject.put("msg", "当前在线人数为：" + ONLINE_COUNT.get());
                webSocketService.sendMessage(jsonObject.toJSONString());
            }
        }
    }

    public Session getSession() {
        return session;
    }

    public String getSessionId() {
        return sessionId;
    }
}
