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
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverage2D;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.style.ColorMap;
import org.opengis.style.RasterSymbolizer;
import org.opengis.util.FactoryException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
     * Render a coverage with :
     * - 3 sample dimensions R,G,B
     * - 1 byte indexed color model
     * - 1 raster, not tiled
     */
    @Test
    public void renderRGBIndexedCoverage() throws FactoryException, PortrayalException {

        final BufferedImage image = new BufferedImage(18, 9, BufferedImage.TYPE_BYTE_INDEXED);
        image.setRGB(0, 0, Color.RED.getRGB());
        image.setRGB(17, 0, Color.GREEN.getRGB());
        image.setRGB(0, 8, Color.BLUE.getRGB());

        final GridExtent extent = new GridExtent(18, 9);
        final AffineTransform2D gridToCrs = new AffineTransform2D(20, 0, 0, 20, -170, -80);
        final GridGeometry grid = new GridGeometry(extent, PixelInCell.CELL_CENTER, gridToCrs, CommonCRS.WGS84.normalizedGeographic());

        /*
         * We volontarely name samples 1 to avoid use of names as a hint.
         */
        final SampleDimension rgb = new SampleDimension.Builder().setName("1").build();

        final GridCoverage2D coverage = new GridCoverage2D(grid, Arrays.asList(rgb), image);

        final MapContext context = MapBuilder.createContext();
        context.layers().add(MapBuilder.createCoverageLayer(new InMemoryGridCoverageResource(coverage)));

        final CanvasDef cdef = new CanvasDef(grid);
        final SceneDef sdef = new SceneDef(context);
        BufferedImage result = DefaultPortrayalService.portray(cdef, sdef);
        assertEquals(Color.RED.getRGB(),   result.getRGB(0, 0));
        assertEquals(Color.GREEN.getRGB(), result.getRGB(17, 0));
        assertEquals(Color.BLUE.getRGB(),  result.getRGB(0, 8));

        // Now, test with a resample (flip axes):
        final GridExtent latLonExtent = new GridExtent(9, 18);
        AffineTransform2D latLonG2C = new AffineTransform2D(20, 0, 0, 20, -80, -170);
        GridGeometry latLonGrid = new GridGeometry(latLonExtent, PixelInCell.CELL_CENTER, latLonG2C, CommonCRS.WGS84.geographic());
        result = DefaultPortrayalService.portray(new CanvasDef(latLonGrid), sdef);
        assertEquals(Color.RED.getRGB(),   result.getRGB(0, 0));
        assertEquals(Color.GREEN.getRGB(), result.getRGB(0, 17));
        assertEquals(Color.BLUE.getRGB(),  result.getRGB(8, 0));

        latLonG2C = new AffineTransform2D(-20, 0, 0, 20, 80, -170);
        latLonGrid = new GridGeometry(latLonExtent, PixelInCell.CELL_CENTER, latLonG2C, CommonCRS.WGS84.geographic());
        result = DefaultPortrayalService.portray(new CanvasDef(latLonGrid), sdef);
        assertEquals(Color.RED.getRGB(),   result.getRGB(8, 0));
        assertEquals(Color.GREEN.getRGB(), result.getRGB(8, 17));
        assertEquals(Color.BLUE.getRGB(),  result.getRGB(0, 0));

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
        final GridCoverage coverage = new GridCoverage2D(new GridGeometry(null, gridEnv, GridOrientation.HOMOTHETY), null, img);

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


    /**
     * Render a coverage with nearest and lanczos interpolation.
     */
    @Test
    public void renderInterpolationCoverage() throws FactoryException, PortrayalException, IOException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final BufferedImage image = new BufferedImage(36, 18, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 18, 9);
        g.setColor(Color.GREEN);
        g.fillRect(18, 0, 18, 9);
        g.setColor(Color.BLUE);
        g.fillRect(0, 9, 18, 9);
        g.setColor(Color.YELLOW);
        g.fillRect(18, 9, 18, 9);
        g.dispose();

        final GridExtent extent = new GridExtent(36, 18);
        final AffineTransform2D gridToCrs = new AffineTransform2D(10, 0, 0, -10, -180, 90);
        final GridGeometry grid = new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCrs, crs);

        final SampleDimension red = new SampleDimension.Builder().setName("1").build();
        final SampleDimension green = new SampleDimension.Builder().setName("2").build();
        final SampleDimension blue = new SampleDimension.Builder().setName("3").build();
        final GridCoverage2D coverage = new GridCoverage2D(grid, Arrays.asList(red,green,blue), image);

        final GridExtent queryextent = new GridExtent(360, 180);
        final GeneralEnvelope queryenv = new GeneralEnvelope(crs);
        queryenv.setRange(0, -180, 180);
        queryenv.setRange(1, -90, 90);
        final GridGeometry querygrid = new GridGeometry(queryextent, queryenv);


        final MapContext context = MapBuilder.createContext();
        context.layers().add(MapBuilder.createCoverageLayer(new InMemoryGridCoverageResource(coverage)));

        final BufferedImage nearest;
        final BufferedImage bicubic;
        final BufferedImage lanczos;
        {
            final Hints hints = new Hints();
            hints.put(GO2Hints.KEY_INTERPOLATION, InterpolationCase.NEIGHBOR);
            final CanvasDef cdef = new CanvasDef(querygrid);
            final SceneDef sdef = new SceneDef(context, hints);
            nearest = DefaultPortrayalService.portray(cdef, sdef);
        }
        {
            final Hints hints = new Hints();
            hints.put(GO2Hints.KEY_INTERPOLATION, InterpolationCase.BICUBIC2);
            final CanvasDef cdef = new CanvasDef(querygrid);
            final SceneDef sdef = new SceneDef(context, hints);
            bicubic = DefaultPortrayalService.portray(cdef, sdef);
        }
        {
            final Hints hints = new Hints();
            hints.put(GO2Hints.KEY_INTERPOLATION, InterpolationCase.LANCZOS);
            final CanvasDef cdef = new CanvasDef(querygrid);
            final SceneDef sdef = new SceneDef(context, hints);
            lanczos = DefaultPortrayalService.portray(cdef, sdef);
        }

        int nearestRgb = nearest.getRGB(179, 0);
        int bicubicRgb = bicubic.getRGB(179, 0);
        int naczosRgb = lanczos.getRGB(179, 0);

        assertTrue(nearestRgb != bicubicRgb);
        assertTrue(bicubicRgb != naczosRgb);
    }

    /**
     * Source coverage will be a matrix <em>with origin lower-left</em>:
     * <table>
     *     <tr><td>4</td><td>3</td></tr>
     *     <tr><td>1</td><td>2</td></tr>
     * </table>
     * @throws PortrayalException
     */
    @Test
    public void coverage_whose_grid_origin_is_lower_left_should_be_flipped() throws PortrayalException {
        final BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_GRAY);
        image.getRaster().setSample(0, 0, 0, 1);
        image.getRaster().setSample(1, 0, 0, 2);
        image.getRaster().setSample(1, 1, 0, 3);
        image.getRaster().setSample(0, 1, 0, 4);

        final GridGeometry geom = new GridGeometry(
                new GridExtent(2, 2),
                PixelInCell.CELL_CENTER,
                new AffineTransform2D(1, 0, 0,1, 10, 10),
                CommonCRS.defaultGeographic()
        );
        final GridCoverage baseData = new GridCoverage2D(geom, null, image);

        CoverageMapLayer layer = MapBuilder.createCoverageLayer(baseData);
        final MapContext ctx = MapBuilder.createContext();
        ctx.items().add(layer);
        BufferedImage rendering = DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(2, 2), geom.getEnvelope()),
                new SceneDef(ctx, new Hints(GO2Hints.KEY_INTERPOLATION, InterpolationCase.NEIGHBOR,
                        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR))
        );

        // As display is oriented upper-left, output should be flipped on y axis. Also, the renderer will stretch values
        // along 256 colors, so we have to adapt comparison.
        final int[] pixels = rendering.getRaster().getPixels(0, 0, 2, 2, (int[]) null);
        final int[] expected = {
                255, 255, 255, 255,  165, 165, 165, 255,
                  0,   0,   0, 255,   88,  88,  88, 255
        };
        assertArrayEquals(expected, pixels);

        final ColorMap colorMap = SF.colorMap(SF.interpolateFunction(null,
                Arrays.asList(
                        SF.interpolationPoint(1, FF.literal(Color.RED)),
                        SF.interpolationPoint(2, FF.literal(Color.GREEN)),
                        SF.interpolationPoint(3, FF.literal(Color.BLUE)),
                        SF.interpolationPoint(4, FF.literal(Color.WHITE))
                ),
                null, null, FF.literal(Color.BLACK)));
        final RasterSymbolizer symbol = SF.rasterSymbolizer(null, null, null, null, colorMap, null, null, null);
        ctx.items().set(0, MapBuilder.createCoverageLayer(baseData, SF.style(symbol), "test"));
        rendering = DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(2, 2), geom.getEnvelope()),
                new SceneDef(ctx, new Hints(GO2Hints.KEY_INTERPOLATION, InterpolationCase.NEIGHBOR,
                        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR))
        );

        assertEquals(Color.WHITE.getRGB(), rendering.getRGB(0, 0));
        assertEquals(Color.BLUE.getRGB(), rendering.getRGB(1, 0));
        assertEquals(Color.RED.getRGB(), rendering.getRGB(0, 1));
        assertEquals(Color.GREEN.getRGB(), rendering.getRGB(1, 1));
    }
}
