/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.epsg;

import java.io.File;
import java.io.IOException;
import javax.sql.DataSource;
import org.geotoolkit.internal.sql.Dialect;
import org.geotoolkit.internal.io.Installation;

import static org.geotoolkit.internal.referencing.CRSUtilities.EPSG_VERSION;


/**
 * @deprecated Moved to {@link org.apache.sis.referencing.factory.sql.EPSGFactory} in Apache SIS.
 */
@Deprecated
public class ThreadedEpsgFactory {
    private ThreadedEpsgFactory() {
    }

    /**
     * The user configuration file, which is {@value}. This file is used if no {@link DataSource}
     * object were specified explicitly to the constructor, either directly or as a hint. In such
     * case, {@code ThreadedEpsgFactory} will look for the first of the following files:
     * <p>
     * <ul>
     *   <li><code>{@value}</code> in the current directory</li>
     *   <li><code>{@value}</code> in the user's home directory</li>
     *   <li>{@code "EPSG/DataSource.properties"} in the Geotk application data directory</li>
     * </ul>
     * <p>
     * This file should contain the properties listed below.
     * <P>
     * <TABLE BORDER="1">
     * <TR BGCOLOR="#EEEEFF">
     *   <TH>Property</TH>
     *   <TH>Type</TH>
     *   <TH>Description</TH>
     * </TR>
     * <TR>
     *   <TD>&nbsp;{@code URL}&nbsp;</TD>
     *   <TD>&nbsp;{@code String}&nbsp;</TD>
     *   <TD>&nbsp;URL to the database.&nbsp;</TD>
     * </TR>
     * <TR>
     *   <TD>&nbsp;{@code schema}&nbsp;</TD>
     *   <TD>&nbsp;{@code String}&nbsp;</TD>
     *   <TD>&nbsp;The schema for the EPSG tables.&nbsp;</TD>
     * </TR>
     * <TR>
     *   <TD>&nbsp;{@code user}&nbsp;</TD>
     *   <TD>&nbsp;{@code String}&nbsp;</TD>
     *   <TD>&nbsp;User used to make database connections.&nbsp;</TD>
     * </TR>
     * <TR>
     *   <TD>&nbsp;{@code password}&nbsp;</TD>
     *   <TD>&nbsp;{@code String}&nbsp;</TD>
     *   <TD>&nbsp;Password used to make database connections.&nbsp;</TD>
     * </TR>
     * </TABLE>
     *
     * @since 3.00
     */
    public static final String CONFIGURATION_FILE = "EPSG-DataSource.properties";

    /**
     * Returns the default JDBC URL to use for connection to the EPSG embedded database.
     * This method returns a URL using the JavaDB driver, connecting to the database in the
     * installation directory specified by the setup program in the
     * <a href="http://www.geotoolkit.org/modules/utility/geotk-setup">geotk-setup</a> module.
     * If this setup program has not been run, then a platform-dependent location relative
     * to the user home directory is returned.
     * <p>
     * If no database exists in the above-cited directory, then a new EPSG database will be
     * created by {@code ThreadedEpsgFactory} when first needed provided that the
     * <a href="http://www.geotoolkit.org/modules/referencing/geotk-epsg">geotk-epsg</a>
     * module is reachable on the classpath.
     * <p>
     * Note that the directory may change in any Geotk version. More specifically, every
     * upgrade of the embedded EPSG database may cause a change of the default directory.
     *
     * @return The default JDBC URL to use for the connection to the EPSG database.
     *
     * @since 3.00
     */
    public static String getDefaultURL() {
        try {
            return getDefaultURL(false);
        } catch (IOException e) {
            // Should never happen when the 'create' argument is 'false'.
            throw new AssertionError(e);
        }
    }

    /**
     * Returns the default JDBC URL to use for connection to the EPSG embedded database.
     * The returned URL expects an existing database, unless the {@code create} parameter
     * is {@code true} in which case the URL allows database creation.
     *
     * @param  create {@code true} if this method should create the database directory if
     *         it does not already exist, or {@code false} otherwise.
     * @return The default JDBC URL to use for the connection to the EPSG database.
     * @throws IOException If the database directory can not be created.
     */
    static String getDefaultURL(boolean create) throws IOException {
        File directory;
        if (create) {
            directory = Installation.EPSG.validDirectory(true);
        } else {
            directory = Installation.EPSG.directory(true);
        }
        String driver  = "derby";
        if (!Dialect.DERBY.isDriverRegistered()) {
            /*
             * If the Dervy driver is not found, looks for the HSQL driver.
             * If it is not found neither, we will keep the Derby driver as
             * the default one.
             */
            try {
                Class.forName(Dialect.HSQL.driverClass);
                directory = new File(directory, "HSQL");
                driver = "hsqldb";
                create = false;
            } catch (ClassNotFoundException e) {
                // Ignore - we will stay with the Derby driver.
            }
        }
        final StringBuilder buffer = new StringBuilder("jdbc:").append(driver).append(':')
                .append(directory.getPath().replace(File.separatorChar, '/'))
                .append('/').append(EPSG_VERSION);
        if (create) {
            // Allow the creation of the database only if the needed scripts are available.
            buffer.append(";create=true");
        }
        return buffer.toString();
    }
}
