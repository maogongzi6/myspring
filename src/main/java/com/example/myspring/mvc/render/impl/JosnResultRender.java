package com.example.myspring.mvc.render.impl;

import com.example.myspring.mvc.RequestProcessorChain;
import com.example.myspring.mvc.render.ResultRender;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@AllArgsConstructor
public class JosnResultRender implements ResultRender {
    private Object jsonObject;

    @Override
    public void render(RequestProcessorChain processorChain) throws Exception {
        HttpServletResponse response = processorChain.getResponse();
        response.setContentType("application/JSON");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(new Gson().toJson(jsonObject));
            writer.flush();
        }

    }
}
