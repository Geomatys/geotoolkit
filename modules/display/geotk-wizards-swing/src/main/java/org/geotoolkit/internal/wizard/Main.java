/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.internal.wizard;

import org.geotoolkit.console.ReferencingCommands;


/**
 * The entry point invoked from the command line. This is intended to be a
 * lightweight branching point. A very different set of classes is going to
 * be loaded depending if a command lone argument has been provided or not.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.01
 * @module
 */
public final class Main {
    /**
     * Do not allow instantiation of this class.
     */
    private Main() {
    }

    /**
     * Invoked from the command line for displaying the main menu, or
     * running the command line if at least one argument is provided.
     *
     * @param args The command line arguments.
     */
    public static void main(final String[] args) {
        if (args.length == 0) {
            Menu.run();
        } else {
            ReferencingCommands.main(args);
        }
    }
}
