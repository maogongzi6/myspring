package com.example.myspring.core;

import com.example.myspring.core.type.ObjectFactory;
import com.example.myspring.inject.BeanDefinitionFactory;
import com.example.myspring.inject.BeanDependencyInjector;
import com.example.myspring.inject.type.BeanDefinition;
import com.example.myspring.util.ClassUtil;
import com.example.myspring.util.CommonUtil;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutorService;

@NoArgsConstructor
public class BeanFactory {
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private final Set<String> beanNameSet = new HashSet<>();
    private final Map<String, Object> singletonBeans = new HashMap<>();
    private final Map<String, Object> earlySingletonBeans = new HashMap<>();
    private final Map<String, ObjectFactory> singletonFactories = new HashMap<>();
    private final Set<String> singletonConcurrentlyInCreation = Collections.newSetFromMap(new HashMap<>());
    private final Set<String> prototypeConcurrentlyInCreation = Collections.newSetFromMap(new HashMap<>());
    private final Set<String> registryBeanNameSet = Collections.newSetFromMap(new HashMap<>());
    private final Set<String> prototypeDependResolved = new HashSet<>();
    //private final Set<String> strictDependResolving = new HashSet<>();

    public void preInstantiateSingletons() {
        for (String beanName: beanNameSet) {
            if (registryBeanNameSet.contains(beanName)) {
                continue;
            }
            BeanDefinition definition = getBeanDefinition(beanName);
            if (definition.isSingleton() && !definition.isLazy()) {
                getBean(definition);
            }
        }
    }

    public Object getBean(String beanName) {
        BeanDefinition definition = getBeanDefinition(beanName);
        return doGetBean(definition);
    }

    public Object getBean(BeanDefinition definition) {
        return doGetBean(definition);
    }

    private Object doGetBean(BeanDefinition definition) {
        if (definition.isPrototype()) {
            return createBean(definition);
        }
        String beanName = definition.getBeanName();
        Object bean;
        if ((bean = singletonBeans.get(beanName))==null) {
            if ((bean = earlySingletonBeans.get(beanName))==null) {
                ObjectFactory objectFactory;
                if ((objectFactory = singletonFactories.get(beanName))==null) {
                    bean = createBean(definition);
                } else {
                    bean = objectFactory.getObject();
                    singletonFactories.remove(beanName);
                    earlySingletonBeans.put(beanName,bean);
                }
            }
        }
        return bean;
    }

    private Object createBean(BeanDefinition definition) {
        String beanName = definition.getBeanName();
        addCurrentlyInCreation(definition);
        String[] dependsOn = definition.getDependsOn();
        if (!prototypeDependResolved.contains(beanName) && !CommonUtil.isEmpty(dependsOn)) {
            resolveDependsOn(dependsOn);
            if (definition.isPrototype()) {
                prototypeDependResolved.add(beanName);
            }
        }
        Object bean = doCreateBean(definition);
        beanCreateFinished(definition,bean);
        return bean;
    }

    private Object doCreateBean(BeanDefinition definition) {
        String beanName = definition.getBeanName();
        Object bean = getBeanInstance(definition);
        if (definition.isSingleton()) {
            singletonFactories.put(beanName, () -> getEarlySingleton(beanName, bean));
        }
        BeanDependencyInjector.populate(bean);
        return bean;
    }

    private void addCurrentlyInCreation(BeanDefinition definition) {
        String beanName = definition.getBeanName();
        if (definition.isSingleton()) {
            if (singletonConcurrentlyInCreation.contains(beanName)) {
                throw new RuntimeException("CIRCLE DEPEND ERROR!! " + beanName);
            }
            singletonConcurrentlyInCreation.add(definition.getBeanName());
        } else if (definition.isPrototype()) {
            if (prototypeConcurrentlyInCreation.contains(beanName)) {
                throw new RuntimeException("CIRCLE DEPEND ERROR!! " + beanName);
            }
            prototypeConcurrentlyInCreation.add(definition.getBeanName());
        }
    }

    private void resolveDependsOn(String[] dependsOn) {
        for (String depend: dependsOn) {
            BeanDefinition definition = getBeanDefinition(depend);
            if (definition.isPrototype()) {
                throw new RuntimeException(definition.getBeanName()+" is prototype which cannot be depended on");
            }
            getBean(definition);
        }
    }

    private Object getEarlySingleton(String beanName, Object bean) {
        singletonFactories.remove(beanName);
        Object beanWrapper = doAopIfNecessary(bean);
        earlySingletonBeans.put(beanName, bean);
        return beanWrapper;
    }

    private Object doAopIfNecessary(Object bean) {
        return bean;
    }

    private void beanCreateFinished(BeanDefinition definition, Object bean) {
        String beanName = definition.getBeanName();
        if (definition.isSingleton()) {
            singletonFactories.remove(beanName);
            earlySingletonBeans.remove(beanName);
            singletonBeans.put(beanName, bean);
            singletonConcurrentlyInCreation.remove(beanName);
        } else if (definition.isPrototype()) {
            prototypeConcurrentlyInCreation.remove(beanName);
        }
    }

    private Object getBeanInstance(BeanDefinition definition) {
        try {
            Object object = ClassUtil.getInstance(definition.getTargetClass(), true);
            return object;
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public BeanDefinition addBeanDefinition(Class<?> clazz) {
        BeanDefinition beanDefinition = BeanDefinitionFactory.createBeanDefinition(clazz);
        beanDefinitionMap.put(beanDefinition.getBeanName(), beanDefinition);
        beanNameSet.add(clazz.getName());
        return beanDefinition;
    }

    public BeanDefinition removeBeanDefinition(Class<?> clazz) {
        return beanDefinitionMap.remove(clazz.getCanonicalName());
    }

    public BeanDefinition removeBeanDefinition(String canonicalName) {
        return beanDefinitionMap.remove(canonicalName);
    }

    public BeanDefinition getBeanDefinition(Class<?> clazz) {
        return beanDefinitionMap.get(clazz.getCanonicalName());
    }

    public BeanDefinition getBeanDefinition(String canonicalName) {
        return beanDefinitionMap.get(canonicalName);
    }

    //这里使用Set.copyOf()返回一个不可变的集合，防止外部修改我们的beanClasses
    public Set<String> getBeanNames(){
        return Set.copyOf(beanNameSet);
    }

}
