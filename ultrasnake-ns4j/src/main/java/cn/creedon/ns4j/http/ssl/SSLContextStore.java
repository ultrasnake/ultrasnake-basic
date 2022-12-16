package cn.creedon.ns4j.http.ssl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：请在此处加入功能描述
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SSLContextStore {

    private boolean isOpenSSL = false;

    /**
     * 默认单向认证
     */
    private boolean needClientAuth = false;

    private String serverKeyStore;
    private String serverKeyStorePassword;

    private String serverTrustKeyStore;
    private String serverTrustKeyStorePassword;

}
