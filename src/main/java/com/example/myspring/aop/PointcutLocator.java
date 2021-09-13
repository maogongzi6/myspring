package com.example.myspring.aop;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;

import java.lang.reflect.Method;

public class PointcutLocator {
    private PointcutParser parser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingContextClassloaderForResolution(
            PointcutParser.getAllSupportedPointcutPrimitives()
    );
    private PointcutExpression expression;

    public PointcutLocator(String str) {
        expression = parser.parsePointcutExpression(str);
    }

    public boolean roughMatch(Class<?> clazz) {
        return expression.couldMatchJoinPointsInType(clazz);
    }

    public boolean accurateMatch(Method method) {
        return expression.matchesMethodExecution(method).alwaysMatches();
    }
}
