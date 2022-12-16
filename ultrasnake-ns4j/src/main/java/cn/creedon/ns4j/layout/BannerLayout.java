package cn.creedon.ns4j.layout;

import cn.creedon.common.layout.Layout;

import java.util.Date;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：banner布局输出
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public class BannerLayout extends Layout<String> {

    public String format() {
        return format(null);
    }

    @Override
    public String format(String content) {
        final StringBuilder sb = new StringBuilder();
        return sb.append("")
                .append("  _ ___ ___.__   __                  _________              __         ©\n")
                .append("|_   | |  |\\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____\n")
                .append("  |  | |  ||  |\\   __\\_  __ \\__  \\  \\_____  \\ /    \\\\__  \\ |  |/ // __ \\\n")
                .append("  |  |_|  /|  |_|  |  |  | \\// __ \\_/        \\   |  \\/ __ \\|    <\\  ___/\n")
                .append("  |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\\\___  >\n")
                .append("  UltraSnake - WDC               \\/        \\/     \\/     \\/     \\/    \\/")
                .append("\r\n").append("\r\n")
                .append(new Date()).append(" +++++ 伟大的开始 +++++ ").toString();
    }
}
