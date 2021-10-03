package com.example.myspring.mvc;

import com.example.myspring.aop.AspectWeaver;
import com.example.myspring.core.BeanContainer;
import com.example.myspring.inject.DependencyInjector;
import com.example.myspring.mvc.processor.RequestProcessor;
import com.example.myspring.mvc.processor.impl.ControllerRequestProcessor;
import com.example.myspring.mvc.processor.impl.JspRequestProcessor;
import com.example.myspring.mvc.processor.impl.PreRequestProcessor;
import com.example.myspring.mvc.processor.impl.StaticResourceRequestProcessor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/*")
public class DispatcherServlet extends HttpServlet {
    private final List<RequestProcessor> PROCESSOR_LIST = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        BeanContainer container = BeanContainer.getInstance();
        try {
            container.loadBeans("demo");
            new AspectWeaver().doAop();
            new DependencyInjector().doIoC();

            PROCESSOR_LIST.add(new PreRequestProcessor());
            PROCESSOR_LIST.add(new JspRequestProcessor(getServletContext()));
            PROCESSOR_LIST.add(new StaticResourceRequestProcessor(getServletContext()));
            PROCESSOR_LIST.add(new ControllerRequestProcessor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestProcessorChain processorChain = new RequestProcessorChain(PROCESSOR_LIST.iterator(), req,resp);
        processorChain.doProcess();
        processorChain.doRender();
    }
}
