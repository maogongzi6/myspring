package com.example.myspring.mvc.processor;

import com.example.myspring.mvc.RequestProcessorChain;

public interface RequestProcessor {
    boolean doProcess(RequestProcessorChain processorChain) throws Exception;
}
