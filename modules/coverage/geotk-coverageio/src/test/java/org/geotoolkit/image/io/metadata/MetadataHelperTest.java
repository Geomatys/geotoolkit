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
package org.geotoolkit.image.io.metadata;

import java.util.Locale;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;

import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.util.Localized;
import org.geotoolkit.display.shape.DoubleDimension2D;
import org.geotoolkit.image.io.ImageMetadataException;
import org.geotoolkit.internal.image.io.GridDomainAccessor;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Tests the {@link MetadataHelper} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.09
 */
public final strictfp class MetadataHelperTest implements Localized {
    /**
     * Small number for floating point comparisons.
     */
    private static final double EPS = 1E-12;

    /**
     * Necessary for making the test locale-insensitive.
     */
    @Override
    public Locale getLocale() {
        return Locale.FRANCE;
    }

    /**
     * Tests the {@link MetadataHelper#getAffineTransform} method.
     * Contains also opportunist tests of the following methods:
     * <p>
     * <ul>
     *   <li>{@link MetadataHelper#getCellDimension}</li>
     *   <li>{@link MetadataHelper#getCellSize}</li>
     *   <li>{@link MetadataHelper#formatCellDimension}</li>
     *   <li>{@link MetadataHelper#getGridToCRS}</li>
     * </ul>
     *
     * @throws ImageMetadataException Should not happen.
     */
    @Test
    public void testAffineTransform() throws ImageMetadataException {
        // Creates a simple metadata.
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        final GridDomainAccessor accessor = new GridDomainAccessor(metadata);
        accessor.setOrigin(-10, -20);
        accessor.addOffsetVector(3, 4);
        accessor.addOffsetVector(0, 8);

        // Tests the metadata.
        final MetadataHelper hlp = new MetadataHelper(this);
        final RectifiedGrid grid = metadata.getInstanceForType(RectifiedGrid.class);
        final AffineTransform tr = hlp.getAffineTransform(grid, null);
        assertEquals(-10, tr.getTranslateX(), EPS);
        assertEquals(-20, tr.getTranslateY(), EPS);
        assertEquals(  3, tr.getScaleX(),     EPS);
        assertEquals(  8, tr.getScaleY(),     EPS);
        assertEquals(  0, tr.getShearX(),     EPS);
        assertEquals(  4, tr.getShearY(),     EPS);
        assertEquals("Testing origin", new Point2D.Double(-10,-20), tr.transform(new Point2D.Double(), null));
        assertEquals("Testing offset vector 1", new Point2D.Double(3,4), tr.deltaTransform(new Point2D.Double(1,0), null));
        assertEquals("Testing offset vector 2", new Point2D.Double(0,8), tr.deltaTransform(new Point2D.Double(0,1), null));
        assertNull(hlp.getCellDimension(tr));
        try {
            hlp.getCellSize(tr);
            fail("Should not allow to compute a cell size.");
        } catch (ImageMetadataException e) {
            // This is the expected exception.
        }
        assertEquals("5 × 8", hlp.formatCellDimension(grid, null));
        /*
         * getGridCRS(...) should returns an equivalent transform.
         */
        final MathTransform gridToCRS = hlp.getGridToCRS(grid);
        assertTrue(gridToCRS instanceof AffineTransform);
        assertTrue(tr.equals(gridToCRS));
    }

    /**
     * Same as {@link #testAffineTransform}, but with a uniform scale.
     *
     * @throws ImageMetadataException Should not happen.
     */
    @Test
    public void testUniformTransform() throws ImageMetadataException {
        // Creates a simple metadata.
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        final GridDomainAccessor accessor = new GridDomainAccessor(metadata);
        accessor.setOrigin(-10, -20);
        accessor.addOffsetVector(4,  0);
        accessor.addOffsetVector(0, -4);

        // Tests the metadata.
        final MetadataHelper hlp = new MetadataHelper(this);
        final RectifiedGrid grid = metadata.getInstanceForType(RectifiedGrid.class);
        final AffineTransform tr = hlp.getAffineTransform(grid, null);
        assertEquals(-10, tr.getTranslateX(),  EPS);
        assertEquals(-20, tr.getTranslateY(),  EPS);
        assertEquals(  4, tr.getScaleX(),      EPS);
        assertEquals( -4, tr.getScaleY(),      EPS);
        assertEquals(  0, tr.getShearX(),      EPS);
        assertEquals(  0, tr.getShearY(),      EPS);
        assertEquals(  4, hlp.getCellSize(tr), EPS);
        assertEquals(new DoubleDimension2D(4,4), hlp.getCellDimension(tr));
        assertEquals("4", hlp.formatCellDimension(grid, null));
    }
}
