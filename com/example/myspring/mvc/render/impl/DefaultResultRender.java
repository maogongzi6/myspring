package com.example.myspring.mvc.render.impl;

import com.example.myspring.mvc.RequestProcessorChain;
import com.example.myspring.mvc.render.ResultRender;

public class DefaultResultRender implements ResultRender {
    @Override
    public void render(RequestProcessorChain processorChain) throws Exception {
        processorChain.getResponse().setStatus(processorChain.getRequestCode());
    }
}
