package com.example.myspring.mvc.type;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParameterClassWithIndex {
    private Class<?> clazz;
    private int index;
}
