/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.referencing.operation.provider;

import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests the {@link MapProjectionParameters} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class MapProjectionParametersTest {
    /**
     * Tests the earth radius dynamic parameter.
     */
    @Test
    public void testEarthRadius() {
        final ParameterValueGroup parameters = LambertConformal2SP.PARAMETERS.createValue();
        parameters.parameter("semi_major").setValue(6378137.000); // WGS84
        parameters.parameter("semi_minor").setValue(6356752.314);
        assertEquals(6371007, parameters.parameter("earth_radius").doubleValue(), 0.5); // Authalic radius.
        assertEquals(6378137, parameters.parameter("semi_major")  .doubleValue(), 0.5);
        assertEquals(6356752, parameters.parameter("semi_minor")  .doubleValue(), 0.5);

        parameters.parameter("earth_radius").setValue(6371000);
        assertEquals(6371000, parameters.parameter("earth_radius").doubleValue(), 0.0);
        assertEquals(6371000, parameters.parameter("semi_major")  .doubleValue(), 0.0);
        assertEquals(6371000, parameters.parameter("semi_minor")  .doubleValue(), 0.0);
    }

    /**
     * Tests the inverse flattening dynamic parameter.
     */
    @Test
    public void testInverseFlattening() {
        final ParameterValueGroup parameters = LambertConformal2SP.PARAMETERS.createValue();
        parameters.parameter("semi_major").setValue(6378206.4); // Clarke 1866
        parameters.parameter("semi_minor").setValue(6356583.8);
        assertEquals(294.97870, parameters.parameter("inverse_flattening").doubleValue(), 0.00001);
        assertEquals(6378206.4, parameters.parameter("semi_major")        .doubleValue(), 0.5);
        assertEquals(6356583.8, parameters.parameter("semi_minor")        .doubleValue(), 0.5);

        parameters.parameter("semi_major").setValue(6378137.000); // WGS84
        parameters.parameter("inverse_flattening").setValue(298.257223563);
        assertEquals(298.257, parameters.parameter("inverse_flattening").doubleValue(), 0.001);
        assertEquals(6378137, parameters.parameter("semi_major")        .doubleValue(), 0.5);
        assertEquals(6356752, parameters.parameter("semi_minor")        .doubleValue(), 0.5);
    }

    /**
     * Tests the inverse flattening dynamic parameter set after the semi-major axis length.
     * This method tests actually the semi-major parameter capability to compute the inverse
     * flattening.
     */
    @Test
    public void testSemiMajor() {
        final ParameterValueGroup parameters = LambertConformal2SP.PARAMETERS.createValue();
        parameters.parameter("inverse_flattening").setValue(298.257223563);
        assertNull(parameters.parameter("semi_major").getValue());
        assertNull(parameters.parameter("semi_minor").getValue());
        parameters.parameter("semi_major").setValue(6378137.000); // WGS84
        assertEquals(298.257, parameters.parameter("inverse_flattening").doubleValue(), 0.001);
        assertEquals(6378137, parameters.parameter("semi_major")        .doubleValue(), 0.5);
        assertEquals(6356752, parameters.parameter("semi_minor")        .doubleValue(), 0.5);
    }

    /**
     * Tests the standard parallel dynamic parameter.
     */
    @Test
    public void testStandardParallel() {
        final ParameterValueGroup parameters = LambertConformal2SP.PARAMETERS.createValue();
        final ParameterValue<?> p  = parameters.parameter("standard_parallel"  );
        final ParameterValue<?> p1 = parameters.parameter("standard_parallel_1");
        final ParameterValue<?> p2 = parameters.parameter("standard_parallel_2");
        assertSame(p,  parameters.parameter("standard_parallel"  ));
        assertSame(p1, parameters.parameter("standard_parallel_1"));
        assertSame(p2, parameters.parameter("standard_parallel_2"));

        /* Empty */      assertArrayEquals(new double[] {     }, p.doubleValueList(), 0.0);
        p1.setValue(40); assertArrayEquals(new double[] {40   }, p.doubleValueList(), 0.0);
        p2.setValue(60); assertArrayEquals(new double[] {40,60}, p.doubleValueList(), 0.0);

        p.setValue(new double[] {30,40});
        assertEquals(30, p1.doubleValue(), 0.0);
        assertEquals(40, p2.doubleValue(), 0.0);

        p.setValue(new double[] {45});
        assertEquals(45,         p1.doubleValue(), 0.0);
        assertEquals(Double.NaN, p2.doubleValue(), 0.0);
    }
}
