/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.data.multires;

import java.awt.Dimension;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.coverage.grid.GridGeometry;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PyramidsTest {

    private static final double DELTA = 0.0;

    @Test
    public void testGridGeometry2DTemplate() throws DataStoreException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GridExtent extent = new GridExtent(null, new long[]{0,0}, new long[]{1000, 512}, false);
        final AffineTransform2D gridToCrs = new AffineTransform2D(1, 0, 0, -1, -50, 40);
        final GridGeometry gg = new GridGeometry2D(extent, gridToCrs, crs);

        final DefiningPyramid pyramid = Pyramids.createTemplate(gg, new Dimension(256, 256));
        Assert.assertEquals(crs, pyramid.getCoordinateReferenceSystem());

        Assert.assertEquals(3, pyramid.getMosaics().size());
        final double[] scales = pyramid.getScales();
        Assert.assertEquals(3, scales.length);
        Assert.assertEquals(1, scales[0], DELTA);
        Assert.assertEquals(2, scales[1], DELTA);
        Assert.assertEquals(4, scales[2], DELTA);

        final Mosaic m1 = pyramid.getMosaics(0).iterator().next();
        final Mosaic m2 = pyramid.getMosaics(1).iterator().next();
        final Mosaic m3 = pyramid.getMosaics(2).iterator().next();

        //upperleft corner is in PixelInCell.CELL_CORNER, causing the 0.5 offset.
        final DirectPosition2D upperleft = new DirectPosition2D(crs, -50.5, 40.5);
        Assert.assertEquals(upperleft, m1.getUpperLeftCorner());
        Assert.assertEquals(upperleft, m2.getUpperLeftCorner());
        Assert.assertEquals(upperleft, m3.getUpperLeftCorner());

        Assert.assertEquals(new Dimension(256, 256), m1.getTileSize());
        Assert.assertEquals(new Dimension(256, 256), m2.getTileSize());
        Assert.assertEquals(new Dimension(256, 256), m3.getTileSize());

        Assert.assertEquals(new Dimension(4, 2), m1.getGridSize());
        Assert.assertEquals(new Dimension(2, 1), m2.getGridSize());
        Assert.assertEquals(new Dimension(1, 1), m3.getGridSize());


    }

}
