/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.image.io.metadata;

import java.util.List;
import java.util.Arrays;

import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.metadata.content.ImageDescription;
import org.opengis.metadata.content.ImagingCondition;

import org.geotoolkit.test.Depend;

import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests {@link SpatialMetadata}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 3.06
 */
@Depend(MetadataProxyTest.class)
public final class SpatialMetadataTest {
    /**
     * Tests the {@link SpatialMetadata#getInstanceForType} method for an {@link ImageDescription}
     * metadata.
     */
    @Test
    public void testImageDescription() {
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        /*
         * Write a few data using the accessor.
         */
        MetadataAccessor accessor = new MetadataAccessor(metadata, null, "ImageDescription", null);
        accessor.setAttribute("imagingCondition", "cloud");
        accessor.setAttribute("cloudCoverPercentage", 20.0);
        /*
         * Read the ImageDescription metadata.
         */
        final ImageDescription description = metadata.getInstanceForType(ImageDescription.class);
        assertEquals(ImagingCondition.CLOUD, description.getImagingCondition());
        assertEquals(Double.valueOf(20), description.getCloudCoverPercentage());
        assertNull(description.getCompressionGenerationQuantity());
        assertSame("The metadata should be cached", description, metadata.getInstanceForType(ImageDescription.class));
    }

    /**
     * Tests the {@link SpatialMetadata#getInstanceForType} method for an {@link RectifiedGrid}
     * metadata.
     */
    @Test
    public void testRectifiedGrid() {
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        /*
         * Write a few data using the accessor.
         */
        final int[] limitsLow  = new int[] {4, 10, 16};
        final int[] limitsHigh = new int[] {6,  2,  8};
        final double[] vector0 = new double[] {2, 5, 8};
        final double[] vector1 = new double[] {3, 1, 4};
        MetadataAccessor accessor = new MetadataAccessor(metadata, null, "RectifiedGridDomain/Limits", null);
        accessor.setAttribute("low",  limitsLow);
        accessor.setAttribute("high", limitsHigh);
        accessor = new MetadataAccessor(metadata, null, "RectifiedGridDomain/OffsetVectors", "OffsetVector");
        accessor.selectChild(accessor.appendChild()); accessor.setAttribute("values", vector0);
        accessor.selectChild(accessor.appendChild()); accessor.setAttribute("values", vector1);
        /*
         * Read the RectifiedGrid metadata.
         */
        final RectifiedGrid grid = metadata.getInstanceForType(RectifiedGrid.class);
        final GridEnvelope ge = grid.getExtent();
        assertArrayEquals(limitsLow,  ge.getLow ().getCoordinateValues());
        assertArrayEquals(limitsHigh, ge.getHigh().getCoordinateValues());
        final List<double[]> vectors = grid.getOffsetVectors();
        assertEquals(2, vectors.size());
        assertTrue(Arrays.equals(vector0, vectors.get(0)));
        assertTrue(Arrays.equals(vector1, vectors.get(1)));
    }
}
