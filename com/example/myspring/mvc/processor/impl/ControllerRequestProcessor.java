package com.example.myspring.mvc.processor.impl;

import com.example.myspring.core.BeanContainer;
import com.example.myspring.mvc.RequestProcessorChain;
import com.example.myspring.mvc.annotation.RequestMapping;
import com.example.myspring.mvc.annotation.RequestParam;
import com.example.myspring.mvc.annotation.ResponseBody;
import com.example.myspring.mvc.processor.RequestProcessor;
import com.example.myspring.mvc.render.ResultRender;
import com.example.myspring.mvc.render.impl.JosnResultRender;
import com.example.myspring.mvc.render.impl.ResultNotFoundRender;
import com.example.myspring.mvc.render.impl.ViewResultRender;
import com.example.myspring.mvc.type.ControllerMethod;
import com.example.myspring.mvc.type.ParameterClassWithIndex;
import com.example.myspring.mvc.type.RequestPathInfo;
import com.example.myspring.util.CommonUtil;
import com.example.myspring.util.ConverterUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ControllerRequestProcessor implements RequestProcessor {
    private BeanContainer beanContainer;
    private Map<RequestPathInfo, ControllerMethod> path2ControllerMethodMap = new HashMap<>();

    public ControllerRequestProcessor() {
        beanContainer = BeanContainer.getInstance();
        Set<Class<?>> classSet = beanContainer.getClassByAnnotation(RequestMapping.class);
        initControllerRequestProcessor(classSet);
    }

    private void initControllerRequestProcessor(Set<Class<?>> classSet) {
        if (CommonUtil.isEmpty(classSet)) {
            return;
        }
        for (Class<?> controllerClass: classSet) {
            RequestMapping requestMapping = controllerClass.getAnnotation(RequestMapping.class);
            String base = CommonUtil.getPathStartWith(requestMapping.value(),"/");
            Method[] methods = controllerClass.getDeclaredMethods();
            if (CommonUtil.isEmpty(methods)) {
                return;
            }
            for (Method method: methods) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    return;
                }
                method.setAccessible(true);
                RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
                String methodPath = methodMapping.value();
                Parameter[] parameters = method.getParameters();
                Map<String, ParameterClassWithIndex> parameterMap = new HashMap<>();
                if (!CommonUtil.isEmpty(parameters)) {
                    for (int i = 0; i < parameters.length; ++i) {
                        addParameterToMap(parameters[i], i, parameterMap);
                    }
                }
                String httpMethod = String.valueOf(requestMapping.method());
                String url = base+methodPath;
                RequestPathInfo pathInfo = new RequestPathInfo(url,httpMethod);
                if (path2ControllerMethodMap.containsKey(pathInfo)) {
                    log.warn("same url for different method: "+url);
                }
                path2ControllerMethodMap.put(pathInfo, new ControllerMethod(controllerClass, method, parameterMap));
            }
        }
    }

    private void addParameterToMap(Parameter parameter, int index, Map<String, ParameterClassWithIndex> map) {
        if (!parameter.isAnnotationPresent(RequestParam.class)) {
            throw new RuntimeException("param not marked by RequestParam! "+parameter.getName());
        }
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        map.put(requestParam.value(), new ParameterClassWithIndex(parameter.getType(),index));
    }

    @Override
    public boolean doProcess(RequestProcessorChain processorChain) throws Exception {
        String requestMethod = processorChain.getRequestMethod();
        String requestPath = processorChain.getRequestPath();
        RequestPathInfo pathInfo = new RequestPathInfo(requestPath,requestMethod);
        ControllerMethod controllerMethod = path2ControllerMethodMap.get(pathInfo);
        if (controllerMethod==null) {
            processorChain.setRender(new ResultNotFoundRender(requestPath,requestMethod));
            return true;
        }
        try {
            Object result = invokeControllerMethod(controllerMethod, processorChain.getRequest());
            setRender(result,controllerMethod,processorChain);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private Object invokeControllerMethod(ControllerMethod controllerMethod, HttpServletRequest request) throws Throwable {
        Method method = controllerMethod.getMethod();
        Class<?> clazz = controllerMethod.getControllerClass();
        Map<String, ParameterClassWithIndex> parametersMap = controllerMethod.getParametersMap();
        Map<String, String[]> requestParameterMap = request.getParameterMap();
        Object[] matchedParameters = getMatchedParameters(requestParameterMap, parametersMap);
        method.setAccessible(true);
        Object controller = beanContainer.getBean(clazz);
        Object result = null;
        if (CommonUtil.isEmpty(matchedParameters)) {
            result = method.invoke(controller);
        } else {
            result = method.invoke(controller, matchedParameters);
        }
        return result;
    }

    private Object[] getMatchedParameters(Map<String, String[]> requestParameterMap, Map<String,ParameterClassWithIndex> methodParameterNameMap) {
        Object[] matchedParameters = new Object[methodParameterNameMap.size()];
        for (Map.Entry<String,ParameterClassWithIndex> entry: methodParameterNameMap.entrySet()) {
            String name = entry.getKey();
            int index = entry.getValue().getIndex();
            Class<?> clazz = entry.getValue().getClazz();
            String[] args = requestParameterMap.get(name);
            if (CommonUtil.isEmpty(args)) {
                throw new RuntimeException("no http parameter named:"+name);
            }
            String arg = args[0];
            if (CommonUtil.isEmpty(arg)) {
                matchedParameters[index] = ConverterUtil.primitiveNull(clazz);
            } else {
                matchedParameters[index] = ConverterUtil.convert(clazz, arg);
            }
        }
        return matchedParameters;
    }

    private void setRender(Object result, ControllerMethod controllerMethod, RequestProcessorChain processorChain) throws Exception {
        if (result==null) {
            return;
        }
        ResultRender resultRender = null;
        if (controllerMethod.getMethod().isAnnotationPresent(ResponseBody.class)) {
            resultRender = new JosnResultRender(result);
        } else {
            resultRender = new ViewResultRender(result);
        }
        processorChain.setRender(resultRender);
    }
}
