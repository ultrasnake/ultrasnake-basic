package cn.creedon.ns4j.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：cglib动态代理方式拦截
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public class MethodProxyHandler implements MethodInterceptor {

    private final Object targetObject;

    public MethodProxyHandler(Object targetObject) {
        this.targetObject = targetObject;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        long s = System.currentTimeMillis();
        System.out.println("这是增强方法前 ...... " + method.getName() + " + " + Arrays.toString(args));
        Object result = method.invoke(targetObject, args);
        long e = System.currentTimeMillis() - s;
        if (e > 50) {
            System.out.println("这是增强方法后 ...... " + result + " cost: " + e + "ms.");
        }
        return result;
    }
}
