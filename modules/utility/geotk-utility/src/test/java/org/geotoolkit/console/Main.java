/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.console;

import java.io.*;


/**
 * A pseudo-main class for testing purpose.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.00
 */
final class Main extends CommandLine {
    @Option
    String string;

    @Option
    boolean flag;

    @Option(mandatory = true)
    int integer;

    @Option
    double real = 23; // Uses a default value.

    @Option
    File file;

    @Option(name="renamed")
    String dummy;

    /**
     * Where standard and error messages are sent.
     */
    final StringBuffer messages;

    /**
     * Creates the main class for the given command-line arguments.
     *
     * @param args The command-line arguments.
     */
    public Main(String[] args) {
        super(null, args);
        final StringWriter buffer = new StringWriter();
        messages = buffer.getBuffer();
        out = new PrintWriter(buffer); // Redirect the standard stream.
        err = new PrintWriter(buffer); // Redirect the error stream.
    }

    /**
     * Prevents test failures to exit the JVM.
     */
    @Override
    protected void exit(final int code) {
        throw new IllegalStateException("Exit code: " + code);
    }
}
