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
package org.geotoolkit.internal.setup;

import org.geotoolkit.console.CommandLine;
import org.geotoolkit.internal.GraphicsUtilities;


/**
 * The installer starting point.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.00
 * @module
 */
public final class Main extends CommandLine {
    /**
     * Invoked with command-line arguments.
     *
     * @param args The command-line arguments.
     */
    private Main(final String[] args) {
        super("java -jar geotk-setup.jar", args);
    }

    /**
     * Displays the setup windows as a Swing application.
     */
    @Override
    protected void unknownAction(final String action) {
        GraphicsUtilities.setLookAndFeel(Main.class, "run");
        ControlPanel.show(locale);
    }

    /**
     * Invoked by the JVM from the command line.
     *
     * @param args The command-line arguments.
     */
    public static void main(final String[] args) {
        final Main m = new Main(args);
        m.run();
    }
}
