package com.example.myspring.mvc.processor.impl;

import com.example.myspring.mvc.RequestProcessorChain;
import com.example.myspring.mvc.processor.RequestProcessor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

public class StaticResourceRequestProcessor implements RequestProcessor {
    public static final String DEFAULT_TOMCAT_SERVLET = "default";
    public static final String STATIC_RESOURCE_PREFIX = "/static/";
    private RequestDispatcher defaultDispatcher;

    public StaticResourceRequestProcessor(ServletContext servletContext) throws Exception {
        defaultDispatcher = servletContext.getNamedDispatcher(DEFAULT_TOMCAT_SERVLET);
        if (defaultDispatcher==null) {
            throw new Exception("defaultDispatcher not found");
        }
    }

    @Override
    public boolean doProcess(RequestProcessorChain processorChain) throws Exception {
        String path = processorChain.getRequestPath();
        if (isStaticSource(path)) {
            defaultDispatcher.forward(processorChain.getRequest(),processorChain.getResponse());
            return false;
        }
        return true;
    }

    private boolean isStaticSource(String path) {
        return path.startsWith(STATIC_RESOURCE_PREFIX);
    }
}
