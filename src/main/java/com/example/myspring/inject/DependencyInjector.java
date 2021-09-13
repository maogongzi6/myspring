package com.example.myspring.inject;

import com.example.myspring.core.BeanContainer;
import com.example.myspring.inject.annotation.Autowired;
import com.example.myspring.util.ClassUtil;
import com.example.myspring.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

@Slf4j
public class DependencyInjector {
    private static final BeanContainer beanContainer = BeanContainer.getInstance();

    public void doIoC() {
        if (CommonUtil.isEmpty(beanContainer.getClasses())) {
            return;
        }
        for (Class<?> clazz: beanContainer.getClasses()) {
            Field []fields = clazz.getDeclaredFields();
            for (Field field: fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    Class<?> fieldType = field.getType();
                    String fieldName = field.getName();
                    Object obj = getFieldInstance(fieldType, fieldName, autowired.value());
                    if (obj!=null) {
                        Object target = beanContainer.getBean(clazz);
                        try {
                            ClassUtil.setField(field, target, obj, true);
                        } catch (IllegalAccessException e) {
                            log.error("illegal access for: "+target);
                            throw new RuntimeException(e);
                        }
                    } else {
                        throw new RuntimeException("ioc failed by" + clazz);
                    }
                }
            }
        }
    }

    private Object getFieldInstance(Class<?> clazz, String name, String annotationValue) {
        Object obj = beanContainer.getBean(clazz);
        if (obj!=null) {
            return obj;
        }
        return getImplInstance(clazz, name, annotationValue);
    }

    //当有多个实现时，如果AutoWired的value被设置了则按照其值查找，如果没有被设置则查找与域名相同的实现类
    private Object getImplInstance(Class<?> clazz, String name, String annotationValue) {
        Set<Class<?>> classSet = beanContainer.getClassesBySuper(clazz);
        if (CommonUtil.isEmpty(classSet)) {
            return null;
        }
        if (classSet.size()==1) {
            return beanContainer.getBean(classSet.toArray(new Class<?>[1])[0]);
        } else {
            Class<?> sameNameClass = null;
            for (Class<?> implClass: classSet) {
                if (implClass.getName().equals(annotationValue)) {
                    return beanContainer.getBean(implClass);
                } else if (annotationValue.equals("")) {
                    char []arr = name.toCharArray();
                    arr[0] = (arr[0]>='a' && arr[0]<='z') ? (char)(arr[0]+'A'-'a') : arr[0];
                    name = String.valueOf(arr);
                    if (name.equals(ClassUtil.getSimpleClassName(implClass))) {
                        sameNameClass = implClass;
                    }
                }
            }
            return sameNameClass==null ? null : beanContainer.getBean(sameNameClass);
        }
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        BeanContainer.getInstance().loadBeans("com/test");
        DependencyInjector d = new DependencyInjector();
        d.doIoC();
        System.out.println();
    }
}
