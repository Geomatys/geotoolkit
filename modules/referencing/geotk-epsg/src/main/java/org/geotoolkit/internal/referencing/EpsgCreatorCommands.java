/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
     * The directory which contain the EPSG scripts. If omitted, the scripts embedded
     * in the "geotk-epsg" JAR file will be used. If this JAR file is not reacheable
     * on the classpath, then the command fails.
     */
    @Option
    private File scripts;

    /**
     * The JDBC URL to the database. If omitted, a default URL to a JavaDB database
     * will be used. This default URL will point toward the Geotoolkit configuration
     * directory, which is platform-dependent (".geotoolkit" on Linux).
     */
    @Option
    private String database;

    /**
     * The user for the database connection (optional).
     */
    @Option
    private String user;

    /**
     * The password for the database connection. Ignored if no user has been specified.
     */
    @Option
    private String password;

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
     * Runs the EPSG scripts from the command lines.
     */
    @Action(minimalArgumentCount=0, maximalArgumentCount=0)
    public void create() {
        final EpsgInstaller installer = new EpsgInstaller();
        installer.setDatabase(database, user, password);
        installer.setScriptDirectory(scripts);
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
        out.print(result.numRows);
        out.println(" rows inserted.");
    }
}
