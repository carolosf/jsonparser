package com.fantasticbytes.jsonparser.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Carolos Foscolos
 */
class JsonSyntax {
    JsonObject jsonObjects = new JsonObject(null, null);

    List<SyntaxError> syntaxErrors = new ArrayList<>();

    interface JsonValue {
    }

    static final class JsonKeyValue {
        private String keyName;
        private JsonValue value;

        JsonKeyValue(String keyName, JsonValue value) {
            this.keyName = keyName;
            this.value = value;
        }

        String getKeyName() {
            return keyName;
        }

        JsonValue getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "\"" + keyName + "\" : " + value;
        }
    }

    interface JsonContainer extends JsonValue {

        void put(JsonKeyValue kv);
        JsonContainer getParent();

        String getKeyName();
    }

    static class JsonObject implements JsonContainer {
        private Map<String, JsonKeyValue> jsonObjects = new LinkedHashMap<>();
        private JsonContainer parent;
        private String keyName;

        JsonObject(JsonContainer parent, String keyName) {
            this.parent = parent;
            this.keyName = keyName;
        }

        @Override
        public void put(JsonKeyValue kv) {
            jsonObjects.put(kv.getKeyName(), kv);
        }

        @Override
        public JsonContainer getParent() {
            return parent;
        }

        @Override
        public String toString() {
            String result = "{";
            String prefix = "";
            for (Map.Entry<String, JsonKeyValue> e : jsonObjects.entrySet()) {
                result += prefix + e.getValue().toString();
                prefix = ", ";
            }
            result += "}";

            return result;
        }

        public String getKeyName() {
            return keyName;
        }
    }

    static class JsonArray implements JsonContainer {
        List<JsonValue> jsonArrayElements = new ArrayList<>();

        private JsonContainer parent;
        private String keyName;

        JsonArray(JsonContainer parent, String keyName) {
            this.parent = parent;
            this.keyName = keyName;
        }

        @Override
        public void put(JsonKeyValue kv) {
            jsonArrayElements.add(kv.getValue());
        }

        @Override
        public JsonContainer getParent() {
            return this.parent;
        }

        @Override
        public String toString() {
            String result = "[";
            String prefix = "";
            for (JsonValue v : jsonArrayElements) {
                result += prefix + v.toString();
                prefix = ", ";
            }
            result += "]";

            return result;
        }

        @Override
        public String getKeyName() {
            return keyName;
        }
    }

    static final class JsonString implements JsonValue {

        JsonString(String value) {
            this.value = value;
        }

        private String value;

        @Override
        public String toString() {
            return "\"" + value + "\"";
        }
    }

    static final class JsonInteger implements JsonValue {
        private int value;

        JsonInteger(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "" + value;
        }
    }

    static final class JsonFloat implements JsonValue {
        private float value;

        JsonFloat(float value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "" + value;
        }
    }

    static final class JsonBoolean implements JsonValue {
        private boolean value;

        JsonBoolean(boolean value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "" + value;
        }
    }

    static final class JsonNull implements JsonValue {
        @Override
        public String toString() {
            return "null";
        }
    }

    static final class SyntaxError {
        Type type;
        String msg;
        int lineNumber;
        int position;

        SyntaxError(Type type, String msg, int lineNumber, int position) {
            this.type = type;
            this.msg = msg;
            this.lineNumber = lineNumber;
            this.position = position;
        }

        public String toString() {
            return String.format("Syntax Error Line: %d, Position: %d.  (%s) %s", lineNumber, position, type.name(), msg);
        }

        enum Type {GENERAL}
    }

    @Override
    public String toString() {
        return jsonObjects.toString();
    }
}
