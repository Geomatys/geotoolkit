/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.process.image.dynamicrange;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.image.BufferedImages;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DynamicRangeStretchTest {
 
    private static final double DELTA = 0.1;
    
    @Test
    public void stretchTest() throws Exception{
        
        final BufferedImage inputImage = BufferedImages.createImage(1, 6, 2, DataBuffer.TYPE_DOUBLE);
        final WritableRaster inputRaster = inputImage.getRaster();
        inputRaster.setPixel(0, 0, new double[]{-9,-5});
        inputRaster.setPixel(0, 1, new double[]{10, 0});
        inputRaster.setPixel(0, 2, new double[]{20,10});
        inputRaster.setPixel(0, 3, new double[]{28,41});
        inputRaster.setPixel(0, 4, new double[]{12,8});
        inputRaster.setPixel(0, 5, new double[]{Double.NaN,Double.NaN});
        
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("image", "dynamicrangestretch");
        assertNotNull(desc);
        
        final ParameterValueGroup params = desc.getInputDescriptor().createValue();
        params.parameter("image").setValue(inputImage);
                                                    //R  G  B  A
        params.parameter("bands").setValue(new int[]{-1, 0, 1,-1});
        params.parameter("ranges").setValue(new double[][]{{},{10,20},{0,10},{}});
        
        final Process process = desc.createProcess(params);
        final ParameterValueGroup result = process.call();
        
        //check result image
        final RenderedImage outImage = (RenderedImage) result.parameter("result").getValue();
        assertEquals(inputImage.getWidth(), outImage.getWidth());
        assertEquals(inputImage.getHeight(), outImage.getHeight());
        
        
        //check values
        final double[] pixel = new double[4];
        final Raster outRaster = outImage.getData();
                                      // R   G   B   A
        assertArrayEquals(new double[]{0,  0,  0,255}, outRaster.getPixel(0, 0, pixel), DELTA);
        assertArrayEquals(new double[]{0,  0,  0,255}, outRaster.getPixel(0, 1, pixel), DELTA);
        assertArrayEquals(new double[]{0,255,255,255}, outRaster.getPixel(0, 2, pixel), DELTA);
        assertArrayEquals(new double[]{0,255,255,255}, outRaster.getPixel(0, 3, pixel), DELTA);
        assertArrayEquals(new double[]{0, 51,204,255}, outRaster.getPixel(0, 4, pixel), DELTA);
        assertArrayEquals(new double[]{0,  0,  0,  0}, outRaster.getPixel(0, 5, pixel), DELTA);
                
    }
    
}
