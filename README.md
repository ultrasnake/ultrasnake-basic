# Netty Server for Java

Netty Server for Java（简称: ns4j）是一个基于Netty的实现的与App客户端通讯的通讯框架，包含https通讯，包含安全数据传输、数据压缩等功能；
应用场景：系统app与服务器端数据通讯、行情的等数据的实时推送等；

## 用法

### 示例

```java
@EnableNs4j(
        port = 8080,
        serverName = "netty-http-example",
        serverPath = "/PATH/TO/"
)
public class ServerLauncher {
	public static void main(String[] args) {
        NettyServerStartup.INSTANCE.boot(ServerLauncher.class, args);
    }	
}
```

## License

[Apache 2.0 License](./LICENSE).
