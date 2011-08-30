/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.mapfile.process;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class MapfileExpressionTokenizer {

    private final String code;
    private int cursorIndex;
    private int lineStartIndex;
    private int lineNumber;
    private Token token;

    private MapfileExpressionTokenizer(String code) {
        this.code = code;
        this.cursorIndex = 0;
        this.lineNumber = 0;
        this.lineStartIndex = 0;
    }

    private boolean hasNext() {
        this.findNext();
        return (this.token != null);
    }

    private Token next() {
        this.findNext();
        final Token candidate = this.token;
        this.token = null;
        return candidate;
    }

    private void findNext() {
        if (this.token != null) {
            //next value already found
            return;
        }

        //search the next token
        int startCharacterIndex = moveToNextCharacter();
        if (startCharacterIndex == -1) {
            //we reached the end of the file
            return;
        }

        //start parsing the token
        token = new Token();
        token.startLineIndex = lineNumber;
        token.endLineIndex = lineNumber;
        token.startCharacterIndex = cursorIndex - lineStartIndex;

        char ch = code.charAt(cursorIndex);
        if (ch == '"') {
            //we hit a string value
            moveToStringEnd('"');
            return;
        }else if (ch == '\'') {
            //we hit a string value
            moveToStringEnd('\'');
            return;
        }

        moveToWordEnd();
        return;
    }

    private void moveToWordEnd() {
        int end = nextDelimiterIndex(cursorIndex);
        if (end == -1) {
            //we reached the end of file
            end = code.length();
        } else if (end == cursorIndex) {
            //token is a delimiter
            end++;
        }

        token.endCharacterIndex = end - lineStartIndex;
        int size = token.endCharacterIndex - token.startCharacterIndex;
        token.value = code.substring(cursorIndex, cursorIndex + size);
        cursorIndex = end;
    }

    private void moveToStringEnd(char endchar) {
        int from = cursorIndex + 1;
        int end;
        while (true) {
            end = code.indexOf(endchar, from);
            if (end == -1) {
                //we reached the end of file without finding the end cote
                throw new RuntimeException("End of String value not found line  " + lineNumber + " column " + token.startCharacterIndex);

            } else if (code.charAt(end - 1) == '\\') {
                //not the end of the string, just an escape character
                from = end + 1;
                continue;
            }
            end++; //we take the " in the value
            break;
        }

        token.endCharacterIndex = end - lineStartIndex;
        int size = token.endCharacterIndex - token.startCharacterIndex;
        token.value = code.substring(cursorIndex, cursorIndex + size);
        cursorIndex = end;
    }

    /**
     * Move to the next not empty character
     */
    private int moveToNextCharacter() {
        while (cursorIndex < code.length()) {
            char ch = code.charAt(cursorIndex);
            if (ch == '\n') {
                this.lineNumber++;
                this.lineStartIndex = this.cursorIndex;
                this.cursorIndex++;
                continue;
            } else if (ch == ' '
                    || ch == '\t'
                    || ch == '\r') {
                this.cursorIndex++;
                continue;
            } else {
                return this.cursorIndex;
            }
        }
        return -1;
    }

    private int nextDelimiterIndex(int from) {
        int index = from;
        while (index < code.length()) {
            final char ch = code.charAt(index);
            if (isDelimiter(ch)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private static boolean isDelimiter(char ch) {
        return (ch == ' '
                || ch == '\n'
                || ch == '\r'
                || ch == '\t'
                || ch == '('
                || ch == ')'
                || ch == '/'
                || ch == '|'
                || ch == '['
                || ch == ']');
    }
    
    
    public static class Token {

        public String value;
        public int startLineIndex;
        public int endLineIndex;
        public int startCharacterIndex;
        public int endCharacterIndex;

        @Override
        public String toString() {
            return value;
        }
    }

    public static List<Token> toTokens(final String code) {

        // read all tokens
        final MapfileExpressionTokenizer tokenizer = new MapfileExpressionTokenizer(code);
        final List<Token> tokenStack = new ArrayList<Token>();

        while (tokenizer.hasNext()) {
            final Token token = tokenizer.next();
            tokenStack.add(token);
        }

        return tokenStack;
    }
    
}

