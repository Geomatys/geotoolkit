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
package org.geotoolkit.processing.image.bandcombine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

/**
 * Image band combine test.
 *
 * @author Johann Sorel (Geomatys)
 */
public class BandCombineTest extends org.geotoolkit.test.TestBase {

    @Test
    public void combineTest() throws Exception{

        final RenderedImage img1 = create(BufferedImage.TYPE_3BYTE_BGR, Color.RED, Color.BLACK);
        final RenderedImage img2 = create(BufferedImage.TYPE_4BYTE_ABGR, Color.BLUE, Color.GREEN);
        final RenderedImage img3 = create(BufferedImage.TYPE_3BYTE_BGR, Color.GREEN, Color.RED);


        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, BandCombineDescriptor.NAME);
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

    @Test
    public void rgbCombineText() throws NoSuchIdentifierException, ProcessException{
        final int WIDTH = 10;
        final int HEIGHT = 10;
        final int SIZE = WIDTH*HEIGHT;

        final byte[] redTable = new byte[SIZE];
        final byte[] greenTable = new byte[SIZE];
        final byte[] blueTable = new byte[SIZE];

        //set colors
        for (int j = 0; j < HEIGHT / 3; j++) {
            for (int i = 0; i < WIDTH; i++) {
                redTable[j * WIDTH + i] = (byte) 127;
            }
        }

        for (int j = HEIGHT / 3; j < (HEIGHT - HEIGHT / 3); j++) {
            for (int i = 0; i < WIDTH; i++) {
                redTable[j * WIDTH + i] = (byte) 127;
                greenTable[j * WIDTH + i] = (byte) 127;
            }
        }

        for (int j = (HEIGHT - HEIGHT / 3); j < HEIGHT; j++) {
            for (int i = 0; i < WIDTH; i++) {
                greenTable[j * WIDTH + i] = (byte) 127;
            }
        }

        DataBuffer buffer = new DataBufferByte(redTable, SIZE);
        WritableRaster raster = WritableRaster.createBandedRaster(buffer, WIDTH, HEIGHT, WIDTH, new int[1], new int[1], new Point(0, 0));
        BufferedImage red = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        red.setData(raster);

        buffer = new DataBufferByte(greenTable, SIZE);
        raster = WritableRaster.createBandedRaster(buffer, WIDTH, HEIGHT, WIDTH, new int[1], new int[1], new Point(0, 0));
        BufferedImage green = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        green.setData(raster);

        buffer = new DataBufferByte(blueTable, SIZE);
        raster = WritableRaster.createBandedRaster(buffer, WIDTH, HEIGHT, WIDTH, new int[1], new int[1], new Point(0, 0));
        BufferedImage blue = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        blue.setData(raster);


        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, BandCombineDescriptor.NAME);
        assertNotNull(desc);

        final ParameterValueGroup params = desc.getInputDescriptor().createValue();
        params.parameter("images").setValue(new RenderedImage[]{red,green,blue});

        final Process process = desc.createProcess(params);
        final ParameterValueGroup result = process.call();

        //check result image
        final RenderedImage outImage = (RenderedImage) result.parameter("result").getValue();
        assertNotNull(outImage);

        //expect a rgb color model

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
