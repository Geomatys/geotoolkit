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
package org.geotoolkit.coverage.io;

import java.io.IOException;
import javax.imageio.IIOParam;
import javax.imageio.ImageWriter;
import java.awt.image.RenderedImage;
import java.awt.geom.AffineTransform;
import javax.imageio.metadata.IIOMetadata;

import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform2D;

import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.image.io.ImageMetadataException;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.apache.sis.referencing.operation.transform.LinearTransform;

import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.image.io.MultidimensionalImageStore.*;


/**
 * An {@link ImageCoverageWriter} which retains some intermediate computation during the
 * writing process.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.14
 */
final strictfp class ImageCoverageWriterInspector extends ImageCoverageWriter {
    /**
     * Small number for comparison of floating point values.
     */
    private static final double EPS = 1E-10;

    /**
     * The caller method.
     */
    private final String caller;

    /**
     * The format name, or {@code null} for auto-detect.
     */
    private final String format;

    /**
     * The difference between the requested grid and the actual grid which is used
     * for fetching the pixel values to write.
     */
    private MathTransform2D differenceTransform;

    /**
     * The metadata created by the writer.
     *
     * @since 3.17
     */
    private IIOMetadata metadata;

    /**
     * Creates a new instance.
     */
    ImageCoverageWriterInspector(final String caller) {
        this(caller, null);
    }

    /**
     * Creates a new instance which will force the usage of the given format.
     */
    ImageCoverageWriterInspector(final String caller, final String format) {
        this.caller = caller;
        this.format = format;
    }

    /**
     * If the format is unspecified, uses the format given at construction time.
     */
    @Override
    protected ImageWriter createImageWriter(String formatName,
            final Object output, final RenderedImage image) throws IOException
    {
        if (formatName == null) {
            formatName = format;
        }
        return super.createImageWriter(formatName, output, image);
    }

    /**
     * Delegates to the default implementation and stores the result.
     */
    @Override
    protected MathTransform2D geodeticToPixelCoordinates(final GridGeometry2D gridGeometry,
            final GridCoverageStoreParam geodeticParam, final IIOParam pixelParam,
            final boolean isNetcdfHack) // TODO: DEPRECATED: to be removed in Apache SIS.
            throws CoverageStoreException
    {
        final MathTransform2D tr = super.geodeticToPixelCoordinates(gridGeometry, geodeticParam, pixelParam, isNetcdfHack);
        differenceTransform = tr;
        return tr;
    }

    /**
     * Retains the image metadata (we don't care about the stream metadata for now).
     *
     * @since 3.17
     */
    @Override
    protected void completeImageMetadata(IIOMetadata metadata, GridCoverage coverage) throws IOException {
        super.completeImageMetadata(metadata, coverage);
        if (coverage != null) {
            this.metadata = metadata;
        }
    }

    /**
     * Asserts that the write operation matched the user request without the need for
     * a scaling of translation. Note however that a need for a expanding the bounds
     * may still be present.
     */
    public void assertNoDifference() {
        assertTrue("No scale or translation expected.", isIdentity(differenceTransform));
    }

    /**
     * Asserts that the difference transform contains no translation terms,
     * and only the scale terms given to this method.
     */
    public void assertDifferenceEqualsScale(final double scaleX, final double scaleY) {
        assertFalse("Expected a scale factor.", isIdentity(differenceTransform));
        assertTrue(differenceTransform instanceof LinearTransform);
        final Matrix m = ((LinearTransform) differenceTransform).getMatrix();
        for (int j=m.getNumRow(); --j>=0;) {
            for (int i=m.getNumCol(); --i>=0;) {
                final double expected;
                if (i == j) {
                    switch (j) {
                        case X_DIMENSION: expected = scaleX; break;
                        case Y_DIMENSION: expected = scaleY; break;
                        default:          expected =      1; break;
                    }
                } else {
                    expected = 0;
                }
                assertEquals(expected, m.getElement(j, i), EPS);
            }
        }
    }

    /**
     * Asserts that the difference transform contains no scale terms,
     * and only the translation terms given to this method.
     */
    public void assertDifferenceEqualsTranslation(final double tx, final double ty) {
        assertFalse("Expected translation terms.", isIdentity(differenceTransform));
        assertTrue(differenceTransform instanceof LinearTransform);
        final Matrix m = ((LinearTransform) differenceTransform).getMatrix();
        final int tc = m.getNumCol() - 1;
        for (int j=m.getNumRow(); --j>=0;) {
            for (int i=m.getNumCol(); --i>=0;) {
                final double expected;
                if (i == tc) {
                    switch (j) {
                        case X_DIMENSION: expected = tx; break;
                        case Y_DIMENSION: expected = ty; break;
                        default:          expected =  1; break;
                    }
                } else {
                    expected = (i == j) ? 1 : 0;
                }
                assertEquals(expected, m.getElement(j, i), EPS);
            }
        }
    }

    /**
     * Tests that the rectified grid is equals to the given affine transform coefficients.
     *
     * @since 3.17
     */
    public void assertRectifiedGridEquals(final double scaleX, final double scaleY,
            final double translateX, final double translateY) throws ImageMetadataException
    {
        assertInstanceOf("Expected a spatial metadata", SpatialMetadata.class, metadata);
        final MetadataHelper helper = new MetadataHelper(null);
        final RectifiedGrid rg = ((SpatialMetadata) metadata).getInstanceForType(RectifiedGrid.class);
        final AffineTransform tr = helper.getAffineTransform(rg, null);
        assertEquals("shearX",     0,          tr.getShearX(),     EPS);
        assertEquals("shearY",     0,          tr.getShearY(),     EPS);
        assertEquals("scaleX",     scaleX,     tr.getScaleX(),     EPS);
        assertEquals("scaleY",     scaleY,     tr.getScaleY(),     EPS);
        assertEquals("translateX", translateX, tr.getTranslateX(), EPS);
        assertEquals("translateY", translateY, tr.getTranslateY(), EPS);
    }

    /**
     * Returns debugging information.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder("differenceTransform in ");
        buffer.append(caller).append(":\n");
        Object print = differenceTransform;
        if (print instanceof LinearTransform) {
            print = ((LinearTransform) differenceTransform).getMatrix();
        }
        return buffer.append(print).append('\n').toString();
    }
}
