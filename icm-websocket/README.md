# DKD WebSocket 长连接模块

## 模块说明

本模块提供基于 WebSocket 的长连接功能，支持实时消息推送、在线状态管理等功能。

## 主要功能

1. **WebSocket 连接管理**
   - 用户连接/断开管理
   - 在线用户统计
   - 会话管理

2. **消息推送**
   - 单用户消息推送
   - 群发消息
   - 心跳检测

3. **消息类型**
   - 系统消息 (type=1)
   - 业务消息 (type=2)
   - 心跳消息 (type=3)

## 使用方式

### 1. 客户端连接

使用 WebSocket 客户端连接到服务器：

```javascript
// 连接 WebSocket，userId 为用户ID
const ws = new WebSocket('ws://localhost:8080/websocket/用户ID');

// 连接成功
ws.onopen = function(event) {
    console.log('WebSocket连接成功');
};

// 接收消息
ws.onmessage = function(event) {
    const message = JSON.parse(event.data);
    console.log('收到消息:', message);
};

// 连接关闭
ws.onclose = function(event) {
    console.log('WebSocket连接关闭');
};

// 发生错误
ws.onerror = function(error) {
    console.error('WebSocket错误:', error);
};
```

### 2. 发送消息

```javascript
// 发送消息
const message = {
    type: 2,              // 消息类型：1-系统消息，2-业务消息，3-心跳
    senderId: '发送者ID',
    receiverId: '接收者ID', // 为空则广播
    content: '消息内容',
    data: '{"key": "value"}' // 扩展数据（JSON字符串）
};

ws.send(JSON.stringify(message));
```

### 3. 心跳保活

```javascript
// 定时发送心跳消息（建议30秒一次）
setInterval(() => {
    if (ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({
            type: 3,
            content: 'ping'
        }));
    }
}, 30000);
```

### 4. 服务端推送

在业务代码中注入 `WebSocketService` 即可使用：

```java
@Autowired
private WebSocketService webSocketService;

// 发送消息给指定用户
webSocketService.sendToUser("userId", 2, "这是一条业务消息");

// 发送消息给指定用户（带扩展数据）
String data = "{\"orderId\": \"12345\"}";
webSocketService.sendToUser("userId", 2, "订单已完成", data);

// 群发消息
webSocketService.sendToAll(1, "系统维护通知");

// 获取在线人数
int count = webSocketService.getOnlineCount();

// 获取在线用户列表
Set<String> users = webSocketService.getOnlineUsers();

// 判断用户是否在线
boolean isOnline = webSocketService.isOnline("userId");
```

## 测试接口

可以使用以下 API 接口进行测试：

1. **发送消息给指定用户**
   - POST `/websocket/sendToUser`
   - 参数：userId, type, content

2. **群发消息**
   - POST `/websocket/sendToAll`
   - 参数：type, content

3. **获取在线人数**
   - GET `/websocket/onlineCount`

4. **获取在线用户列表**
   - GET `/websocket/onlineUsers`

5. **判断用户是否在线**
   - GET `/websocket/isOnline/{userId}`

## 应用场景

1. **售货机实时状态监控**
   - 售货机上线/离线通知
   - 货道状态实时更新
   - 故障报警推送

2. **订单实时推送**
   - 新订单通知
   - 订单状态变更通知
   - 支付结果通知

3. **系统消息通知**
   - 系统公告
   - 运维通知
   - 权限变更通知

4. **多用户协同**
   - 在线用户列表
   - 实时聊天
   - 数据同步

## 注意事项

1. 连接时必须提供 userId 参数
2. 建议实现心跳机制，防止连接超时断开
3. 生产环境建议配置 Nginx 进行 WebSocket 反向代理
4. 集群部署时建议使用 Redis 做消息中转

## 扩展配置

如需支持集群部署，可以集成 Redis 发布订阅功能，实现跨节点消息推送。

## 技术栈

- Spring Boot WebSocket
- FastJSON
- Lombok
