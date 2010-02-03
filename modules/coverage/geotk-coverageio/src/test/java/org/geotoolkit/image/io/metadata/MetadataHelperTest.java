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
package org.geotoolkit.image.io.metadata;

import java.awt.geom.AffineTransform;

import org.opengis.coverage.grid.RectifiedGrid;

import org.geotoolkit.display.shape.DoubleDimension2D;
import org.geotoolkit.image.io.ImageMetadataException;
import org.geotoolkit.internal.image.io.GridDomainAccessor;

import static org.geotoolkit.image.io.metadata.MetadataHelper.INSTANCE;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link MetadataHelper} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.09
 */
public final class MetadataHelperTest {
    /**
     * Small number for floating point comparisons.
     */
    private static final double EPS = 1E-12;

    /**
     * Tests the {@link MetadataHelper#getAffineTransform} method.
     * Also tests related methods fetching the grid size.
     *
     * @throws ImageMetadataException Should not happen.
     */
    @Test
    public void testAffineTransform() throws ImageMetadataException {
        // Creates a simple metadata.
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        final GridDomainAccessor accessor = new GridDomainAccessor(metadata);
        accessor.setOrigin(-10, -20);
        accessor.addOffsetVector(3, 4);
        accessor.addOffsetVector(0, 8);

        // Tests the metadata.
        final RectifiedGrid grid = metadata.getInstanceForType(RectifiedGrid.class);
        final AffineTransform tr = INSTANCE.getAffineTransform(grid, null);
        assertEquals(-10, tr.getTranslateX(), EPS);
        assertEquals(-20, tr.getTranslateY(), EPS);
        assertEquals(  3, tr.getScaleX(),     EPS);
        assertEquals(  8, tr.getScaleY(),     EPS);
        assertEquals(  4, tr.getShearX(),     EPS);
        assertEquals(  0, tr.getShearY(),     EPS);
        assertNull(INSTANCE.getCellDimension(tr));
        try {
            INSTANCE.getCellSize(tr);
            fail("Should not allow to compute a cell size.");
        } catch (ImageMetadataException e) {
            // This is the expected exception.
        }
        assertEquals("5 × 8", INSTANCE.getCellDimensionAsText(grid, null));
    }

    /**
     * Same as {@link #testAffineTransform}, but with a uniform scale.
     *
     * @throws ImageMetadataException Should not happen.
     */
    @Test
    public void testUniformTransform() throws ImageMetadataException {
        // Creates a simple metadata.
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        final GridDomainAccessor accessor = new GridDomainAccessor(metadata);
        accessor.setOrigin(-10, -20);
        accessor.addOffsetVector(4,  0);
        accessor.addOffsetVector(0, -4);

        // Tests the metadata.
        final RectifiedGrid grid = metadata.getInstanceForType(RectifiedGrid.class);
        final AffineTransform tr = INSTANCE.getAffineTransform(grid, null);
        assertEquals(-10, tr.getTranslateX(), EPS);
        assertEquals(-20, tr.getTranslateY(), EPS);
        assertEquals(  4, tr.getScaleX(),     EPS);
        assertEquals( -4, tr.getScaleY(),     EPS);
        assertEquals(  0, tr.getShearX(),     EPS);
        assertEquals(  0, tr.getShearY(),     EPS);
        assertEquals(  4, INSTANCE.getCellSize(tr), EPS);
        assertEquals(new DoubleDimension2D(4,4), INSTANCE.getCellDimension(tr));
        assertEquals("4", INSTANCE.getCellDimensionAsText(grid, null));
    }
}
