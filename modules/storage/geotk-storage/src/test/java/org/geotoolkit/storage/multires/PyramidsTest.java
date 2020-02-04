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
package org.geotoolkit.storage.multires;

import java.awt.Dimension;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.EstimatedGridGeometry;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PyramidsTest {

    private static final double DELTA = 0.0;

    @Test
    public void testGridGeometry2DTemplate() throws DataStoreException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GridExtent extent = new GridExtent(1000, 512);
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

        final Mosaic m1 = pyramid.getMosaics(scales[0]).iterator().next();
        final Mosaic m2 = pyramid.getMosaics(scales[1]).iterator().next();
        final Mosaic m3 = pyramid.getMosaics(scales[2]).iterator().next();

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

    /**
     * Create a pyramid template from a grid geometry with only an envelope and resolution
     */
    @Test
    public void testCreateTemplateFromResolution() throws FactoryException, DataStoreException, TransformException {

        final CoordinateReferenceSystem baseCrs = CommonCRS.WGS84.normalizedGeographic();
        final GeneralEnvelope baseEnv = new GeneralEnvelope(baseCrs);
        baseEnv.setRange(0, 10, 20);
        baseEnv.setRange(1, 20, 30);
        final EstimatedGridGeometry gridGeom = new EstimatedGridGeometry(baseEnv, new double[]{0.01, 0.01});

        {// Wgs84 TEMPLATE /////////////////////////////////////////////////////
            final CoordinateReferenceSystem tempCrs = CRS.forCode("CRS:84");
            final DefiningPyramid template = Pyramids.createTemplate(gridGeom, tempCrs, new Dimension(256, 256));
            final Envelope tempEnv = template.getEnvelope();
            Assert.assertEquals(tempCrs, template.getCoordinateReferenceSystem());
            Assert.assertEquals(tempCrs, tempEnv.getCoordinateReferenceSystem());
            Assert.assertTrue(new GeneralEnvelope(Envelopes.transform(tempEnv, baseCrs)).contains(baseEnv));
            Assert.assertEquals(3, template.getMosaics().size());
        }

        {// Mercator TEMPLATE /////////////////////////////////////////////////////
            final CoordinateReferenceSystem tempCrs = CRS.forCode("EPSG:3395");
            final DefiningPyramid template = Pyramids.createTemplate(gridGeom, tempCrs, new Dimension(256, 256));
            final Envelope tempEnv = template.getEnvelope();
            Assert.assertEquals(tempCrs, template.getCoordinateReferenceSystem());
            Assert.assertEquals(tempCrs, tempEnv.getCoordinateReferenceSystem());
            Assert.assertTrue(new GeneralEnvelope(Envelopes.transform(tempEnv, baseCrs)).contains(baseEnv));
            Assert.assertEquals(4, template.getMosaics().size());
        }
    }


}
