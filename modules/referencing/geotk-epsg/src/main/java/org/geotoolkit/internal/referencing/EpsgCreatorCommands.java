/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.internal.referencing;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.opengis.referencing.FactoryException;

import org.geotoolkit.console.Action;
import org.geotoolkit.console.CommandLine;
import org.geotoolkit.console.Option;
import org.geotoolkit.referencing.factory.epsg.EpsgInstaller;


/**
 * Creates the EPSG database from the scripts given in a directory.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public class EpsgCreatorCommands extends CommandLine {
    /**
     * The directory which contain the EPSG scripts. If omitted, the scripts embedded in
     * the {@code geotk-epsg.jar} file will be used. If this JAR file is not reacheable
     * on the classpath, then the command fails.
     */
    @Option
    private File scripts;

    /**
     * The schema where to put the tables, or {@code null} for the default value. The
     * default value is {@code "epsg"} if no scripts are explicitly provided, or no
     * schema if the {@code --scripts} argument has been provided.
     */
    @Option
    private String schema;

    /**
     * Creates a new instance of {@code EpsgCreatorCommands}.
     *
     * @param arguments The command-line arguments.
     */
    protected EpsgCreatorCommands(final String[] arguments) {
        super("java -jar geotk-epsg.jar", arguments);
    }

    /**
     * Creates a new instance of {@code EpsgCreatorCommands} with the given arguments
     * and {@linkplain #run() run} it.
     *
     * @param arguments Command line arguments.
     */
    public static void main(final String[] arguments) {
        final EpsgCreatorCommands console = new EpsgCreatorCommands(arguments);
        console.run();
    }

    /**
     * Runs the EPSG scripts from the command lines. This action expects up to three arguments:
     * <p>
     * <ul>
     *   <li>The JDBC URL to the database. If omitted, a default URL to a JavaDB database
     *       will be used. This default URL will point toward the Geotk configuration
     *       directory, which is platform-dependent ({@code ".geotoolkit"} on Linux).</li>
     *   <li>The user for the database connection (optional).</li>
     *   <li>The password for the database connection. Ignored if no user has been specified.</li>
     * </ul>
     */
    @Action(minimalArgumentCount=0, maximalArgumentCount=3)
    @SuppressWarnings("fallthrough")
    public void create() {
        String database=null, user=null, password=null;
        switch (arguments.length) {
            default: // Should not happen actually...
            case 3:  password = arguments[2]; // fallthrough
            case 2:  user     = arguments[1]; // fallthrough
            case 1:  database = arguments[0]; // fallthrough
            case 0:  break;
        }
        final EpsgInstaller installer = new EpsgInstaller();
        installer.setDatabase(database, user, password);
        installer.setScriptDirectory(scripts);
        if (schema != null || scripts != null) {
            installer.setSchema(schema);
        }
        final EpsgInstaller.Result result;
        try {
            result = installer.call();
        } catch (FactoryException exception) {
            printException(exception);
            final Throwable cause = exception.getCause();
            exit((cause instanceof SQLException) ? SQL_EXCEPTION_EXIT_CODE :
                 (cause instanceof IOException)  ?  IO_EXCEPTION_EXIT_CODE :
                 INTERNAL_ERROR_EXIT_CODE);
            return;
        }
        out.println(result);
    }
}
