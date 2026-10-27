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
package org.geotoolkit.dggs.a5.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test helpers shared by the A5 tests : fixture loading and the vitest matchers of
 * upstream {@code tests/utils/matchers.ts}.
 *
 * <p>Fixtures are copies of the upstream a5 {@code tests/} JSON files, stored under
 * {@code src/test/resources/org/geotoolkit/dggs/a5/internal/} with their upstream
 * relative path (for example {@code fixtures/serialization.json}).</p>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Fixtures {

    /**
     * Default number of decimal places for {@link #assertCloseToArray(double[], double[])}.
     */
    public static final int DEFAULT_TOLERANCE = 13;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Fixtures(){}

    /**
     * Loads a fixture file.
     *
     * @param path path relative to the upstream {@code tests/} folder, e.g. {@code "fixtures/serialization.json"}
     * @return parsed JSON tree
     */
    public static JsonNode load(String path) {
        try (InputStream in = Fixtures.class.getResourceAsStream(path)) {
            if (in == null) throw new IllegalArgumentException("Missing fixture " + path);
            return MAPPER.readTree(in);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * Parses an unsigned 64 bits hexadecimal cell id, as written by upstream {@code u64ToHex}.
     */
    public static long hex(String hex) {
        return Long.parseUnsignedLong(hex, 16);
    }

    /**
     * Parses an unsigned 64 bits hexadecimal cell id from a JSON text node.
     */
    public static long hex(JsonNode node) {
        return hex(node.asText());
    }

    /**
     * Parses an unsigned 64 bits decimal integer, from a JSON text or number node.
     */
    public static long u64(JsonNode node) {
        return node.isTextual() ? Long.parseUnsignedLong(node.asText()) : node.bigIntegerValue().longValue();
    }

    /**
     * Parses an array of hexadecimal cell ids.
     */
    public static long[] hexArray(JsonNode array) {
        final long[] result = new long[array.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = hex(array.get(i));
        }
        return result;
    }

    /**
     * Converts a JSON array of numbers to a double array.
     */
    public static double[] doubles(JsonNode array) {
        final double[] result = new double[array.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = array.get(i).asDouble();
        }
        return result;
    }

    /**
     * Converts a JSON array of number arrays to a double matrix.
     */
    public static double[][] doubles2(JsonNode array) {
        final double[][] result = new double[array.size()][];
        for (int i = 0; i < result.length; i++) {
            result[i] = doubles(array.get(i));
        }
        return result;
    }

    /**
     * Converts a JSON array of arrays of number arrays (e.g. polygon rings) to a double cube.
     */
    public static double[][][] doubles3(JsonNode array) {
        final double[][][] result = new double[array.size()][][];
        for (int i = 0; i < result.length; i++) {
            result[i] = doubles2(array.get(i));
        }
        return result;
    }

    /**
     * Port of the upstream {@code toBeCloseToArray} matcher with the default tolerance of 13 decimals.
     */
    public static void assertCloseToArray(double[] expected, double[] received) {
        assertCloseToArray(expected, received, DEFAULT_TOLERANCE);
    }

    /**
     * Port of the upstream {@code toBeCloseToArray} matcher :
     * same length and every value within {@code 10^-tolerance}.
     */
    public static void assertCloseToArray(double[] expected, double[] received, int tolerance) {
        assertEquals(expected.length, received.length, "array length");
        final double eps = Math.pow(10, -tolerance);
        for (int i = 0; i < expected.length; i++) {
            final double e = expected[i];
            final double r = received[i];
            assertTrue(Math.abs(r - e) < eps,
                    () -> "Expected arrays to be close to each other within " + tolerance + " decimal places. Received: "
                    + java.util.Arrays.toString(received) + ", Expected: " + java.util.Arrays.toString(expected));
        }
    }

    /**
     * Port of vitest {@code toBeCloseTo(expected, numDigits)} : passes when
     * {@code |expected - received| < 10^-numDigits / 2}.
     */
    public static void assertCloseTo(double expected, double received, int numDigits) {
        final double eps = Math.pow(10, -numDigits) / 2;
        assertTrue(Math.abs(expected - received) < eps,
                () -> "Expected " + received + " to be close to " + expected + " (" + numDigits + " digits)");
    }

    /**
     * Port of vitest {@code toBeCloseTo(expected)} with its default of 2 digits.
     */
    public static void assertCloseTo(double expected, double received) {
        assertCloseTo(expected, received, 2);
    }
}
