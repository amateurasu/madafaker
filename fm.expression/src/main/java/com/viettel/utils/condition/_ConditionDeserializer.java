package com.viettel.utils.condition;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.viettel.utils.condition.logic.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.*;
import java.util.ArrayList;

import static com.fasterxml.jackson.core.JsonToken.*;

@Deprecated
public class _ConditionDeserializer extends JsonDeserializer<ICondition> {

    @Override
    public ICondition deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        var token = parser.getCurrentToken();
        switch (token) {
            case START_OBJECT:
                return parseObject(parser);
            case START_ARRAY:
                return parseLogicAtStart(parser, "$and");
            default:
                throw new JsonParseException(parser, "Do not expect token '${token}': ${parser.getValueAsString()}");
        }
    }

    private ICondition parseObject(JsonParser parser) throws IOException {
        expect(parser, FIELD_NAME);

        var name = parser.getCurrentName();
        if (name == null) {
            throw new JsonParseException(parser, "Illegal name");
        }
        System.out.println(name);
        var expression =
            name.equals("$and") || name.equals("$or") ? parseLogic(parser, name) : parseCondition(parser, name);
        expect(parser, END_OBJECT);
        return expression;
    }

    private ICondition parseLogic(JsonParser parser, String op) throws IOException {
        expect(parser, START_ARRAY);
        // expect(parser, END_ARRAY);
        return parseLogicAtStart(parser, op);
    }

    private ICondition parseLogicAtStart(JsonParser parser, String op) throws IOException {
        var token = parser.getCurrentToken();
        var list = new ArrayList<ICondition>();

        read_json:
        while ((token = parser.nextToken()) != null) {
            switch (token) {
                case START_OBJECT:
                    var operator = parseObject(parser);
                    if (operator != null) list.add(operator);
                    break;
                case END_ARRAY:
                    break read_json;
                default:
                    throw new JsonParseException(parser, "Do not expect token " + token);
            }
        }

        return list.isEmpty() ? null : LogicCondition.of(op, list);
    }

    private ICondition parseCondition(JsonParser parser, String name) throws IOException {
        var token = parser.nextToken();
        switch (token) {
            case START_OBJECT:
                expect(parser, FIELD_NAME);
                return parseOperator(parser, name, parser.getCurrentName());
            case VALUE_FALSE:
                return ComparableCondition.of(name, "$eq", false);
            case VALUE_TRUE:
                return ComparableCondition.of(name, "$eq", true);
            case VALUE_NUMBER_INT:
                return ComparableCondition.of(name, "$eq", parser.getIntValue());
            case VALUE_NUMBER_FLOAT:
                return ComparableCondition.of(name, "$eq", parser.getDoubleValue());
            default:
                throw new JsonParseException(parser, "Do not expect token '" + token + "' in this context!");
        }
    }

    private ICondition parseOperator(JsonParser parser, String name, String operator) throws IOException {
        switch (operator) {
            case "$eq": case "$gt": case "$ge": case "$lt": case "$le":
                return parseComparable(parser, name, operator);
            case "$match": case "$find":
                return parseString(parser, name, operator);
            case "$in": case "$nin":
                return parseList(parser, name, operator);
            default:
                throw new JsonParseException(parser, "Do not expect this operator '" + operator + "'!");
        }
    }

    private ICondition parseComparable(JsonParser parser, String name, String op) throws IOException {
        var compare = new ArrayList<ICondition>();
        JsonToken token;
        read_json:
        while ((token = parser.nextToken()) != null) {
            switch (token) {
                case FIELD_NAME:
                    switch (op = parser.getCurrentName()) {
                        case "$eq": case "$gt": case "$ge": case "$lt": case "$le":
                            continue;
                    }
                    throw new JsonParseException(parser, "Do not expect operator '" + op + "' in numeric context!");
                case VALUE_NUMBER_INT:
                    compare.add(ComparableCondition.of(name, op, parser.getLongValue()));
                    break;
                case VALUE_NUMBER_FLOAT:
                    compare.add(ComparableCondition.of(name, op, parser.getDoubleValue()));
                    break;
                case VALUE_STRING:
                    var time = parseTime(parser);
                    if (time != null) {
                        compare.add(ComparableCondition.of(name, op, time));
                    }
                    break;
                case END_OBJECT:
                    break read_json;
                default:
                    throw new JsonParseException(parser, "Do not expect token " + token);
            }
        }

        return compare.size() == 1 ? compare.get(0) : LogicCondition.of("$and", compare);
    }

    private ICondition parseList(JsonParser parser, String name, String op) throws IOException {
        var token = expect(parser, START_ARRAY);
        var list = new ArrayList<>();

        read_json:
        while ((token = parser.nextToken()) != null) {
            switch (token) {
                case VALUE_NUMBER_INT:
                    list.add(parser.getIntValue());
                    break;
                case VALUE_NUMBER_FLOAT:
                    list.add(parser.getFloatValue());
                    break;
                case VALUE_STRING:
                    list.add(parser.getValueAsString());
                    break;
                case END_ARRAY:
                    break read_json;
                default:
                    throw new JsonParseException(parser, "Expect number value only, but got " + token);
            }
        }
        expect(parser, END_OBJECT);
        return ListCondition.of(op, name, list);
    }

    private ICondition parseString(JsonParser parser, String name, String op) throws IOException {
        try {
            expect(parser, VALUE_STRING);
            return StringCondition.of(op, name, parser.getValueAsString());
        } finally {
            expect(parser, END_OBJECT);
        }
    }

    private Comparable<?> parseTime(JsonParser parser) throws IOException {
        var string = parser.getValueAsString();
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException ignored) { }

        try {
            return Timestamp.valueOf(string);
        } catch (Exception ignored) { }

        try {
            return Instant.parse(string);
        } catch (Exception ignored) { }

        try {
            return LocalDateTime.parse(string);
        } catch (Exception ignored) { }

        try {
            return LocalDate.parse(string);
        } catch (Exception ignored) { }

        try {
            return LocalTime.parse(string);
        } catch (Exception ignored) { }

        throw new JsonParseException(parser, "");
    }

    private JsonToken expect(JsonParser parser, JsonToken expect) throws IOException {
        var token = parser.nextToken();
        if (token != expect) {
            throw new JsonParseException(parser, "Expect ${expect} but got ${token}");
        }
        return token;
    }
}
