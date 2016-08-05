package com.fantasticbytes.jsonparser.parser;

/**
 * @author Carolos Foscolos
 */
interface Builder {

    void syntaxError(ParserState state, ParserEvent event, int line, int pos);

    void newObject();

    void newArray();

    void newValue();

    void exitContainer();
}
