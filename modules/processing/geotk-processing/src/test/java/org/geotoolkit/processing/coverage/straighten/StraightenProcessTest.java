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

import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcessTest;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.datum.PixelInCell;

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
        gcb.setValues(BufferedImages.toDataBuffer1D(matrix));
        gcb.setRanges(new SampleDimension.Builder().setName(0).build());
        final AffineTransform2D gridToCrs = new AffineTransform2D(1,0,0,-1,20,30);
        final GridExtent gridEnv = new GridExtent(60, 40);
        final GridGeometry gridGeom = new GridGeometry(gridEnv, PixelInCell.CELL_CORNER, gridToCrs, CommonCRS.WGS84.normalizedGeographic());
        gcb.setDomain(gridGeom);
        final GridCoverage coverage = gcb.build();


        final ProcessDescriptor desc = StraightenDescriptor.INSTANCE;
        final Parameters in = Parameters.castOrWrap(desc.getInputDescriptor().createValue());
        in.getOrCreate(StraightenDescriptor.COVERAGE_IN).setValue(coverage);
        final Process process = desc.createProcess(in);
        final ParameterValueGroup out = process.call();

        final GridCoverage res = (GridCoverage) out.parameter(StraightenDescriptor.COVERAGE_OUT.getName().getCode()).getValue();

        assertEquals(coverage.getCoordinateReferenceSystem(),
                   res.getCoordinateReferenceSystem());
        assertEquals(coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER),
                   res.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER));
        assertEquals(coverage.getGridGeometry().getEnvelope(),
                   res.getGridGeometry().getEnvelope());
        assertEquals(coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER),
                   res.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER));
        assertEquals(coverage.getGridGeometry().getExtent(),
                   res.getGridGeometry().getExtent());
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
        gcb.setValues(BufferedImages.toDataBuffer1D(matrix));
        gcb.setRanges(new SampleDimension.Builder().setName(0).build());
        final AffineTransform2D gridToCrs = new AffineTransform2D(1,0,0,1,20,30);
        final GridExtent gridEnv = new GridExtent(60, 40);
        final GridGeometry gridGeom = new GridGeometry(gridEnv, PixelInCell.CELL_CORNER, gridToCrs, CommonCRS.WGS84.normalizedGeographic());
        gcb.setDomain(gridGeom);
        final GridCoverage coverage = gcb.build();

        final ProcessDescriptor desc = StraightenDescriptor.INSTANCE;
        final Parameters in = Parameters.castOrWrap(desc.getInputDescriptor().createValue());
        in.getOrCreate(StraightenDescriptor.COVERAGE_IN).setValue(coverage);
        final Process process = desc.createProcess(in);
        final ParameterValueGroup out = process.call();

        final GridCoverage res = (GridCoverage) out.parameter(StraightenDescriptor.COVERAGE_OUT.getName().getCode()).getValue();

        assertEquals(coverage.getCoordinateReferenceSystem(),
                   res.getCoordinateReferenceSystem());
        assertEquals(new AffineTransform2D(1, 0, 0, -1, 20, 71),
                res.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER));
    }

}
