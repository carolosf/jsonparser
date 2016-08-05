package com.fantasticbytes.jsonparser.lexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

interface TokenCollectorInterface {
    void collectorEvent(String o, int line, int pos);
}

public class Lexer {
    private static Pattern whiteSpacePattern = Pattern.compile("^\\s+");
    private static Pattern stringPattern = Pattern.compile("^\"(.*?)\"");
    private static Pattern intPattern = Pattern.compile("^\\d+");
    private static Pattern floatPattern = Pattern.compile("^\\d+\\.\\d+");
    private static Pattern booleanPattern = Pattern.compile("^true|^false");
    private static Pattern nullPattern = Pattern.compile("^null");

    private TokenCollector collector;

    private int lineNumber;
    private int position;

    public Lexer(TokenCollector collector) {
        this.collector = collector;
    }

    public void lex(String s) {
        lineNumber = 1;
        String lines[] = s.split("\n");
        for (String line : lines) {
            lexLine(line);
            lineNumber++;
        }
    }

    private void lexLine(String line) {
        for (position = 0; position < line.length(); ) {
            lexToken(line);
        }
    }

    private void lexToken(String line) {
        if (!findToken(line)) {
            collector.error(lineNumber, position + 1);
            position += 1;
        }
    }

    private boolean findToken(String line) {
        return findWhiteSpace(line) ||
                findSingleCharacterToken(line) ||
                findFloat(line) ||
                findInteger(line) ||
                findNull(line) ||
                findBoolean(line) ||
                findString(line);
    }

    private boolean findWhiteSpace(String line) {
        Matcher matcher = whiteSpacePattern.matcher(line.substring(position));
        if (matcher.find()) {
            position += matcher.end();
            return true;
        }
        return false;
    }

    private boolean findSingleCharacterToken(String line) {
        String c = line.substring(position, position + 1);
        switch (c) {
            case "{":
                collector.openBrace(lineNumber, position);
                break;
            case "}":
                collector.closeBrace(lineNumber, position);
                break;
            case ",":
                collector.comma(lineNumber, position);
                break;
            case ":":
                collector.colon(lineNumber, position);
                break;
            case "[":
                collector.openBracket(lineNumber, position);
                break;
            case "]":
                collector.closeBracket(lineNumber, position);
                break;
            default:
                return false;
        }
        position++;
        return true;
    }

    private boolean findString(String line) {
        return findByMatcher(line, stringPattern, (value, lineNum, pos) -> collector.stringField(value, lineNum, pos));
    }

    private boolean findInteger(String line) {
        return findByMatcher(
                line,
                intPattern,
                (value, lineNumber, pos) -> collector.integerField(Integer.parseInt(value), lineNumber, pos)
        );
    }

    private boolean findFloat(String line) {
        return findByMatcher(
                line,
                floatPattern,
                (value, lineNumber, pos) -> collector.floatField(Float.parseFloat(value), lineNumber, pos)
        );
    }

    private boolean findBoolean(String line) {
        return findByMatcher(
                line,
                booleanPattern,
                (value, lineNumber, pos) -> collector.booleanField(Boolean.parseBoolean(value), lineNumber, pos)
        );
    }

    private boolean findNull(String line) {
        return findByMatcher(
                line,
                nullPattern,
                (value, lineNumber, pos) -> collector.nullField(lineNumber, pos)
        );
    }

    private boolean findByMatcher(String line, Pattern pattern, TokenCollectorInterface i) {
        Matcher matcher = pattern.matcher(line.substring(position));
        if (matcher.find()) {
            i.collectorEvent(matcher.group(matcher.groupCount()), lineNumber, position);
            position += matcher.end();
            return true;
        }
        return false;
    }
}
