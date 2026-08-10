package com.dkd.web.controller.websocket;

import com.dkd.common.core.controller.BaseController;
import com.dkd.common.core.domain.AjaxResult;
import com.dkd.websocket.service.WebSocketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * WebSocket测试控制器
 * 
 * @author dkd
 */
@Api(tags = "WebSocket管理")
@RestController
@RequestMapping("/websocket")
public class WebSocketController extends BaseController {
    
    @Autowired
    private WebSocketService webSocketService;
    
    /**
     * 发送消息给指定用户
     */
    @ApiOperation("发送消息给指定用户")
    @PostMapping("/sendToUser")
    public AjaxResult sendToUser(@RequestParam String userId, 
                                  @RequestParam(defaultValue = "2") Integer type,
                                  @RequestParam String content) {
        webSocketService.sendToUser(userId, type, content);
        return success("发送成功");
    }
    
    /**
     * 群发消息
     */
    @ApiOperation("群发消息")
    @PostMapping("/sendToAll")
    public AjaxResult sendToAll(@RequestParam(defaultValue = "2") Integer type,
                                 @RequestParam String content) {
        webSocketService.sendToAll(type, content);
        return success("发送成功");
    }
    
    /**
     * 获取在线人数
     */
    @ApiOperation("获取在线人数")
    @GetMapping("/onlineCount")
    public AjaxResult getOnlineCount() {
        int count = webSocketService.getOnlineCount();
        return success(count);
    }
    
    /**
     * 获取在线用户列表
     */
    @ApiOperation("获取在线用户列表")
    @GetMapping("/onlineUsers")
    public AjaxResult getOnlineUsers() {
        Set<String> users = webSocketService.getOnlineUsers();
        return success(users);
    }
    
    /**
     * 判断用户是否在线
     */
    @ApiOperation("判断用户是否在线")
    @GetMapping("/isOnline/{userId}")
    public AjaxResult isOnline(@PathVariable String userId) {
        boolean online = webSocketService.isOnline(userId);
        return success(online);
    }

    /**
     * 获取业务订阅(行为监控)在线手机号列表
     */
    @ApiOperation("获取行为监控订阅手机号列表")
    @GetMapping("/behaviorSubscribers")
    public AjaxResult getBehaviorSubscribers(@RequestParam(required = false, defaultValue = "behaviorLog") String scene) {
        return success(webSocketService.getSubscribedMobiles(scene));
    }

    /**
     * 获取业务订阅(行为监控)在线连接数
     */
    @ApiOperation("获取行为监控订阅连接数")
    @GetMapping("/behaviorSubscriberCount")
    public AjaxResult getBehaviorSubscriberCount() {
        return success(webSocketService.getBehaviorSubscriberCount());
    }
}
