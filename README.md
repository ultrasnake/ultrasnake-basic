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
