/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.process.coverage.resample;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.coverage.AbstractProcessTest;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import static org.junit.Assert.*;
import org.junit.*;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ResampleTest extends AbstractProcessTest {

    static final int WIDTH = 10;
    static final int HEIGHT = 10;
    static final int SIZE = WIDTH*HEIGHT;

    public ResampleTest() {
        super(ResampleDescriptor.NAME);
    }

    @Test
    public void testProcess() throws Exception {

        final float[][] matrix = new float[][]{
            {0,1,2,3},
            {4,5,6,7},
            {0,1,2,3},
            {4,5,6,7}
        };

        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setRenderedImage(matrix);
        gcb.setEnvelope(env);
        final GridCoverage2D coverage = gcb.getGridCoverage2D();


        //get the description of the process we want
        ProcessDescriptor desc = null;
        try {
            desc = ProcessFinder.getProcessDescriptor("coverage", "Resample");
        } catch (NoSuchIdentifierException ex) {
            Logger.getLogger(ResampleTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());
        }

        //create a process
        //set the input parameters
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("coverage").setValue(coverage);
        input.parameter("crs").setValue(CRS.decode("EPSG:3395"));
        final Process p = desc.createProcess(input);
        ParameterValueGroup output = null;
        try {
            //get the result
            output = p.call();
        } catch (ProcessException ex) {
            Logger.getLogger(ResampleTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());
        }

        assertNotNull(output);
        Object res = output.parameter("result").getValue();

        assertNotNull(res);
        assertTrue(res instanceof GridCoverage2D);
        GridCoverage2D toTest = (GridCoverage2D)res;

        assertEquals(toTest.getCoordinateReferenceSystem(), CRS.decode("EPSG:3395"));

    }

}
