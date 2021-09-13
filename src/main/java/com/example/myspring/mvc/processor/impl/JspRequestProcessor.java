package com.example.myspring.mvc.processor.impl;

import com.example.myspring.mvc.RequestProcessorChain;
import com.example.myspring.mvc.processor.RequestProcessor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

public class JspRequestProcessor implements RequestProcessor {
    private static final String JSP_SERVLET = "jsp";
    private static final String  JSP_RESOURCE_PREFIX = "/templates/";
    private RequestDispatcher dispatcher;

    public JspRequestProcessor(ServletContext context) {
        dispatcher = context.getNamedDispatcher(JSP_SERVLET);
    }

    @Override
    public boolean doProcess(RequestProcessorChain processorChain) throws Exception {
        if (isJspResource(processorChain.getRequestPath())) {
            dispatcher.forward(processorChain.getRequest(), processorChain.getResponse());
            return false;
        }
        return true;
    }

    private boolean isJspResource(String requestPath) {
        return requestPath.startsWith(JSP_RESOURCE_PREFIX);
    }
}
