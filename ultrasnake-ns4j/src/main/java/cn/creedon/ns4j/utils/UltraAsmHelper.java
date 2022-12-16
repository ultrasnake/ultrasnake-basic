package cn.creedon.ns4j.utils;

import org.objectweb.asm.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：封装的类操作工具类
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public class UltraAsmHelper {

    /**
     * 获取方法的参数名
     *
     * @param method
     * @return
     */
    public static String[] getMethodParamNames(final Method method) {
        final String[] paramNames = new String[method.getParameterTypes().length];
        final String n = method.getDeclaringClass().getName();
        ClassReader cr = null;
        try {
            cr = new ClassReader(n);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cr.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                final Type[] args = Type.getArgumentTypes(desc);
                if (!name.equals(method.getName()) || !sameType(args, method.getParameterTypes())) {
                    return super.visitMethod(access, name, desc, signature, exceptions);
                }
                MethodVisitor v = super.visitMethod(access, name, desc, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM5, v) {
                    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
                        int i = index - 1;
                        if (Modifier.isStatic(method.getModifiers())) {
                            i = index;
                        }
                        if (i >= 0 && i < paramNames.length) {
                            paramNames[i] = name;
                        }
                        super.visitLocalVariable(name, desc, signature, start, end, index);
                    }
                };
            }
        }, 0);
        return paramNames;
    }

    /**
     * 比较参数类型是否一致
     *
     * @param types   asm的类型({@link Type})
     * @param classes java 类型({@link Class})
     * @return
     */
    private static boolean sameType(Type[] types, Class<?>[] classes) {
        if (types.length != classes.length) {
            return false;
        }
        for (int i = 0; i < types.length; i++) {
            if (!Type.getType(classes[i]).equals(types[i])) {
                return false;
            }
        }
        return true;
    }
}
