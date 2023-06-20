/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.display2d;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverage2D;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.portrayal.MapLayers;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.WritableFeatureSet;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.painter.GradiantColorPainter;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.OutputDef;
import org.geotoolkit.display2d.service.PortrayalExtension;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import static org.geotoolkit.style.StyleConstants.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.PointSymbolizer;
import org.opengis.util.FactoryException;

import static org.junit.Assert.*;
import static org.opengis.test.Assert.assertBetween;


/**
 * Testing color model optimisations.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ColorModelTest extends org.geotoolkit.test.TestBase {

    private static final GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();
    private static final MutableStyleFactory SF = new DefaultStyleFactory();

    private final List<FeatureSet> featureColls = new ArrayList<>();
    private final List<GridCoverage> coverages = new ArrayList<>();
    private final List<Envelope> envelopes = new ArrayList<>();
    private final List<Date[]> dates = new ArrayList<>();
    private final List<Double[]> elevations = new ArrayList<>();

    public ColorModelTest() throws Exception {


        // create the feature collection for tests -----------------------------
        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("test");
        sftb.addAttribute(Point.class).setName("geom").setCRS(CommonCRS.WGS84.normalizedGeographic());
        sftb.addAttribute(String.class).setName("att1");
        sftb.addAttribute(Double.class).setName("att2");
        final FeatureType sft = sftb.build();
        WritableFeatureSet col = new InMemoryFeatureSet("id", sft);

        Feature sf1 = sft.newInstance();
        sf1.setPropertyValue("geom", GF.createPoint(new Coordinate(0, 0)));
        sf1.setPropertyValue("att1", "value1");
        Feature sf2 = sft.newInstance();
        sf2.setPropertyValue("geom", GF.createPoint(new Coordinate(-180, -90)));
        sf2.setPropertyValue("att1", "value1");
        Feature sf3 = sft.newInstance();
        sf3.setPropertyValue("geom", GF.createPoint(new Coordinate(-180, 90)));
        sf3.setPropertyValue("att1", "value1");
        Feature sf4 = sft.newInstance();
        sf4.setPropertyValue("geom", GF.createPoint(new Coordinate(180, -90)));
        sf4.setPropertyValue("att1", "value1");
        Feature sf5 = sft.newInstance();
        sf5.setPropertyValue("geom", GF.createPoint(new Coordinate(180, -90)));
        sf5.setPropertyValue("att1", "value1");

        col.add(Arrays.asList(sf1, sf2, sf3, sf4, sf5).iterator());

        featureColls.add(col);


        //create a serie of envelopes for tests --------------------------------
        GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.geographic());
        env.setRange(0, -90, 90);
        env.setRange(1, -180, 180);
        envelopes.add(env);
        env = new GeneralEnvelope(CommonCRS.WGS84.geographic());
        env.setRange(0, -12, 31);
        env.setRange(1, -5, 46);
        envelopes.add(env);
        env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        envelopes.add(env);
        env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -5, 46);
        env.setRange(1, -12, 31);
        envelopes.add(env);
        env = new GeneralEnvelope(CRS.forCode("EPSG:3395"));
        env.setRange(0, -1200000, 3100000);
        env.setRange(1, -500000, 4600000);
        envelopes.add(env);

        //create a serie of date ranges ----------------------------------------
        dates.add(new Date[]{new Date(1000),new Date(15000)});
        dates.add(new Date[]{null,          new Date(15000)});
        dates.add(new Date[]{new Date(1000),null});
        dates.add(new Date[]{null,          null});

        //create a serie of elevation ranges -----------------------------------
        elevations.add(new Double[]{-15d,   50d});
        elevations.add(new Double[]{null,   50d});
        elevations.add(new Double[]{-15d,   null});
        elevations.add(new Double[]{null,   null});


        //create some coverages ------------------------------------------------

        env = new GeneralEnvelope(CRS.forCode("EPSG:32738"));
        env.setRange(0,  695035,  795035);
        env.setRange(1, 7545535, 7645535);
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.RED);
        g.fill(new Rectangle(0, 0, 100, 100));
        GridCoverage coverage = new GridCoverage2D(new GridGeometry(null, env, GridOrientation.HOMOTHETY), null, img);
        coverages.add(coverage);

        env = new GeneralEnvelope(CommonCRS.WGS84.geographic());
        env.setRange(0,  -10,  25);
        env.setRange(1, -56, -21);
        img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        g = img.createGraphics();
        g.setColor(Color.RED);
        g.fill(new Rectangle(0, 0, 100, 100));
        coverage = new GridCoverage2D(new GridGeometry(null, env, GridOrientation.HOMOTHETY), null, img);
        coverages.add(coverage);

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testNoDataCM() throws NoSuchAuthorityCodeException, FactoryException, PortrayalException {
        final MapLayers context = MapBuilder.createContext();
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.geographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final RenderedImage img = DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(800, 600), env),
                new SceneDef(context));

        assertTrue( img.getColorModel() instanceof IndexColorModel);

        final IndexColorModel icm = (IndexColorModel) img.getColorModel();

        //we should have only two value
        assertEquals(2, icm.getMapSize());
        //with one being transparent
        assertTrue(icm.getTransparentPixel() >= 0);
    }

    @Test
    public void testSolidColorBackground() throws NoSuchAuthorityCodeException, FactoryException, PortrayalException {
        final MapLayers context = MapBuilder.createContext();
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.geographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final CanvasDef cdef = new CanvasDef(new Dimension(800, 600), env);
        cdef.setBackground(Color.GREEN);
        final RenderedImage img = DefaultPortrayalService.portray(cdef, new SceneDef(context));

        assertTrue( img.getColorModel() instanceof IndexColorModel);

        final IndexColorModel icm = (IndexColorModel) img.getColorModel();

        //we should have only two value
        assertEquals(2, icm.getMapSize());
        assertTrue(Color.GREEN.getRGB() == icm.getRGB(0) || Color.GREEN.getRGB() == icm.getRGB(1));
    }

    @Test
    public void testSolidColorBackgroundWithAA() throws NoSuchAuthorityCodeException, FactoryException, PortrayalException {
        final MapLayers context = MapBuilder.createContext();
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.geographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final CanvasDef cdef = new CanvasDef(new Dimension(800, 600), env);
        cdef.setBackground(Color.GREEN);
        final RenderedImage img = DefaultPortrayalService.portray(cdef,
                new SceneDef(context, new Hints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)));

        //background is single color opaque we should obtain an RGB color model because of active
        //anti-aliasing
        assertTrue(!(img.getColorModel() instanceof IndexColorModel));
        assertEquals(ColorSpace.TYPE_RGB, img.getColorModel().getColorSpace().getType());
        assertEquals(3, img.getColorModel().getNumComponents());
        assertEquals(3, img.getColorModel().getNumColorComponents());
    }

    @Test
    public void testAlphaColorBackground() throws NoSuchAuthorityCodeException, FactoryException, PortrayalException {
        final MapLayers context = MapBuilder.createContext();
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.geographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final CanvasDef cdef = new CanvasDef(new Dimension(800, 600), env);
        cdef.setBackground(new Color(0.5f, 0.1f, 0.7f, 0.6f));
        final RenderedImage img = DefaultPortrayalService.portray(cdef, new SceneDef(context));

        //background is not opaque we should obtain an RGBA color model
        assertTrue(!(img.getColorModel() instanceof IndexColorModel));
        assertEquals(ColorSpace.TYPE_RGB, img.getColorModel().getColorSpace().getType());
        assertEquals(4, img.getColorModel().getNumComponents());
        assertEquals(3, img.getColorModel().getNumColorComponents());
    }

    @Test
    public void testOpaqueUnpredictableBackground() throws NoSuchAuthorityCodeException, FactoryException, PortrayalException {
        final MapLayers context = MapBuilder.createContext();
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.geographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final CanvasDef cdef = new CanvasDef(new Dimension(800, 600), env);
        cdef.setBackground(Color.GREEN);
        final RenderedImage img = DefaultPortrayalService.portray(cdef,
                new SceneDef(context,null, new PortrayalExtension() {
                        @Override
                        public void completeCanvas(J2DCanvas canvas) throws PortrayalException {
                            canvas.setBackgroundPainter(new GradiantColorPainter());
                        }
                    }));

        //background is opaque we should obtain an RGB color model
        assertTrue(!(img.getColorModel() instanceof IndexColorModel));
        assertEquals(ColorSpace.TYPE_RGB, img.getColorModel().getColorSpace().getType());
        assertEquals(3, img.getColorModel().getNumComponents());
        assertEquals(3, img.getColorModel().getNumColorComponents());
    }

    @Test
    public void testOpaqueStyleDatas() throws NoSuchAuthorityCodeException, FactoryException, PortrayalException {
        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(createLayer(Color.BLUE,Color.RED,Color.YELLOW));
        context.getComponents().add(createLayer(Color.BLUE,Color.GREEN,Color.GRAY));

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.geographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final CanvasDef cdef = new CanvasDef(new Dimension(800, 600), env);
        cdef.setBackground(Color.WHITE);
        final RenderedImage img = DefaultPortrayalService.portray(cdef, new SceneDef(context));

        assertTrue( img.getColorModel() instanceof IndexColorModel);
        final IndexColorModel icm = (IndexColorModel) img.getColorModel();
        assertEquals(Transparency.OPAQUE, icm.getTransparency());
        assertEquals(-1, icm.getTransparentPixel());
        assertFalse(icm.hasAlpha());

        //we should have only six value
        assertEquals(6, icm.getMapSize());

        final Set<Integer> colors = new HashSet<Integer>();
        colors.add(Color.WHITE.getRGB());
        colors.add(Color.BLUE.getRGB());
        colors.add(Color.RED.getRGB());
        colors.add(Color.YELLOW.getRGB());
        colors.add(Color.GREEN.getRGB());
        colors.add(Color.GRAY.getRGB());

        for(int i=0;i<icm.getMapSize();i++){
            assertTrue(colors.contains(icm.getRGB(i)));
        }

    }

    @Test
    public void testRasterData() throws NoSuchAuthorityCodeException, FactoryException, PortrayalException {
        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(MapBuilder.createCoverageLayer(coverages.get(0), SF.style(SF.rasterSymbolizer()), "test"));

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.geographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final CanvasDef cdef = new CanvasDef(new Dimension(800, 600), env);
        cdef.setBackground(Color.WHITE);
        final RenderedImage img = DefaultPortrayalService.portray(cdef, new SceneDef(context));

        //background is opaque we should obtain an RGB color model since raster styles
        //are unpredictable
        assertTrue(!(img.getColorModel() instanceof IndexColorModel));
        assertEquals(ColorSpace.TYPE_RGB, img.getColorModel().getColorSpace().getType());
        assertEquals(3, img.getColorModel().getNumComponents());
        assertEquals(3, img.getColorModel().getNumColorComponents());

    }

    /**
     * Test that when asking a jpeg output, the resulting writen image has been
     * configured with a white background.
     */
    @Test
    public void testJPEGOutput() throws NoSuchAuthorityCodeException, FactoryException, IOException, PortrayalException{
        final MapLayers context = MapBuilder.createContext();

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.geographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        File tempFile = File.createTempFile("testjpeg", ".jpg");
        tempFile.deleteOnExit();

        final CanvasDef cdef = new CanvasDef(new Dimension(800, 600), env);
        DefaultPortrayalService.portray(cdef,
                new SceneDef(context),
                new OutputDef("image/jpeg", tempFile));

        //we should obtain a white background image
        final BufferedImage img = ImageIO.read(tempFile);

        for(int x=0; x<img.getWidth(); x++){
            for(int y=0; y<img.getHeight(); y++){
                //jpeg can't encode a perfect white image, CMY to RGB conversion lost I guess.
                Color c = new Color(img.getRGB(x, y));
                assertBetween("color is not white", 250, 255, c.getRed());
                assertBetween("color is not white", 250, 255, c.getGreen());
                assertBetween("color is not white", 250, 255, c.getBlue());
                assertBetween("color is not white", 250, 255, c.getAlpha());
            }
        }
    }

    @Test
    public void testReprojectionCoverageARGB() throws TransformException, PortrayalException, NoSuchAuthorityCodeException, FactoryException{

         //create a test coverage
        final BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.GREEN);
        g2d.fillRect(0, 0, 500, 500);
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 20);
        env.setRange(1, 0, 20);
        final GridCoverage coverage = new GridCoverage2D(new GridGeometry(null, env, GridOrientation.HOMOTHETY), null, img);

        //display it
        final MapLayers context = MapBuilder.createContext();
        final MapLayer cl = MapBuilder.createCoverageLayer(coverage, SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), "coverage");
        context.getComponents().add(cl);

        final Envelope envelope = Envelopes.transform(env, CRS.forCode("EPSG:3031"));

        final CanvasDef cdef = new CanvasDef(new Dimension(800, 600), envelope);
        cdef.setBackground(Color.WHITE);
        final RenderedImage result = DefaultPortrayalService.portray(cdef, new SceneDef(context));

        //background is opaque we should obtain an RGB color model since raster styles
        //are unpredictable
        assertTrue(!(result.getColorModel() instanceof IndexColorModel));
        assertEquals(ColorSpace.TYPE_RGB, result.getColorModel().getColorSpace().getType());
        assertEquals(3, result.getColorModel().getNumComponents());
        assertEquals(3, result.getColorModel().getNumColorComponents());

        //check we don't have any black reprojection pixels
        int[] buffer = new int[4];
        final Raster raster = result.getData();
        for(int x=0;x<raster.getWidth();x++){
            for(int y=0;y<raster.getHeight();y++){
                raster.getPixel(x, y, buffer);
                if(buffer[0] == 0 && buffer[1] == 0 && buffer[2] == 0){
                    //black pixel
                    fail("reprojection should not have generated black pixels.");
                }
            }
        }

    }

    @Test
    public void testReprojectionCoverageRGB() throws TransformException, PortrayalException, NoSuchAuthorityCodeException, FactoryException{

         //create a test coverage
        final BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.GREEN);
        g2d.fillRect(0, 0, 500, 500);
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 20);
        env.setRange(1, 0, 20);
        final GridCoverage coverage = new GridCoverage2D(new GridGeometry(null, env, GridOrientation.HOMOTHETY), null, img);

        //display it
        final MapLayers context = MapBuilder.createContext();
        final MapLayer cl = MapBuilder.createCoverageLayer(coverage, SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), "coverage");
        context.getComponents().add(cl);

        final Envelope envelope = Envelopes.transform(env, CRS.forCode("EPSG:3031"));

        final CanvasDef cdef = new CanvasDef(new Dimension(800, 600), envelope);
        cdef.setBackground(Color.WHITE);
        final RenderedImage result = DefaultPortrayalService.portray(cdef, new SceneDef(context));

        //background is opaque we should obtain an RGB color model since raster styles
        //are unpredictable
        assertTrue(!(result.getColorModel() instanceof IndexColorModel));
        assertEquals(ColorSpace.TYPE_RGB, result.getColorModel().getColorSpace().getType());
        assertEquals(3, result.getColorModel().getNumComponents());
        assertEquals(3, result.getColorModel().getNumColorComponents());

        //check we don't have any black reprojection pixels
        int[] buffer = new int[4];
        final Raster raster = result.getData();
        for(int x=0;x<raster.getWidth();x++){
            for(int y=0;y<raster.getHeight();y++){
                raster.getPixel(x, y, buffer);
                if(buffer[0] == 0 && buffer[1] == 0 && buffer[2] == 0){
                    //black pixel
                    fail("reprojection should not have generated black pixels.");
                }
            }
        }

    }

    private MapLayer createLayer(final Color ... colors){
        MapLayer layer = MapBuilder.createLayer(featureColls.get(0));
        layer.setStyle(createStyle(colors));
        return layer;
    }

    private static MutableStyle createStyle(final Color ... colors){
        final MutableStyle style = SF.style();

        for(Color c : colors){
            final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
            final GraphicalSymbol gs = SF.mark(MARK_CIRCLE, SF.fill(c), SF.stroke(c, 1));
            final PointSymbolizer symbol = SF.pointSymbolizer(SF.graphic(
                    Collections.singletonList(gs),
                    LITERAL_ONE_FLOAT,
                    LITERAL_ONE_FLOAT,
                    LITERAL_ZERO_FLOAT,
                    DEFAULT_ANCHOR_POINT,
                    DEFAULT_DISPLACEMENT),
                    null);
            fts.rules().add(SF.rule(symbol));
            style.featureTypeStyles().add(fts);
        }

        return style;
    }

}
