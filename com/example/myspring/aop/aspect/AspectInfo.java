package com.example.myspring.aop.aspect;

import com.example.myspring.aop.PointcutLocator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AspectInfo {
    private PointcutLocator locator;
    private DefaultAspect aspect;
    private int order;
}
