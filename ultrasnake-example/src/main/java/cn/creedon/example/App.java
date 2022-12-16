package cn.creedon.example;

import cn.creedon.ns4j.NettyServerStartup;
import cn.creedon.ns4j.annotations.EnableNs4j;

@EnableNs4j(
        port = 8080,
        serverName = "example-project",
        serverPath = "/PATH/TO/"
)
public class App {
    public static void main(String[] args) {
        NettyServerStartup.INSTANCE.boot(App.class, args);
    }
}
