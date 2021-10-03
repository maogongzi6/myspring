package com.example.myspring.mvc;

import com.example.myspring.mvc.processor.RequestProcessor;
import com.example.myspring.mvc.render.ResultRender;
import com.example.myspring.mvc.render.impl.DefaultResultRender;
import com.example.myspring.mvc.render.impl.InternalErrorResultRender;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

@Slf4j
@Data
public class RequestProcessorChain {
    private Iterator<RequestProcessor> iterator;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String requestMethod;
    private String requestPath;
    private int requestCode;
    private ResultRender render;

    public RequestProcessorChain(Iterator<RequestProcessor> iterator, HttpServletRequest request, HttpServletResponse response) {
        this.iterator = iterator;
        this.request = request;
        this.response = response;
        this.requestMethod = request.getMethod();
        this.requestPath = request.getPathInfo();
        this.requestCode = HttpServletResponse.SC_OK;
    }

    public void doProcess() {
        try {
            while (iterator.hasNext()) {
                if (!iterator.next().doProcess(this)) {
                    break;
                }
            }
        } catch (Exception e) {
            render = new InternalErrorResultRender(e.getMessage());
            log.error("process exception:"+e);
        }
    }

    public void doRender() {
        if (render == null) {
            render = new DefaultResultRender();
        }
        try {
            render.render(this);
        } catch (Exception e) {
            log.error("render failed:"+e);
            throw new RuntimeException(e);
        }
    }
}
