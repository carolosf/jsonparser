package com.fantasticbytes.jsonparser.parser;

/**
 * @author Carolos Foscolos
 */
enum ParserState {
    START,
    OBJECT_ENTRY,
    OBJECT_EXIT,
    KEY,
    COLON,
    VALUE,
    COMMA,
    ARRAY_ENTRY,
    ARRAY_EXIT,
    END
}
