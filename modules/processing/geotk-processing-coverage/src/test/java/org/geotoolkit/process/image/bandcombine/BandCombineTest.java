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
package org.geotoolkit.process.image.bandcombine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.Process;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Image band combine test.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class BandCombineTest {
 
    @Test
    public void combineTest() throws Exception{
        
        final RenderedImage img1 = create(BufferedImage.TYPE_3BYTE_BGR, Color.RED, Color.BLACK);
        final RenderedImage img2 = create(BufferedImage.TYPE_4BYTE_ABGR, Color.BLUE, Color.GREEN);
        final RenderedImage img3 = create(BufferedImage.TYPE_3BYTE_BGR, Color.GREEN, Color.RED);
        
        
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("image", "bandcombine");
        assertNotNull(desc);
        
        final ParameterValueGroup params = desc.getInputDescriptor().createValue();
        params.parameter("images").setValue(new RenderedImage[]{img1,img2,img3});
        
        final Process process = desc.createProcess(params);
        final ParameterValueGroup result = process.call();
        
        //check result image
        final RenderedImage outImage = (RenderedImage) result.parameter("result").getValue();
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
    
    private static RenderedImage create(int type, Color color1, Color color2){
        final BufferedImage inputImage = new BufferedImage(100, 100, type);
        final Graphics2D g = inputImage.createGraphics();
        g.setColor(color1);
        g.fillRect(0, 0, 100, 50);
        g.setColor(color2);
        g.fillRect(0, 50, 100, 50);
        return inputImage;
    }
    
}
