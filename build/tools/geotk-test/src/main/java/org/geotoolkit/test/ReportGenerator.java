/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Writer;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;


/**
 * Base class for tools generating reports. Those tools are not really tests, but failure to
 * execute those tools would be an indication of problem. The report generators are executed
 * manually. For example {@link org.geotoolkit.referencing.factory.epsg.SupportedListGenerator}
 * is executed after the EPSG database has been upgraded, or the map projection implementations
 * changed.
 * <p>
 * This class provides only static utility methods. The instance fields are left to subclasses.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 */
public abstract strictfp class ReportGenerator {
    /**
     * The locale to use for formatting messages of every reports.
     */
    protected static final Locale LOCALE = Locale.ENGLISH;

    /**
     * The encoding of every reports.
     */
    protected static final String ENCODING = "UTF-8";

    /**
     * Creates a new report generator.
     */
    protected ReportGenerator() {
    }

    /**
     * Opens the given file for writing and writes an HTML header, including the {@code <HTML>}
     * and {@code <BODY>} elements, and the title in {@code <h1>} section.
     *
     * @param  file The file to open.
     * @param  title The tile.
     * @return The stream to use for writing.
     * @throws IOException If an error occurred while opening the file or writing to it.
     */
    protected static Writer openHTML(final File file, final String title) throws IOException {
        final Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), ENCODING));
        out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" +
                  "<HTML>\n" +
                  "  <HEAD>\n" +
                  "    <META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                  "    <TITLE>");
        out.write(title);
        out.write("</TITLE>\n  </HEAD>\n  <BODY>\n  <h1>");
        out.write(title);
        out.write("</h1>\n");
        return out;
    }

    /**
     * Writes the closing {@code </BODY>} and {@code </HTML>} elements and closes the given stream.
     *
     * @param  out The stream to close.
     * @throws IOException If an error occurred while writing the elements.
     */
    protected static void closeHTML(final Writer out) throws IOException {
        out.write("  </BODY>\n</HTML>\n");
        out.close();
    }
}
