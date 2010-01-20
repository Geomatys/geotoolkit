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
package org.geotoolkit.image.io;

import java.util.List;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.image.io.DimensionSlice.API.*;


/**
 * Tests {@link SpatialImageReadParam}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 */
public final class SpatialImageReadParamTest {
    /**
     * Tests setting indices and API in dimension slices.
     */
    @Test
    public void testDimensionSlice() {
        final SpatialImageReadParam param = new SpatialImageReadParam(null);
        final DimensionSlice timeSlice = param.newDimensionSlice();
        timeSlice.addDimensionId("time");
        timeSlice.setIndice(20);

        final DimensionSlice depthSlice = param.newDimensionSlice();
        depthSlice.addDimensionId("depth");
        depthSlice.setIndice(25);

        assertSame(timeSlice,  param.getDimensionSlice("time"));
        assertSame(depthSlice, param.getDimensionSlice("depth"));
        assertNull(            param.getDimensionSlice("dummy"));

        assertEquals(20, param.getSourceIndiceForDimension("time"));
        assertEquals(25, param.getSourceIndiceForDimension("depth"));
        assertEquals( 0, param.getSourceIndiceForDimension("dummy"));

        assertEquals(NONE, timeSlice .getAPI());
        assertEquals(NONE, depthSlice.getAPI());

        final List<String> AXES = Arrays.asList("longitude", "latitude", "depth", "time");
        assertEquals(2, param.getDimensionForAPI(BANDS, AXES));

        timeSlice.setAPI(BANDS);
        assertEquals(3, param.getDimensionForAPI(BANDS, AXES));
    }
}
