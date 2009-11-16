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
import java.util.Collection;
import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.content.ImageDescription;
import org.opengis.metadata.content.ImagingCondition;

import org.geotoolkit.test.Depend;
import org.geotoolkit.util.NumberRange;
import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests {@link MetadataProxy}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 3.06
 */
@Depend(MetadataAccessorTest.class)
public final class MetadataProxyTest {
    /**
     * Tests the proxy with some properties defined under the {@code "ImageDescription"} node
     * without children.
     */
    @Test
    public void testImageDescription() {
        final SpatialMetadata  metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        final MetadataAccessor accessor = new MetadataAccessor(metadata, "ImageDescription", null);
        final ImageDescription proxy    = MetadataProxy.newProxyInstance(ImageDescription.class, accessor, -1);
        /*
         * Test the properties before they are defined.
         */
        assertNull(proxy.getImagingCondition());
        assertNull(proxy.getCloudCoverPercentage());
        /*
         * Now define the properties and test again.
         */
        accessor.setAttributeAsString("imagingCondition", "cloud");
        accessor.setAttributeAsDouble("cloudCoverPercentage", 20);
        assertEquals(20.0, proxy.getCloudCoverPercentage(), 0.0);
        assertEquals(ImagingCondition.CLOUD, proxy.getImagingCondition());
        /*
         * Ask for an element which was excluded from the metadata format.
         * The intend is to ensure that no exception is thrown.
         */
        assertNull(proxy.getAttributeDescription());
    }

    /**
     * Tests {@link MetadataProxyList} using the {@code "ImageDescription/Dimensions"} node.
     */
    @Test
    public void testDimensionList() {
        final SpatialMetadata   metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        final MetadataAccessor  accessor = new MetadataAccessor(metadata, "ImageDescription/Dimensions");
        final List<SampleDimension> list = MetadataProxyList.create(SampleDimension.class, accessor);
        assertEquals(0, list.size());
        /*
         * Add a few childs.
         */
        for (int i=1; i<=4; i++) {
            accessor.selectChild(accessor.appendChild());
            assertNull(accessor.getAttributeAsDouble("minValue"));
            assertNull(accessor.getAttributeAsDouble("maxValue"));
            accessor.setAttributeAsDouble("minValue", -i);
            accessor.setAttributeAsDouble("maxValue",  i);
            accessor.setAttributeAsRange("validSampleValues", NumberRange.create(-i, i));
            assertEquals(i, accessor.childCount());
        }
        /*
         * Test the child proxies.
         */
        int index = 0;
        for (final SampleDimension dim : list) {
            assertSame(dim, list.get(index++));
            assertEquals(Double.valueOf(-index), dim.getMinValue());
            assertEquals(Double.valueOf( index), dim.getMaxValue());
            assertEquals(NumberRange.create((byte) -index, (byte) index), dim.getValidSampleValues());
        }
    }

    /**
     * Tests the proxy with the children defined under the {@code "ImageDescription/Dimensions"}
     * node. This is also an indirect test of {@link MetadataProxyList}, except that this time
     * the list is created implicitly.
     */
    @Test
    public void testDimensions() {
        final SpatialMetadata  metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        final MetadataAccessor accessor = new MetadataAccessor(metadata, "ImageDescription/Dimensions", "Dimension");
        final ImageDescription proxy    = MetadataProxy.newProxyInstance(ImageDescription.class, accessor, -1);
        final Collection<? extends RangeDimension> dimensions = proxy.getDimensions();
        assertTrue(dimensions.isEmpty());
        for (int i=1; i<=4; i++) {
            accessor.selectChild(accessor.appendChild());
            accessor.setAttributeAsDouble("minValue", -i);
            accessor.setAttributeAsDouble("maxValue",  i);
            assertEquals(i, dimensions.size());
        }
        int index = 0;
        for (final RangeDimension dim : dimensions) {
            index++;
            assertTrue(dim instanceof SampleDimension);
            final SampleDimension sd = (SampleDimension) dim;
            assertEquals(Double.valueOf(-index), sd.getMinValue());
            assertEquals(Double.valueOf( index), sd.getMaxValue());
        }
    }
}
