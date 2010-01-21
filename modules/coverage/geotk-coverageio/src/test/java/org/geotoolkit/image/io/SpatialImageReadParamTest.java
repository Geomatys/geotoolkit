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

import java.awt.Point;
import java.util.List;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;
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
     * Tests the API enumeration.
     */
    @Test
    public void testAPI() {
        int index = 0;
        for (final DimensionSlice.API api : DimensionSlice.API.VALIDS) {
            assertEquals("Array of valid enum is not consistent with ordinal values.", index++, api.ordinal());
        }
        assertEquals("The first non-reserved API should be bands.", RESERVED, BANDS.ordinal());
    }

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
        /*
         * Simulates NetCDF file having longitude, latitude, depth and time dimensions.
         * The longitude and latitude are unknown for now, but the depth and time should
         * be recognized. Note: in this test, we intentionally swp (x,y) axis order.
         */
        final List<String> AXES = Arrays.asList("latitude", "longitude", "depth", "time");
        assertEquals( 0, param.findDimensionForAPI(COLUMNS, AXES));
        assertEquals( 1, param.findDimensionForAPI(ROWS,    AXES));
        assertEquals( 2, param.findDimensionForAPI(BANDS,   AXES));
        assertEquals(-1, param.findDimensionForAPI(IMAGES,  AXES));
        assertEquals(-1, param.findDimensionForAPI(NONE,    AXES));
        assertEquals(NONE, timeSlice .getAPI());
        assertEquals(NONE, depthSlice.getAPI());
        assertNull(param.getSourceBands());

        timeSlice.setAPI(BANDS);
        assertEquals( 0, param.findDimensionForAPI(COLUMNS, AXES));
        assertEquals( 1, param.findDimensionForAPI(ROWS,    AXES));
        assertEquals( 3, param.findDimensionForAPI(BANDS,   AXES));
        assertEquals(-1, param.findDimensionForAPI(IMAGES,  AXES));
        assertEquals(-1, param.findDimensionForAPI(NONE,    AXES));
        assertEquals(BANDS, timeSlice .getAPI());
        assertEquals(NONE,  depthSlice.getAPI());
        assertArrayEquals(new int[] {20}, param.getSourceBands());

        depthSlice.setAPI(BANDS);
        assertEquals( 0, param.findDimensionForAPI(COLUMNS, AXES));
        assertEquals( 1, param.findDimensionForAPI(ROWS,    AXES));
        assertEquals( 2, param.findDimensionForAPI(BANDS,   AXES));
        assertEquals(-1, param.findDimensionForAPI(IMAGES,  AXES));
        assertEquals(-1, param.findDimensionForAPI(NONE,    AXES));
        assertEquals(NONE,  timeSlice .getAPI());
        assertEquals(BANDS, depthSlice.getAPI());
        assertArrayEquals(new int[] {25}, param.getSourceBands());

        try {
            depthSlice.setAPI(ROWS);
            fail("Changing the API to ROWS should not be allowed.");
        } catch (IllegalArgumentException e) {
            // This is the expected exception.
        }
        assertEquals(BANDS, depthSlice.getAPI());
        assertNull("Should not have been set by any of the above.", param.getSourceRegion());
        assertEquals("Should not have been set by any of the above.", new Point(), param.getDestinationOffset());

        assertMultilinesEquals(decodeQuotes(
            "SpatialImageReadParam[bands={25}]\n" +
            "  ├─ DimensionSlice[id={“time”}, indice=20]\n" +
            "  └─ DimensionSlice[id={“depth”}, indice=25, API=BANDS]"), param.toString());

        depthSlice.removeDimensionId("depth");
        assertMultilinesEquals(decodeQuotes(
            "SpatialImageReadParam[bands={25}]\n" +
            "  └─ DimensionSlice[id={“time”}, indice=20]"), param.toString());
        /*
         * Now adds name to the (latitude, longitude) dimensions.
         */
        param.getDimensionSlice(0).addDimensionId("longitude");
        param.getDimensionSlice(1).addDimensionId("latitude");
        assertMultilinesEquals(decodeQuotes(
            "SpatialImageReadParam[bands={25}]\n" +
            "  ├─ DimensionSlice[id={“time”}, indice=20]\n" +
            "  ├─ DimensionSlice[id={0, “longitude”}, indice=0, API=COLUMNS]\n" +
            "  └─ DimensionSlice[id={1, “latitude”}, indice=0, API=ROWS]"), param.toString());
    }
}
