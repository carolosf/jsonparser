package com.fantasticbytes.jsonparser.lexer;


import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LexerTest implements TokenCollector {

    private String actions = "";


    private void addAction(String action, int line, int pos) {
        actions += action + "(" + line + "," + pos + ") ";
    }

    @Override
    public void openBrace(int line, int pos) {
        addAction("OB", line, pos);
    }

    @Override
    public void closeBrace(int line, int pos) {
        addAction("CB", line, pos);
    }

    @Override
    public void comma(int line, int pos) {
        addAction("CM", line, pos);
    }

    @Override
    public void colon(int line, int pos) {
        addAction("CL", line, pos);
    }

    @Override
    public void openBracket(int line, int pos) {
        addAction("OBT", line, pos);
    }

    @Override
    public void closeBracket(int line, int pos) {
        addAction("CBT", line, pos);
    }

    @Override
    public void stringField(String stringValue, int line, int pos) {
        addAction("STRING:\""+ stringValue + "\"", line, pos);
    }

    @Override
    public void integerField(int integerValue, int line, int pos) {
        addAction("INT:"+ integerValue, line, pos);
    }

    @Override
    public void floatField(float floatValue, int line, int pos) {
        addAction("FLOAT:"+ floatValue, line, pos);
    }

    @Override
    public void booleanField(boolean booleanValue, int line, int pos) {
        addAction("BOOLEAN:"+ booleanValue, line, pos);
    }

    @Override
    public void nullField(int line, int pos) { addAction("NULL", line, pos); }

    @Override
    public void error(int line, int pos) {
        addAction("ERROR", line, pos);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        this.actions = "";
    }

    @Test
    public void testOpenBrace() {
        Lexer lexer = new Lexer(this);
        lexer.lex("{");
        Assert.assertEquals(this.actions, "OB(1,0) ");
    }

    @Test
    public void testCloseBrace() {
        Lexer lexer = new Lexer(this);
        lexer.lex("}");
        Assert.assertEquals(this.actions, "CB(1,0) ");
    }

    @Test
    public void testCloseBraceWithWhiteSpace() {
        Lexer lexer = new Lexer(this);
        lexer.lex(" }");
        Assert.assertEquals(this.actions, "CB(1,1) ");
    }

    @Test
    public void testString() {
        Lexer lexer = new Lexer(this);
        lexer.lex("\"blah\"");
        Assert.assertEquals(this.actions, "STRING:\"blah\"(1,0) ");
    }

    @Test
    public void testInt() {
        Lexer lexer = new Lexer(this);
        lexer.lex("123");
        Assert.assertEquals(this.actions, "INT:123(1,0) ");
    }

    @Test
    public void testFloat() {
        Lexer lexer = new Lexer(this);
        lexer.lex("123.123");
        Assert.assertEquals(this.actions, "FLOAT:123.123(1,0) ");
    }

    @Test
    public void testBooleanTrue() {
        Lexer lexer = new Lexer(this);
        lexer.lex("true");
        Assert.assertEquals(this.actions, "BOOLEAN:true(1,0) ");
    }

    @Test
    public void testBooleanFalse() {
        Lexer lexer = new Lexer(this);
        lexer.lex("false");
        Assert.assertEquals(this.actions, "BOOLEAN:false(1,0) ");
    }

    @Test
    public void testIntFull() {
        Lexer lexer = new Lexer(this);
        lexer.lex("{\"name\":123}");
        Assert.assertEquals(this.actions, "OB(1,0) STRING:\"name\"(1,1) CL(1,7) INT:123(1,8) CB(1,11) ");
    }

    @Test
    public void testBooleanTrueFull() {
        Lexer lexer = new Lexer(this);
        lexer.lex("{\"name\":true}");
        Assert.assertEquals(this.actions, "OB(1,0) STRING:\"name\"(1,1) CL(1,7) BOOLEAN:true(1,8) CB(1,12) ");
    }

    @Test
    public void testBooleanFalseFull() {
        Lexer lexer = new Lexer(this);
        lexer.lex("{\"name\":false}");
        Assert.assertEquals(this.actions, "OB(1,0) STRING:\"name\"(1,1) CL(1,7) BOOLEAN:false(1,8) CB(1,13) ");
    }

    @Test
    public void testNull() {
        Lexer lexer = new Lexer(this);
        lexer.lex("{\"name\":null}");
        Assert.assertEquals(this.actions, "OB(1,0) STRING:\"name\"(1,1) CL(1,7) NULL(1,8) CB(1,12) ");
    }

    @Test
    public void testWikipediaExample() {
        Lexer lexer = new Lexer(this);
        lexer.lex("" + "{\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Smith\",\n" +
                "  \"isAlive\": true,\n" +
                "  \"age\": 25,\n" +
                "  \"address\": {\n" +
                "    \"streetAddress\": \"21 2nd Street\",\n" +
                "    \"city\": \"New York\",\n" +
                "    \"state\": \"NY\",\n" +
                "    \"postalCode\": \"10021-3100\"\n" +
                "  },\n" +
                "  \"phoneNumbers\": [\n" +
                "    {\n" +
                "      \"type\": \"home\",\n" +
                "      \"number\": \"212 555-1234\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"office\",\n" +
                "      \"number\": \"646 555-4567\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"mobile\",\n" +
                "      \"number\": \"123 456-7890\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"children\": [],\n" +
                "  \"spouse\": null\n" +
                "}"
        );
        Assert.assertEquals(this.actions, "OB(1,0) STRING:\"firstName\"(2,2) CL(2,13) STRING:\"John\"(2,15) CM(2,21) STRING:\"lastName\"(3,2) CL(3,12) STRING:\"Smith\"(3,14) CM(3,21) STRING:\"isAlive\"(4,2) CL(4,11) BOOLEAN:true(4,13) CM(4,17) STRING:\"age\"(5,2) CL(5,7) INT:25(5,9) CM(5,11) STRING:\"address\"(6,2) CL(6,11) OB(6,13) STRING:\"streetAddress\"(7,4) CL(7,19) STRING:\"21 2nd Street\"(7,21) CM(7,36) STRING:\"city\"(8,4) CL(8,10) STRING:\"New York\"(8,12) CM(8,22) STRING:\"state\"(9,4) CL(9,11) STRING:\"NY\"(9,13) CM(9,17) STRING:\"postalCode\"(10,4) CL(10,16) STRING:\"10021-3100\"(10,18) CB(11,2) CM(11,3) STRING:\"phoneNumbers\"(12,2) CL(12,16) OBT(12,18) OB(13,4) STRING:\"type\"(14,6) CL(14,12) STRING:\"home\"(14,14) CM(14,20) STRING:\"number\"(15,6) CL(15,14) STRING:\"212 555-1234\"(15,16) CB(16,4) CM(16,5) OB(17,4) STRING:\"type\"(18,6) CL(18,12) STRING:\"office\"(18,14) CM(18,22) STRING:\"number\"(19,6) CL(19,14) STRING:\"646 555-4567\"(19,16) CB(20,4) CM(20,5) OB(21,4) STRING:\"type\"(22,6) CL(22,12) STRING:\"mobile\"(22,14) CM(22,22) STRING:\"number\"(23,6) CL(23,14) STRING:\"123 456-7890\"(23,16) CB(24,4) CBT(25,2) CM(25,3) STRING:\"children\"(26,2) CL(26,12) OBT(26,14) CBT(26,15) CM(26,16) STRING:\"spouse\"(27,2) CL(27,10) NULL(27,12) CB(28,0) ");
    }
}
