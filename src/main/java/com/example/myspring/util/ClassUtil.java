package com.example.myspring.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ClassUtil {
    public static Set<Class<?>> getClassesFromPackage(String packageName) {
        ClassLoader classLoader = getClassLoader();
        URL url = classLoader.getResource(packageName.replace(".","/"));
        if (url==null) {
            log.warn("URL is null!");
            return null;
        }
        Set<Class<?>> classSet = new HashSet<>();
        if (url.getProtocol().equals("file")) {
            File file = new File(url.getPath());
            getClasses(classSet,file,packageName);
        }
        return classSet;
    }

    public static void getClasses(Set<Class<?>> classSet, File targetFile, String packageName) {
        if (targetFile.isFile()) {
            return;
        }
        File []dictionaries = targetFile.listFiles(new FileFilter() {
            @SneakyThrows
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    String urlByDot = file.getAbsolutePath().replace(File.separator, ".");
                    String className = urlByDot.substring(urlByDot.indexOf(packageName),urlByDot.lastIndexOf("."));
                    classSet.add(loadClass(className));
                    return false;
                }
            }
        });
        if (dictionaries!=null) {
            for (File dictionary: dictionaries) {
                getClasses(classSet, dictionary, packageName);
            }
        }
    }

    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static Class<?> loadClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    public static <T> T getInstance(Class<T> clazz, boolean access) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(access);
        return constructor.newInstance();
    }

    public static void setField(Field field, Object target, Object value, boolean access) throws IllegalAccessException {
        field.setAccessible(access);
        field.set(target,value);
    }

    public static String getSimpleClassName(Class<?> clazz) {
        return clazz.getName().substring(clazz.getName().lastIndexOf('.')+1);
    }

    public static void main(String[] args) {
        getClassesFromPackage("com.example.myspring.core");
    }
}
