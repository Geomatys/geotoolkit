/**
 * Copyright (C) 2026 Geomatys and A5 contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.dggs.a5.internal.lattice.lsystem;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The A5 curve's L-system grammar: the motif rules and the string operations that
 * expand them. This file is the source of truth for the curve's definition and is
 * purely symbolic — the turtle geometry that interprets the symbols lives in
 * {@link Turtle}, and the compilation to descent tables in {@link Tables}.
 *
 * 7 self-referential motifs over the alphabet {A B C M P Q R} (+ their lowercase
 * reverses) + the draw terminals {E e S s U u D d T t} + the 60° turns +/-.
 * A LOWERCASE motif is its uppercase counterpart REVERSED, generated automatically
 * by {@link #reverseMotif(String)} — so only the 7 uppercase rules below need to be authored.
 *
 * <p>Port of upstream {@code lattice/lsystem/grammar.ts}.
 * The TS {@code Record<string, string>} tables are insertion-ordered unmodifiable maps
 * (the motif order defines the motif indices, see {@link Tables#compileGrammar(Map, Map)}).</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Grammar {

    /** Each motif's production rule (the 7 authored motifs). */
    public static final Map<String, String> RULES = orderedMap(
        "A", "PQAB",
        "B", "B+++PQ---A",
        "C", "P---RMb+++",
        "M", "qQ+++C---b",
        "P", "PpB---B+++",
        "Q", "PQ---Cb+++",
        "R", "b+++a---qQ");

    /** Each motif's leaf draw symbol — the terminal it renders as at the base case. */
    public static final Map<String, String> DRAWS = orderedMap(
        "A", "E", "B", "+e-", "C", "-e+", "M", "T", "P", "S", "Q", "D", "R", "+++D---");

    private Grammar() {
    }

    /**
     * Creates an insertion-ordered unmodifiable map from key/value pairs.
     * Equivalent of a TS object literal used as a record.
     *
     * @param keyValues alternated keys and values
     * @return unmodifiable ordered map
     */
    public static Map<String, String> orderedMap(String... keyValues) {
        final Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put(keyValues[i], keyValues[i + 1]);
        }
        return Collections.unmodifiableMap(map);
    }

    private static char swapCase(char c) {
        return (c >= 'a' && c <= 'z' ? Character.toUpperCase(c) : Character.toLowerCase(c));
    }

    /**
     * The reverse of a motif/draw string — traced end to start. Uniform transform:
     * reverse the order, swap the case of every letter (uppercase&lt;-&gt;lowercase =
     * forward&lt;-&gt;reverse partner), and flip every `+`/`-`. This is how the lowercase
     * motifs are derived from the authored uppercase rules.
     */
    public static String reverseMotif(String s) {
        final StringBuilder sb = new StringBuilder(s.length());
        for (int i = s.length() - 1; i >= 0; i--) {
            final char c = s.charAt(i);
            sb.append(c == '+' ? '-' : c == '-' ? '+' : swapCase(c));
        }
        return sb.toString();
    }

    /**
     * One expansion pass over `str`: replace each symbol using `table` (RULES or
     * DRAWS). A lowercase motif whose uppercase is in `table` expands to that rule
     * REVERSED; turns and unknown symbols pass through unchanged.
     */
    public static String expandOnce(String str, Map<String, String> table) {
        final StringBuilder out = new StringBuilder();
        for (int i = 0, n = str.length(); i < n; i++) {
            final String ch = String.valueOf(str.charAt(i));
            final String up = ch.toUpperCase(Locale.ROOT);
            if (table.get(ch) != null) out.append(table.get(ch));
            else if (!ch.equals(up) && table.get(up) != null) out.append(reverseMotif(table.get(up)));
            else out.append(ch);
        }
        return out.toString();
    }
}
