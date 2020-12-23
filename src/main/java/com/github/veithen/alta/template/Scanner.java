/*
 * #%L
 * Alta Maven Plugin
 * %%
 * Copyright (C) 2014 - 2020 Andreas Veithen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.veithen.alta.template;

final class Scanner {
    private final String s;
    private int pos;

    Scanner(String s) {
        this.s = s;
    }

    int peek() {
        return pos == s.length() ? -1 : s.charAt(pos);
    }

    char consume() throws InvalidTemplateException {
        if (pos == s.length()) {
            throw new InvalidTemplateException("Unexpected end of string");
        } else {
            return s.charAt(pos++);
        }
    }

    void expect(char expected) throws InvalidTemplateException {
        if (pos == s.length()) {
            throw new InvalidTemplateException("Unexpected end of string");
        }
        char c = s.charAt(pos);
        if (c != expected) {
            throw new InvalidTemplateException("Expected '" + expected + "' but got '" + c + "'");
        }
        pos++;
    }

    boolean consume(char expected) throws InvalidTemplateException {
        if (peek() == expected) {
            consume();
            return true;
        } else {
            return false;
        }
    }

    String parseString(String stopChars) throws InvalidTemplateException {
        StringBuilder buffer = new StringBuilder();
        while (true) {
            int c = peek();
            if (c == '\\') {
                consume();
                if (peek() != -1) {
                    c = consume();
                    if (stopChars.indexOf(c) == -1) {
                        buffer.append('\\');
                    }
                }
                buffer.append((char) c);
            } else if (c == -1 || stopChars.indexOf(c) != -1) {
                return buffer.toString();
            } else {
                buffer.append(consume());
            }
        }
    }

    String parseAtom() throws InvalidTemplateException {
        StringBuilder buffer = new StringBuilder();
        while (isAtomChar(peek())) {
            buffer.append(consume());
        }
        if (buffer.length() == 0) {
            throw new InvalidTemplateException("Expected identifier");
        }
        return buffer.toString();
    }

    private static boolean isAtomChar(int c) {
        return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z';
    }
}
