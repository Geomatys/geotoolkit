/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.coverage.grid;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Arrays;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BufferedGridCoverageTest {

    @Test
    public void testCoverage2D() {

        //create coverage
        final GridExtent extent = new GridExtent(null, new long[]{0,0}, new long[]{1,1}, true);
        final MathTransform gridToCrs = new AffineTransform2D(1, 0, 0, 1, 0, 0);
        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GridGeometry gridgeom = new GridGeometry(extent, PixelInCell.CELL_CENTER, gridToCrs, crs);

        final MathTransform1D toUnits = (MathTransform1D) MathTransforms.linear(0.5, 100);
        final SampleDimension sd = new SampleDimension.Builder().setName("t").addQuantitative("data", NumberRange.create(-10, true, 10, true), toUnits, Units.CELSIUS).build();

        final BufferedGridCoverage coverage = new BufferedGridCoverage(gridgeom, Arrays.asList(sd), DataBuffer.TYPE_SHORT);

        BufferedImage img = (BufferedImage) coverage.render(null);
        img.getRaster().setSample(0, 0, 0, 0);
        img.getRaster().setSample(1, 0, 0, 5);
        img.getRaster().setSample(0, 1, 0, -5);
        img.getRaster().setSample(1, 1, 0, -10);

        //test not converted values
        RenderedImage notConverted = (BufferedImage) coverage.render(null);
        testSamples(notConverted, new double[][]{{0,5},{-5,-10}});

        //test converted values
        org.apache.sis.coverage.grid.GridCoverage convertedCoverage = coverage.forConvertedValues(true);
        RenderedImage converted = (BufferedImage) convertedCoverage.render(null);
        testSamples(converted, new double[][]{{100,102.5},{97.5,95}});

    }

    private void testSamples(RenderedImage image, double[][] values) {

        final Raster raster = image.getData();

        for (int y=0;y<values.length;y++) {
            for (int x=0;x<values[0].length;x++) {
                double value = raster.getSampleDouble(x, y, 0);
                Assert.assertEquals(values[y][x], value, 0.0);
            }
        }
    }

}
