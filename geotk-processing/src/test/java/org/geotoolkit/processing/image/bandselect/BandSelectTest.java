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
package org.geotoolkit.processing.image.bandselect;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BandSelectTest {

    @Test
    public void selectTest() throws Exception{

        final BufferedImage inputImage = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics2D g = inputImage.createGraphics();
        g.setColor(new Color(100, 30, 50));
        g.fillRect(0, 0, 50, 100);
        g.setColor(new Color(80, 200, 10));
        g.fillRect(50, 0, 50, 100);

        final SampleModel inSampleModel = inputImage.getSampleModel();

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, BandSelectDescriptor.NAME);
        assertNotNull(desc);

        final ParameterValueGroup params = desc.getInputDescriptor().createValue();
        params.parameter("image").setValue(inputImage);
        params.parameter("bands").setValue(new int[]{0,2});

        final Process process = desc.createProcess(params);
        final ParameterValueGroup result = process.call();

        //check result image
        final RenderedImage outImage = (RenderedImage) result.parameter("result").getValue();
        final SampleModel outSampleModel = outImage.getSampleModel();
        assertEquals(inputImage.getWidth(), outImage.getWidth());
        assertEquals(inputImage.getHeight(), outImage.getHeight());
        assertEquals(2, outSampleModel.getNumBands());
        assertEquals(inSampleModel.getDataType(), outSampleModel.getDataType());

        //check values
        final Raster outRaster = outImage.getData();
        final int[] sample = new int[2];
        final int[] color1 = new int[]{100,50};
        final int[] color2 = new int[]{80, 10};
        for(int y=0;y<100;y++){
            for(int x=0;x<100;x++){
                outRaster.getPixel(x, y, sample);
                if(x<50){
                    assertArrayEquals("coord "+x+","+y,color1, sample);
                }else{
                    assertArrayEquals("coord "+x+","+y,color2, sample);
                }
            }
        }
    }

    /**
     * Generate more bands then source image using -1 index.
     */
    @Test
    public void expandTest() throws Exception{

        final BufferedImage inputImage = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics2D g = inputImage.createGraphics();
        g.setColor(new Color(100, 30, 50));
        g.fillRect(0, 0, 50, 100);
        g.setColor(new Color(80, 200, 10));
        g.fillRect(50, 0, 50, 100);

        final SampleModel inSampleModel = inputImage.getSampleModel();

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, BandSelectDescriptor.NAME);
        assertNotNull(desc);

        final ParameterValueGroup params = desc.getInputDescriptor().createValue();
        params.parameter("image").setValue(inputImage);
        params.parameter("bands").setValue(new int[]{-1,0,-1,2,-1,-1});

        final Process process = desc.createProcess(params);
        final ParameterValueGroup result = process.call();

        //check result image
        final RenderedImage outImage = (RenderedImage) result.parameter("result").getValue();
        final SampleModel outSampleModel = outImage.getSampleModel();
        assertEquals(inputImage.getWidth(), outImage.getWidth());
        assertEquals(inputImage.getHeight(), outImage.getHeight());
        assertEquals(6, outSampleModel.getNumBands());
        assertEquals(inSampleModel.getDataType(), outSampleModel.getDataType());

        //check values
        final Raster outRaster = outImage.getData();
        final int[] sample = new int[6];
        final int[] color1 = new int[]{0,100,0,50,0,0};
        final int[] color2 = new int[]{0,80,0,10,0,0};
        for(int y=0;y<100;y++){
            for(int x=0;x<100;x++){
                outRaster.getPixel(x, y, sample);
                if(x<50){
                    assertArrayEquals("coord "+x+","+y,color1, sample);
                }else{
                    assertArrayEquals("coord "+x+","+y,color2, sample);
                }
            }
        }

    }
}
