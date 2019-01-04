/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.processing.coverage.straighten;

import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcessTest;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.parameter.ParameterValueGroup;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class StraightenProcessTest extends AbstractProcessTest {

    public StraightenProcessTest() {
        super(StraightenDescriptor.NAME);
    }

    @Test
    public void testStraightenNoChange() throws ProcessException {
        final float[][] matrix = new float[40][60];
        for(int y=0;y<40;y++){
            for(int x=0;x<60;x++){
                matrix[y][x] = x+y;
            }
        }

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setRenderedImage(matrix);
        final AffineTransform2D gridToCrs = new AffineTransform2D(1,0,0,-1,20,30);
        final GridExtent gridEnv = new GridExtent(null, new long[]{0, 0}, new long[]{60, 40}, false);
        final GridGeometry2D gridGeom = new GridGeometry2D(gridEnv, PixelOrientation.UPPER_LEFT, gridToCrs, CommonCRS.WGS84.normalizedGeographic());
        gcb.setGridGeometry(gridGeom);
        final GridCoverage2D coverage = gcb.getGridCoverage2D();


        final ProcessDescriptor desc = StraightenDescriptor.INSTANCE;
        final Parameters in = Parameters.castOrWrap(desc.getInputDescriptor().createValue());
        in.getOrCreate(StraightenDescriptor.COVERAGE_IN).setValue(coverage);
        final Process process = desc.createProcess(in);
        final ParameterValueGroup out = process.call();

        final GridCoverage2D res = (GridCoverage2D) out.parameter(StraightenDescriptor.COVERAGE_OUT.getName().getCode()).getValue();

        assertEquals(coverage.getCoordinateReferenceSystem(),
                   res.getCoordinateReferenceSystem());
        assertEquals(coverage.getGridGeometry().getGridToCRS2D(),
                   res.getGridGeometry().getGridToCRS2D());
        assertEquals(coverage.getEnvelope(),
                   res.getEnvelope());
        assertEquals(coverage.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT),
                   res.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT));
        assertEquals(coverage.getGridGeometry().getExtent2D(),
                   res.getGridGeometry().getExtent2D());
    }

    @Test
    public void testStraightenVerticalFlip() throws ProcessException {
        final float[][] matrix = new float[40][60];
        for(int y=0;y<40;y++){
            for(int x=0;x<60;x++){
                matrix[y][x] = x+y;
            }
        }

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setRenderedImage(matrix);
        final AffineTransform2D gridToCrs = new AffineTransform2D(1,0,0,1,20,30);
        final GridExtent gridEnv = new GridExtent(null, new long[]{0, 0}, new long[]{60, 40}, false);
        final GridGeometry2D gridGeom = new GridGeometry2D(gridEnv, PixelOrientation.UPPER_LEFT, gridToCrs, CommonCRS.WGS84.normalizedGeographic());
        gcb.setGridGeometry(gridGeom);
        final GridCoverage2D coverage = gcb.getGridCoverage2D();


        final ProcessDescriptor desc = StraightenDescriptor.INSTANCE;
        final Parameters in = Parameters.castOrWrap(desc.getInputDescriptor().createValue());
        in.getOrCreate(StraightenDescriptor.COVERAGE_IN).setValue(coverage);
        final Process process = desc.createProcess(in);
        final ParameterValueGroup out = process.call();

        final GridCoverage2D res = (GridCoverage2D) out.parameter(StraightenDescriptor.COVERAGE_OUT.getName().getCode()).getValue();

        assertEquals(coverage.getCoordinateReferenceSystem(),
                   res.getCoordinateReferenceSystem());
        assertEquals(new AffineTransform2D(1, 0, 0, -1, 20, 71),
                res.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT));
    }

}
