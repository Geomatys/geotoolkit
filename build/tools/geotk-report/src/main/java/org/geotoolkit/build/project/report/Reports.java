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
package org.geotoolkit.build.project.report;

import java.util.Properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.sis.internal.storage.IOUtilities;
import org.geotoolkit.util.Utilities;


/**
 * Miscellaneous helper tools for report implementations in this package.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.16
 */
final class Reports {
    /**
     * Do not allow instantiation of this class.
     */
    private Reports() {
    }

    /**
     * Sets the Geotk project information into the given properties map. For a list of legal
     * property keys, see {@link ParameterNamesReport#ParameterNamesReport(Properties)} and
     * {@link org.opengis.test.report.Report} javadoc.
     */
    static void initialize(final Properties p) {
        p.setProperty("PRODUCT.NAME",    "Geotoolkit.org");
        p.setProperty("PRODUCT.VERSION", getGeotkVersion());
        p.setProperty("PRODUCT.URL",     "http://www.geotoolkit.org");
        p.setProperty("JAVADOC.GEOAPI",  "http://www.geoapi.org/snapshot/javadoc");
    }

    /**
     * Returns the current Geotoolkit.org version, with the {@code -SNAPSHOT} trailing
     * part omitted.
     *
     * @return The current Geotk version.
     */
    private static String getGeotkVersion() {
        String version = Utilities.VERSION.toString();
        final int snapshot = version.lastIndexOf('-');
        if (snapshot >= 2) {
            version = version.substring(0, snapshot);
        }
        return version;
    }

    /**
     * Returns the root directory of the Geotk project.
     *
     * @return The project root directory.
     * @throws IOException If the root directory can not be found.
     */
    static File getProjectRootDirectory() throws IOException {
        File file = IOUtilities.toFile(Reports.class.getResource("Reports.class"), null);
        while (file != null) {
            if (new File(file, "pom.xml").isFile() &&
                new File(file, "modules").isDirectory() &&
                new File(file, "demos")  .isDirectory() &&
                new File(file, "build")  .isDirectory())
            {
                return file;
            }
            file = file.getParentFile();
        }
        throw new FileNotFoundException("Project root not found.");
    }
}
