package com.fantasticbytes.jsonparser.parser;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Carolos Foscolos
 */
public class JsonSyntaxTest {

    private JsonSyntax jsonSyntax;

    @BeforeMethod
    public void setUp() throws Exception {
        jsonSyntax = new JsonSyntax();
    }

    @Test
    public void testEmpty() {
        Assert.assertEquals(jsonSyntax.jsonObjects.toString(), "{}");
    }

    @Test
    public void testSingleString() {
        jsonSyntax.jsonObjects.put(new JsonSyntax.JsonKeyValue("name", new JsonSyntax.JsonString("Carolos")));
        Assert.assertEquals(jsonSyntax.toString(), "{\"name\" : \"Carolos\"}");
    }

    @Test
    public void testNestedString() throws Exception {
        JsonSyntax.JsonObject nameObject = new JsonSyntax.JsonObject(jsonSyntax.jsonObjects, null);
        nameObject.put(new JsonSyntax.JsonKeyValue("first", new JsonSyntax.JsonString("Carolos")));
        jsonSyntax.jsonObjects.put(new JsonSyntax.JsonKeyValue("name", nameObject));
        Assert.assertEquals(jsonSyntax.toString(), "{\"name\" : {\"first\" : \"Carolos\"}}");
    }
}
