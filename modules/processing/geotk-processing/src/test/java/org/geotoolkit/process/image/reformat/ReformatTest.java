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
package org.geotoolkit.process.image.reformat;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.junit.Test;
import org.opengis.util.NoSuchIdentifierException;
import static org.junit.Assert.*;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Test reformat process.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class ReformatTest {
    
    private static final double DELTA = 0.00000001;
    
    @Test
    public void testIntToDouble() throws NoSuchIdentifierException, ProcessException{
        
        final BufferedImage inputImage = new BufferedImage(100, 20, BufferedImage.TYPE_3BYTE_BGR);
        final Graphics2D g = inputImage.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 50, 10);
        g.setColor(Color.BLUE);
        g.fillRect(50, 0, 50, 10);
        g.setColor(Color.GREEN);
        g.fillRect(0, 10, 50, 10);
        g.setColor(Color.BLACK);
        g.fillRect(50, 10, 50, 10);
        
        final SampleModel inSampleModel = inputImage.getSampleModel();
        
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("image", "reformat");
        assertNotNull(desc);
        
        final ParameterValueGroup params = desc.getInputDescriptor().createValue();
        params.parameter("image").setValue(inputImage);
        params.parameter("datatype").setValue(DataBuffer.TYPE_DOUBLE);
        
        final Process process = desc.createProcess(params);
        final ParameterValueGroup result = process.call();
        
        //check result coverage
        final RenderedImage outImage = (RenderedImage) result.parameter("result").getValue();
        final SampleModel outSampleModel = outImage.getSampleModel();
        assertEquals(inputImage.getWidth(), outImage.getWidth());
        assertEquals(inputImage.getHeight(), outImage.getHeight());
        assertEquals(inSampleModel.getNumBands(), outSampleModel.getNumBands());
        assertEquals(DataBuffer.TYPE_DOUBLE, outSampleModel.getDataType());
        assertFalse(inSampleModel.getDataType() == outSampleModel.getDataType());
        
        //check values
        final Raster outRaster = outImage.getData();
        final double[] sample = new double[3];
        final double[] red = new double[]{255,0,0};
        final double[] blue = new double[]{0,0,255};
        final double[] green = new double[]{0,255,0};
        final double[] black = new double[]{0,0,0};
        for(int y=0;y<20;y++){
            for(int x=0;x<100;x++){
                outRaster.getPixel(x, y, sample);
                if(x<50){
                    assertArrayEquals("coord "+x+","+y,(y<10)?red:green, sample,DELTA);
                }else{
                    assertArrayEquals("coord "+x+","+y,(y<10)?blue:black, sample,DELTA);
                }
            }
        }
        
    }
    
}
