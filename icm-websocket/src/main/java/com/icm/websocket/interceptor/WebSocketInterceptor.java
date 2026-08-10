package com.icm.websocket.interceptor;

import com.icm.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;

/**
 * WebSocket握手拦截器
 * 
 * @author icm
 */
@Component
public class WebSocketInterceptor extends ServerEndpointConfig.Configurator {
    
    private static final Logger log = LoggerFactory.getLogger(WebSocketInterceptor.class);
    
    /**
     * 握手前处理：只负责把 token 存入 session，鉴权在 onOpen 中执行
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        // 获取token
        List<String> tokenList = request.getParameterMap().get("token");
        String token = null;
        if (tokenList != null && !tokenList.isEmpty()) {
            token = tokenList.get(0);
        }

        // 将token存储到session中（onOpen 里做最终校验）
        sec.getUserProperties().put("token", token != null ? token : "");
        if (StringUtils.isNotEmpty(token)) {
            log.info("WebSocket握手成功，token: {}", token);
        } else {
            log.warn("WebSocket握手，token为空（onOpen将拒绝连接）");
        }

        super.modifyHandshake(sec, request, response);
    }
}
