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
package org.geotoolkit.image.io;

import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageReadParam;

import org.junit.*;

import org.geotoolkit.image.io.DimensionSlice.API;

import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.test.Commons.*;
import static org.geotoolkit.image.io.DimensionSlice.API.*;


/**
 * Tests {@link SpatialImageReadParam}. This class tests also (indirectly) {@link DimensionSet}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.08
 */
public final strictfp class SpatialImageReadParamTest {
    /**
     * Tests the API enumeration.
     */
    @Test
    public void testAPI() {
        int index = 0;
        for (final DimensionSlice.API api : DimensionSlice.API.VALIDS) {
            assertEquals("Array of valid enum is not consistent with ordinal values.", index++, api.ordinal());
        }
    }

    /**
     * Ensures that setting an API to BANDS has the side-effect of setting
     * the source bands to {0}.
     */
    @Test
    public void testAssignBands() {
        final Reader reader = new Reader();
        assertTrue (reader.getAPIForDimensions().isEmpty());
        assertFalse(reader.getAPIForDimensions().contains(BANDS));
        SpatialImageReadParam param = reader.getDefaultReadParam();
        assertNull(param.getSourceBands());
        /*
         * Assign the API to bands, and check again newly-created parameters.
         */
        reader.getDimensionForAPI(BANDS).addDimensionId("depth");
        assertTrue(reader.getAPIForDimensions().contains(BANDS));
        param = reader.getDefaultReadParam();
        assertNull(param.getSourceBands());
        /*
         * When a dimension is assigned to bands, the slice index
         * shall be the first band.
         */
        param.setSourceBands(new int[] {8, 4, 2});
        final DimensionSlice bandsSlice = param.newDimensionSlice();
        assertEquals(0, bandsSlice.getSliceIndex());
        bandsSlice.addDimensionId("depth");
        assertEquals(8, bandsSlice.getSliceIndex());
    }

    /**
     * Tests setting indices and API in dimension slices.
     */
    @Test
    public void testDimensionSlice() {
        final SpatialImageReadParam param = new SpatialImageReadParam(null);
        final Set<DimensionSlice> slices = param.getDimensionSlices();
        assertTrue(slices.isEmpty());

        final DimensionSlice timeSlice = param.newDimensionSlice();
        timeSlice.addDimensionId("time");
        timeSlice.setSliceIndex(20);
        assertFalse(slices.isEmpty());
        assertEquals(1, slices.size());

        final DimensionSlice depthSlice = param.newDimensionSlice();
        depthSlice.addDimensionId("depth");
        depthSlice.setSliceIndex(25);
        assertEquals(2, slices.size());
        assertArrayEquals(new DimensionSlice[] {timeSlice, depthSlice}, slices.toArray());

        assertSame(timeSlice,  param.getDimensionSlice("time"));
        assertSame(depthSlice, param.getDimensionSlice("depth"));
        assertNull(            param.getDimensionSlice("dummy"));

        assertEquals(20, param.getSliceIndex("time"));
        assertEquals(25, param.getSliceIndex("depth"));
        assertEquals( 0, param.getSliceIndex("dummy"));
        /*
         * Simulates NetCDF file having longitude, latitude, depth and time dimensions.
         * The longitude and latitude are unknown for now, but the depth and time should
         * be recognized.
         */
        final List<String> AXES = Arrays.asList("longitude", "latitude", "depth", "time");
        assertNull(param.getSourceBands());
        assertNull(param.getSourceRegion());
        assertEquals(new Point(), param.getDestinationOffset());

        param.setSourceBands(new int[] {25});
        assertMultilinesEquals(decodeQuotes(
            "SpatialImageReadParam[sourceBands={25}]\n" +
            "  ├─ DimensionSlice[id={“time”}, sliceIndex=20]\n" +
            "  └─ DimensionSlice[id={“depth”}, sliceIndex=25]"), param.toString());

        depthSlice.removeDimensionId("depth");
        assertMultilinesEquals(decodeQuotes(
            "SpatialImageReadParam[sourceBands={25}]\n" +
            "  └─ DimensionSlice[id={“time”}, sliceIndex=20]"), param.toString());
        /*
         * Now adds name to the (latitude, longitude) dimensions.
         */
        param.newDimensionSlice().addDimensionId("longitude");
        param.newDimensionSlice().addDimensionId("latitude");
        assertMultilinesEquals(decodeQuotes(
            "SpatialImageReadParam[sourceBands={25}]\n" +
            "  ├─ DimensionSlice[id={“time”}, sliceIndex=20]\n" +
            "  ├─ DimensionSlice[id={“longitude”}, sliceIndex=0]\n" +
            "  └─ DimensionSlice[id={“latitude”}, sliceIndex=0]"), param.toString());

        param.getDimensionSlice("longitude").setSliceIndex(100);
        param.getDimensionSlice("latitude" ).setSliceIndex(200);
        param.setDestinationOffset(new Point(2,3));
        assertMultilinesEquals(decodeQuotes(
            "SpatialImageReadParam[sourceBands={25}, destinationOffset=(2,3)]\n" +
            "  ├─ DimensionSlice[id={“time”}, sliceIndex=20]\n" +
            "  ├─ DimensionSlice[id={“longitude”}, sliceIndex=100]\n" +
            "  └─ DimensionSlice[id={“latitude”}, sliceIndex=200]"), param.toString());
    }

    /**
     * A dummy implementation of {@link SpatialImageReader} which implements the
     * {@link MultidimensionalImageStore} interface.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.15
     *
     * @since 3.15
     */
    private static final strictfp class Reader extends SpatialImageReader implements MultidimensionalImageStore {
        private final DimensionSet dimensions;

        public Reader() {
            super(null);
            dimensions = new DimensionSet(this);
        }

        @Override public DimensionIdentification getDimensionForAPI(API api) {
            return dimensions.getOrCreate(api);
        }

        @Override public API getAPIForDimension(Object... identifiers) {
            return dimensions.getAPI(identifiers);
        }

        @Override public Set<API> getAPIForDimensions() {
            return dimensions.getAPIs();
        }

        @Override public int getWidth(int imageIndex) {
            throw new UnsupportedOperationException();
        }

        @Override public int getHeight(int imageIndex) {
            throw new UnsupportedOperationException();
        }

        @Override public BufferedImage read(int imageIndex, ImageReadParam param) {
            throw new UnsupportedOperationException();
        }
    }
}
