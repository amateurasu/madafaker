package com.viettel.ems.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static List<String> split(String string, String sep) {
        var list = new ArrayList<String>();
        for (String s : string.split(sep)) {
            String trim = s.trim();
            if (!trim.isEmpty()) {
                list.add(trim);
            }
        }
        return list;
    }
}
