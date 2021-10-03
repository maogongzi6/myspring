package com.example.myspring.inject.type;

import lombok.Data;

@Data
public class BeanDefinition {
    public BeanDefinition(Class<?> targetClass) {
        this.targetClass = targetClass;
        this.beanName = targetClass.getCanonicalName();
    }

    public boolean isSingleton() {
        return scopeType==ScopeType.SINGLETON;
    }

    public boolean isPrototype() {
        return scopeType==ScopeType.PROTOTYPE;
    }

    private Class<?> targetClass;
    private String beanName;
    private ScopeType scopeType = ScopeType.SINGLETON;
    private boolean isLazy = false;
    private String[] dependsOn;
}
