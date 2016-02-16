/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.processing.coverage.merge;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.opengis.coverage.Coverage;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MergeTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000001;

    @Test
    public void mergeTest() throws NoSuchAuthorityCodeException, FactoryException, ProcessException{

        //first image, CRS:84, 3 bytes, 4x scale
        final BufferedImage inputImage1 = new BufferedImage(1440, 720, BufferedImage.TYPE_3BYTE_BGR);
        final Graphics2D g = inputImage1.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 1440, 720);
        g.setColor(Color.GREEN);
        g.fillRect(720, 0, 720, 720);
        final GridCoverageBuilder gcb1 = new GridCoverageBuilder();
        gcb1.setRenderedImage(inputImage1);
        gcb1.setCoordinateReferenceSystem(CommonCRS.WGS84.normalizedGeographic());
        gcb1.setEnvelope(-180,-90,+180,+90);
        final GridCoverage2D inCoverage1 = (GridCoverage2D) gcb1.build();

        //second image, EPSG:4326, 1 float, 2x scale
        final float[][] data = new float[720][360];
        for(int x=0;x<360;x++){
            for(int y=0;y<720;y++){
                data[y][x] = x+y;
            }
        }
        final GridCoverageBuilder gcb2 = new GridCoverageBuilder();
        gcb2.setRenderedImage(data);
        gcb2.setCoordinateReferenceSystem(CRS.decode("EPSG:4326"));
        gcb2.setEnvelope(-90,-180,+90,+180);
        final GridCoverage2D inCoverage2 = (GridCoverage2D) gcb2.build();


        //call the merge process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("coverage", "merge");
        assertNotNull(desc);

        final GeneralEnvelope penv = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        penv.setRange(0, -45, 45);
        penv.setRange(1, -45, 45);

        final ParameterValueGroup params = desc.getInputDescriptor().createValue();
        params.parameter("coverages").setValue(new Coverage[]{inCoverage1,inCoverage2});
        params.parameter("envelope").setValue(penv);
        params.parameter("resolution").setValue(1);

        final Process process = desc.createProcess(params);
        final ParameterValueGroup result = process.call();

        //check result coverage
        final GridCoverage2D outCoverage = (GridCoverage2D) result.parameter("result").getValue();
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), outCoverage.getCoordinateReferenceSystem());
        assertEquals(penv.getMinimum(0), outCoverage.getEnvelope().getMinimum(0), DELTA);
        assertEquals(penv.getMinimum(1), outCoverage.getEnvelope().getMinimum(1), DELTA);
        assertEquals(penv.getMaximum(0), outCoverage.getEnvelope().getMaximum(0), DELTA);
        assertEquals(penv.getMaximum(1), outCoverage.getEnvelope().getMaximum(1), DELTA);

        final RenderedImage outImage = outCoverage.getRenderedImage();
        final SampleModel outSampleModel = outImage.getSampleModel();
        assertEquals(90, outImage.getWidth());
        assertEquals(90, outImage.getHeight());
        assertEquals(4, outSampleModel.getNumBands());
        assertEquals(DataBuffer.TYPE_FLOAT, outSampleModel.getDataType());

        //check values
        final Raster outRaster = outImage.getData();
        final float[] sample = new float[4];
        for(int y=0;y<90;y++){
            for(int x=0;x<90;x++){
                outRaster.getPixel(x, y, sample);
                //TODO check pixels
            }
        }

    }

}
