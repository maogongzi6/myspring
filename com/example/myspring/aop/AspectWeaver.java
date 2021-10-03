package com.example.myspring.aop;

import com.example.myspring.aop.annotion.Aspect;
import com.example.myspring.aop.annotion.Order;
import com.example.myspring.aop.aspect.AspectInfo;
import com.example.myspring.aop.aspect.DefaultAspect;
import com.example.myspring.core.BeanContainer;
import com.example.myspring.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AspectWeaver {
    private BeanContainer beanContainer;
    public AspectWeaver() {
        beanContainer = BeanContainer.getInstance();
    }

    public void doAop() {
        Set<Class<?>> aspectClasses = beanContainer.getClassByAnnotation(Aspect.class);
        if (CommonUtil.isEmpty(aspectClasses)) {
            return;
        }
        List<AspectInfo> aspectInfoList = getAspectInfoList(aspectClasses);
        for (Class<?> targetClass: beanContainer.getClasses()) {
            if (targetClass.isAnnotationPresent(Aspect.class)) {
                continue;
            }
            List<AspectInfo> roughMatchedList = getRoughMatchedList(targetClass, aspectInfoList);
            weaveIfMatched(targetClass, roughMatchedList);
        }
    }


    private List<AspectInfo> getAspectInfoList(Set<Class<?>> classes) {
        List<AspectInfo> aspectInfos = new ArrayList<>();
        for (Class<?> clazz: classes) {
            if (!verifyAspectClass(clazz)) {
                throw new RuntimeException("wrong use of aop annotation");
            }
            Order order = clazz.getAnnotation(Order.class);
            Aspect aspect = clazz.getAnnotation(Aspect.class);
            PointcutLocator locator = new PointcutLocator(aspect.pointcut());
            DefaultAspect defaultAspect = (DefaultAspect) beanContainer.getBean(clazz);
            aspectInfos.add(new AspectInfo(locator,defaultAspect,order.order()));
        }
        return aspectInfos;
    }

    private List<AspectInfo> getRoughMatchedList(Class<?> targetClass, List<AspectInfo> aspectInfoList) {
        List<AspectInfo> roughMatchedList = new ArrayList<>();
        for (AspectInfo aspectInfo: aspectInfoList) {
            if (aspectInfo.getLocator().roughMatch(targetClass)) {
                roughMatchedList.add(aspectInfo);
            }
        }
        return roughMatchedList;
    }

    private void weaveIfMatched(Class<?> clazz, List<AspectInfo> roughMatchedList) {
        if (CommonUtil.isEmpty(roughMatchedList)) {
            return;
        }
        AspectListExecutor aspectListExecutor = new AspectListExecutor(clazz,roughMatchedList);
        Object aspect = ProxyCreator.createProxy(clazz,aspectListExecutor);
        beanContainer.addBean(clazz,aspect);
    }

    private boolean verifyAspectClass(Class<?> aspectClass) {
        return aspectClass.isAnnotationPresent(Order.class) && aspectClass.isAnnotationPresent(Aspect.class) && DefaultAspect.class.isAssignableFrom(aspectClass);
    }
}
