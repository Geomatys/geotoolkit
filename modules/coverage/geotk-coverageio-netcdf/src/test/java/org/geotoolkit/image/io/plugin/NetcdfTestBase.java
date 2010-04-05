/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.image.io.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.geotoolkit.test.TestData;
import org.geotoolkit.internal.io.Installation;

import static org.junit.Assert.*;
import static org.junit.Assume.*;


/**
 * Base class (when possible) for tests requirying a NetCDF file.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 */
public abstract class NetcdfTestBase {
    /**
     * Default constructor for subclasses.
     */
    protected NetcdfTestBase() {
    }

    /**
     * Returns the test file, which is optional. Current implementation reuse the configuration
     * properties file which was actually created for the {@code geotk-coverage-sql} module.
     *
     * @return The test file (never null).
     */
    public static File getTestFile() {
        File file = new File(Installation.TESTS.directory(true), "coverage-sql.properties");
        assumeTrue(file.isFile()); // All tests will be skipped if the above resources is not found.
        final Properties properties;
        try {
            properties = TestData.readProperties(file);
        } catch (IOException e) {
            throw new AssertionError(e); // Will cause a JUnit test failure.
        }
        final String directory = properties.getProperty("rootDirectory");
        assertNotNull("Missing \"rootDirectory\" property.", directory);
        file = new File(directory, "World/Coriolis/OA_RTQCGL01_20070606_FLD_TEMP.nc");
        assertTrue("OA_RTQCGL01_20070606_FLD_TEMP.nc file not found.", file.isFile());
        return file;
    }
}
