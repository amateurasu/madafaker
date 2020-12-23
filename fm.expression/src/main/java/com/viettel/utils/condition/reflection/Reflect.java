package com.viettel.utils.condition.reflection;

import com.viettel.utils.condition.Column;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Reflect {

    protected static final Map<Class<?>, Map<String, Name>> classMemoization = new HashMap<>();

    protected final Class<?> klass;
    protected final Map<String, Name> fields;

    public Reflect(Class<?> klass) {
        this.klass = klass;
        fields = classMemoization.computeIfAbsent(klass, Reflect::findColumns);
    }

    public boolean containsField(String name) {
        return fields.containsKey(name);
    }

    public String getField(String name) throws NoSuchFieldException {
        if (fields.containsKey(name)) {
            return name;
        }

        throw new NoSuchFieldException(
            "There is no such field '" + name + "' of type " + klass.getCanonicalName() + " in database!");
    }

    public String getDbField(String name) throws NoSuchFieldException {
        var names = fields.get(name);
        if (names != null) {
            return names.getDbExp();
        }

        throw new NoSuchFieldException(
            "There is no such field as '" + name + "' of type " + klass.getCanonicalName() + " in database!");
    }

    public static void load(Class<?>... classes) {
        if (classes != null) {
            for (var klass : classes) {
                classMemoization.computeIfAbsent(klass, Reflect::findColumns);
            }
        }
    }

    private static Map<String, Name> findColumns(Class<?> klass) {
        Map<String, Name> map = new HashMap<>();
        Class<?> clss = klass;
        while (clss != null) {
            for (Field field : clss.getDeclaredFields()) {
                var column = field.getAnnotation(Column.class);
                if (column == null) continue;
                try {
                    var fieldName = field.getName();
                    var name = column.name().isBlank() ? fieldName : column.name();
                    var dbExp = column.db().isBlank() ? name : column.db();

                    map.put(name, new Name(dbExp, getter(field, fieldName)));
                } catch (Exception e) {
                    log.error("Cannot get property from annotation", e);
                }
            }
            clss = clss.getSuperclass();
        }
        return map;
    }

    private static String getter(Field field, String fieldName) {
        var type = field.getType();
        var c = Character.toUpperCase(fieldName.charAt(0));
        return (type == boolean.class || type == Boolean.class ? "is" : "get") + c + fieldName.substring(1);
    }

    @Getter
    @RequiredArgsConstructor
    public static class Name {
        private final String dbExp;
        private final String getter;
    }
}
