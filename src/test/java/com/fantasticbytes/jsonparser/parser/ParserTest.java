package com.fantasticbytes.jsonparser.parser;

import com.fantasticbytes.jsonparser.lexer.Lexer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ParserTest {

    private Lexer lexer;
    private SyntaxBuilder builder;

    @BeforeMethod
    public void setUp() throws Exception {
        builder = new SyntaxBuilder();
        lexer = new Lexer(new Parser(builder));
    }


    private void assertParseResult(String s, String expected) {
        lexer.lex(s);
        Assert.assertEquals(builder.getJsonSyntax().syntaxErrors.size(), 0, builder.getJsonSyntax().syntaxErrors.toString());
        Assert.assertEquals(builder.getJsonSyntax().toString(), expected);
    }

    @Test
    public void testEmpty() {
        assertParseResult("", "{}");
        assertParseResult("{}", "{}");
    }

    @Test
    public void testSingleString() {
        assertParseResult("{\"name\":\"Carolos\"}", "{\"name\" : \"Carolos\"}");
    }

    @Test
    public void testSingleInt() {
        assertParseResult("{\"height\":175}", "{\"height\" : 175}");
    }

    @Test
    public void testSingleBooleanTrue() {
        assertParseResult("{\"name\":true}", "{\"name\" : true}");
    }

    @Test
    public void testSingleBooleanFalse() {
        assertParseResult("{\"name\":false}", "{\"name\" : false}");
    }

    @Test
    public void testStringAndInt() {
        assertParseResult("{\"name\":\"Carolos\", \"height\":175}", "{\"name\" : \"Carolos\", \"height\" : 175}");
    }

    @Test
    public void testNull() {
        assertParseResult("{\"name\":null}", "{\"name\" : null}");
    }

    @Test
    public void testAllBasicTypes() {
        assertParseResult(
                "{\"name\":\"Carolos\", \"height\":1.75,  \"age\": 30, \"retired\": false, \"awesome\": true, \"children\": null}",
                "{\"name\" : \"Carolos\", \"height\" : 1.75, \"age\" : 30, \"retired\" : false, \"awesome\" : true, \"children\" : null}"
        );
    }

    @Test
    public void testMultiline() {
        assertParseResult(
                "" + "{\n" +
                        "  \"firstName\": \"John\",\n" +
                        "  \"lastName\": \"Smith\",\n" +
                        "  \"isAlive\": true,\n" +
                        "  \"age\": 25,\n" +
                        "  \"spouse\": null\n" +
                        "}",
                "{\"firstName\" : \"John\", \"lastName\" : \"Smith\", \"isAlive\" : true, \"age\" : 25, \"spouse\" : null}"
        );
    }

    @Test
    public void testNested() {
        assertParseResult(
                "" + "{\n" +
                "  \"address\": {\n" +
                "    \"state\": \"NY\"\n" +
                "  }\n" +
                "}",
                "{\"address\" : {\"state\" : \"NY\"}}"
        );
    }

    @Test
    public void testArray() {
        assertParseResult(
                "" + "{\n" +
                        "  \"address\": [\n" +
                        "       \"NY\"\n" +
                        "  ]\n" +
                        "}",
                "{\"address\" : [\"NY\"]}"
        );
    }

    @Test
    public void testArrayInts() {
        assertParseResult(
                "" + "{\n" +
                        "  \"address\": [\n" +
                        "   1,2,3,4" +
                        "  ]\n" +
                        "}",
                "{\"address\" : [1, 2, 3, 4]}"
        );
    }

    @Test
    public void testArrayObjects() {
        assertParseResult(
                "" + "{\n" +
                        "  \"address\": [\n" +
                        "   {\"a\":\"b\"}," +
                        "   {\"c\":\"d\"}" +
                        "  ],\n" +
                        "   \"children\":[]" +
                        "}",
                "{\"address\" : [{\"a\" : \"b\"}, {\"c\" : \"d\"}], \"children\" : []}"
        );
    }


    @Test
    public void testArrayOfObjects() {
        assertParseResult(
                "" + "{\n" +
                        "  \"phoneNumbers\": [\n" +
                        "    {\n" +
                        "      \"type\": \"home\",\n" +
                        "      \"number\": \"212 555-1234\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}",
                "{\"phoneNumbers\" : [{\"type\" : \"home\", \"number\" : \"212 555-1234\"}]}"
        );
    }

    @Test
    public void testSemiComplicated() {
        assertParseResult(
                "" + "{\n" +
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
                        "  \"children\": [],\n" +
                        "  \"spouse\": null\n" +
                        "}",
                "{\"firstName\" : \"John\", \"lastName\" : \"Smith\", \"isAlive\" : true, \"age\" : 25, \"address\" : {\"streetAddress\" : \"21 2nd Street\", \"city\" : \"New York\", \"state\" : \"NY\", \"postalCode\" : \"10021-3100\"}, \"children\" : [], \"spouse\" : null}"
        );
    }


    @Test
    public void testComplicated() {
        assertParseResult(
                "" + "{\n" +
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
                "}",
                "{\"firstName\" : \"John\", \"lastName\" : \"Smith\", \"isAlive\" : true, \"age\" : 25, \"address\" : {\"streetAddress\" : \"21 2nd Street\", \"city\" : \"New York\", \"state\" : \"NY\", \"postalCode\" : \"10021-3100\"}, \"phoneNumbers\" : [{\"type\" : \"home\", \"number\" : \"212 555-1234\"}, {\"type\" : \"office\", \"number\" : \"646 555-4567\"}, {\"type\" : \"mobile\", \"number\" : \"123 456-7890\"}], \"children\" : [], \"spouse\" : null}"
        );
    }

    @Test
    public void testDeepNesting() {
        assertParseResult(
                "{\"a\": { \"b\" : { \"c\" : { \"d\" : \"e\" } } }",
                "{\"a\" : {\"b\" : {\"c\" : {\"d\" : \"e\"}}}}"
        );
    }

}
