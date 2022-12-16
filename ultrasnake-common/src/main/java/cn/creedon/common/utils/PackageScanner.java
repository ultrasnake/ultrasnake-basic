package cn.creedon.common.utils;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
public class PackageScanner {

    /**
     * 获取目标目录下的所有Class
     *
     * @param targetPackageName 目标包名称
     * @return 目标包路径下所有类集合
     */
    public static Set<Class<?>> scan(final String targetPackageName) {
        ClassLoader targetClassLoader = Thread.currentThread().getContextClassLoader();
        return scan(targetClassLoader, targetPackageName, true);
    }

    /**
     * 获取目标目录下的所有Class
     *
     * @param targetPackageName 目标包名称
     * @param ignoreAnonymous   忽略匿名内部类
     * @return 目标包路径下所有类集合
     */
    public static Set<Class<?>> scan(final String targetPackageName, final boolean ignoreAnonymous) {
        ClassLoader targetClassLoader = Thread.currentThread().getContextClassLoader();
        return scan(targetClassLoader, targetPackageName, ignoreAnonymous);
    }

    /**
     * 获取目标目录下的所有Class
     *
     * @param targetClassLoader 目标ClassLoader
     * @param targetPackageName 目标包名称
     * @param ignoreAnonymous   忽略内部类
     * @return 目标包路径下所有类集合
     */
    public static Set<Class<?>> scan(final ClassLoader targetClassLoader, final String targetPackageName, final boolean ignoreAnonymous) {
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        String packageDirName = targetPackageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = targetClassLoader.getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equalsIgnoreCase(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesByFile(targetClassLoader, targetPackageName, filePath, classes, ignoreAnonymous);
                } else if ("jar".equalsIgnoreCase(protocol)) {
                    JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                    findAndAddClassesByJars(targetClassLoader, targetPackageName, jarFile, classes, ignoreAnonymous);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param classLoader     目标ClassLoader
     * @param packageName     目标包名称
     * @param packagePath     packagePath
     * @param classes         classes
     * @param ignoreAnonymous 忽略匿名内部类
     */
    private static void findAndAddClassesByFile(ClassLoader classLoader,
                                                String packageName,
                                                String packagePath,
                                                Set<Class<?>> classes,
                                                boolean ignoreAnonymous) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (ignoreAnonymous) {
                    return (file.isDirectory() || file.getName().endsWith(".class")) && !file.getName().contains("$");
                } else {
                    return file.isDirectory() || file.getName().endsWith(".class");
                }
            }
        });
        if (dirFiles != null) {
            for (File file : dirFiles) {
                if (file.isDirectory()) {
                    findAndAddClassesByFile(
                            classLoader,
                            packageName + "." + file.getName(),
                            file.getAbsolutePath(),
                            classes,
                            ignoreAnonymous
                    );
                } else {
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    try {
                        classes.add(classLoader.loadClass(packageName + '.' + className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 以Jar包的形式来获取包下的所有Class
     *
     * @param classLoader     目标ClassLoader
     * @param packageName     目标包名称
     * @param jarFile         jarFile
     * @param classes         classes
     * @param ignoreAnonymous 忽略匿名内部类
     */
    private static void findAndAddClassesByJars(ClassLoader classLoader,
                                                String packageName,
                                                JarFile jarFile,
                                                Set<Class<?>> classes,
                                                boolean ignoreAnonymous) {
        Enumeration<JarEntry> entries = jarFile.entries();
        String packageDirName = packageName.replace('.', '/');
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                if (idx != -1) {
                    packageName = name.substring(0, idx).replace('/', '.');
                }
                if (name.endsWith(".class") && !entry.isDirectory()) {
                    if (ignoreAnonymous && name.contains("$")) {
                        continue;
                    }
                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                    try {
                        classes.add(classLoader.loadClass(packageName + '.' + className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
