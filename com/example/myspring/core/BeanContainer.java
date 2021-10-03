package com.example.myspring.core;

import com.example.myspring.aop.annotion.Aspect;
import com.example.myspring.core.annotation.Component;
import com.example.myspring.core.annotation.Controller;
import com.example.myspring.core.annotation.Repository;
import com.example.myspring.core.annotation.Service;
import com.example.myspring.util.ClassUtil;
import com.example.myspring.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BeanContainer {
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();
    private static final List<Class<? extends Annotation>> markAnnotationList = Arrays.asList(Component.class, Controller.class, Repository.class, Service.class, Aspect.class);
    private boolean loaded = false;

    private static final BeanContainer instance = new BeanContainer();

    private BeanContainer() {}

    public static BeanContainer getInstance() {
        return instance;
    }

    public synchronized void loadBeans(String packageName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (loaded) {
            log.warn("bean container has already bean loaded");
            return;
        }
        Set<Class<?>> classSet = ClassUtil.getClassesFromPackage(packageName);
        if (CommonUtil.isEmpty(classSet)) {
            log.warn("no class detected!");
            return;
        }
        for (Class<?> clazz: classSet) {
            for (Class<? extends Annotation> annotationClass: markAnnotationList) {
                if (clazz.isAnnotationPresent(annotationClass)) {
                    try {
                        Object obj = ClassUtil.getInstance(clazz,true);
                        beanMap.put(clazz, obj);
                        break;
                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        log.error("load bean fail for: "+clazz);
                        throw e;
                    }
                }
            }
        }
        loaded = true;
    }

    public Object addBean(Class<?> clazz, Object bean) {
        return beanMap.put(clazz, bean);
    }

    public Object removeBean(Class<?> clazz) {
        return beanMap.remove(clazz);
    }

    public Object getBean(Class<?> clazz) {
        return beanMap.get(clazz);
    }

    public Set<Class<?>> getClasses(){
        return beanMap.keySet();
    }

    public Set<Object> getBeans(){
        return new HashSet<>( beanMap.values());
    }

    public Set<Class<?>> getClassByAnnotation(Class<? extends Annotation> annotationClass) {
        Set<Class<?>> classSet = new HashSet<>(), keySet = beanMap.keySet();
        if (CommonUtil.isEmpty(keySet)) {
            return null;
        }
        for (Class<?> clazz: keySet) {
            if (clazz.isAnnotationPresent(annotationClass)) {
                classSet.add(clazz);
            }
        }
        return CommonUtil.isEmpty(classSet) ? null : classSet;
    }

    public Set<Class<?>> getClassesBySuper(Class<?> interfaceOrClass){
        Set<Class<?>> keySet = getClasses();
        if(CommonUtil.isEmpty(keySet)){
            log.warn("nothing in beanMap");
            return null;
        }
        Set<Class<?>> classSet = new HashSet<>();
        for(Class<?> clazz : keySet){
            if(interfaceOrClass.isAssignableFrom(clazz) && !clazz.equals(interfaceOrClass)){
                classSet.add(clazz);
            }
        }
        return classSet.size() > 0? classSet: null;
    }


    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        BeanContainer container = BeanContainer.getInstance();
        container.loadBeans("com/test");
        container.getClassByAnnotation(Repository.class);
    }
}
