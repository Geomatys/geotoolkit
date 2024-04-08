/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.test;

import java.util.Objects;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import static java.lang.StrictMath.abs;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.CharSequences;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.test.xml.DocumentComparator;

import static org.junit.Assert.*;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.xml.sax.SAXException;


/**
 * Copy of assertion methods provided by Apache SIS.
 */
public final class Assertions {
    private Assertions() {
    }

    public static void assertNotDeepEquals(final Object o1, final Object o2) {
        assertNotSame("same", o1, o2);
        assertFalse("equals",                      Objects  .equals    (o1, o2));
        assertFalse("deepEquals",                  Objects  .deepEquals(o1, o2));
        assertFalse("deepEquals(STRICT)",          Utilities.deepEquals(o1, o2, ComparisonMode.STRICT));
        assertFalse("deepEquals(BY_CONTRACT)",     Utilities.deepEquals(o1, o2, ComparisonMode.BY_CONTRACT));
        assertFalse("deepEquals(IGNORE_METADATA)", Utilities.deepEquals(o1, o2, ComparisonMode.IGNORE_METADATA));
        assertFalse("deepEquals(APPROXIMATE)",     Utilities.deepEquals(o1, o2, ComparisonMode.APPROXIMATE));
    }

    public static void assertMultilinesEquals(final CharSequence expected, final CharSequence actual) {
        assertMultilinesEquals(null, expected, actual);
    }

    public static void assertMultilinesEquals(final String message, final CharSequence expected, final CharSequence actual) {
        final CharSequence[] expectedLines = CharSequences.splitOnEOL(expected);
        final CharSequence[] actualLines   = CharSequences.splitOnEOL(actual);
        final int length = StrictMath.min(expectedLines.length, actualLines.length);
        final StringBuilder buffer = new StringBuilder(message != null ? message : "Line").append('[');
        final int base = buffer.length();
        for (int i=0; i<length; i++) {
            CharSequence e = expectedLines[i];
            CharSequence a = actualLines[i];
            e = e.subSequence(0, CharSequences.skipTrailingWhitespaces(e, 0, e.length()));
            a = a.subSequence(0, CharSequences.skipTrailingWhitespaces(a, 0, a.length()));
            assertEquals(buffer.append(i).append(']').toString(), e, a);
            buffer.setLength(base);
        }
        if (expectedLines.length > actualLines.length) {
            fail(buffer.append(length).append("] missing line: ").append(expectedLines[length]).toString());
        }
        if (expectedLines.length < actualLines.length) {
            fail(buffer.append(length).append("] extraneous line: ").append(actualLines[length]).toString());
        }
    }

    public static <T> T assertSerializedEquals(final T object) {
        Objects.requireNonNull(object);
        final Object deserialized;
        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(buffer)) {
                out.writeObject(object);
            }
            // Now reads the object we just serialized.
            final byte[] data = buffer.toByteArray();
            try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data))) {
                try {
                    deserialized = in.readObject();
                } catch (ClassNotFoundException e) {
                    throw new AssertionError(e);
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e.toString(), e);
        }
        assertNotNull("Deserialized object shall not be null.", deserialized);
        @SuppressWarnings("unchecked")
        final Class<? extends T> type = (Class<? extends T>) object.getClass();
        assertEquals("Deserialized object not equal to the original one.", object, deserialized);
        assertEquals("Deserialized object has a different hash code.",
                object.hashCode(), deserialized.hashCode());
        return type.cast(deserialized);
    }

    public static void assertXmlEquals(final Object expected, final Object actual, final String... ignoredAttributes) {
        assertXmlEquals(expected, actual, 0, null, ignoredAttributes);
    }

    public static void assertXmlEquals(final Object expected, final Object actual,
            final double tolerance, final String[] ignoredNodes, final String[] ignoredAttributes)
    {
        final DocumentComparator comparator;
        try {
            comparator = new DocumentComparator(expected, actual);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new AssertionError(e);
        }
        comparator.tolerance = tolerance;
        comparator.ignoreComments = true;
        if (ignoredNodes != null) {
            for (final String node : ignoredNodes) {
                comparator.ignoredNodes.add(DocumentComparator.substitutePrefix(node));
            }
        }
        if (ignoredAttributes != null) {
            for (final String attribute : ignoredAttributes) {
                comparator.ignoredAttributes.add(DocumentComparator.substitutePrefix(attribute));
            }
        }
        comparator.compare();
    }

    /**
     * Asserts that two envelopes have the same minimum and maximum coordinates.
     * This method ignores the envelope type (i.e. the implementation class) and
     * the CRS.
     *
     * @param expected the expected envelope.
     * @param actual the envelope to compare with the expected one.
     * @param tolerances the tolerance threshold on location along each axis. If
     *                   this array length is shorter than the number of dimensions, then the last
     *                   tolerance is reused for all remaining axes. If this array is empty, then
     *                   the tolerance threshold is zero.
     */
    public static void assertEnvelopeEquals(final Envelope expected, final Envelope actual, final double... tolerances) {
       final int dimension = expected.getDimension();
       assertEquals("dimension", dimension, actual.getDimension());
       final DirectPosition expectedLower = expected.getLowerCorner();
       final DirectPosition expectedUpper = expected.getUpperCorner();
       final DirectPosition actualLower   = actual  .getLowerCorner();
       final DirectPosition actualUpper   = actual  .getUpperCorner();
       double tolerance = 0;
       for (int i=0; i<dimension; i++) {
           if (i < tolerances.length) {
                tolerance = tolerances[i];
           }
           if (abs(expectedLower.getCoordinate(i) - actualLower.getCoordinate(i)) > tolerance ||
               abs(expectedUpper.getCoordinate(i) - actualUpper.getCoordinate(i)) > tolerance)
               {
                fail("Envelopes are not equal in dimension " + i + ":\n"
                       + "expected " + Envelopes.toString(expected) + "\n"
                       + " but got " + Envelopes.toString(actual));
               }
       }
    }
}
