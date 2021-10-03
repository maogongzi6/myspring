package com.example.myspring.inject;

import com.example.myspring.core.ApplicationContext;
import com.example.myspring.core.BeanFactory;
import com.example.myspring.inject.annotation.Autowired;
import com.example.myspring.util.ClassUtil;
import com.example.myspring.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class BeanDependencyInjector {
    private static final BeanFactory beanFactory = ApplicationContext.getInstance().getBeanFactory();

    static public void populate(Object bean) {
        Field[]fields = bean.getClass().getDeclaredFields();
        for (Field field: fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                Class<?> fieldType = field.getType();
                Object obj = getFieldInstance(fieldType, autowired.value());
                if (obj!=null) {
                    try {
                        ClassUtil.setField(field, bean, obj, true);
                    } catch (IllegalAccessException e) {
                        log.error("illegal access for: "+bean);
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException("ioc failed by " + bean.getClass()+", field "+ field.getName());
                }
            }
        }
    }

    static private Object getFieldInstance(Class<?> clazz, String value) {
        if (CommonUtil.isEmpty(value)) {
            return beanFactory.getBean(clazz.getCanonicalName());
        } else {
            return beanFactory.getBean(value);
        }
    }

}
