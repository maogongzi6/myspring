package com.example.myspring.mvc.type;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class RequestPathInfo {
    private String path;
    private String methodName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestPathInfo that = (RequestPathInfo) o;

        if (!Objects.equals(path, that.path)) return false;
        return Objects.equals(methodName, that.methodName);
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        return result;
    }
}
