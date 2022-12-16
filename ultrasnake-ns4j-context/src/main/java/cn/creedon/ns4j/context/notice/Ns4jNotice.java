package cn.creedon.ns4j.context.notice;

import cn.creedon.ns4j.context.exceptions.ServerNoticeException;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：服务启动成功以后通知
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public interface Ns4jNotice {

    void run() throws ServerNoticeException;

}
