package cn.creedon.common.cache;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：缓存基类
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public interface Cache<K, V> {

    String getId();

    void put(K key, V value);

    V get(K key);

    V remove(K key);

    void clear();

    long size();

}
