package com.icm.websocket.handler;

import com.alibaba.fastjson.JSON;
import com.icm.websocket.interceptor.WebSocketInterceptor;
import com.icm.websocket.message.WebSocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户行为实时监控 WebSocket 端点（按场景+手机号订阅）
 *
 * 场景说明：
 *   - behaviorLog   ：MySQL tb_real_time_data_verification（实时数据验证）
 *   - behaviorTrack ：ClickHouse 轨迹表（用户行为轨迹）
 *
 * 连接地址：ws://host/ws/{scene}/{userMobile}?token=xxx
 *  - scene      ：behaviorLog | behaviorTrack
 *  - userMobile ：订阅的手机号（11位）
 *  - token      ：登录 JWT，握手时校验（见 WebSocketInterceptor）
 *
 * 消息协议（复用 WebSocketMessage）：
 *   - 客户端 → 服务端：type=3 心跳
 *   - 服务端 → 客户端：type=1 连接成功 / type=2 业务推送（data 为增量数据 JSON 数组）/ type=3 pong
 *
 * @author icm
 */
@Component
@ServerEndpoint(value = "/ws/{scene}/{userMobile}", configurator = WebSocketInterceptor.class)
public class UserBehaviorWebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(UserBehaviorWebSocketServer.class);

    /** 订阅 key 分隔符 */
    public static final String KEY_SEPARATOR = "::";

    /** 连接池：key = scene::userMobile，value = Session */
    private static final ConcurrentHashMap<String, Session> SUBSCRIBERS = new ConcurrentHashMap<>();

    /** 当前会话的订阅 key */
    private String subscribeKey;

    /**
     * 连接建立
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("scene") String scene,
                       @PathParam("userMobile") String userMobile) {
        // 参数校验：场景必须合法，手机号必须 11 位数字
        if (!isValidScene(scene) || userMobile == null || !userMobile.matches("^\\d{11}$")) {
            log.warn("WebSocket 订阅参数非法: scene={}, userMobile={}", scene, userMobile);
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "非法订阅参数"));
            } catch (IOException ignored) {
            }
            return;
        }

        // token 鉴权：握手时由 WebSocketInterceptor 将 token 存入 userProperties，空 token 拒绝连接
        Object tokenObj = session.getUserProperties().get("token");
        String token = tokenObj != null ? tokenObj.toString() : "";
        if (token.isEmpty()) {
            log.warn("WebSocket 鉴权失败(无token), 拒绝连接: scene={}, userMobile={}", scene, userMobile);
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "未认证"));
            } catch (IOException ignored) {
            }
            return;
        }

        this.subscribeKey = buildKey(scene, userMobile);
        // 同一 key 重复连接时，先关掉旧连接（保持最新会话）
        Session old = SUBSCRIBERS.put(subscribeKey, session);
        if (old != null && old.isOpen()) {
            try {
                old.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "被新连接替代"));
            } catch (IOException ignored) {
            }
        }

        log.info("WebSocket 订阅成功: scene={}, userMobile={}, 当前订阅数={}",
                scene, userMobile, SUBSCRIBERS.size());

        // 发送连接成功消息（type=1）
        WebSocketMessage message = new WebSocketMessage(1, "连接成功");
        message.setSenderId("system");
        message.setReceiverId(userMobile);
        sendMessage(session, message);
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(Session session) {
        if (subscribeKey != null) {
            // 仅当当前 Session 仍是连接池中的会话时才移除，避免误删新连接
            SUBSCRIBERS.remove(subscribeKey, session);
            log.info("WebSocket 订阅断开: {}, 当前订阅数={}", subscribeKey, SUBSCRIBERS.size());
        }
    }

    /**
     * 收到客户端消息（心跳处理）
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            WebSocketMessage wsMessage = JSON.parseObject(message, WebSocketMessage.class);
            // 心跳：type=3，回 pong
            if (wsMessage != null && wsMessage.getType() != null && wsMessage.getType() == 3) {
                WebSocketMessage pong = new WebSocketMessage(3, "pong");
                pong.setSenderId("system");
                pong.setReceiverId(subscribeKey != null ? subscribeKey.split(KEY_SEPARATOR)[1] : null);
                sendMessage(session, pong);
            }
        } catch (Exception e) {
            log.error("WebSocket 消息处理失败", e);
        }
    }

    /**
     * 发生错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket 发生错误: key={}, err={}", subscribeKey, error.getMessage());
    }

    /**
     * 向订阅了指定场景+手机号的连接推送业务消息（type=2）
     *
     * @param scene     场景
     * @param userMobile 手机号
     * @param content    摘要
     * @param data      增量数据 JSON 数组字符串
     * @return 是否推送成功（有在线连接）
     */
    public static boolean pushToSubscriber(String scene, String userMobile, String content, String data) {
        Session session = SUBSCRIBERS.get(buildKey(scene, userMobile));
        if (session == null || !session.isOpen()) {
            return false;
        }
        WebSocketMessage message = new WebSocketMessage(2, content);
        message.setSenderId("system");
        message.setReceiverId(userMobile);
        message.setData(data);
        try {
            synchronized (session) {
                session.getBasicRemote().sendText(JSON.toJSONString(message));
            }
            return true;
        } catch (IOException e) {
            log.error("WebSocket 推送失败: key={}, err={}", buildKey(scene, userMobile), e.getMessage());
            SUBSCRIBERS.remove(buildKey(scene, userMobile), session);
            return false;
        }
    }

    /**
     * 获取指定场景的订阅手机号列表
     */
    public static java.util.Set<String> getSubscribedMobiles(String scene) {
        java.util.Set<String> result = new java.util.HashSet<>();
        String prefix = scene + KEY_SEPARATOR;
        for (String key : SUBSCRIBERS.keySet()) {
            if (key.startsWith(prefix)) {
                result.add(key.substring(prefix.length()));
            }
        }
        return result;
    }

    /**
     * 获取在线订阅数
     */
    public static int getSubscriberCount() {
        return SUBSCRIBERS.size();
    }

    /** 构建订阅 key */
    private static String buildKey(String scene, String userMobile) {
        return scene + KEY_SEPARATOR + userMobile;
    }

    /** 场景合法性校验 */
    private static boolean isValidScene(String scene) {
        return "behaviorLog".equals(scene) || "behaviorTrack".equals(scene);
    }

    /** 发送消息（当前会话） */
    private void sendMessage(Session session, WebSocketMessage message) {
        try {
            synchronized (session) {
                session.getBasicRemote().sendText(JSON.toJSONString(message));
            }
        } catch (IOException e) {
            log.error("WebSocket 发送消息失败", e);
        }
    }
}
