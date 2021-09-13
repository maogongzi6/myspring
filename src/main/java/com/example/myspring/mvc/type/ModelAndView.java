package com.example.myspring.mvc.type;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ModelAndView {
    private String view;
    private Map<String, Object> model = new HashMap<>();

    public ModelAndView setView(String view) {
        this.view = view;
        return this;
    }

    public ModelAndView setModelData(String key, Object value) {
        model.put(key, value);
        return this;
    }
}
