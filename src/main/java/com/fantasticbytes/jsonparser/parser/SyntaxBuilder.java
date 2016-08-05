package com.fantasticbytes.jsonparser.parser;

/**
 * @author Carolos Foscolos
 */
class SyntaxBuilder implements Builder {

    private String keyName;
    private String containerKeyName;
    private JsonSyntax.JsonValue value;
    private JsonSyntax jsonSyntax;

    private JsonSyntax.JsonContainer currentContainer;

    JsonSyntax getJsonSyntax() {
        return jsonSyntax;
    }

    void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    void setValue(JsonSyntax.JsonValue value) {
        this.value = value;
    }

    SyntaxBuilder() {
        this.jsonSyntax = new JsonSyntax();
        this.currentContainer = jsonSyntax.jsonObjects;
    }

    @Override
    public void syntaxError(ParserState state, ParserEvent event, int line, int pos) {
        jsonSyntax.syntaxErrors.add(new JsonSyntax.SyntaxError(JsonSyntax.SyntaxError.Type.GENERAL, "Parser error: state <" + state + "> event <" + event + ">", line, pos));
    }

    @Override
    public void newObject() {
        currentContainer = new JsonSyntax.JsonObject(currentContainer, keyName);
        containerKeyName = keyName;
    }

    @Override
    public void newArray() {
        currentContainer = new JsonSyntax.JsonArray(currentContainer, keyName);
        containerKeyName = keyName;
    }

    @Override
    public void newValue() {
        currentContainer.put(new JsonSyntax.JsonKeyValue(keyName, value));
        keyName = null;
        value = null;
    }

    @Override
    public void exitContainer() {
        if (currentContainer.getParent() != null) {
            currentContainer.getParent().put(new JsonSyntax.JsonKeyValue(containerKeyName, currentContainer));
            currentContainer = currentContainer.getParent();
            containerKeyName = currentContainer.getKeyName();
        }
    }

    boolean isInArray() {
        return  currentContainer instanceof JsonSyntax.JsonArray;
    }
}
