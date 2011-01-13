/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.internal.referencing;

import java.io.File;

import org.geotoolkit.console.Action;
import org.geotoolkit.console.CommandLine;


/**
 * Packs the EPSG data script from the command line.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final class EpsgPackCommands extends CommandLine {
    /**
     * Creates a new instance of {@code EpsgPackCommands}.
     *
     * @param arguments The command-line arguments.
     */
    protected EpsgPackCommands(final String[] arguments) {
        super("java -jar geotk-epsg-pack.jar", arguments);
    }

    /**
     * Creates a new instance of {@code EpsgPackCommands} with the given arguments
     * and {@linkplain #run() run} it.
     *
     * @param arguments Command line arguments.
     */
    public static void main(final String[] arguments) {
        final EpsgPackCommands console = new EpsgPackCommands(arguments);
        console.run();
    }

    /**
     * Compacts the source file (first argument) and write the result in a new file
     * (the second argument).
     */
    @Action(minimalArgumentCount=2, maximalArgumentCount=2)
    public void compact() {
        try {
            new EpsgDataPack().run(new File(arguments[0]), new File(arguments[1]));
        } catch (Exception exception) {
            printException(exception);
        }
    }
}
