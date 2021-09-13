package com.example.myspring.aop;

import com.example.myspring.aop.aspect.AspectInfo;
import com.example.myspring.util.CommonUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AspectListExecutor implements MethodInterceptor {
    private Class<?> targetClass;
    private List<AspectInfo> roughMatchedAspectList;

    public AspectListExecutor(Class<?> clazz, List<AspectInfo> list) {
        targetClass = clazz;
        list.sort(Comparator.comparingInt(AspectInfo::getOrder));
        roughMatchedAspectList = list;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object returnValue = null;
        List<AspectInfo> accurateMatchedAspectList = accurateMatch(method);
        if (CommonUtil.isEmpty(roughMatchedAspectList)) {
            return methodProxy.invokeSuper(o,args);
        }
        invokeBeforeAspects(accurateMatchedAspectList,method,args);
        try {
            returnValue = methodProxy.invokeSuper(o, args);
            returnValue = invokeAfterReturningAspects(accurateMatchedAspectList,method, args, returnValue);
        } catch (Throwable e) {
            invokeAfterThrowingAspects(accurateMatchedAspectList,method, args, e);
        }
        return returnValue;
    }

    private List<AspectInfo> accurateMatch(Method method) {
        //这样写是不行的，这里直接减少roughMatchedAspectList会导致后面得方法匹配不到被去掉的aspect，所以需要一个精确匹配的拷贝来执行
        /*
        Iterator<AspectInfo> iterator = roughMatchedAspectList.listIterator();
        while (iterator.hasNext()) {
            AspectInfo info = iterator.next();
            if (!info.getLocator().accurateMatch(method)) {
                iterator.remove();
            }
        }
        */
        List<AspectInfo> accurateMatchedAspectList = new ArrayList<>();
        for (AspectInfo info: roughMatchedAspectList) {
            if (info.getLocator().accurateMatch(method)) {
                accurateMatchedAspectList.add(info);
            }
        }


        return accurateMatchedAspectList;
    }

    private void invokeBeforeAspects(List<AspectInfo> accurateMatchedAspectList,Method method, Object[] args) throws Throwable {
        for (AspectInfo aspectInfo: accurateMatchedAspectList) {
            aspectInfo.getAspect().before(targetClass, method, args);
        }
    }

    private Object invokeAfterReturningAspects(List<AspectInfo> accurateMatchedAspectList,Method method, Object[] args, Object returnValue) throws Throwable {
        for (int i = accurateMatchedAspectList.size()-1; i>=0; --i) {
            returnValue = accurateMatchedAspectList.get(i).getAspect().afterReturning(targetClass, method, args, returnValue);
        }
        return returnValue;
    }

    private void invokeAfterThrowingAspects(List<AspectInfo> accurateMatchedAspectList, Method method, Object[] args, Throwable e) throws Throwable {
        for (int i = accurateMatchedAspectList.size()-1; i>=0; --i) {
            accurateMatchedAspectList.get(i).getAspect().afterThrowing(targetClass, method, args, e);
        }
    }
}
