/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.storage;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import org.opengis.referencing.operation.MathTransform1D;
import org.apache.sis.coverage.grid.GridCoverage2D;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.internal.storage.MemoryGridResource;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.test.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests {@link ConvertedCoverageResource}.
 *
 * @author  Johann Sorel (Geomatys)
 * @version 1.4
 * @since   1.4
 */
public final class ConvertedCoverageResourceTest extends TestCase {
    /**
     * Tests {@link ConvertedCoverageResource}.
     */
    @Test
    public void testConvert() throws DataStoreException {

        //source coverage
        final BufferedImage data = new BufferedImage(360, 180, BufferedImage.TYPE_BYTE_GRAY);
        final GridGeometry grid = new GridGeometry(new GridExtent(360,180), CRS.getDomainOfValidity(CommonCRS.WGS84.normalizedGeographic()), GridOrientation.HOMOTHETY);
        final GridCoverage2D coverage = new GridCoverage2D(grid, null, data);
        final GridCoverageResource source = new MemoryGridResource(null, coverage, null);

        //converted coverage
        final MathTransform1D converter = (MathTransform1D) MathTransforms.linear(2, 10);
        final ConvertedCoverageResource converted = new ConvertedCoverageResource(source, new MathTransform1D[]{converter}, null);

        //ensure structure is preserved
        assertEquals(source.getGridGeometry(), converted.getGridGeometry());
        assertEquals(source.getSampleDimensions(), converted.getSampleDimensions());
        assertEquals(source.getIdentifier(), converted.getIdentifier());

        //ensure values are modified
        final RenderedImage convertedImage = converted.read(grid, 0).render(grid.getExtent());
        final PixelIterator ite = PixelIterator.create(convertedImage);
        ite.moveTo(0, 0);
        assertEquals(10, ite.getSample(0));
    }
}
