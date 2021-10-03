package com.example.myspring.aop.aspect;

import java.lang.reflect.Method;

public abstract class DefaultAspect {
    public void before(Class<?> clazz, Method method, Object[] args) throws Throwable {}
    public Object afterReturning(Class<?> clazz, Method method, Object[] args, Object returnValue) throws Throwable {
        return returnValue;
    }
    public void afterThrowing(Class<?> clazz, Method method, Object[] args, Throwable e) throws Throwable {}
}
