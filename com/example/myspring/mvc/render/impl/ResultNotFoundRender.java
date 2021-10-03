package com.example.myspring.mvc.render.impl;

import com.example.myspring.mvc.RequestProcessorChain;
import com.example.myspring.mvc.render.ResultRender;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;

@AllArgsConstructor
public class ResultNotFoundRender implements ResultRender {
    private String httpPath;
    private String method;

    @Override
    public void render(RequestProcessorChain processorChain) throws Exception {
        processorChain.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND, httpPath+"/"+method+" not found");
    }
}
