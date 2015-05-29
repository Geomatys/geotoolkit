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
package org.geotoolkit.image.io.metadata;

import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import java.util.logging.Level;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.content.ImageDescription;
import org.opengis.metadata.content.ImagingCondition;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.identification.Resolution;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.coverage.grid.RectifiedGrid;

import org.apache.sis.test.DependsOn;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.iso.SimpleInternationalString;

import org.geotoolkit.metadata.Citations;
import org.junit.*;
import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.test.Commons.*;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Tests {@link MetadataProxy}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.06
 */
@DependsOn(MetadataNodeAccessorTest.class)
public final strictfp class MetadataProxyTest {
    /**
     * Tests the proxy with some properties defined under the {@code "ImageDescription"} node
     * without children.
     */
    @Test
    public void testImageDescription() {
        final SpatialMetadata  metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        final MetadataNodeAccessor accessor = new MetadataNodeAccessor(metadata, null, "ImageDescription", null);
        final ImageDescription proxy    = accessor.newProxyInstance(ImageDescription.class);
        /*
         * Test the properties before they are defined.
         */
        assertNull(proxy.getImagingCondition());
        assertNull(proxy.getCloudCoverPercentage());
        /*
         * Now define the properties and test again.
         */
        accessor.setAttribute("imagingCondition", "cloud");
        accessor.setAttribute("cloudCoverPercentage", 20.0);
        assertEquals(20.0, proxy.getCloudCoverPercentage(), 0.0);
        assertEquals(ImagingCondition.CLOUD, proxy.getImagingCondition());
        /*
         * Ask for an element which was excluded from the metadata format.
         * The intend is to ensure that no exception is thrown.
         */
        final Level originalLevel = accessor.setWarningLevel(Level.OFF);
        assertNull(proxy.getAttributeDescription());
        accessor.setWarningLevel(originalLevel);
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
        final SpatialMetadata  metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        final MetadataNodeAccessor accessor = new MetadataNodeAccessor(metadata, null, "ImageDescription", null);
        final MetadataNodeAccessor qualityA = new MetadataNodeAccessor(metadata, null, "ImageDescription/ImageQualityCode", null);
        qualityA.setAttribute("code",      "okay");
        qualityA.setAttribute("authority", "Geotoolkit.org");
        accessor.setAttribute("cloudCoverPercentage", 20); // Test mixing attributes with elements.

        final ImageDescription proxy = accessor.newProxyInstance(ImageDescription.class);
        assertEquals("Safety check against regression.", 20.0, proxy.getCloudCoverPercentage(), 0.0);

        final Identifier identifier = proxy.getImageQualityCode();
        assertSame(Citations.GEOTOOLKIT, identifier.getAuthority());
        assertEquals("okay", identifier.getCode());

        assertSame(identifier, proxy.getImageQualityCode());
        assertNull(proxy.getProcessingLevelCode());
    }

    /**
     * Test the {@code "DiscoveryMetadata/DescriptiveKeywords/.../keywords"} metadata.
     * This attribute is of kind {@code Collection<String>}.
     */
    @Test
    public void testKeywords() {
        final SpatialMetadata  metadata = new SpatialMetadata(SpatialMetadataFormat.getStreamInstance(GEOTK_FORMAT_NAME));
        final MetadataNodeAccessor accessor = new MetadataNodeAccessor(metadata, null,
                "DiscoveryMetadata/DescriptiveKeywords", "DescriptiveKeywordsEntry");
        accessor.selectChild(accessor.appendChild());
        accessor.setAttribute("keywords", "red", "yellow or green", "blue");
        accessor.selectChild(accessor.appendChild());
        accessor.setAttribute("keywords", "rouge", "jaune ou vert", "bleu");
        /*
         * Build the metadata objects.
         */
        final MetadataNodeAccessor rootAccessor = new MetadataNodeAccessor(metadata, null, "DiscoveryMetadata", null);
        final DataIdentification identification = rootAccessor.newProxyInstance(DataIdentification.class);
        assertMultilinesEquals(decodeQuotes(
            "DataIdentification[“DiscoveryMetadata”]\n" +
            "└───DescriptiveKeywords\n" +
            "    ├───DescriptiveKeywordsEntry\n" +
            "    │   └───keywords=“red yellow\u00A0or\u00A0green blue”\n" +
            "    └───DescriptiveKeywordsEntry\n" +
            "        └───keywords=“rouge jaune\u00A0ou\u00A0vert bleu”\n"), identification.toString());

        final Iterator<? extends Keywords> it = identification.getDescriptiveKeywords().iterator();
        assertTrue(it.hasNext());
        assertEquals(Arrays.asList(
                new SimpleInternationalString("red"),
                new SimpleInternationalString("yellow or green"),
                new SimpleInternationalString("blue")), it.next().getKeywords());
        assertTrue(it.hasNext());
        assertEquals(Arrays.asList(
                new SimpleInternationalString("rouge"),
                new SimpleInternationalString("jaune ou vert"),
                new SimpleInternationalString("bleu")), it.next().getKeywords());
        assertFalse(it.hasNext());
    }

    /**
     * Tests the list of {@code "ImageDescription/Dimensions/Dimension"} elements.
     * This method is actually a test of the {@link MetadataProxyList} class.
     */
    @Test
    public void testDimensionList() {
        final SpatialMetadata  metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        final MetadataNodeAccessor accessor = new MetadataNodeAccessor(metadata, null, "ImageDescription/Dimensions", "Dimension");
        final List<SampleDimension> dimensions = accessor.newProxyList(SampleDimension.class);
        for (int i=1; i<=4; i++) {
            accessor.selectChild(accessor.appendChild());
            assertNull(accessor.getAttributeAsDouble("minValue"));
            assertNull(accessor.getAttributeAsDouble("maxValue"));
            accessor.setAttribute("minValue", -i);
            accessor.setAttribute("maxValue",  i);
            accessor.setAttribute("validSampleValues", NumberRange.create(-i, true, i, true));
            assertEquals(i, dimensions.size());
        }
        int index = 0;
        for (final RangeDimension dim : dimensions) {
            assertSame(dim, dimensions.get(index++));
            assertTrue(dim instanceof SampleDimension);
            final SampleDimension sd = (SampleDimension) dim;
            assertEquals(Double.valueOf(-index), sd.getMinValue());
            assertEquals(Double.valueOf( index), sd.getMaxValue());
            assertEquals(NumberRange.create((byte) -index, true, (byte) index, true), sd.getValidSampleValues());
            /*
             * Check the methods defined in java.lang.Object.
             */
            assertEquals("hashCode consistency.", dim.hashCode(), dim.hashCode());
            assertEquals(dim, dim);
            assertFalse(dim.equals(accessor));
        }
    }

    /**
     * Tests the proxy with the children defined under the {@code "ImageDescription/Dimensions"}
     * node. This is also an indirect test of {@link MetadataProxyList}, except that this time
     * the list is created implicitly.
     */
    @Test
    public void testDimensions() {
        final SpatialMetadata  metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        final MetadataNodeAccessor accessor = new MetadataNodeAccessor(metadata, null, "ImageDescription/Dimensions", "Dimension");
        for (int i=1; i<=4; i++) {
            accessor.selectChild(accessor.appendChild());
            accessor.setAttribute("minValue", -i);
            accessor.setAttribute("maxValue",  i);
        }
        /*
         * We need to create the list only after the elements have been added, because the
         * current implementation does not detect element additions when they are not performed
         * by the same accessor.
         */
        final MetadataNodeAccessor rootAccessor = new MetadataNodeAccessor(metadata, null, "ImageDescription", null);
        final ImageDescription proxy = rootAccessor.newProxyInstance(ImageDescription.class);
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

    /**
     * Test the {@code "DiscoveryMetadata/SpatialResolution"} metadata. This is a collection
     * in metadata interface, but declared as a singleton in {@link SpatialMetadataFormat}.
     * The proxy should be able to handle that.
     */
    @Test
    public void testSpatialResolution() {
        final SpatialMetadata  metadata = new SpatialMetadata(SpatialMetadataFormat.getStreamInstance(GEOTK_FORMAT_NAME));
        final MetadataNodeAccessor accessor = new MetadataNodeAccessor(metadata, null, "DiscoveryMetadata/SpatialResolution", null);
        accessor.setAttribute("distance", 40);
        /*
         * Build the metadata objects.
         */
        final MetadataNodeAccessor rootAccessor = new MetadataNodeAccessor(metadata, null, "DiscoveryMetadata", null);
        final DataIdentification identification = rootAccessor.newProxyInstance(DataIdentification.class);
        assertMultilinesEquals(decodeQuotes(
            "DataIdentification[“DiscoveryMetadata”]\n" +
            "└───SpatialResolution\n" +
            "    └───distance=“40”\n"), identification.toString());

        final Iterator<? extends Resolution> it = identification.getSpatialResolutions().iterator();
        assertTrue(it.hasNext());
        assertEquals(Double.valueOf(40), it.next().getDistance());
        assertFalse(it.hasNext());
    }

    /**
     * Tests the {@code RectifiedGridDomain} node.
     *
     * @since 3.16
     */
    @Test
    public void testRectifiedGrid() {
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        final MetadataNodeAccessor rootAccessor = new MetadataNodeAccessor(metadata, null, "RectifiedGridDomain", null);
        rootAccessor.setAttribute("origin", -180.0, 90.0);
        MetadataNodeAccessor accessor = new MetadataNodeAccessor(rootAccessor, "Limits", null);
        accessor.setAttribute("low",  new int[] {  0,   0});
        accessor.setAttribute("high", new int[] {719, 359});
        accessor = new MetadataNodeAccessor(rootAccessor, "OffsetVectors", "OffsetVector");
        accessor.selectChild(accessor.appendChild()); accessor.setAttribute("values", 0.5,  0);
        accessor.selectChild(accessor.appendChild()); accessor.setAttribute("values", 0, -0.5);
        /*
         * Build the metadata proxy.
         */
        final RectifiedGrid grid = rootAccessor.newProxyInstance(RectifiedGrid.class);
        assertNull(grid.getCells());
        assertEquals(2, grid.getExtent().getDimension());
        assertEquals(2, grid.getOffsetVectors().size());
        assertTrue(Arrays.equals(new double[] { 0.5,    0}, grid.getOffsetVectors().get(0)));
        assertTrue(Arrays.equals(new double[] { 0.0, -0.5}, grid.getOffsetVectors().get(1)));
        assertTrue(Arrays.equals(new double[] {-180,   90}, grid.getOrigin().getCoordinate()));
        assertArrayEquals(new int[] {0,     0}, grid.getExtent().getLow ().getCoordinateValues());
        assertArrayEquals(new int[] {719, 359}, grid.getExtent().getHigh().getCoordinateValues());
        assertEquals(0,   grid.getExtent().getLow (1));
        assertEquals(359, grid.getExtent().getHigh(1));
        assertEquals(360, grid.getExtent().getSpan(1));
    }
}
