package com.icm.websocket.message;

import lombok.Data;
import java.io.Serializable;

/**
 * WebSocket消息实体
 * 
 * @author icm
 */
@Data
public class WebSocketMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 消息类型：1-系统消息，2-业务消息，3-心跳消息
     */
    private Integer type;
    
    /**
     * 发送者ID
     */
    private String senderId;
    
    /**
     * 接收者ID（为空则广播）
     */
    private String receiverId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 扩展数据（JSON格式）
     */
    private String data;
    
    /**
     * 消息时间戳
     */
    private Long timestamp;
    
    public WebSocketMessage() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public WebSocketMessage(Integer type, String content) {
        this.type = type;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }
}
