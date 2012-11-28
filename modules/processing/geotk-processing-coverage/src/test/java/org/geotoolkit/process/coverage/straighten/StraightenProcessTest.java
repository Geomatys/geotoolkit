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
package org.geotoolkit.process.coverage.straighten;

import org.geotoolkit.process.Process;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.coverage.AbstractProcessTest;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.parameter.ParameterValueGroup;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StraightenProcessTest extends AbstractProcessTest {

    public StraightenProcessTest() {
        super(StraightenDescriptor.NAME);
    }
    
    @Test
    public void testStraighten() throws ProcessException {
        final float[][] matrix = new float[100][100];
        for(int x=0;x<100;x++){
            for(int y=0;y<100;y++){
                matrix[x][y] = x+y;
            }
        }
        
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setRenderedImage(matrix);
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, 0, 100);
        env.setRange(1, 0, 100);
        gcb.setEnvelope(env);
        final GridCoverage2D coverage = gcb.getGridCoverage2D();
        
        
        final ProcessDescriptor desc = StraightenDescriptor.INSTANCE;
        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        Parameters.getOrCreate(StraightenDescriptor.COVERAGE_IN, in).setValue(coverage);
        final Process process = desc.createProcess(in);
        final ParameterValueGroup out = process.call();
        
        final GridCoverage2D res = (GridCoverage2D) out.parameter(StraightenDescriptor.COVERAGE_OUT.getName().getCode()).getValue();
        
        assertEquals(coverage.getCoordinateReferenceSystem(), res.getCoordinateReferenceSystem());
        assertEquals(coverage.getGridGeometry().getGridToCRS2D(), res.getGridGeometry().getGridToCRS2D());
        
    }
    
}
