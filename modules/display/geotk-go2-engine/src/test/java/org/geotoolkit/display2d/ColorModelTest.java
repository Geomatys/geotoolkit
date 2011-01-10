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

import javax.imageio.ImageIO;
import org.geotoolkit.display2d.service.OutputDef;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.geotoolkit.coverage.CoverageStack;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.painter.GradiantColorPainter;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.PortrayalExtension;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.coverage.Coverage;
import org.opengis.style.PointSymbolizer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.style.GraphicalSymbol;

import static org.junit.Assert.*;
import static org.geotoolkit.style.StyleConstants.*;

/**
 * Testing color model optimisations.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ColorModelTest {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final GridCoverageFactory GCF = new GridCoverageFactory();
    private static final MutableStyleFactory SF = new DefaultStyleFactory();

    private final List<FeatureCollection> featureColls = new ArrayList<FeatureCollection>();
    private final List<GridCoverage2D> coverages = new ArrayList<GridCoverage2D>();
    private final List<Envelope> envelopes = new ArrayList<Envelope>();
    private final List<Date[]> dates = new ArrayList<Date[]>();
    private final List<Double[]> elevations = new ArrayList<Double[]>();

    private final Coverage coverage4D;

    public ColorModelTest() throws Exception {

        // create the feature collection for tests -----------------------------
        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("test");
        sftb.add("geom", Point.class, DefaultGeographicCRS.WGS84);
        sftb.add("att1", String.class);
        sftb.add("att2", Double.class);
        final SimpleFeatureType sft = sftb.buildSimpleFeatureType();
        FeatureCollection col = DataUtilities.collection("id", sft);

        final FeatureWriter writer = col.getSession().getDataStore().getFeatureWriterAppend(sft.getName());

        SimpleFeature sf = (SimpleFeature) writer.next();
        sf.setAttribute("geom", GF.createPoint(new Coordinate(0, 0)));
        sf.setAttribute("att1", "value1");
        writer.write();
        sf = (SimpleFeature) writer.next();
        sf.setAttribute("geom", GF.createPoint(new Coordinate(-180, -90)));
        sf.setAttribute("att1", "value1");
        writer.write();
        sf = (SimpleFeature) writer.next();
        sf.setAttribute("geom", GF.createPoint(new Coordinate(-180, 90)));
        sf.setAttribute("att1", "value1");
        writer.write();
        sf = (SimpleFeature) writer.next();
        sf.setAttribute("geom", GF.createPoint(new Coordinate(180, -90)));
        sf.setAttribute("att1", "value1");
        writer.write();
        sf = (SimpleFeature) writer.next();
        sf.setAttribute("geom", GF.createPoint(new Coordinate(180, -90)));
        sf.setAttribute("att1", "value1");
        writer.write();

        writer.close();

        featureColls.add(col);


        //create a serie of envelopes for tests --------------------------------
        GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -90, 90);
        env.setRange(1, -180, 180);
        envelopes.add(env);
        env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -12, 31);
        env.setRange(1, -5, 46);
        envelopes.add(env);
        env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        envelopes.add(env);
        env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -5, 46);
        env.setRange(1, -12, 31);
        envelopes.add(env);
        env = new GeneralEnvelope(CRS.decode("EPSG:3395"));
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

        env = new GeneralEnvelope(CRS.decode("EPSG:32738"));
        env.setRange(0,  695035,  795035);
        env.setRange(1, 7545535, 7645535);
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.RED);
        g.fill(new Rectangle(0, 0, 100, 100));
        GridCoverage2D coverage = GCF.create("test1", img, env);
        coverages.add(coverage);

        env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0,  -10,  25);
        env.setRange(1, -56, -21);
        img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        g = img.createGraphics();
        g.setColor(Color.RED);
        g.fill(new Rectangle(0, 0, 100, 100));
        coverage = GCF.create("test2", img, env);
        coverages.add(coverage);

        //create some ND coverages ---------------------------------------------
        CoordinateReferenceSystem crs = new DefaultCompoundCRS("4D crs",
                    CRS.decode("EPSG:4326"),
                    DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT,
                    DefaultTemporalCRS.JAVA);

        List<Coverage> temps = new ArrayList<Coverage>();
        for(int i=0; i<10; i++){
            final List<Coverage> eles = new ArrayList<Coverage>();
            for(int k=0;k<10;k++){
                env = new GeneralEnvelope(crs);
                env.setRange(0,  0,  10);
                env.setRange(1, 0, 10);
                env.setRange(2, k, k+1);
                env.setRange(3, i, i+1);
                img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
                coverage = GCF.create("2D", img, env);
                eles.add(coverage);
            }
            temps.add(new CoverageStack("3D", eles));
        }
        coverage4D = new CoverageStack("4D", coverages);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testNoDataCM() throws NoSuchAuthorityCodeException, FactoryException, PortrayalException {
        final MapContext context = MapBuilder.createContext();
        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final BufferedImage img = DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(800, 600), null),
                new SceneDef(context),
                new ViewDef(env));

        assertTrue( img.getColorModel() instanceof IndexColorModel);

        final IndexColorModel icm = (IndexColorModel) img.getColorModel();

        //we should have only two value
        assertEquals(2, icm.getMapSize());
        //with one being transparent
        assertTrue(icm.getTransparentPixel() >= 0);
    }

    @Test
    public void testSolidColorBackground() throws NoSuchAuthorityCodeException, FactoryException, PortrayalException {
        final MapContext context = MapBuilder.createContext();
        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final BufferedImage img = DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(800, 600), Color.GREEN),
                new SceneDef(context),
                new ViewDef(env));

        assertTrue( img.getColorModel() instanceof IndexColorModel);

        final IndexColorModel icm = (IndexColorModel) img.getColorModel();

        //we should have only two value
        assertEquals(2, icm.getMapSize());
        assertTrue(Color.GREEN.getRGB() == icm.getRGB(0) || Color.GREEN.getRGB() == icm.getRGB(1));
    }

    @Test
    public void testSolidColorBackgroundWithAA() throws NoSuchAuthorityCodeException, FactoryException, PortrayalException {
        final MapContext context = MapBuilder.createContext();
        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final BufferedImage img = DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(800, 600), Color.GREEN),
                new SceneDef(context, new Hints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)),
                new ViewDef(env));

        //background is single color opaque we should obtain an RGB color model because of active
        //anti-aliasing
        assertTrue(!(img.getColorModel() instanceof IndexColorModel));
        assertEquals(ColorSpace.TYPE_RGB, img.getColorModel().getColorSpace().getType());
        assertEquals(3, img.getColorModel().getNumComponents());
        assertEquals(3, img.getColorModel().getNumColorComponents());
    }

    @Test
    public void testAlphaColorBackground() throws NoSuchAuthorityCodeException, FactoryException, PortrayalException {
        final MapContext context = MapBuilder.createContext();
        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final BufferedImage img = DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(800, 600), new Color(0.5f, 0.1f, 0.7f, 0.6f)),
                new SceneDef(context),
                new ViewDef(env));

        //background is not opaque we should obtain an RGBA color model
        assertTrue(!(img.getColorModel() instanceof IndexColorModel));
        assertEquals(ColorSpace.TYPE_RGB, img.getColorModel().getColorSpace().getType());
        assertEquals(4, img.getColorModel().getNumComponents());
        assertEquals(3, img.getColorModel().getNumColorComponents());
    }

    @Test
    public void testOpaqueUnpredictableBackground() throws NoSuchAuthorityCodeException, FactoryException, PortrayalException {
        final MapContext context = MapBuilder.createContext();
        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final BufferedImage img = DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(800, 600),null),
                new SceneDef(context,null, new PortrayalExtension() {
                        @Override
                        public void completeCanvas(J2DCanvas canvas) throws PortrayalException {
                            canvas.setBackgroundPainter(new GradiantColorPainter());
                        }
                    }),
                new ViewDef(env));

        //background is opaque we should obtain an RGB color model
        assertTrue(!(img.getColorModel() instanceof IndexColorModel));
        assertEquals(ColorSpace.TYPE_RGB, img.getColorModel().getColorSpace().getType());
        assertEquals(3, img.getColorModel().getNumComponents());
        assertEquals(3, img.getColorModel().getNumColorComponents());
    }

    @Test
    public void testOpaqueStyleDatas() throws NoSuchAuthorityCodeException, FactoryException, PortrayalException {
        final MapContext context = MapBuilder.createContext();
        context.layers().add(createLayer(Color.BLUE,Color.RED,Color.YELLOW));
        context.layers().add(createLayer(Color.BLUE,Color.GREEN,Color.GRAY));

        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final BufferedImage img = DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(800, 600), Color.WHITE),
                new SceneDef(context),
                new ViewDef(env));

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
        final MapContext context = MapBuilder.createContext();
        context.layers().add(MapBuilder.createCoverageLayer(coverages.get(0), SF.style(SF.rasterSymbolizer()), "test"));

        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final BufferedImage img = DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(800, 600), Color.WHITE),
                new SceneDef(context),
                new ViewDef(env));

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
        final MapContext context = MapBuilder.createContext();

        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        File tempFile = File.createTempFile("testjpeg", ".jpg");
        tempFile.deleteOnExit();

        DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(800, 600), null),
                new SceneDef(context),
                new ViewDef(env),
                new OutputDef("image/jpeg", tempFile));

        //we should obtain a white background image
        final BufferedImage img = ImageIO.read(tempFile);

        //jpeg can't encode a perfect white image, CMY to RGB conversion lost I guess.
        //it's R:253 G:255 B:255
        final int white = new Color(253, 255, 255, 255).getRGB();

        for(int x=0; x<img.getWidth(); x++){
            for(int y=0; y<img.getHeight(); y++){
                assertEquals(white, img.getRGB(x, y));
            }
        }
    }


    private MapLayer createLayer(final Color ... colors){
        return MapBuilder.createFeatureLayer(featureColls.get(0), createStyle(colors));
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
