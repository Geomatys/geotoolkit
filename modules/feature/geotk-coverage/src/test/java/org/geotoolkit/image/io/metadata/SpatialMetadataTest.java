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
import java.util.Collections;
import java.util.logging.Level;

import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.metadata.acquisition.Instrument;
import org.opengis.metadata.content.ImageDescription;
import org.opengis.metadata.content.ImagingCondition;

import org.apache.sis.test.DependsOn;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.metadata.iso.acquisition.DefaultInstrument;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.content.DefaultImageDescription;

import org.junit.*;

import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Tests {@link SpatialMetadata}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.06
 */
@DependsOn(MetadataProxyTest.class)
public final strictfp class SpatialMetadataTest {
    /**
     * The warning level to use in this test. We set them to FINE in order to avoid
     * polluting the console output during Maven build. In order to see the warnings,
     * set this field to Level.WARNING.
     */
    private static final Level WARNING_LEVEL = Level.FINE;

    /**
     * Tests the {@link SpatialMetadata#getInstanceForType} method for an {@link ImageDescription}
     * metadata.
     */
    @Test
    public void testImageDescription() {
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        /*
         * Write a few data using the accessor.
         */
        MetadataNodeAccessor accessor = new MetadataNodeAccessor(metadata, null, "ImageDescription", null);
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
        /*
         * Test the copy to an org.apache.sis.metadata.iso object.
         * Note that the following warning is emmited:
         *
         *    org.opengis.metadata.content.ImageDescription getAttributeDescription
         *    WARNING: No such element: AttributeDescription
         *
         * We set the level to FINE in order to not pollute
         * the console output during Maven build.
         */
        metadata.setWarningLevel(WARNING_LEVEL);
        final ImageDescription copy = new DefaultImageDescription(description);
        assertEquals(ImagingCondition.CLOUD, copy.getImagingCondition());
        assertEquals(Double.valueOf(20), copy.getCloudCoverPercentage());
        assertNull(copy.getCompressionGenerationQuantity());
        assertNull(copy.getAttributeDescription());
    }

    /**
     * Tests the {@link SpatialMetadata#getInstanceForType} method for an {@link Instrument}
     * metadata.
     *
     * @since 3.07
     */
    @Test
    public void testInstrument() {
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getStreamInstance(GEOTK_FORMAT_NAME));
        MetadataNodeAccessor accessor = new MetadataNodeAccessor(metadata, null, "AcquisitionMetadata/Platform/Instruments", "Instrument");
        accessor.selectChild(accessor.appendChild());
        accessor.setAttribute("type", "Currentmeter");
        accessor.setAttribute("citation", "Some paper");
        /*
         * Read the Instrument metadata.
         */
        final List<Instrument> instruments = metadata.getListForType(Instrument.class);
        assertEquals("getListForType(...) should have returned a singleton " +
                     "since our test declared only one instrument.", 1, instruments.size());
        final Instrument instrument = instruments.get(0);
        assertSame("getInstanceForType(...) should have detected that the node is a list, " +
                   "and returned the first element of that list as a convenience.",
                   instrument, metadata.getInstanceForType(Instrument.class));

        // Expected values
        final CharSequence type = new SimpleInternationalString("Currentmeter");
        final List<?> citations = Collections.singletonList(new DefaultCitation("Some paper"));
        assertEquals("getType()",      type,      instrument.getType());
        assertEquals("getCitations()", citations, instrument.getCitations());
        /*
         * Test the copy to an org.apache.sis.metadata.iso object.
         * Note that the following warning is emmited:
         *
         *    org.opengis.metadata.acquisition.Instrument getMountedOn
         *    WARNING: No such element: MountedOn
         *
         * We set the level to FINE in order to not pollute
         * the console output during Maven build.
         */
        metadata.setWarningLevel(WARNING_LEVEL);
        final Instrument copy = new DefaultInstrument(instrument);
        assertEquals("getType()",      type,      copy.getType());
        assertEquals("getCitations()", citations, copy.getCitations());
        assertNull  ("getMountedOn()",            copy.getMountedOn());
    }

    /**
     * Tests the {@link SpatialMetadata#getInstanceForType} method for an {@link RectifiedGrid}
     * metadata.
     */
    @Test
    public void testRectifiedGrid() {
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        /*
         * Write a few data using the accessor.
         */
        final int[] limitsLow  = new int[] {4, 10, 16};
        final int[] limitsHigh = new int[] {6,  2,  8};
        final double[] vector0 = new double[] {2, 5, 8};
        final double[] vector1 = new double[] {3, 1, 4};
        MetadataNodeAccessor accessor = new MetadataNodeAccessor(metadata, null, "RectifiedGridDomain/Limits", null);
        accessor.setAttribute("low",  limitsLow);
        accessor.setAttribute("high", limitsHigh);
        accessor = new MetadataNodeAccessor(metadata, null, "RectifiedGridDomain/OffsetVectors", "OffsetVector");
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

    /**
     * Tests the {@link SpatialMetadata#getInstanceForType} method for non-existent elements.
     *
     * @since 3.08
     */
    @Test
    public void testInexistentNode() {
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        metadata.setReadOnly(true);
        assertNull(metadata.getListForType(SampleDimension.class));
        assertMultilinesEquals("No node should have been created.", GEOTK_FORMAT_NAME + '\n', metadata.toString());
        metadata.setReadOnly(false);
        assertNull(metadata.getListForType(SampleDimension.class));
        assertNull(metadata.getInstanceForType(SampleDimension.class));
        assertMultilinesEquals("No node should have been created.", GEOTK_FORMAT_NAME + '\n', metadata.toString());
    }
}
