/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.processing.coverage.bandcombine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.Process;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.coverage.Coverage;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BandCombineTest extends org.geotoolkit.test.TestBase {

    @Test
    public void combineTest() throws Exception{

        final GridCoverage2D cov1 = create(BufferedImage.TYPE_3BYTE_BGR, Color.RED, Color.BLACK);
        final GridCoverage2D cov2 = create(BufferedImage.TYPE_4BYTE_ABGR, Color.BLUE, Color.GREEN);
        final GridCoverage2D cov3 = create(BufferedImage.TYPE_3BYTE_BGR, Color.GREEN, Color.RED);


        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, BandCombineDescriptor.NAME);
        assertNotNull(desc);

        final ParameterValueGroup params = desc.getInputDescriptor().createValue();
        params.parameter("coverages").setValue(new Coverage[]{cov1,cov2,cov3});

        final Process process = desc.createProcess(params);
        final ParameterValueGroup result = process.call();

        //check result coverage
        final GridCoverage2D outCoverage = (GridCoverage2D) result.parameter("result").getValue();
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), outCoverage.getCoordinateReferenceSystem());

        final RenderedImage outImage = outCoverage.getRenderedImage();
        final SampleModel outSampleModel = outImage.getSampleModel();
        assertEquals(100, outImage.getWidth());
        assertEquals(100, outImage.getHeight());
        assertEquals(10, outSampleModel.getNumBands());
        assertEquals(DataBuffer.TYPE_BYTE, outSampleModel.getDataType());

        //check values
        final Raster outRaster = outImage.getData();
        final int[] sample = new int[10];
        final int[] color1 = new int[]{255,0,0,0,0  ,255,255,0  ,255,0}; //3B_RED, 4B_BLUE, 3B_GREEN
        final int[] color2 = new int[]{0  ,0,0,0,255,0  ,255,255,0  ,0}; //3B_BLACK, 4B_GREEN, 3B_RED
        for(int y=0;y<100;y++){
            for(int x=0;x<100;x++){
                outRaster.getPixel(x, y, sample);
                if(y<50){
                    assertArrayEquals("coord "+x+","+y,color1, sample);
                }else{
                    assertArrayEquals("coord "+x+","+y,color2, sample);
                }
            }
        }

    }

    private static GridCoverage2D create(int type, Color color1, Color color2){
        final BufferedImage inputImage = new BufferedImage(100, 100, type);
        final Graphics2D g = inputImage.createGraphics();
        g.setColor(color1);
        g.fillRect(0, 0, 100, 50);
        g.setColor(color2);
        g.fillRect(0, 50, 100, 50);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setRenderedImage(inputImage);
        gcb.setCoordinateReferenceSystem(CommonCRS.WGS84.normalizedGeographic());
        gcb.setEnvelope(0,0,500,30);
        final GridCoverage2D inCoverage = (GridCoverage2D) gcb.build();
        return inCoverage;
    }

}
