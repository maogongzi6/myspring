package com.example.myspring.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class ProxyCreator {
    public static Object createProxy(Class<?> clazz, MethodInterceptor interceptor) {
        return Enhancer.create(clazz, interceptor);
    }
}
