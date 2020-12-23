package com.viettel.utils.condition.reflection;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectReflect<T> extends Reflect {

    @Getter
    private final T object;

    public ObjectReflect(T object) {
        super(object.getClass());
        this.object = object;
    }

    @SuppressWarnings("unchecked")
    public <R> R getValue(String expr) throws Exception {
        var getter = fields.get(expr).getGetter();
        if (getter == null) {
            throw new NoSuchFieldException(
                "There is no such getter as '" + expr + "' of type " + klass.getCanonicalName() + " in database!");
        }

        return (R) klass.getDeclaredMethod(getter).invoke(object);
    }


}
