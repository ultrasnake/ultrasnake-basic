package cn.creedon.common.utils;

import java.util.Arrays;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：封装对象HashCode
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public class ObjectHashCode {

    /**
     * 获取对象的hashCode
     *
     * @param obj Object
     * @return
     */
    public static int hashCode(Object obj) {
        if (obj == null) {
            return 0;
        }
        final Class<?> clazz = obj.getClass();
        if (!clazz.isArray()) {
            return obj.hashCode();
        }
        final Class<?> componentType = clazz.getComponentType();
        if (long.class.equals(componentType)) {
            return Arrays.hashCode((long[]) obj);
        } else if (int.class.equals(componentType)) {
            return Arrays.hashCode((int[]) obj);
        } else if (short.class.equals(componentType)) {
            return Arrays.hashCode((short[]) obj);
        } else if (char.class.equals(componentType)) {
            return Arrays.hashCode((char[]) obj);
        } else if (byte.class.equals(componentType)) {
            return Arrays.hashCode((byte[]) obj);
        } else if (boolean.class.equals(componentType)) {
            return Arrays.hashCode((boolean[]) obj);
        } else if (float.class.equals(componentType)) {
            return Arrays.hashCode((float[]) obj);
        } else if (double.class.equals(componentType)) {
            return Arrays.hashCode((double[]) obj);
        } else {
            return Arrays.deepHashCode((Object[]) obj);
        }
    }

}
