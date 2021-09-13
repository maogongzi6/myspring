package com.example.myspring.util;

import java.util.Collection;

public class CommonUtil {
    static public boolean isEmpty(Object[] objects) {
        return objects==null || objects.length==0;
    }
    static public boolean isEmpty(Collection<?> collection) {
        return collection==null || collection.size()==0;
    }
    static public boolean isEmpty(String str) {return str==null || str.equals(""); }
    static public String getPathStartWith(String str, String prefix) {
        return str.startsWith(prefix) ? str : prefix+str;
    }
}
