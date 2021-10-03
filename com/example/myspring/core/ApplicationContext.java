package com.example.myspring.core;

import com.example.myspring.aop.annotion.Aspect;
import com.example.myspring.core.annotation.Component;
import com.example.myspring.core.annotation.Controller;
import com.example.myspring.core.annotation.Repository;
import com.example.myspring.core.annotation.Service;
import com.example.myspring.inject.type.BeanDefinition;
import com.example.myspring.util.ClassUtil;
import com.example.myspring.util.CommonUtil;
import demo2.circle.prototype.ManP;
import demo2.circle.prototype.WomenP;
import demo2.depend.PrototypeTest;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Slf4j
public class ApplicationContext {
    //这里封装一个BeanDefinitionContainer是为了保存其beanDefinitionMap，beanClasses还有三级缓存之间的一致性，防止以后编写ApplicationContext的代码时无意间打破了一致性
    private final BeanFactory beanFactory = new BeanFactory();
    private static final List<Class<? extends Annotation>> markAnnotationList = Arrays.asList(Component.class, Controller.class, Repository.class, Service.class, Aspect.class);
    private boolean loaded = false;

    private static final ApplicationContext instance = new ApplicationContext();

    private ApplicationContext() {}

    public static ApplicationContext getInstance() {
        return instance;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public synchronized void loadBeanDefinition(String packageName) {
        if (loaded) {
            log.warn("application context has already bean loaded");
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
                    addBeanDefinition(clazz);
                    break;
                }
            }
        }
        loaded = true;
    }

    public void refresh() {
        beanFactory.preInstantiateSingletons();
    }

    public Object getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }

    public Object getBean(Class<?> clazz) {
        return beanFactory.getBean(clazz.getCanonicalName());
    }

    public BeanDefinition addBeanDefinition(Class<?> clazz) {
        return beanFactory.addBeanDefinition(clazz);
    }

    public BeanDefinition removeBeanDefinition(Class<?> clazz) {
        return beanFactory.removeBeanDefinition(clazz);
    }

    public BeanDefinition removeBeanDefinition(String canonicalName) {
        return beanFactory.removeBeanDefinition(canonicalName);
    }

    public BeanDefinition getBeanDefinition(Class<?> clazz) {
        return beanFactory.getBeanDefinition(clazz);
    }

    public BeanDefinition getBeanDefinition(String canonicalName) {
        return beanFactory.getBeanDefinition(canonicalName);
    }

    //这里使用Set.copyOf()返回一个不可变的集合，防止外部修改我们的beanClasses
    public Set<String> getBeanNames(){
        return beanFactory.getBeanNames();
    }
/*
    public Set<Class<?>> getClassByAnnotation(Class<? extends Annotation> annotationClass) {
        Set<Class<?>> classSet = new HashSet<>(), beanNam = beanFactory.get();
        if (CommonUtil.isEmpty(beanClasses)) {
            return null;
        }
        for (Class<?> clazz: beanClasses) {
            if (clazz.isAnnotationPresent(annotationClass)) {
                classSet.add(clazz);
            }
        }
        return CommonUtil.isEmpty(classSet) ? null : classSet;
    }

    public Set<Class<?>> getClassesBySuper(Class<?> interfaceOrClass){
        Set<Class<?>> beanClasses = getClasses();
        if(CommonUtil.isEmpty(beanClasses)){
            log.warn("nothing in beanMap");
            return null;
        }
        Set<Class<?>> classSet = new HashSet<>();
        for(Class<?> clazz : beanClasses){
            if(interfaceOrClass.isAssignableFrom(clazz) && !clazz.equals(interfaceOrClass)){
                classSet.add(clazz);
            }
        }
        return classSet.size() > 0? classSet: null;
    }
*/

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ApplicationContext applicationContext = ApplicationContext.getInstance();
        applicationContext.loadBeanDefinition("demo2.depend");
        applicationContext.refresh();
        applicationContext.getBean(PrototypeTest.class);
        applicationContext.getBean(PrototypeTest.class);
        //applicationContext.getBean(WomenP.class);
       // System.out.println(applicationContext.getClassByAnnotation(Aspect.class));
        System.out.println();
    }

}
