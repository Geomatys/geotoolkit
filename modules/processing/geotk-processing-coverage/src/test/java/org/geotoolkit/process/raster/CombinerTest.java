/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.process.raster;

import java.awt.Point;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import static org.junit.Assert.*;
import org.junit.*;
import org.opengis.coverage.Coverage;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class CombinerTest {

    static final int WIDTH = 10;
    static final int HEIGHT = 10;
    static final int SIZE = WIDTH*HEIGHT;

    public CombinerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testProcess() {
        byte[] redTable = new byte[SIZE];
        byte[] greenTable = new byte[SIZE];
        byte[] blueTable = new byte[SIZE];

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
        BufferedImage result = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        result.setData(raster);
        Coverage red = createCoverage(result);

        buffer = new DataBufferByte(greenTable, SIZE);
        raster = WritableRaster.createBandedRaster(buffer, WIDTH, HEIGHT, WIDTH, new int[1], new int[1], new Point(0, 0));
        result = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        result.setData(raster);
        Coverage green = createCoverage(result);

        buffer = new DataBufferByte(blueTable, SIZE);
        raster = WritableRaster.createBandedRaster(buffer, WIDTH, HEIGHT, WIDTH, new int[1], new int[1], new Point(0, 0));
        result = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        result.setData(raster);
        Coverage blue = createCoverage(result);

        //get the description of the process we want
        ProcessDescriptor desc = null;
        try {
            desc = ProcessFinder.getProcessDescriptor("coverage", "Combiner");
        } catch (NoSuchIdentifierException ex) {
            Logger.getLogger(CombinerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());
        }

        //create a process
        //set the input parameters
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("red").setValue(red);
        input.parameter("green").setValue(green);
        input.parameter("blue").setValue(blue);
        final Process p = desc.createProcess(input);
        ParameterValueGroup output = null;
        try {
            //get the result
            output = p.call();
        } catch (ProcessException ex) {
            Logger.getLogger(CombinerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());
        }

        assertNotNull(output);
        Object res = output.parameter("result").getValue();

        assertNotNull(res);
        assertTrue(res instanceof GridCoverage2D);
        GridCoverage2D toTest = (GridCoverage2D)res;

        assertNotNull(toTest.getRenderedImage());
        assertTrue(toTest.getRenderedImage() instanceof RenderedImage);
        RenderedImage img = (RenderedImage) toTest.getRenderedImage();
        //assertEquals(BufferedImage.TYPE_3BYTE_BGR, img.getDataType());

    }

    private Coverage createCoverage(RenderedImage img){

        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, 0, 100);
        env.setRange(1, 0, 100);

        //prepare coverages
        final GridCoverageBuilder builder = new GridCoverageBuilder();
        builder.setName("7357");
        builder.setEnvelope(env);

        builder.setRenderedImage(img);

        return builder.getGridCoverage2D();
    }
}
