package com.example.myspring.mvc.render;

import com.example.myspring.mvc.RequestProcessorChain;

public interface ResultRender {
    void render(RequestProcessorChain processorChain) throws Exception;
}
