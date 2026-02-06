# Netty IM - 即时通讯项目

基于 Netty 的 Android 即时通讯库，提供稳定的 WebSocket 连接服务。

## 功能特性

- 🚀 基于 Netty 的高性能 WebSocket 客户端
- 🔒 支持自动重连机制
- 💬 简洁易用的 API 设计
- 📦 模块化架构，易于集成
- 🔧 支持 Protobuf 数据序列化

## 快速开始

### 1. 添加依赖

在 app 模块的 `build.gradle.kts` 中添加 `lib_websocket` 依赖：

```kotlin
dependencies {
    // 其他依赖...

    // 添加 lib_websocket 依赖
    implementation(project(":libs:lib_websocket"))
}
```

### 2. 基础使用

在 Activity 或 Application 中启动 WebSocket 服务：

```kotlin
import com.xiaotimel.im.WebSocketService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 启动 WebSocket 服务
        val websocketUrl = "ws://your-server-address:port"
        WebSocketService.start(this, websocketUrl)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 关闭 WebSocket 连接（可选）
        WebSocketService.stop(this)
    }
}
```

### 3. 在 AndroidManifest.xml 中声明服务

```xml
<application
    ...>

    <service
        android:name="com.xiaotimel.im.WebSocketService"
        android:enabled="true"
        android:exported="false" />

</application>
```

## API 文档

### WebSocketService

#### start(context: Context, url: String)

启动 WebSocket 服务并连接到指定服务器。

**参数：**
- `context` - Android 上下文（Context）
- `url` - WebSocket 服务器地址（如：`ws://example.com:8080`）

**示例：**
```kotlin
WebSocketService.start(context, "ws://192.168.1.100:8080/chat")
```

#### stop(context: Context)

停止 WebSocket 服务并关闭连接。

**参数：**
- `context` - Android 上下文（Context）

**示例：**
```kotlin
WebSocketService.stop(context)
```

### WebSocketListener

如果需要处理 WebSocket 事件，可以实现 `WebSocketListener` 接口：

```kotlin
interface WebSocketListener {
    // 连接成功
    fun onConnected()

    // 连接失败
    fun onConnectFailed(error: String)

    // 收到消息
    fun onMessage(message: ByteArray)

    // 连接断开
    fun onDisconnected()

    // 连接状态变化
    fun onConnectionChanged(isConnected: Boolean)
}
```

**使用示例：**
```kotlin
WebSocketService.addListener(object : WebSocketListener {
    override fun onConnected() {
        Log.d("WebSocket", "连接成功")
    }

    override fun onMessage(message: ByteArray) {
        // 处理收到的消息
        Log.d("WebSocket", "收到消息: ${message.size} 字节")
    }

    override fun onDisconnected() {
        Log.d("WebSocket", "连接断开")
    }
})
```

### 发送消息

```kotlin
// 发送字符串消息
WebSocketService.sendString("Hello, Server!")

// 发送字节数组（Protobuf 消息）
val message = buildMessage {
    // 构建你的 Protobuf 消息
}
WebSocketService.sendBytes(message.toByteArray())
```

## 配置说明

### 自动重连

WebSocket 服务默认支持自动重连，重连参数可以通过修改配置调整：

```kotlin
// 设置重连参数（可选）
WebSocketService.setMaxReconnectAttempts(5)  // 最大重连次数
WebSocketService.setReconnectInterval(3000)  // 重连间隔（毫秒）
```

### 心跳机制

```kotlin
// 启用心跳检测（默认开启）
WebSocketService.setHeartbeatInterval(30000)  // 心跳间隔（毫秒）
WebSocketService.setHeartbeatMessage("ping")  // 心跳消息
```

## 项目结构

```
netty-im/
├── app/                    # 应用主模块
├── libs/
│   └── lib_websocket/     # WebSocket 库模块
│       ├── src/main/proto/ # Protobuf 定义
│       └── src/main/java/ # WebSocket 实现
├── gradle/                 # Gradle 配置
├── config.gradle          # 全局配置
└── README.md             # 项目说明
```

## 开发环境

- **Gradle:** 8.4.0
- **Kotlin:** 2.0.21
- **Android SDK:** 35 (API 35)
- **Min SDK:** 24

## 许可证

MIT License