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
import java.util.logging.Level;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.content.ImageDescription;
import org.opengis.metadata.content.ImagingCondition;

import org.geotoolkit.test.Depend;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.metadata.iso.citation.Citations;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;


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
        final ImageDescription proxy    = MetadataProxy.newProxyInstance(ImageDescription.class, accessor);
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
        final Level originalLevel = accessor.setWarningsLevel(Level.OFF);
        assertNull(proxy.getAttributeDescription());
        accessor.setWarningsLevel(originalLevel);
        /*
         * Check the methods defined in java.lang.Object.
         */
        assertEquals("hashCode consistency.", proxy.hashCode(), proxy.hashCode());
        assertEquals(proxy, proxy);
        assertFalse(proxy.equals(accessor));
        assertMultilinesEquals(decodeQuotes(
            "ImageDescription[“ImageDescription”]\n" +
            "├───imagingCondition=“cloud”\n" +
            "└───cloudCoverPercentage=“20.0”\n"), proxy.toString());
    }

    /**
     * Tests the proxy with a property which is an other metadata object.
     * In this test, the child metadata is an {@link Identifier}.
     */
    @Test
    public void testImageQualityCode() {
        final SpatialMetadata  metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        final MetadataAccessor accessor = new MetadataAccessor(metadata, "ImageDescription", null);
        final MetadataAccessor qualityA = new MetadataAccessor(metadata, "ImageDescription/ImageQualityCode", null);
        qualityA.setAttributeAsString("code",      "okay");
        qualityA.setAttributeAsString("authority", "Geotoolkit.org");
        accessor.setAttributeAsDouble("cloudCoverPercentage", 20); // Test mixing attributes with elements.

        final ImageDescription proxy = MetadataProxy.newProxyInstance(ImageDescription.class, accessor);
        assertEquals("Safety check against regression.", 20.0, proxy.getCloudCoverPercentage(), 0.0);

        final Identifier identifier = proxy.getImageQualityCode();
        assertSame(Citations.GEOTOOLKIT, identifier.getAuthority());
        assertEquals("okay", identifier.getCode());

        assertSame(identifier, proxy.getImageQualityCode());
        final Identifier other = proxy.getProcessingLevelCode();
        assertNotSame(identifier, other);
        assertNull(other.getAuthority());
        assertNull(other.getCode());
    }

    /**
     * Tests the list of {@code "ImageDescription/Dimensions/Dimension"} elements.
     * This method is actually a test of the {@link MetadataProxyList} class.
     */
    @Test
    public void testDimensionList() {
        final SpatialMetadata  metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        final MetadataAccessor accessor = new MetadataAccessor(metadata, "ImageDescription/Dimensions", "Dimension");
        final List<SampleDimension> dimensions = MetadataProxyList.create(SampleDimension.class, accessor);
        for (int i=1; i<=4; i++) {
            accessor.selectChild(accessor.appendChild());
            assertNull(accessor.getAttributeAsDouble("minValue"));
            assertNull(accessor.getAttributeAsDouble("maxValue"));
            accessor.setAttributeAsDouble("minValue", -i);
            accessor.setAttributeAsDouble("maxValue",  i);
            accessor.setAttributeAsRange("validSampleValues", NumberRange.create(-i, i));
            assertEquals(i, dimensions.size());
        }
        int index = 0;
        for (final RangeDimension dim : dimensions) {
            assertSame(dim, dimensions.get(index++));
            assertTrue(dim instanceof SampleDimension);
            final SampleDimension sd = (SampleDimension) dim;
            assertEquals(Double.valueOf(-index), sd.getMinValue());
            assertEquals(Double.valueOf( index), sd.getMaxValue());
            assertEquals(NumberRange.create((byte) -index, (byte) index), sd.getValidSampleValues());
            /*
             * Check the methods defined in java.lang.Object.
             */
            assertEquals("hashCode consistency.", dim.hashCode(), dim.hashCode());
            assertEquals(dim, dim);
            assertFalse(dim.equals(accessor));
            assertTrue(dim.toString().startsWith("SampleDimension[\"Dimensions\"]"));
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
        for (int i=1; i<=4; i++) {
            accessor.selectChild(accessor.appendChild());
            accessor.setAttributeAsDouble("minValue", -i);
            accessor.setAttributeAsDouble("maxValue",  i);
        }
        /*
         * We need to create the list only after the elements have been added, because the
         * current implementation does not detect element additions when they are not performed
         * by the same accessor.
         */
        final MetadataAccessor reader = new MetadataAccessor(metadata, "ImageDescription", null);
        final ImageDescription proxy  = MetadataProxy.newProxyInstance(ImageDescription.class, reader);
        final Collection<? extends RangeDimension> dimensions = proxy.getDimensions();
        assertEquals(4, dimensions.size());
        /*
         * Check the content.
         */
        int index = 0;
        for (final RangeDimension dim : dimensions) {
            index++;
            assertTrue(dim instanceof SampleDimension);
            final SampleDimension sd = (SampleDimension) dim;
            assertEquals(Double.valueOf(-index), sd.getMinValue());
            assertEquals(Double.valueOf( index), sd.getMaxValue());
        }
        /*
         * Check the methods defined in java.lang.Object.
         */
        assertEquals("hashCode consistency.", proxy.hashCode(), proxy.hashCode());
        assertEquals(proxy, proxy);
        assertFalse(proxy.equals(accessor));
        assertTrue(proxy.toString().startsWith("ImageDescription[\"ImageDescription\"]"));
    }
}
