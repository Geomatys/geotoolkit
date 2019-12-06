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
package org.geotoolkit.display2d.style;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.coverage.GridCoverage2D;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.util.FactoryException;

/**
 * Test that raster symbolizer are properly rendered.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class RasterSymbolizerTest extends org.geotoolkit.test.TestBase {

    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    protected static final FilterFactory FF = DefaultFactories.forBuildin(FilterFactory.class);

    /**
     * Render a coverage with :
     * - 3 sample dimensions R,G,B
     * - 1 byte per sample
     * - Component color model
     * - 1 raster, not tiled
     */
    @Test
    public void renderRGB8BitsCoverage() throws FactoryException, PortrayalException {

        final BufferedImage image = new BufferedImage(360, 180, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, Color.RED.getRGB());
        image.setRGB(359, 0, Color.GREEN.getRGB());
        image.setRGB(0, 179, Color.BLUE.getRGB());

        final GridExtent extent = new GridExtent(360, 180);
        final AffineTransform2D gridToCrs = new AffineTransform2D(1, 0, 0, 1, 0, 0);
        final GridGeometry grid = new GridGeometry(extent, PixelInCell.CELL_CENTER, gridToCrs, CommonCRS.WGS84.normalizedGeographic());

        /*
         * We volontarely name samples 1,2,3 to avoid use of names as a hint.
         */
        final SampleDimension red = new SampleDimension.Builder().setName("1").build();
        final SampleDimension green = new SampleDimension.Builder().setName("2").build();
        final SampleDimension blue = new SampleDimension.Builder().setName("3").build();

        final GridCoverage2D coverage = new GridCoverage2D(grid, Arrays.asList(red,green,blue), image);

        final MapContext context = MapBuilder.createContext();
        context.layers().add(MapBuilder.createCoverageLayer(new InMemoryGridCoverageResource(coverage)));

        final CanvasDef cdef = new CanvasDef(grid);
        final SceneDef sdef = new SceneDef(context);
        final BufferedImage result = DefaultPortrayalService.portray(cdef, sdef);
        assertEquals(Color.RED.getRGB(),   result.getRGB(0, 0));
        assertEquals(Color.GREEN.getRGB(), result.getRGB(359, 0));
        assertEquals(Color.BLUE.getRGB(),  result.getRGB(0, 179));
    }

    /**
     * Check proper image reprojection in UTM
     */
    @Ignore
    @Test
    public void UTM32632Test() throws Exception{

        final BufferedImage img = new BufferedImage(120, 90, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.GREEN);
        g2d.fillRect(0, 0, 120, 90);

         //set it's envelope
        final GeneralEnvelope gridEnv = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        gridEnv.setRange(0, 0, 120);
        gridEnv.setRange(1, 0, 90);

        //create the coverage
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setEnvelope(gridEnv);
        gcb.setName("myCoverage");
        gcb.setRenderedImage(img);
        final GridCoverage coverage = gcb.getGridCoverage2D();


        final MapContext context = MapBuilder.createContext();
        final MapLayer cl = MapBuilder.createCoverageLayer(coverage, SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), "coverage");
        context.layers().add(cl);

        final GeneralEnvelope env = new GeneralEnvelope(CRS.forCode("EPSG:32632"));
        env.setRange(0, -2574823.6832217844, 5487970.783439655);
        env.setRange(1, 4289777.45228916, 1.0491927042028729E7);

        final Hints hints = new Hints();
        final SceneDef scenedef = new SceneDef(context,hints);
        final CanvasDef canvasdef = new CanvasDef(new Dimension(800, 800), env);
        canvasdef.setBackground(Color.WHITE);

        final BufferedImage buffer = DefaultPortrayalService.portray(canvasdef, scenedef);
        ImageIO.write(buffer, "PNG", new File("test.png"));

        //We should obtain a green triangle crossing the image looking like this :
        //
        // |\
        // |_\
        //we can't test the shape so we test we found more and more green pixels on each line

        //we expect to have a blue label at the center of the image
        final int[] pixel = new int[4];
        final int[] green = new int[]{0,255,0,255};

        int nbGreen = 0;

        final Raster raster = buffer.getData();
        for(int y=0; y<800;y++){
            int nb = 0;

            for(int x=0;x<800;x++){
                raster.getPixel(x, y, pixel);
                if(Arrays.equals(green, pixel)){
                    nb++;
                }
            }

            assertTrue("expected at least one green pixel", nb>0);
            assertTrue(nb >= nbGreen);
            nbGreen = nb;
        }

    }


}
