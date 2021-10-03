package com.example.myspring.inject;

import com.example.myspring.inject.annotation.*;
import com.example.myspring.inject.type.BeanDefinition;

public class BeanDefinitionFactory {
    static public BeanDefinition createBeanDefinition(Class<?> clazz) {
        BeanDefinition beanDefinition = new BeanDefinition(clazz);
        if (clazz.isAnnotationPresent(Lazy.class)) {
            beanDefinition.setLazy(clazz.getAnnotation(Lazy.class).isLazy());
        }
        if (clazz.isAnnotationPresent(Scope.class)) {
            beanDefinition.setScopeType(clazz.getAnnotation(Scope.class).scope());
        }
        if (clazz.isAnnotationPresent(DependsOn.class)) {
            beanDefinition.setDependsOn(clazz.getAnnotation(DependsOn.class).dependsOn());
        }
        return beanDefinition;
    }
}
