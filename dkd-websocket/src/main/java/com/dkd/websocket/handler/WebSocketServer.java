package com.dkd.websocket.handler;

import com.alibaba.fastjson.JSON;
import com.dkd.websocket.message.WebSocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket服务端
 * 
 * @author dkd
 */
@Component
@ServerEndpoint("/websocket/{userId}")
public class WebSocketServer {
    
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    
    /**
     * 在线连接数
     */
    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);
    
    /**
     * 存储所有在线客户端
     * key: userId, value: Session
     */
    private static final ConcurrentHashMap<String, Session> CLIENTS = new ConcurrentHashMap<>();
    
    /**
     * 当前会话的用户ID
     */
    private String userId;
    
    /**
     * 当前会话
     */
    private Session session;
    
    /**
     * 连接建立成功调用
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        
        // 添加到连接池
        CLIENTS.put(userId, session);
        
        // 在线数加1
        int count = ONLINE_COUNT.incrementAndGet();
        
        log.info("用户连接成功，userId={}，当前在线人数为：{}", userId, count);
        
        // 发送连接成功消息
        WebSocketMessage message = new WebSocketMessage(1, "连接成功");
        sendMessage(message);
    }
    
    /**
     * 连接关闭调用
     */
    @OnClose
    public void onClose() {
        // 从连接池移除
        CLIENTS.remove(userId);
        
        // 在线数减1
        int count = ONLINE_COUNT.decrementAndGet();
        
        log.info("用户断开连接，userId={}，当前在线人数为：{}", userId, count);
    }
    
    /**
     * 收到客户端消息后调用
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自userId={}的消息：{}", userId, message);
        
        try {
            // 解析消息
            WebSocketMessage wsMessage = JSON.parseObject(message, WebSocketMessage.class);
            
            // 处理心跳消息
            if (wsMessage.getType() == 3) {
                WebSocketMessage pong = new WebSocketMessage(3, "pong");
                sendMessage(pong);
                return;
            }
            
            // 判断是单发还是群发
            if (wsMessage.getReceiverId() != null && !wsMessage.getReceiverId().isEmpty()) {
                // 单发
                sendToUser(wsMessage.getReceiverId(), wsMessage);
            } else {
                // 群发
                sendToAll(wsMessage);
            }
        } catch (Exception e) {
            log.error("消息处理失败", e);
        }
    }
    
    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误，userId={}", userId, error);
    }
    
    /**
     * 发送消息给当前用户
     */
    public void sendMessage(WebSocketMessage message) {
        try {
            this.session.getBasicRemote().sendText(JSON.toJSONString(message));
        } catch (IOException e) {
            log.error("发送消息失败", e);
        }
    }
    
    /**
     * 发送消息给指定用户
     */
    public static void sendToUser(String userId, WebSocketMessage message) {
        Session session = CLIENTS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(JSON.toJSONString(message));
                log.info("发送消息给用户userId={}成功", userId);
            } catch (IOException e) {
                log.error("发送消息给用户userId={}失败", userId, e);
            }
        } else {
            log.warn("用户userId={}不在线", userId);
        }
    }
    
    /**
     * 群发消息
     */
    public static void sendToAll(WebSocketMessage message) {
        for (String userId : CLIENTS.keySet()) {
            sendToUser(userId, message);
        }
    }
    
    /**
     * 获取在线人数
     */
    public static int getOnlineCount() {
        return ONLINE_COUNT.get();
    }
    
    /**
     * 获取在线用户列表
     */
    public static ConcurrentHashMap<String, Session> getClients() {
        return CLIENTS;
    }
}
