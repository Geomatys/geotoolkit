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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The turtle alphabet on the integer (a,b) triangular lattice (basis u=(√3/4,1/4),
 * v=(0,1/2)). Draw symbols {E e S s U u D d T t} are unit segments; `+`/`-` are 60°
 * turns. Each symbol also carries the 3 corners of the triangular cell it hosts.
 * Everything here is exact integer — √3 only enters when (a,b) is later mapped to
 * A5 triple coordinates (in {@link LSystem}).
 *
 * <p>Port of upstream {@code lattice/lsystem/turtle.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Turtle {

    /**
     * A point (or vector) of the integer (a,b) triangular lattice.
     * Components are {@code long}: corner sums of resolution 30 cells exceed the int range.
     *
     * @param a coordinate along u
     * @param b coordinate along v
     */
    public record AB(long a, long b) {
    }

    /**
     * Callback of {@link #walk(String, AB, int, DrawCallback)}.
     */
    @FunctionalInterface
    public interface DrawCallback {
        /**
         * Called for each draw symbol, before advancing.
         *
         * @param sym draw symbol
         * @param from turtle position
         * @param heading turtle heading (0-5)
         */
        void onDraw(String sym, AB from, int heading);
    }

    /**
     * Final turtle state, result of {@link #walk(String, AB, int, DrawCallback)}.
     *
     * @param pos final position
     * @param heading final heading (0-5)
     */
    public record State(AB pos, int heading) {
    }

    /**
     * Net displacement and heading of a draw string, result of {@link #netOf(String)}.
     *
     * @param disp net (a,b) displacement
     * @param dHeading net heading
     */
    public record Net(AB disp, int dHeading) {
    }

    private Turtle() {
    }

    public static AB add(AB p, AB q) {
        return new AB(p.a + q.a, p.b + q.b);
    }

    /** 180° rotation. */
    public static AB neg(AB p) {
        return new AB(-p.a, -p.b);
    }

    /** 60° CCW, order 6. */
    private static AB rot60(AB p) {
        return new AB(-p.b, p.a + p.b);
    }

    public static AB rotTimes(AB p, int n) {
        AB r = p;
        final int k = ((n % 6) + 6) % 6;
        for (int i = 0; i < k; i++) r = rot60(r);
        return r;
    }

    // Step vector of each draw symbol at heading 0. Lowercase = same step, cell hosted
    // on the other side (see HOST_OFFSETS).
    private static final Map<String, AB> BASE;
    /** Draw symbols. */
    public static final Set<String> DRAW;
    // The 3 corner offsets (heading 0, from the segment start) of the cell each symbol hosts.
    private static final Map<String, AB[]> HOST_OFFSETS;
    static {
        final Map<String, AB> base = new HashMap<>();
        base.put("E", new AB(4, 0));
        base.put("e", new AB(4, 0));
        base.put("S", new AB(4, -2));
        base.put("s", new AB(4, -2));
        base.put("U", new AB(0, 2));
        base.put("u", new AB(0, 2));
        base.put("D", new AB(0, -2));
        base.put("d", new AB(0, -2));
        base.put("T", new AB(-4, 0));
        base.put("t", new AB(-4, 0));
        BASE = Collections.unmodifiableMap(base);
        DRAW = Collections.unmodifiableSet(base.keySet());

        final Map<String, AB[]> host = new HashMap<>();
        host.put("E", new AB[] {new AB(0, 0), new AB(4, 0), new AB(4, -4)});
        host.put("e", new AB[] {new AB(0, 0), new AB(4, 0), new AB(0, 4)});
        host.put("S", new AB[] {new AB(0, 0), new AB(4, 0), new AB(4, -4)});
        host.put("s", new AB[] {new AB(4, -2), new AB(0, 2), new AB(0, -2)});
        host.put("U", new AB[] {new AB(0, 2), new AB(0, -2), new AB(4, -2)});
        host.put("u", new AB[] {new AB(0, 0), new AB(0, 4), new AB(-4, 4)});
        host.put("D", new AB[] {new AB(0, 2), new AB(0, -2), new AB(4, -2)});
        host.put("d", new AB[] {new AB(0, 0), new AB(0, -4), new AB(-4, 0)});
        host.put("T", new AB[] {new AB(0, -4), new AB(-4, 0), new AB(-4, -4)});
        host.put("t", new AB[] {new AB(-4, 4), new AB(0, 0), new AB(0, 4)});
        HOST_OFFSETS = Collections.unmodifiableMap(host);
    }

    /** The 3 (a,b) corners of the cell hosted by `sym`, drawn from `from` at `heading`. */
    public static AB[] hostCorners(String sym, AB from, int heading) {
        final AB[] offsets = HOST_OFFSETS.get(sym);
        final AB[] result = new AB[offsets.length];
        for (int i = 0; i < offsets.length; i++) result[i] = add(from, rotTimes(offsets[i], heading));
        return result;
    }

    /** The corner SUM (= 3·centroid, an exact integer) of that cell. */
    public static AB hostSum(String sym, AB from, int heading) {
        final AB[] c = hostCorners(sym, from, heading);
        final AB p = c[0], q = c[1], r = c[2];
        return new AB(p.a + q.a + r.a, p.b + q.b + r.b);
    }

    /**
     * Walk a draw string (draw symbols + `+`/`-` turns) from (pos, heading). Calls
     * `onDraw(sym, from, heading)` for each draw symbol (before advancing). Returns the
     * final turtle state.
     *
     * @param onDraw callback, may be null
     */
    public static State walk(String s, AB pos, int heading, DrawCallback onDraw) {
        AB p = pos;
        int h = ((heading % 6) + 6) % 6;
        for (int i = 0, n = s.length(); i < n; i++) {
            final char c = s.charAt(i);
            if (c == '+') {
                h = (h + 1) % 6;
                continue;
            }
            if (c == '-') {
                h = (h + 5) % 6;
                continue;
            }
            final String ch = String.valueOf(c);
            if (!DRAW.contains(ch)) continue;
            if (onDraw != null) onDraw.onDraw(ch, p, h);
            p = add(p, rotTimes(BASE.get(ch), h));
        }
        return new State(p, h);
    }

    /** Net (a,b) displacement + net heading of a draw string, from origin at heading 0. */
    public static Net netOf(String s) {
        final State end = walk(s, new AB(0, 0), 0, null);
        return new Net(end.pos(), end.heading());
    }
}
