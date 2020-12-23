package com.viettel.utils.condition.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.utils.condition.ICondition;
import com.viettel.utils.condition.reflection.Reflect;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ObjectCondition implements ICondition {

    protected final String expression;

    @Override
    public boolean validate(Reflect reflect) {
        return reflect.containsField(expression);
    }

    @Override
    public String toString() {
        try {
            var mapper = new ObjectMapper();
            return mapper.writer().writeValueAsString(toJson(mapper));
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
