/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.regex.Pattern;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link EpsgScriptRunner}.
 * <p>
 * Note: There is no test of {@link EpsgInstaller} in this module.
 * The installer is tested in the {@code geotk-epsg} module.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final strictfp class EpsgScriptRunnerTest {
    /**
     * Tests the {@link EpsgScriptRunner#REPLACE_STATEMENT} pattern.
     */
    @Test
    public void testReplacePattern() {
        // Statement as in the EPSG scripts prior as of EPSG version 7.06.
        assertTrue(Pattern.matches(EpsgScriptRunner.REPLACE_STATEMENT,
                "UPDATE epsg_datum\n" +
                "SET datum_name = replace(datum_name, CHR(182), CHR(10))"));

        // Statement as in the EPSG scripts prior to EPSG version 7.06.
        assertTrue(Pattern.matches(EpsgScriptRunner.REPLACE_STATEMENT,
                "UPDATE epsg_datum\n" +
                "SET datum_name = replace(datum_name, CHAR(182), CHAR(10))"));

        // Modified statement with MS-Access table name in a schema.
        assertTrue(Pattern.matches(EpsgScriptRunner.REPLACE_STATEMENT,
                "UPDATE epsg.\"Alias\"\n" +
                "SET object_table_name = replace(object_table_name, CHAR(182), CHAR(10))"));

        // Like above, but the table name contains a space.
        assertTrue(Pattern.matches(EpsgScriptRunner.REPLACE_STATEMENT,
                "UPDATE epsg.\"Coordinate Axis\"\n" +
                "SET coord_axis_orientation = replace(coord_axis_orientation, CHAR(182), CHAR(10))"));
    }
}
