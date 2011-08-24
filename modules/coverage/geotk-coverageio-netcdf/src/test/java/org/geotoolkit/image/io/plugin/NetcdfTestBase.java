/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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

import javax.measure.unit.Unit;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.NonSI.DAY;
import static javax.measure.unit.NonSI.DEGREE_ANGLE;

import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;

import org.geotoolkit.test.TestBase;
import org.geotoolkit.test.TestData;
import org.geotoolkit.internal.io.Installation;

import static org.junit.Assert.*;
import static org.junit.Assume.*;


/**
 * Base class (when possible) for tests requerying a NetCDF file.
 * Those tests require large test files. For more information, see:
 * <p>
 * <a href="http://hg.geotoolkit.org/geotoolkit/raw-file/tip/modules/coverage/geotk-coverage-sql/src/test/resources/Tests/README.html">About large test files</a>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.10
 */
public strictfp class NetcdfTestBase extends TestBase {
    /**
     * The file to be used for the tests.
     */
    private static final String FILENAME = "OA_RTQCGL01_20070606_FLD_TEMP.nc";

    /**
     * The name of variables in the {@value #FILENAME} file.
     */
    protected static final String[] VARIABLE_NAMES = new String[] {"temperature", "pct_variance"};

    /**
     * The size of the grid in each dimension.
     */
    protected static final int[] GRID_SIZE = new int[] {720, 499, 59, 1};

    /**
     * Name of axes in the {@value #FILENAME} file.
     */
    private static final String[] AXIS_NAMES = new String[] {"longitude", "latitude", "depth", "time"},
                        PROJECTED_AXIS_NAMES = new String[] {"Easting",   "Northing", "depth", "time"};

    /**
     * Abbreviations of axes in the {@value #FILENAME} file.
     */
    private static final String[] AXIS_ABBREVIATIONS = new String[] {"λ", "φ", "d", "t"},
                        PROJECTED_AXIS_ABBREVIATIONS = new String[] {"E", "N", "d", "t"};

    /**
     * Directions of axes in the {@value #FILENAME} file.
     */
    private static final AxisDirection[] AXIS_DIRECTIONS = new AxisDirection[] {
        AxisDirection.EAST, AxisDirection.NORTH, AxisDirection.DOWN, AxisDirection.FUTURE
    };

    /**
     * Units of axes in the {@value #FILENAME} file.
     */
    private static final Unit<?>[] AXIS_UNITS = new Unit<?>[] {DEGREE_ANGLE, DEGREE_ANGLE,  METRE, DAY},
                         PROJECTED_AXIS_UNITS = new Unit<?>[] {METRE,        METRE,         METRE, DAY};

    /**
     * Default constructor for subclasses.
     */
    protected NetcdfTestBase() {
    }

    /**
     * Returns the test file, which is optional.
     * <p>
     * A convenient way to define the {@code coverage-netcdf.properties} file is to
     * create a symbolic link to {@code coverage-sql.properties}, in order to leverage
     * the test file created for the {@code geotk-coverage-sql} module.
     *
     * @return The test file (never null).
     */
    public static File getTestFile() {
        File file = new File(Installation.TESTS.directory(true), "coverage-netcdf.properties");
        assumeTrue(file.isFile()); // All tests will be skipped if the above resources is not found.
        final Properties properties;
        try {
            properties = TestData.readProperties(file);
        } catch (IOException e) {
            throw new AssertionError(e); // Will cause a JUnit test failure.
        }
        final String directory = properties.getProperty("rootDirectory");
        assertNotNull("Missing \"rootDirectory\" property.", directory);
        file = new File(directory, "World/Coriolis/" + FILENAME);
        assertTrue(FILENAME + " file not found.", file.isFile());
        return file;
    }

    /**
     * Ensures that axes in the given coordinate system have the expected name, abbreviation,
     * direction and unit for a geographic or projected CRS.
     *
     * @param cs The coordinate system to test.
     * @param isProjected {@code true} if the CRS is expected to be projected,
     *                     or {@code false} if it is expected to be geographic.
     */
    protected static void assertExpectedAxes(final CoordinateSystem cs, final boolean isProjected) {
        assertNotNull("The coordinate system can't be null.", cs);
        final int dimension = cs.getDimension();
        final String [] axisNames;
        final String [] axisAbbreviations;
        final Unit<?>[] axisUnits;
        if (isProjected) {
            axisNames         = PROJECTED_AXIS_NAMES;
            axisAbbreviations = PROJECTED_AXIS_ABBREVIATIONS;
            axisUnits         = PROJECTED_AXIS_UNITS;
        } else {
            axisNames         = AXIS_NAMES;
            axisAbbreviations = AXIS_ABBREVIATIONS;
            axisUnits         = AXIS_UNITS;
        }
        for (int i=0; i<dimension; i++) {
            final CoordinateSystemAxis axis = cs.getAxis(i);
            assertEquals("Unexpected axis name.",      axisNames        [i], axis.getName().getCode());
            assertEquals("Unexpected abbreviation.",   axisAbbreviations[i], axis.getAbbreviation());
            assertEquals("Unexpected axis direction.", AXIS_DIRECTIONS  [i], axis.getDirection());
            assertEquals("Unexpected axis unit.",      axisUnits        [i], axis.getUnit());
            if (!isProjected) {
                assertEquals("Unexpected toString().", "NetCDF:" + AXIS_NAMES[i], axis.toString());
            }
        }
    }
}
