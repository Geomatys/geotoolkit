/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.metadata;

import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.test.Depend;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link AbstractMetadata#isEmpty()} and {@link ModifiableMetadata#prune()} methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
@Depend(PropertyMapTest.class)
public final strictfp class PrunerTest {
    /** The root metadata object being tested. */
    private final DefaultMetadata metadata;

    /** A child of the metadata object being tested. */
    private final DefaultDataIdentification identification;

    /** A child of the metadata object being tested. */
    private final DefaultExtent extent;

    /** A child of the metadata object being tested. */
    private final DefaultGeographicBoundingBox bbox;

    /**
     * Creates the metadata objects to be used for the test.
     */
    public PrunerTest() {
        metadata       = new DefaultMetadata();
        identification = new DefaultDataIdentification();
        extent         = new DefaultExtent();
        bbox           = new DefaultGeographicBoundingBox();
        extent.getGeographicElements().add(bbox);
        identification.getExtents().add(extent);
        metadata.getIdentificationInfo().add(identification);
    }

    /**
     * Tests the {@link AbstractMetadata#isEmpty()} method.
     */
    @Test
    public void testIsEmpty() {
        /*
         * Initially empty tree, or tree with only empty element.
         */
        assertTrue("GeographicBoundingBox", bbox.isEmpty());
        assertTrue("Extent",                extent.isEmpty());
        assertTrue("DataIdentification",    identification.isEmpty());
        assertTrue("Metadata",              metadata.isEmpty());
        /*
         * Set a non-empty identification info.
         */
        identification.setCitation(new DefaultCitation("A citation title"));
        assertTrue ("GeographicBoundingBox", bbox.isEmpty());
        assertTrue ("Extent",                extent.isEmpty());
        assertFalse("DataIdentification",    identification.isEmpty());
        assertFalse("Metadata",              metadata.isEmpty());
        /*
         * Set a non-empty metadata info.
         */
        metadata.setFileIdentifier("A file identifiers");
        assertTrue ("GeographicBoundingBox", bbox.isEmpty());
        assertTrue ("Extent",                extent.isEmpty());
        assertFalse("DataIdentification",    identification.isEmpty());
        assertFalse("Metadata",              metadata.isEmpty());
        /*
         * Set an empty string in an element.
         */
        identification.setCitation(new DefaultCitation("  "));
        assertTrue ("GeographicBoundingBox", bbox.isEmpty());
        assertTrue ("Extent",                extent.isEmpty());
        assertTrue ("DataIdentification",    identification.isEmpty());
        assertFalse("Metadata",              metadata.isEmpty());
        /*
         * Set an empty string in an element.
         */
        metadata.setFileIdentifier("   ");
        assertTrue("Metadata", metadata.isEmpty());
    }

    /**
     * Tests the {@link ModifiableMetadata#prune()} method.
     */
    @Test
    public void testPrune() {
        metadata.setFileIdentifier("A file identifiers");
        identification.setCitation(new DefaultCitation("A citation title"));
        assertFalse(PropertyAccessor.isEmpty(metadata.getFileIdentifier()));
        assertFalse(PropertyAccessor.isEmpty(identification.getCitation()));
        assertEquals(1, metadata.getIdentificationInfo().size());
        assertEquals(1, identification.getExtents().size());
        assertEquals(1, extent.getGeographicElements().size());
        assertFalse(metadata.isEmpty());

        metadata.prune();
        assertFalse(PropertyAccessor.isEmpty(metadata.getFileIdentifier()));
        assertFalse(PropertyAccessor.isEmpty(identification.getCitation()));
        assertEquals(1, metadata.getIdentificationInfo().size());
        assertEquals(0, identification.getExtents().size());
        assertEquals(0, extent.getGeographicElements().size());
        assertFalse(metadata.isEmpty());

        metadata.setFileIdentifier(" ");
        identification.setCitation(new DefaultCitation(" "));
        assertNotNull(metadata.getFileIdentifier());
        metadata.prune();

        assertNull(metadata.getFileIdentifier());
        assertNull(identification.getCitation());
        assertTrue(metadata.getIdentificationInfo().isEmpty());
        assertTrue(identification.getExtents().isEmpty());
        assertTrue(extent.getGeographicElements().isEmpty());
        assertTrue(metadata.isEmpty());
    }
}
