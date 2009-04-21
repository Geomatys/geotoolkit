/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.test;

import java.io.PrintStream;


/**
 * A set of utility methods for provided as a help for developpers writing test suite.
 * This utility class is not used during the tests; only during the writing of those tests.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 */
public final class Tools {
    /**
     * The character to be substituted to {@code '"'} in {@link #printAsJavaCode}.
     */
    static final char OPENING_QUOTE = '\u201C', CLOSING_QUOTE = '\u201D';

    /**
     * Do not allows instantiation of this class.
     */
    private Tools() {
    }

    /**
     * Prints the given text to the standard output stream, formatted as a Java {@link String}
     * declaration constant. The quote character is escaped to special unicode characters for
     * easier reading. The generated code presumes that the following import statement is
     * declared in the class where to code is going to be copied:
     *
     * {@preformat java
     *     import static org.geotoolkit.test.Commons.*;
     * }
     *
     * @param text The text to format as Java code.
     */
    public static void printAsJavaCode(final String text) {
        final PrintStream out = System.out;
        out.print("        final String text =");
        final boolean hasQuotes = text.indexOf('"') >= 0;
        if (hasQuotes) {
            out.print(" decodeQuotes(");
        }
        out.println();

        final String margin = "                "; // 4 indentation levels (16 spaces).
        boolean continuing = false;
        for (final StringIterator it=new StringIterator(text); it.hasNext();) {
            if (continuing) {
                out.println("\\n\" +");
            }
            continuing = true;
            out.print(margin);
            out.print('"');
            int quotes = 0;
            final String line = it.next();
            for (int i=0; i<line.length(); i++) {
                char c = line.charAt(i);
                switch (c) {
                    case OPENING_QUOTE: // fallthrough
                    case CLOSING_QUOTE: {
                        throw new IllegalArgumentException("Text already contains quotation marks.");
                    }
                    case '"': {
                        c = (quotes & 1) == 0 ? OPENING_QUOTE : CLOSING_QUOTE;
                        quotes++;
                    }
                }
                out.print(c);
            }
        }
        out.print('"');
        if (hasQuotes) {
            out.print(')');
        }
        out.println(';');
    }
}
