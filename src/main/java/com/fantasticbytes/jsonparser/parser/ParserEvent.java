package com.fantasticbytes.jsonparser.parser;

/**
 * @author Carolos Foscolos
 */
enum ParserEvent {
    KEY,
    OPEN_BRACE,
    CLOSED_BRACE,
    COMMA,
    COLON,
    OPEN_BRACKET,
    CLOSED_BRACKET,
    STRING_EVENT,
    INTEGER_EVENT,
    FLOAT_EVENT,
    BOOLEAN_EVENT,
    NULL_EVENT,
    EOF
}