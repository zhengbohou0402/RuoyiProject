package com.dkd.websocket.service;

import com.dkd.websocket.handler.UserBehaviorWebSocketServer;
import com.dkd.websocket.handler.WebSocketServer;
import com.dkd.websocket.message.WebSocketMessage;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * WebSocket业务服务类
 * 
 * @author dkd
 */
@Service
public class WebSocketService {
    
    /**
     * 发送消息给指定用户
     * 
     * @param userId 用户ID
     * @param type 消息类型
     * @param content 消息内容
     */
    public void sendToUser(String userId, Integer type, String content) {
        WebSocketMessage message = new WebSocketMessage(type, content);
        message.setSenderId("system");
        message.setReceiverId(userId);
        WebSocketServer.sendToUser(userId, message);
    }
    
    /**
     * 发送消息给指定用户（带扩展数据）
     * 
     * @param userId 用户ID
     * @param type 消息类型
     * @param content 消息内容
     * @param data 扩展数据
     */
    public void sendToUser(String userId, Integer type, String content, String data) {
        WebSocketMessage message = new WebSocketMessage(type, content);
        message.setSenderId("system");
        message.setReceiverId(userId);
        message.setData(data);
        WebSocketServer.sendToUser(userId, message);
    }
    
    /**
     * 群发消息
     * 
     * @param type 消息类型
     * @param content 消息内容
     */
    public void sendToAll(Integer type, String content) {
        WebSocketMessage message = new WebSocketMessage(type, content);
        message.setSenderId("system");
        WebSocketServer.sendToAll(message);
    }
    
    /**
     * 群发消息（带扩展数据）
     * 
     * @param type 消息类型
     * @param content 消息内容
     * @param data 扩展数据
     */
    public void sendToAll(Integer type, String content, String data) {
        WebSocketMessage message = new WebSocketMessage(type, content);
        message.setSenderId("system");
        message.setData(data);
        WebSocketServer.sendToAll(message);
    }

    /**
     * 向订阅了指定场景+手机号的连接推送业务消息
     *
     * @param scene      场景（behaviorLog / behaviorTrack）
     * @param userMobile 手机号
     * @param content    消息摘要
     * @param data       增量数据 JSON 数组字符串
     * @return 是否推送成功（有在线连接）
     */
    public boolean pushBehavior(String scene, String userMobile, String content, String data) {
        return UserBehaviorWebSocketServer.pushToSubscriber(scene, userMobile, content, data);
    }

    /**
     * 获取指定场景的订阅手机号列表
     */
    public Set<String> getSubscribedMobiles(String scene) {
        return UserBehaviorWebSocketServer.getSubscribedMobiles(scene);
    }

    /**
     * 获取业务订阅在线连接数
     */
    public int getBehaviorSubscriberCount() {
        return UserBehaviorWebSocketServer.getSubscriberCount();
    }
    
    /**
     * 获取在线人数
     * 
     * @return 在线人数
     */
    public int getOnlineCount() {
        return WebSocketServer.getOnlineCount();
    }
    
    /**
     * 获取在线用户ID列表
     * 
     * @return 用户ID集合
     */
    public Set<String> getOnlineUsers() {
        return WebSocketServer.getClients().keySet();
    }
    
    /**
     * 判断用户是否在线
     * 
     * @param userId 用户ID
     * @return true-在线，false-离线
     */
    public boolean isOnline(String userId) {
        return WebSocketServer.getClients().containsKey(userId);
    }
}
