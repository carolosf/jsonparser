package com.fantasticbytes.jsonparser.lexer;

/**
 * @author Carolos Foscolos
 */
public interface TokenCollector {
    void openBrace(int line, int pos);
    void closeBrace(int line, int pos);
    void comma(int line, int pos);
    void colon(int line, int pos);

    void openBracket(int line, int pos);
    void closeBracket(int line, int pos);

    void stringField(String stringValue, int line, int pos);
    void integerField(int integerValue, int line, int pos);
    void floatField(float floatValue, int line, int pos);
    void booleanField(boolean booleanValue, int line, int pos);
    void nullField(int line, int pos);

    void error(int line, int pos);
}
