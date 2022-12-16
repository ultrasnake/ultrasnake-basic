package cn.creedon.ns4j.layout;

import cn.creedon.common.layout.Layout;
import cn.creedon.ns4j.context.StandardContext;
import com.alibaba.fastjson.JSON;

import java.util.Arrays;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：上下文布局输出
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public class ContextLayout extends Layout<StandardContext> {

    @Override
    public String format(StandardContext context) {
        final StringBuilder sb = new StringBuilder("StandardContext {");
        sb.append("\r\n");
        sb.append("\t Environment=").append(context.getEnvironment()).append(",\r\n");
        sb.append("\t Port=").append(context.getPort()).append(",\r\n");
        sb.append("\t ServerName='").append(context.getServerName()).append("',\r\n");
        sb.append("\t ServerPath='").append(context.getServerPath()).append("',\r\n");
        sb.append("\t HandlerPkg=").append(Arrays.toString(context.getHandlerPkg())).append(",\r\n");
        sb.append("\t PrintLog=").append(context.isPrintLog()).append(",\r\n");
        sb.append("\t OpenGzip=").append(context.isOpenGzip()).append(",\r\n");
        sb.append("\t OpenTraffic=").append(context.isOpenTraffic()).append(",\r\n");
        sb.append("\t TrafficStore=").append(JSON.toJSONString(context.getTrafficStore())).append(",\r\n");
        sb.append("\t SSLContextStore=").append(context.getSslStore()).append(",\r\n");
        sb.append('}');
        return sb.toString();
    }

}
