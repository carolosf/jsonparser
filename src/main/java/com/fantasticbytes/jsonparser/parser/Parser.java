package com.fantasticbytes.jsonparser.parser;

import com.fantasticbytes.jsonparser.lexer.TokenCollector;

import java.util.function.Consumer;

import static com.fantasticbytes.jsonparser.parser.ParserState.*;

/**
 * @author Carolos Foscolos
 */
class Parser implements TokenCollector {
    private ParserState state = ParserState.START;

    private SyntaxBuilder builder;
    private Transition[] transitions = new Transition[]{
            new Transition(ParserState.START, ParserEvent.OPEN_BRACE, OBJECT_ENTRY, null),

            new Transition(OBJECT_ENTRY, ParserEvent.KEY, ParserState.KEY, null),
            new Transition(ARRAY_ENTRY, ParserEvent.KEY, ParserState.KEY, null),

            new Transition(OBJECT_ENTRY, ParserEvent.CLOSED_BRACE, ParserState.OBJECT_EXIT, null),
            new Transition(ARRAY_ENTRY, ParserEvent.CLOSED_BRACKET, ParserState.ARRAY_EXIT, Builder::exitContainer),
            new Transition(ARRAY_ENTRY, ParserEvent.OPEN_BRACE, ParserState.OBJECT_ENTRY, Builder::newObject),

            new Transition(ParserState.KEY, ParserEvent.COLON, ParserState.COLON, null),

            new Transition(ParserState.VALUE, ParserEvent.COMMA, COMMA, null),
            new Transition(ParserState.VALUE, ParserEvent.CLOSED_BRACE, ParserState.OBJECT_EXIT, Builder::exitContainer),
            new Transition(ParserState.VALUE, ParserEvent.CLOSED_BRACKET, ParserState.ARRAY_EXIT, Builder::exitContainer),

            new Transition(COMMA, ParserEvent.KEY, ParserState.KEY, null),
            new Transition(COMMA, ParserEvent.OPEN_BRACE, ParserState.OBJECT_ENTRY, Builder::newObject),


            new Transition(ParserState.COLON, ParserEvent.OPEN_BRACE, OBJECT_ENTRY, Builder::newObject),
            new Transition(ParserState.COLON, ParserEvent.OPEN_BRACKET, ARRAY_ENTRY, Builder::newArray),

            new Transition(ParserState.COLON, ParserEvent.BOOLEAN_EVENT, ParserState.VALUE, Builder::newValue),
            new Transition(ParserState.COLON, ParserEvent.INTEGER_EVENT, ParserState.VALUE, Builder::newValue),
            new Transition(ParserState.COLON, ParserEvent.FLOAT_EVENT, ParserState.VALUE, Builder::newValue),
            new Transition(ParserState.COLON, ParserEvent.STRING_EVENT, ParserState.VALUE, Builder::newValue),
            new Transition(ParserState.COLON, ParserEvent.NULL_EVENT, ParserState.VALUE, Builder::newValue),

            new Transition(OBJECT_EXIT, ParserEvent.CLOSED_BRACE, ParserState.OBJECT_EXIT, Builder::exitContainer),
            new Transition(OBJECT_EXIT, ParserEvent.COMMA, ParserState.COMMA, null),
            new Transition(OBJECT_EXIT, ParserEvent.CLOSED_BRACKET, ParserState.ARRAY_EXIT, Builder::exitContainer),

            new Transition(ARRAY_EXIT, ParserEvent.CLOSED_BRACKET, ParserState.ARRAY_EXIT, Builder::exitContainer),
            new Transition(ARRAY_EXIT, ParserEvent.COMMA, ParserState.COMMA, null),
            new Transition(ARRAY_EXIT, ParserEvent.CLOSED_BRACE, ParserState.OBJECT_EXIT, Builder::exitContainer),

            new Transition(ParserState.END, ParserEvent.EOF, ParserState.END, null)
    };
    private Transition[] arrayTransitions = new Transition[]{
            new Transition(ARRAY_ENTRY, ParserEvent.OPEN_BRACE, ParserState.OBJECT_ENTRY, Builder::newObject),
            new Transition(ARRAY_ENTRY, ParserEvent.CLOSED_BRACKET, ParserState.ARRAY_EXIT, Builder::exitContainer),
            new Transition(ParserState.ARRAY_ENTRY, ParserEvent.BOOLEAN_EVENT, ParserState.VALUE, Builder::newValue),
            new Transition(ParserState.ARRAY_ENTRY, ParserEvent.INTEGER_EVENT, ParserState.VALUE, Builder::newValue),
            new Transition(ParserState.ARRAY_ENTRY, ParserEvent.FLOAT_EVENT, ParserState.VALUE, Builder::newValue),
            new Transition(ParserState.ARRAY_ENTRY, ParserEvent.STRING_EVENT, ParserState.VALUE, Builder::newValue),
            new Transition(ParserState.ARRAY_ENTRY, ParserEvent.NULL_EVENT, ParserState.VALUE, Builder::newValue),

            new Transition(ARRAY_ENTRY, ParserEvent.CLOSED_BRACKET, ParserState.ARRAY_EXIT, null),
            new Transition(ARRAY_ENTRY, ParserEvent.OPEN_BRACE, ParserState.OBJECT_ENTRY, Builder::newObject),

            new Transition(ParserState.VALUE, ParserEvent.COMMA, COMMA, null),
            new Transition(ParserState.VALUE, ParserEvent.CLOSED_BRACKET, ParserState.ARRAY_EXIT, Builder::exitContainer),


            new Transition(ParserState.COMMA, ParserEvent.BOOLEAN_EVENT, ParserState.VALUE, Builder::newValue),
            new Transition(ParserState.COMMA, ParserEvent.INTEGER_EVENT, ParserState.VALUE, Builder::newValue),
            new Transition(ParserState.COMMA, ParserEvent.FLOAT_EVENT, ParserState.VALUE, Builder::newValue),
            new Transition(ParserState.COMMA, ParserEvent.STRING_EVENT, ParserState.VALUE, Builder::newValue),
            new Transition(ParserState.COMMA, ParserEvent.NULL_EVENT, ParserState.VALUE, Builder::newValue),

            new Transition(COMMA, ParserEvent.OPEN_BRACE, ParserState.OBJECT_ENTRY, Builder::newObject),

            new Transition(OBJECT_EXIT, ParserEvent.COMMA, ParserState.COMMA, null),
            new Transition(OBJECT_EXIT, ParserEvent.CLOSED_BRACKET, ParserState.ARRAY_EXIT, Builder::exitContainer),

            new Transition(ARRAY_EXIT, ParserEvent.CLOSED_BRACKET, ParserState.ARRAY_EXIT, Builder::exitContainer),
            new Transition(ARRAY_EXIT, ParserEvent.COMMA, ParserState.COMMA, null),

            new Transition(ParserState.END, ParserEvent.EOF, ParserState.END, null)
    };

    Parser(SyntaxBuilder syntaxBuilder) {
        this.builder = syntaxBuilder;
    }

    @Override
    public void openBrace(int line, int pos) {
        handleEvent(ParserEvent.OPEN_BRACE, line, pos);
    }

    @Override
    public void closeBrace(int line, int pos) {
        handleEvent(ParserEvent.CLOSED_BRACE, line, pos);
    }

    @Override
    public void comma(int line, int pos) {
        handleEvent(ParserEvent.COMMA, line, pos);
    }

    @Override
    public void colon(int line, int pos) {
        handleEvent(ParserEvent.COLON, line, pos);
    }

    @Override
    public void openBracket(int line, int pos) {
        handleEvent(ParserEvent.OPEN_BRACKET, line, pos);
    }

    @Override
    public void closeBracket(int line, int pos) {
        handleEvent(ParserEvent.CLOSED_BRACKET, line, pos);
    }

    @Override
    public void stringField(String stringValue, int line, int pos) {

        if (!builder.isInArray()) {
            switch (state) {
                case OBJECT_ENTRY:
                case ARRAY_ENTRY:
                case COMMA:
                    builder.setKeyName(stringValue);
                    handleEvent(ParserEvent.KEY, line, pos);
                    return;
            }
        }

        builder.setValue(new JsonSyntax.JsonString(stringValue));
        handleEvent(ParserEvent.STRING_EVENT, line, pos);
    }

    @Override
    public void integerField(int integerValue, int line, int pos) {
        builder.setValue(new JsonSyntax.JsonInteger(integerValue));
        handleEvent(ParserEvent.INTEGER_EVENT, line, pos);
    }

    @Override
    public void floatField(float floatValue, int line, int pos) {
        builder.setValue(new JsonSyntax.JsonFloat(floatValue));
        handleEvent(ParserEvent.FLOAT_EVENT, line, pos);
    }

    @Override
    public void booleanField(boolean booleanValue, int line, int pos) {
        builder.setValue(new JsonSyntax.JsonBoolean(booleanValue));
        handleEvent(ParserEvent.BOOLEAN_EVENT, line, pos);
    }

    @Override
    public void nullField(int line, int pos) {
        builder.setValue(new JsonSyntax.JsonNull());
        handleEvent(ParserEvent.NULL_EVENT, line, pos);
    }

    @Override
    public void error(int line, int pos) {
        handleEvent(ParserEvent.OPEN_BRACE, line, pos);
    }

    private void handleEvent(ParserEvent event, int line, int pos) {
        Transition[] selectedTransitions = transitions;
        if (builder.isInArray()) {
            selectedTransitions = arrayTransitions;
        }


        for (Transition t : selectedTransitions) {
            if (t.currentState == state && t.event == event) {
                state = t.newState;
                if (t.action != null)
                    t.action.accept(builder);
                return;
            }
        }
        handleEventError(event, line, pos);
    }

    private void handleEventError(ParserEvent event, int line, int pos) {
        builder.syntaxError(state, event, line, pos);
    }

    private class Transition {
        ParserState currentState;
        ParserEvent event;
        ParserState newState;
        Consumer<Builder> action;

        Transition(ParserState currentState, ParserEvent event,
                   ParserState newState, Consumer<Builder> action) {
            this.currentState = currentState;
            this.event = event;
            this.newState = newState;
            this.action = action;
        }
    }
}
