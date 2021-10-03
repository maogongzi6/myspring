package com.example.myspring.mvc.render.impl;

import com.example.myspring.mvc.RequestProcessorChain;
import com.example.myspring.mvc.render.ResultRender;
import com.example.myspring.mvc.type.ModelAndView;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ViewResultRender implements ResultRender {
    public static final String VIEW_PATH = "/templates/";
    private ModelAndView modelAndView;

    public ViewResultRender(Object obj) throws Exception {
        if (obj instanceof String) {
            modelAndView = new ModelAndView().setView((String) obj);
        } else if (obj instanceof ModelAndView) {
            modelAndView = (ModelAndView) obj;
        } else {
            throw new Exception("model and view new instance failed");
        }
    }

    @Override
    public void render(RequestProcessorChain processorChain) throws Exception {
        HttpServletResponse response = processorChain.getResponse();
        HttpServletRequest request = processorChain.getRequest();
        String path = modelAndView.getView();
        Map<String, Object> model = modelAndView.getModel();
        for(Map.Entry<String, Object> entry : model.entrySet()){
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        request.getRequestDispatcher(VIEW_PATH +path).forward(request, response);
    }
}
