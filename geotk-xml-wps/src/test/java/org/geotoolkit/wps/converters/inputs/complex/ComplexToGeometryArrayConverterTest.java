/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.inputs.complex;

import org.locationtech.jts.geom.Geometry;
import java.io.IOException;
import org.geotoolkit.wps.converters.ConvertersTestUtils;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Data;
import org.junit.Test;

/**
 *
 * @author Theo Zozime
 */
public class ComplexToGeometryArrayConverterTest extends org.geotoolkit.test.TestBase {

    @Test
    public void testJSONConversion() throws IOException {
        Geometry[] geometryArray = ConvertersTestUtils.initAndRunInputConversion(Data.class,
                Geometry[].class,
                "/inputs/geometrycollection.json",
                WPSMimeType.APP_GEOJSON.val(),
                WPSEncoding.UTF8.getValue(),
                null);

        // Test geometry validity
        ConvertersTestUtils.assertGeometryArrayIsValid(geometryArray);
    }
}
