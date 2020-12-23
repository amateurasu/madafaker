package com.viettel.ems.model.cache;

import java.util.Map;

@SuppressWarnings("all")
public interface Cache<K, V> extends Map<K, V> {
    boolean invalidate(K... keys);
}
