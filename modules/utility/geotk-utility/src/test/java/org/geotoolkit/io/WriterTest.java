/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.io;

import java.io.*;
import org.geotoolkit.test.Depend;
import org.geotoolkit.util.UtilitiesTest;
import static org.geotoolkit.io.LineWrapWriter.SOFT_HYPHEN;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests some {@link Writer} implementations.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@Depend(UtilitiesTest.class)
public final strictfp class WriterTest {
    /**
     * The buffer where to write test data. All those buffers
     * are expected to contains identical data.
     */
    private StringWriter buffer0, buffer1, buffer2;

    /**
     * Initialize the buffers.
     */
    @Before
    public void initialize() {
        buffer0 = new StringWriter();
        buffer1 = new StringWriter();
        buffer2 = new StringWriter();
    }

    /**
     * Creates a writer which is going to write in all the given writers together, using
     * {@code write(String)}, {@code write(char[])} and {@code write(char)} methods. This
     * is used in order to increate test coverage.
     */
    private static EchoWriter echo(final Writer out0, final Writer out1, final Writer out2) {
        return new EchoWriter(out0, new CharWriter(new EchoWriter(out1, new CharWriter(out2))));
    }

    /**
     * Ensures that the buffer content are equal to the given string.
     *
     * @param expected The expected content.
     */
    private void assertOutput(final String expected) {
        assertMultilinesEquals("Using write(String)", expected, buffer0.toString());
        assertMultilinesEquals("Using write(char[])", expected, buffer1.toString());
        assertMultilinesEquals("Using write(char)",   expected, buffer2.toString());
    }

    /**
     * Tests {@link LineWriter}.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testLine() throws IOException {
        final LineWriter out0 = new LineWriter(buffer0, " ");
        final LineWriter out1 = new LineWriter(buffer1, " ");
        final LineWriter out2 = new LineWriter(buffer2, " ");
        final EchoWriter out  = echo(out0, out1, out2);

        assertEquals(" ", out0.getLineSeparator());

        out.write("Le vrai\npolicitien, ");
        out.write("c'est celui\r\nqui\r");
        out.write("\narrive à garder \r\n");
        out.write("son\ridéal   \nt");
        out.write("out en perdant\r\ns");
        out.write("es illusions.");
        out.flush();

        assertOutput("Le vrai policitien, c'est celui qui arrive à garder son idéal " +
                     "tout en perdant ses illusions.");
    }

    /**
     * Tests {@link LineWrapWriter}.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testLineWrap() throws IOException {
        final LineWrapWriter out0 = new LineWrapWriter(new LineWriter(buffer0, "\n"), 10);
        final LineWrapWriter out1 = new LineWrapWriter(new LineWriter(buffer1, "\n"), 10);
        final LineWrapWriter out2 = new LineWrapWriter(new LineWriter(buffer2, "\n"), 10);
        final EchoWriter     out  = echo(out0, out1, out2);

        assertEquals(10, out0.getMaximalLineLength());
        final String BLUE    = X364.FOREGROUND_BLUE.sequence();
        final String DEFAULT = X364.FOREGROUND_DEFAULT.sequence();

        // Extract from Émile Nelligan (1879-1941) with soft hyphen and X3.64 sequences added.
        out.write("Ah! comme la " + BLUE + "neige" + DEFAULT + " a neigé!\n");
        out.write("Ma vitre est un jar" + SOFT_HYPHEN + "din de givre.");
        out.flush();

        // Limits:   ".........."
        assertOutput("Ah! comme\n" +
                     "la " + BLUE + "neige" + DEFAULT + " a\n" +
                     "neigé!\n" +
                     "Ma vitre\n" +
                     "est un jar" + SOFT_HYPHEN + '\n' +
                     "din de\n" +
                     "givre.");
    }

    /**
     * Tests {@link NumberedLineWriter}.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testNumbered() throws IOException {
        final NumberedLineWriter out0 = new NumberedLineWriter(new LineWriter(buffer0, "\n"));
        final NumberedLineWriter out1 = new NumberedLineWriter(new LineWriter(buffer1, "\n"));
        final NumberedLineWriter out2 = new NumberedLineWriter(new LineWriter(buffer2, "\n"));
        final EchoWriter out = echo(out0, out1, out2);

        assertEquals(1, out0.getLineNumber());

        // Extract from Arthur RIMBAUD (1854-1891), "Le bateau ivre"
        out.write("Comme je descendais des Fleuves impassibles,\n" +
                  "Je ne me sentis plus guidé par les haleurs :\n");
        out.write("Des Peaux-Rouges criards les avaient pris pour cibles,\r\n");
        out.write("Les ayant cloués nus ");
        out.write("aux poteaux de couleurs.\r");
        out.flush();

        assertOutput("[  1] Comme je descendais des Fleuves impassibles,\n" +
                     "[  2] Je ne me sentis plus guidé par les haleurs :\n" +
                     "[  3] Des Peaux-Rouges criards les avaient pris pour cibles,\n" +
                     "[  4] Les ayant cloués nus aux poteaux de couleurs.\n");
    }

    /**
     * Tests {@link ExpandedTabWriter}.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testExpandedTab() throws IOException {
        final ExpandedTabWriter out0 = new ExpandedTabWriter(buffer0);
        final ExpandedTabWriter out1 = new ExpandedTabWriter(buffer1);
        final ExpandedTabWriter out2 = new ExpandedTabWriter(buffer2);
        final EchoWriter out = echo(out0, out1, out2);

        assertEquals(8, out0.getTabWidth());

        out.write("12\t8\n");
        out.write("1234\t8\n");
        out.flush();

        assertOutput("12      8\n" +
                     "1234    8\n");
    }

    /**
     * Tests {@link TableWriter}.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testTable() throws IOException {
        final TableWriter out0 = new TableWriter(buffer0);
        final TableWriter out1 = new TableWriter(buffer1);
        final TableWriter out2 = new TableWriter(buffer2);
        final EchoWriter out = echo(out0, out1, out2);

        out0.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        out1.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        out2.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);

        // r.e.d. = Equatorial diameter Measured relative to the Earth.
        // Source: "Planet" on wikipedia on July 25, 2008.
        out.write("English\tFrench\tr.e.d.\n");
        out0.writeHorizontalSeparator();
        out1.writeHorizontalSeparator();
        out2.writeHorizontalSeparator();
        out.write("Mercury\tMercure\t0.382\n");
        out.write("Venus\tVénus\t0.949\n");
        out.write("Earth\tTerre"); out.write("\t1.00\n");
        out.write("Mars\tMa"); out.write("rs\t0.532\n");
        out.write("Jupiter\tJupiter\t11.209"); out.write("\n");
        out.write("Saturn"); out.write("\tSaturne\t"); out.write("9.449\n");
        out.write("Uranus\tUranus\t4.007\nNeptune\tNeptune\t3.883\n");
        out0.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        out1.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        out2.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        out.flush();

        assertOutput("╔═════════╤═════════╤════════╗\n" +
                     "║ English │ French  │ r.e.d. ║\n" +
                     "╟─────────┼─────────┼────────╢\n" +
                     "║ Mercury │ Mercure │ 0.382  ║\n" +
                     "║ Venus   │ Vénus   │ 0.949  ║\n" +
                     "║ Earth   │ Terre   │ 1.00   ║\n" +
                     "║ Mars    │ Mars    │ 0.532  ║\n" +
                     "║ Jupiter │ Jupiter │ 11.209 ║\n" +
                     "║ Saturn  │ Saturne │ 9.449  ║\n" +
                     "║ Uranus  │ Uranus  │ 4.007  ║\n" +
                     "║ Neptune │ Neptune │ 3.883  ║\n" +
                     "╚═════════╧═════════╧════════╝\n");
    }
}
