package cn.creedon.ns4j.http.ssl;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

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
@Slf4j
public final class SecureSSLContextFactory {

    private static volatile SSLContext single = null;

    public static SSLContext getSingleInstance(SSLContextStore store) {
        if (null == single) {
            synchronized (SecureSSLContextFactory.class) {
                if (null == single) {
                    try {
                        final String server_key_store = store.getServerKeyStore();
                        final String server_key_store_password = store.getServerKeyStorePassword();
                        SSLContext ctx = SSLContext.getInstance("TLS");
                        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                        ks.load(new FileInputStream(server_key_store), server_key_store_password.toCharArray());
                        kmf.init(ks, server_key_store_password.toCharArray());
                        //双向认证
                        if (store.isNeedClientAuth()) {
                            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                            final String server_trust_key_store = store.getServerTrustKeyStore();
                            final String server_trust_key_store_password = store.getServerTrustKeyStorePassword();
                            KeyStore tks = KeyStore.getInstance(KeyStore.getDefaultType());
                            tks.load(new FileInputStream(server_trust_key_store), server_trust_key_store_password.toCharArray());
                            tmf.init(tks);
                            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                            log.info("开启双向Https认证......");
                        } else {
                            ctx.init(kmf.getKeyManagers(), null, null);
                            log.info("开启单向Https认证......");
                        }
                        single = ctx;
                    } catch (Exception e) {
                        throw new Error("SSLContext 初始化异常" + e, e);
                    }
                }
            }
        }
        return single;
    }
}