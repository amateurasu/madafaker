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
import java.util.List;

import static com.fasterxml.jackson.core.JsonToken.*;

public class ConditionDeserializer extends JsonDeserializer<ICondition> {

    @Override
    public ICondition deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        var token = parser.getCurrentToken();
        switch (token) {
            case START_ARRAY:
                return LogicGroupCondition.of("", parseExpressions(parser));
            case START_OBJECT:
                return parseExpressionGroup(parser);
            default:
                throw new JsonParseException(parser, "Do not expect '" + token + "': " + parser.getValueAsString());
        }
    }

    private List<LogicWrapper> parseExpressions(JsonParser parser) throws IOException {
        var list = new ArrayList<LogicWrapper>();
        JsonToken token;

        parse_condition:
        while ((token = parser.nextToken()) != null) {
            switch (token) {
                case START_OBJECT:
                    expect(parser, FIELD_NAME);
                    var name = parser.getCurrentName();
                    // System.out.format("> %5s ", name);
                    list.add(LogicWrapper.of(name, parseExpression(parser)));
                    break;
                case END_ARRAY:
                    // System.out.println("END!!");
                    break parse_condition;
            }
        }
        return list;
    }

    private ICondition parseExpression(JsonParser parser) throws IOException {
        JsonToken token;
        switch (token = parser.nextToken()) {
            case START_ARRAY:
                return parseExpressionContent(parser);
            case START_OBJECT:
                return parseExpressionGroup(parser);
            default:
                throw new JsonParseException(parser, "Do not expect token " + token);
        }
    }

    private ICondition parseExpressionContent(JsonParser parser) throws IOException {
        //field name
        expect(parser, VALUE_STRING);
        var name = parser.getValueAsString();

        //operator
        expect(parser, VALUE_STRING);
        var op = parser.getValueAsString();
        // System.out.format("%15s %5s ", name, op);

        var exp = parseValue(parser, name, op);
        expect(parser, END_ARRAY);
        return exp;
    }

    private ICondition parseExpressionGroup(JsonParser parser) throws IOException {
        JsonToken token;
        String field = null;
        String name = null;
        List<LogicWrapper> expressions = null;

        parse_group:
        while ((token = parser.nextToken()) != null) {
            switch (token) {
                case FIELD_NAME:
                    field = parser.getCurrentName();
                    break;
                case VALUE_STRING:
                    var value = parser.getValueAsString();
                    if ("name".equals(field)) {
                        name = value;
                        break;
                    }
                    throw new JsonParseException(parser, "Expected name value, got (" + field + ":" + value + ")");
                case START_ARRAY:
                    if ("exp".equals(field)) {
                        expressions = parseExpressions(parser);
                        break;
                    }
                    throw new JsonParseException(parser, "Expected expression, but got " + field);
                case END_OBJECT:
                    break parse_group;
            }
        }

        return LogicGroupCondition.of(name, expressions);
    }

    private ICondition parseValue(JsonParser parser, String name, String operator) throws IOException {
        switch (operator) {
            case "eq": case "gt": case "ge": case "lt": case "le": case "ne":
                return parseComparable(parser, name, operator);
            case "in": case "nin":
                return parseList(parser, name, operator);
            case "contains": case "alike": case "match":
                return parseString(parser, name, operator);
            default:
                throw new JsonParseException(parser, "Do not expect this operator '" + operator + "'!");
        }
    }

    private ICondition parseComparable(JsonParser parser, String name, String op) throws IOException {
        var token = parser.nextToken();

        switch (token) {
            case VALUE_NUMBER_INT:
                return ComparableCondition.of(name, op, parser.getIntValue());
            case VALUE_NUMBER_FLOAT:
                return ComparableCondition.of(name, op, parser.getDoubleValue());
            case VALUE_STRING:
                var string = parser.getValueAsString();
                var time = parseTime(string);
                return time != null ? ComparableCondition.of(name, op, time) : ComparableCondition.of(name, op, string);
            case VALUE_TRUE:
                return ComparableCondition.of(name, op, true);
            case VALUE_FALSE:
                return ComparableCondition.of(name, op, false);
            default:
                throw new JsonParseException(parser, "Do not expect token " + token);
        }
    }

    private ICondition parseString(JsonParser parser, String name, String op) throws IOException {
        expect(parser, VALUE_STRING);
        return StringCondition.of(name, op, parser.getValueAsString());
    }

    private ICondition parseList(JsonParser parser, String name, String op) throws IOException {
        var token = expect(parser, START_ARRAY);
        var list = new ArrayList<>();

        read_json:
        while ((token = parser.nextToken()) != null) {
            switch (token) {
                case VALUE_STRING:
                    list.add(parser.getValueAsString());
                    break;
                case VALUE_NUMBER_INT:
                    list.add(parser.getIntValue());
                    break;
                case VALUE_NUMBER_FLOAT:
                    list.add(parser.getFloatValue());
                    break;
                case END_ARRAY:
                    break read_json;
                default:
                    throw new JsonParseException(parser, "Expect number value only, but got " + token);
            }
        }
        return ListCondition.of(name, op, list);
    }

    private Comparable<?> parseTime(String time) {
        try {
            var epoch = Long.parseLong(time);
            var instant = Instant.ofEpochMilli(epoch);
            var zoneId = ZoneId.systemDefault();
            return LocalDateTime.ofInstant(instant, zoneId);
        } catch (NumberFormatException ignored) { }

        try {
            return Instant.parse(time);
        } catch (Exception ignored) { }

        try {
            return Timestamp.valueOf(time).toLocalDateTime();
        } catch (Exception ignored) { }

        try {
            return LocalDateTime.parse(time);
        } catch (Exception ignored) { }

        try {
            return LocalDate.parse(time);
        } catch (Exception ignored) { }

        try {
            return LocalTime.parse(time);
        } catch (Exception ignored) { }

        return null;
    }

    private JsonToken expect(JsonParser parser, JsonToken expect) throws IOException {
        var token = parser.nextToken();
        if (token != expect) {
            if (token == VALUE_STRING || token == FIELD_NAME) {
                throw new JsonParseException(parser,
                    "Expect " + expect + " but got " + token + " (" + parser.getValueAsString() + ")");
            }
            throw new JsonParseException(parser, "Expect " + expect + " but got " + token);
        }
        return token;
    }
}
