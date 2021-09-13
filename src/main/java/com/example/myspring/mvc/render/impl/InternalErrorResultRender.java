package com.example.myspring.mvc.render.impl;

import com.example.myspring.mvc.RequestProcessorChain;
import com.example.myspring.mvc.render.ResultRender;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;

@AllArgsConstructor
public class InternalErrorResultRender implements ResultRender {
    private String errorMessage;
    @Override
    public void render(RequestProcessorChain processorChain) throws Exception {
        processorChain.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
    }
}
