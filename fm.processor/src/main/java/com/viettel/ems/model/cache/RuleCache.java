package com.viettel.ems.model.cache;

import com.viettel.ems.model.rule.Rule;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RuleCache extends ConcurrentHashMap<Integer, Rule> {

    @Override
    public Rule get(Object key) {
        return super.get(key);
    }
}
