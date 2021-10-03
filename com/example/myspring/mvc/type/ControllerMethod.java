package com.example.myspring.mvc.type;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;

@AllArgsConstructor
@Data
public class ControllerMethod {
    private Class<?> controllerClass;
    private Method method;
    private Map<String, ParameterClassWithIndex> parametersMap;
}
