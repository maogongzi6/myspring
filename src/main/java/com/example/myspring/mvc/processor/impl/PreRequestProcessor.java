package com.example.myspring.mvc.processor.impl;

import com.example.myspring.mvc.RequestProcessorChain;
import com.example.myspring.mvc.processor.RequestProcessor;

import javax.servlet.http.HttpServletRequest;

public class PreRequestProcessor implements RequestProcessor {
    @Override
    public boolean doProcess(RequestProcessorChain processorChain) throws Exception {
        HttpServletRequest request = processorChain.getRequest();
        request.setCharacterEncoding("UTF-8");
        String requestPath = processorChain.getRequestPath();
        if (requestPath.length()>1 && requestPath.endsWith("/")) {
            processorChain.setRequestPath(requestPath.substring(0,requestPath.length()-1));
        }
        return true;
    }
}
