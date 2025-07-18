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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.BufferedGridCoverage;
import org.apache.sis.coverage.grid.DisjointExtentException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverage2D;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.DataType;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.privy.RasterFactory;
import org.apache.sis.referencing.privy.AffineTransform2D;
import org.apache.sis.map.MapLayer;
import org.apache.sis.map.MapLayers;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.canvas.control.NeverFailMonitor;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.style.renderer.AbstractCoverageSymbolizerRenderer;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.image.internal.ImageUtilities;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;

import static java.lang.Double.NaN;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.opengis.coverage.grid.SequenceType;
import org.opengis.filter.Expression;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.coverage.grid.PixelInCell;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.ColorMap;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Symbolizer;
import org.opengis.util.FactoryException;

/**
 * Test that raster symbolizer are properly rendered.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class RasterSymbolizerTest {

    private static final MutableStyleFactory SF = DefaultStyleFactory.provider();
    protected static final FilterFactory FF = FilterUtilities.FF;

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

        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(MapBuilder.createCoverageLayer(new InMemoryGridCoverageResource(coverage)));

        final CanvasDef cdef = new CanvasDef(grid);
        final SceneDef sdef = new SceneDef(context);
        final RenderedImage result = DefaultPortrayalService.portray(cdef, sdef);
        assertEquals(Color.RED.getRGB(),   getRGB(result, 0, 0));
        assertEquals(Color.GREEN.getRGB(), getRGB(result, 359, 0));
        assertEquals(Color.BLUE.getRGB(),  getRGB(result, 0, 179));
    }

    /*
     * Current implementation assume that we have a buffered image.
     * TODO: update this method if this is no longer the case.
     */
    private static int getRGB(RenderedImage image, int x, int y) {
        return ((BufferedImage) image).getRGB(x, y);
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

        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(MapBuilder.createCoverageLayer(new InMemoryGridCoverageResource(coverage)));

        final CanvasDef cdef = new CanvasDef(grid);
        final SceneDef sdef = new SceneDef(context);
        RenderedImage result = DefaultPortrayalService.portray(cdef, sdef);
        assertEquals(Color.RED.getRGB(),   getRGB(result, 0, 0));
        assertEquals(Color.GREEN.getRGB(), getRGB(result, 17, 0));
        assertEquals(Color.BLUE.getRGB(),  getRGB(result, 0, 8));

        // Now, test with a resample (flip axes):
        final GridExtent latLonExtent = new GridExtent(9, 18);
        AffineTransform2D latLonG2C = new AffineTransform2D(20, 0, 0, 20, -80, -170);
        GridGeometry latLonGrid = new GridGeometry(latLonExtent, PixelInCell.CELL_CENTER, latLonG2C, CommonCRS.WGS84.geographic());
        result = DefaultPortrayalService.portray(new CanvasDef(latLonGrid), sdef);
        assertEquals(Color.RED.getRGB(),   getRGB(result, 0, 0));
        assertEquals(Color.GREEN.getRGB(), getRGB(result, 0, 17));
        assertEquals(Color.BLUE.getRGB(),  getRGB(result, 8, 0));

        latLonG2C = new AffineTransform2D(-20, 0, 0, 20, 80, -170);
        latLonGrid = new GridGeometry(latLonExtent, PixelInCell.CELL_CENTER, latLonG2C, CommonCRS.WGS84.geographic());
        result = DefaultPortrayalService.portray(new CanvasDef(latLonGrid), sdef);
        assertEquals(Color.RED.getRGB(),   getRGB(result, 8, 0));
        assertEquals(Color.GREEN.getRGB(), getRGB(result, 8, 17));
        assertEquals(Color.BLUE.getRGB(),  getRGB(result, 0, 0));

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

        final MapLayers context = MapBuilder.createContext();
        final MapLayer cl = MapBuilder.createCoverageLayer(coverage, SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), "coverage");
        context.getComponents().add(cl);

        final GeneralEnvelope env = new GeneralEnvelope(CRS.forCode("EPSG:32632"));
        env.setRange(0, -2574823.6832217844, 5487970.783439655);
        env.setRange(1, 4289777.45228916, 1.0491927042028729E7);

        final Hints hints = new Hints();
        final SceneDef scenedef = new SceneDef(context,hints);
        final CanvasDef canvasdef = new CanvasDef(new Dimension(800, 800), env);
        canvasdef.setBackground(Color.WHITE);

        final RenderedImage buffer = DefaultPortrayalService.portray(canvasdef, scenedef);
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
        final GridGeometry querygrid = new GridGeometry(queryextent, queryenv, GridOrientation.HOMOTHETY);


        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(MapBuilder.createCoverageLayer(new InMemoryGridCoverageResource(coverage)));

        final RenderedImage nearest;
        final RenderedImage bicubic;
        final RenderedImage lanczos;
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

        int nearestRgb = getRGB(nearest, 179, 0);
        int bicubicRgb = getRGB(bicubic, 179, 0);
        int naczosRgb  = getRGB(lanczos, 179, 0);

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

        // Make a first test using default raster style
        MapLayer layer = MapBuilder.createLayer(new InMemoryGridCoverageResource(baseData));
        final MapLayers ctx = MapBuilder.createContext();
        ctx.getComponents().add(layer);
        RenderedImage rendering = DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(2, 2), geom.getEnvelope()),
                new SceneDef(ctx, new Hints(GO2Hints.KEY_INTERPOLATION, InterpolationCase.NEIGHBOR,
                        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR))
        );

        // As display is oriented upper-left, output should be flipped on y axis.
        // IMPORTANT: The renderer will stretch values along 256 colors, so we have to adapt comparison.
        // As we use default style, 0 to 4 should be scaled approximately as : 0 -> 0, 1 -> 85, 2 -> 170, 3 -> 255.
        // IF TESTED VALUES DIFFERS TOO MUCH, THERE IS A PROBLEM WITH THE DEFAULT STYLE EVALUATION.
        // PLEASE DO NOT HIDE THE PROBLEM BY CHANGING DEFAULT VALUES OR INCREASING DELTA.
        final float[] pixels = rendering.getData().getPixels(0, 0, 2, 2, (float[]) null);
        final float[] expected = {
                255, 255, 255, 255,  170, 170, 170, 255,
                  0,   0,   0, 255,   85, 85, 85, 255
        };
        assertArrayEquals(expected, pixels, 10f);

        // Same test but overriding style with a custom color palette, to ensure the behavior remains consistent
        // whatever applied style
        final ColorMap colorMap = SF.colorMap(SF.interpolateFunction(null,
                Arrays.asList(
                        SF.interpolationPoint(1, FF.literal(Color.RED)),
                        SF.interpolationPoint(2, FF.literal(Color.GREEN)),
                        SF.interpolationPoint(3, FF.literal(Color.BLUE)),
                        SF.interpolationPoint(4, FF.literal(Color.WHITE))
                ),
                null, null, FF.literal(Color.BLACK)));
        final RasterSymbolizer symbol = SF.rasterSymbolizer(null, null, null, null, colorMap, null, null, null);
        ctx.getComponents().set(0, MapBuilder.createCoverageLayer(baseData, SF.style(symbol), "test"));
        rendering = DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(2, 2), geom.getEnvelope()),
                new SceneDef(ctx, new Hints(GO2Hints.KEY_INTERPOLATION, InterpolationCase.NEIGHBOR,
                        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR))
        );

        assertEquals(Color.WHITE.getRGB(), getRGB(rendering, 0, 0));
        assertEquals(Color.BLUE .getRGB(), getRGB(rendering, 1, 0));
        assertEquals(Color.RED  .getRGB(), getRGB(rendering, 0, 1));
        assertEquals(Color.GREEN.getRGB(), getRGB(rendering, 1, 1));
    }

    @Test
    public void disjointCoverageIsPropertyShorted() {
        final BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_GRAY);
        ImageUtilities.fill(image.getRaster(), 42);
        final GridGeometry geom = new GridGeometry(
                new GridExtent(2, 2),
                PixelInCell.CELL_CENTER,
                new AffineTransform2D(1, 0, 0,1, 10, 10),
                CommonCRS.defaultGeographic()
        );

        final GridCoverage baseData = new GridCoverage2D(geom, null, image);

        final GridGeometry canvasGeom = new GridGeometry(
                new GridExtent(5, 5),
                new Envelope2D(CommonCRS.defaultGeographic(), -100, -20, 40, 20),
                GridOrientation.HOMOTHETY
        );

        new MockRasterRenderer(canvasGeom)
                .testObjectiveDisjoint(new InMemoryGridCoverageResource(baseData));
    }

    @Test
    public void renderWithMask() throws Exception {

        final BufferedImage image = new BufferedImage(360, 180, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 360, 180);

        final GridExtent extent = new GridExtent(360, 180);
        final AffineTransform2D gridToCrs = new AffineTransform2D(1, 0, 0, -1, -180, 90);
        final GridGeometry grid = new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCrs, CommonCRS.WGS84.normalizedGeographic());

        final GridCoverage2D coverage = new GridCoverage2D(grid, null, image);
        final GridCoverageResource resource = new InMemoryGridCoverageResource(coverage);

        final GeometryFactory gf = new GeometryFactory();
        final Polygon polygon = gf.createPolygon(new Coordinate[]{
            new Coordinate(0, 0),
            new Coordinate(0, 60),
            new Coordinate(60, 60),
            new Coordinate(60, 0),
            new Coordinate(0, 0)
        });
        polygon.setUserData(CommonCRS.WGS84.normalizedGeographic());

        final MutableStyleFactory sf = GO2Utilities.STYLE_FACTORY;
        final MutableStyle style = sf.style();
        final MutableFeatureTypeStyle fts = sf.featureTypeStyle();
        final MutableRule rule = sf.rule();
        final Expression geomExp = GO2Utilities.FILTER_FACTORY.literal(polygon);
        final RasterSymbolizer rs = sf.rasterSymbolizer(null, geomExp, null, null, null, null, null, null, null, null, null);

        style.featureTypeStyles().add(fts);
        fts.rules().add(rule);
        rule.symbolizers().add(rs);

        final MapLayer layer = new MapLayer();
        layer.setData(resource);
        layer.setStyle(style);


        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(layer);

        final CanvasDef cdef = new CanvasDef(grid);
        final SceneDef sdef = new SceneDef(context);
        final RenderedImage result = DefaultPortrayalService.portray(cdef, sdef);

        final int RED = Color.RED.getRGB();
        assertEquals(0,   getRGB(result, 0,   0));
        assertEquals(0,   getRGB(result, 10,  10));
        assertEquals(0,   getRGB(result, 189, 28));
        assertEquals(RED, getRGB(result, 189, 31));
        assertEquals(0,   getRGB(result, 178, 66));
        assertEquals(RED, getRGB(result, 181, 66));
        assertEquals(0,   getRGB(result, 241, 66));
        assertEquals(RED, getRGB(result, 238, 66));
        assertEquals(0,   getRGB(result, 219, 91));
        assertEquals(RED, getRGB(result, 219, 88));
        assertEquals(RED, getRGB(result, 209, 58));
    }

    private static RenderingContext2D mockRenderingContext(final GridGeometry target) {
        final RenderingContext2D ctx = new RenderingContext2D(target, new NeverFailMonitor());
        return ctx;
    }

    private static class MockRasterRenderer extends AbstractCoverageSymbolizerRenderer {

        public MockRasterRenderer(final GridGeometry target) {
            super(null, new CachedSymbolizer<Symbolizer>(new MokSymbolizer() {}, null) {
                @Override
                public float getMargin(Object candidate, RenderingContext2D ctx) {
                    return Float.NaN;
                }

                @Override
                protected void evaluate() {
                    throw new UnsupportedOperationException("Not supported yet");
                }

                @Override
                public boolean isVisible(Object candidate) {
                    throw new UnsupportedOperationException("Not supported yet");
                }
            }, mockRenderingContext(target));
        }

        public void testObjectiveDisjoint(final GridCoverageResource data) {
            try {
                getObjectiveCoverage(data, renderingContext.getGridGeometry(), false, null);
                fail("objective data computing should have failed with a disjoint extent error.");
            } catch (DisjointExtentException e) {
                // Expected outcome
            } catch (Exception e) {
                throw new AssertionError("Rendering should have been shorted silently, but failed instead", e);
            }
        }
    }

    /**
     * Anti-regression test that ensures margins are properly applied on the following corner-case:
     *
     *   - Input resource does not provide full resolution information
     *   - Input resource strictly enforces requested domain on read
     *   - An edge of the requested rendering intersects a small part of an input pixel
     *     (Therefore, this problem is easier to trigger on very low-resolution datasets)
     *
     * To trigger this behavior, the test creates a coverage with a partly undefined vertical component,
     * and overrides in-memory coverage `read` mathod  to enforce strictly input domain.
     *
     * This happened with real NetCDF datasets.
     */
    @Test
    public void testRenderOverlappingCellsWithMassiveUpscale() throws Exception {
        // Prepare test data: an image with only two pixels on top of each other,
        // a red pixel over a green pixel.
        final BufferedImage image = new BufferedImage(1, 2, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 1, 1);
        g.setColor(Color.GREEN);
        g.fillRect(0, 1, 1, 1);

        // Setup data domain: latitude, longitude (red pixel west of greenwich, green pixel on the east).
        // The important part is that time range is partly undefined, to force undefined resolution.
        var dataEnv = new GeneralEnvelope(CommonCRS.WGS84.geographic3D());
        dataEnv.setRange(0, -90, 90);
        dataEnv.setRange(1, -180, 180);
        dataEnv.setRange(2, 0, NaN);
        final GridGeometry grid = new GridGeometry(
                new GridExtent(null, new long[3], new long[]{0, 1, 0}, true),
                dataEnv,
                GridOrientation.DISPLAY
        );

        final GridCoverage2D coverage = new GridCoverage2D(grid, null, image);
        final GridCoverageResource resource = new StrictInMemoryGridCoverageResource(coverage);

        final MapLayer layer = new MapLayer();
        layer.setData(resource);
        layer.setStyle(SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER));
        layer.setVisible(true);
        layer.setOpacity(1.0);

        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(layer);

        // Request a rendering that intersects only a small part of the red pixel of input data
        var canvasEnvelope = new GeneralEnvelope(CommonCRS.WGS84.geographic3D());
        canvasEnvelope.setRange(0, -45, 45);
        canvasEnvelope.setRange(1, -11.25, 180 - 11.25);
        canvasEnvelope.setRange(2, -1, 1);
        // Draw a square intersecting both pixels from source, upscaled.
        final CanvasDef cdef = new CanvasDef(new GridGeometry(
                new GridExtent(null, new long[3], new long[]{255, 255, 0}, true),
                canvasEnvelope,
                // Envelopes.transform(new Envelope2D(CommonCRS.defaultGeographic(), -11.25, -45, 180, 90), CRS.forCode("EPSG:3395")),
                GridOrientation.DISPLAY
        ));
        final SceneDef sdef = new SceneDef(context);
        final RenderedImage result = DefaultPortrayalService.portray(cdef, sdef);

        // Ensure the small part intersecting the red pixel is drawn correctly
        var b = new PixelIterator.Builder();
        b.setRegionOfInterest(new Rectangle(0, 0, 256, 16));
        var it = b.create(result);
        var nbTestedPixels = 0;
        var buffer = new int[4];
        var expectedColor = new int[]{ 255, 0, 0, 255 };
        while (it.next()) {
            it.getPixel(buffer);
            assertArrayEquals("Pixel "+it.getPosition()+" should be red", expectedColor, buffer);
            nbTestedPixels++;
        }
        assertEquals("Number of tested pixels should match a rectangle of 256 * 16 pixels", 256 * 16, nbTestedPixels);

        // Test the remaining pixels, they should be green
        b = new PixelIterator.Builder();
        b.setRegionOfInterest(new Rectangle(0, 16, 256, 240));
        it = b.create(result);
        nbTestedPixels = 0;
        expectedColor = new int[]{ 0, 255, 0, 255 };
        while (it.next()) {
            it.getPixel(buffer);
            assertArrayEquals("Pixel "+it.getPosition()+" should be green", expectedColor, buffer);
            nbTestedPixels++;
        }
        assertEquals("Number of tested pixels should match a square of 256 * 240 pixels", 256 * 240, nbTestedPixels);
    }

    @Test
    public void RenderLocalWrapAround() throws Exception {
        var sourceImg = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        var g = sourceImg.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 1, 1);
        g.setColor(Color.RED);
        g.fillRect(1, 0, 1, 1);
        g.setColor(Color.GREEN);
        g.fillRect(1, 1, 1, 1);
        g.setColor(Color.BLUE);
        g.fillRect(0, 1, 1, 1);
        g.dispose();

        final GridGeometry grid = new GridGeometry(
                new GridExtent(2, 2),
                new Envelope2D(CommonCRS.defaultGeographic(), 170, -10, 20, 20),
                GridOrientation.DISPLAY
        );
        assertEquals(
                "Envelope have been normalized and do not cross anti-meridian as expected",
                190d,
                grid.getEnvelope().getMaximum(0),
                1e-1
        );
        var resource = new StrictInMemoryGridCoverageResource(new GridCoverage2D(grid, null, sourceImg));

        final MapLayer layer = new MapLayer();
        layer.setData(resource);
        layer.setStyle(SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER));

        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(layer);

        // Draw entire world with "conventional" boundaries (-180 to 180). Expects a piece of the coverage on each bound.
        var cdef = new CanvasDef(new GridGeometry(
                new GridExtent(36, 18),
                new Envelope2D(CommonCRS.defaultGeographic(), -180, -90, 360, 180),
                // Envelopes.transform(new Envelope2D(CommonCRS.defaultGeographic(), -11.25, -45, 180, 90), CRS.forCode("EPSG:3395")),
                GridOrientation.DISPLAY
        ));
        var sdef = new SceneDef(context);
        var result = DefaultPortrayalService.portray(cdef, sdef);
        var expectedImg = new BufferedImage(36, 18, BufferedImage.TYPE_INT_ARGB);
        g = expectedImg.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(35, 8, 1, 1);
        g.setColor(Color.RED);
        g.fillRect(0, 8, 1, 1);
        g.setColor(Color.GREEN);
        g.fillRect(0, 9, 1, 1);
        g.setColor(Color.BLUE);
        g.fillRect(35, 9, 1, 1);
        g.dispose();

        assertImageEquals(expectedImg, result);

        // Draw world in a 0 to 360 context
        cdef = new CanvasDef(new GridGeometry(
                new GridExtent(36, 18),
                new Envelope2D(CommonCRS.defaultGeographic(), 0, -90, 360, 180),
                GridOrientation.DISPLAY
        ));
        result = DefaultPortrayalService.portray(cdef, sdef);
        expectedImg = new BufferedImage(36, 18, BufferedImage.TYPE_INT_ARGB);
        g = expectedImg.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(17, 8, 1, 1);
        g.setColor(Color.RED);
        g.fillRect(18, 8, 1, 1);
        g.setColor(Color.GREEN);
        g.fillRect(18, 9, 1, 1);
        g.setColor(Color.BLUE);
        g.fillRect(17, 9, 1, 1);
        g.dispose();

        assertImageEquals(expectedImg, result);

        // Partially draw upscaled image
        cdef = new CanvasDef(new GridGeometry(
                new GridExtent(32, 32),
                new Envelope2D(CommonCRS.defaultGeographic(), 179, -3, 4, 4),
                GridOrientation.DISPLAY
        ));
        result = DefaultPortrayalService.portray(cdef, sdef);
        expectedImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        g = expectedImg.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 8, 8);
        g.setColor(Color.RED);
        g.fillRect(8, 0, 24, 8);
        g.setColor(Color.GREEN);
        g.fillRect(8, 8, 24, 24);
        g.setColor(Color.BLUE);
        g.fillRect(0, 8, 8, 24);
        g.dispose();

        assertImageEquals(expectedImg, result);

        // Imply a simple reprojection: axis inversion
        cdef = new CanvasDef(new GridGeometry(
                new GridExtent(2, 2),
                grid.getEnvelope(CommonCRS.WGS84.geographic()),
                GridOrientation.DISPLAY
        ));
        result = DefaultPortrayalService.portray(cdef, sdef);
        expectedImg = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        g = expectedImg.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 1, 1);
        g.setColor(Color.RED);
        g.fillRect(0, 1, 1, 1);
        g.setColor(Color.GREEN);
        g.fillRect(1, 1, 1, 1);
        g.setColor(Color.BLUE);
        g.fillRect(1, 0, 1, 1);
        g.dispose();

        assertImageEquals(expectedImg, result);

        // Imply a more complex reprojection:
        cdef = new CanvasDef(new GridGeometry(
                new GridExtent(2, 2),
                grid.getEnvelope(CRS.forCode("EPSG:3857")),
                GridOrientation.DISPLAY
        ));
        result = DefaultPortrayalService.portray(cdef, sdef);
        expectedImg = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        g = expectedImg.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 1, 1);
        g.setColor(Color.RED);
        g.fillRect(1, 0, 1, 1);
        g.setColor(Color.GREEN);
        g.fillRect(1, 1, 1, 1);
        g.setColor(Color.BLUE);
        g.fillRect(0, 1, 1, 1);
        g.dispose();

        assertImageEquals(expectedImg, result);
    }

    /**
     * When a dataset has multiple dimensions, we want to ensure that the renderer will choose an arbitrary slice
     * if user has not specified which one it wants.
     */
    @Test
    public void testMultiDimensionalDefaultSlicing() throws Exception {
        // Test a datastore that strictly return what is requested upon read
        testMultiDimensionalDefaultSlicing(true);
        // Test a datastore that returns more content on read.
        // In this case, it will return multiple slices.
        // It means this case test the ability of the renderer to bypass datastore filtering issues.
        testMultiDimensionalDefaultSlicing(false);
    }

    private void testMultiDimensionalDefaultSlicing(boolean readStrict) throws Exception {

        var dataEnv = new GeneralEnvelope(CRS.compound(
                CommonCRS.defaultGeographic(),
                CommonCRS.Vertical.ELLIPSOIDAL.crs(),
                CommonCRS.Temporal.JAVA.crs()
        ));
        dataEnv.setRange(0, 10, 20);
        dataEnv.setRange(1, 30, 40);
        dataEnv.setRange(2, 0, 2);
        dataEnv.setRange(3, 0, 2);
        var dataGrid = new GridGeometry(
                new GridExtent(null, new long[4], new long[] { 2, 2, 2, 2 }, false),
                dataEnv,
                GridOrientation.DISPLAY
        );

        var dataBuffer = ByteBuffer.wrap(new byte[] {
                // v0 t0
                0, 0,
                2, 3,
                // v1 t0
                1, 0,
                2, 3,
                // v0 t1
                0, 1,
                2, 3,
                // v1 t1
                1, 1,
                2, 3
        });

        var coverage = new BufferedGridCoverage(
                dataGrid,
                List.of(new SampleDimension.Builder().setName("test").build()),
                RasterFactory.wrap(DataType.BYTE, dataBuffer)
        );
        var resource = readStrict ? new StrictInMemoryGridCoverageResource(coverage)
                                  : new InMemoryGridCoverageResource(coverage);

        final MapLayer layer = new MapLayer();
        layer.setData(resource);
        layer.setStyle(SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER));

        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(layer);

        final
        // Draw entire world with "conventional" boundaries (-180 to 180). Expects a piece of the coverage on each bound.
        var cdef = new CanvasDef(new GridGeometry(
                new GridExtent(2, 2),
                new Envelope2D(CommonCRS.defaultGeographic(), 10, 30, 10, 10),
                GridOrientation.DISPLAY
        ));
        var sdef = new SceneDef(context);
        var result = DefaultPortrayalService.portray(cdef, sdef);
        assertEquals(2, result.getWidth());
        assertEquals(2, result.getHeight());
        var raster = result.getData();
        // Input image bottom row should contain exactly the same samples as repeated on data slices (see data buffer above)
        assertEquals(2, raster.getSample(0, 1, 0));
        assertEquals(3, raster.getSample(1, 1, 0));
    }

    private static void assertImageEquals(final RenderedImage expected, final RenderedImage actual) {
        var pixBuilder = new PixelIterator.Builder();
        pixBuilder.setIteratorOrder(SequenceType.LINEAR);
        var expectedPix = pixBuilder.create(expected);
        var actualPix = pixBuilder.create(actual);
        assertEquals("Number of bands differ !", expectedPix.getNumBands(), actualPix.getNumBands());
        assertEquals("Image domains (origin and dimension) differ !", expectedPix.getDomain(), actualPix.getDomain());
        var expectedBuf = new int[expectedPix.getNumBands()];
        var actualBuf = new int[actualPix.getNumBands()];
        while (expectedPix.next()) {
            assertTrue(actualPix.next());
            assertArrayEquals("Colors are not equal on pixel "+expectedPix.getPosition(), expectedPix.getPixel(expectedBuf), actualPix.getPixel(actualBuf));
        }
    }

    /**
     * Force requested domain on {@link #read(GridGeometry, int...)}.
     * It is necessary to mimic behavior of some datastores, that crop input data to match user region of interest.
     */
    private static class StrictInMemoryGridCoverageResource extends InMemoryGridCoverageResource {

        public StrictInMemoryGridCoverageResource(final GridCoverage source) {
            super(source);
        }

        @Override
        public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
            assert range == null : "band selection not supported for this test";
            var coverage = super.read(domain, range);
            if (domain == null) return coverage;
            try {
                return new GridCoverageProcessor().resample(coverage, domain);
            } catch (TransformException e) {
                throw new DataStoreException("Cannot resample source data", e);
            }
        }
    }
}
