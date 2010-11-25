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
package org.geotoolkit.display2d.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.geotoolkit.coverage.CoverageStack;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.display.canvas.control.StopOnErrorMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
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
import org.geotoolkit.style.MutableStyleFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.coverage.Coverage;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import static org.junit.Assert.*;

/**
 * Testing portrayal service.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PortrayalServiceTest {

    private static final double EPS = 0.000000001d;

    private static final GeometryFactory GF = new GeometryFactory();
    private static final GridCoverageFactory GCF = new GridCoverageFactory();
    private static final MutableStyleFactory SF = new DefaultStyleFactory();

    private final List<FeatureCollection> featureColls = new ArrayList<FeatureCollection>();
    private final List<GridCoverage2D> coverages = new ArrayList<GridCoverage2D>();
    private final List<Envelope> envelopes = new ArrayList<Envelope>();
    private final List<Date[]> dates = new ArrayList<Date[]>();
    private final List<Double[]> elevations = new ArrayList<Double[]>();

    private final Coverage coverage4D;

    public PortrayalServiceTest() throws Exception {

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
    public void testEnvelopeNotNull() throws NoSuchAuthorityCodeException, FactoryException, PortrayalException {
        MapContext context = MapBuilder.createContext(CRS.decode("EPSG:4326"));
        GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(800, 600), null),
                new SceneDef(context),
                new ViewDef(env));



        //CRS can not obtain envelope for this projection. we check that we don't reaise any error.
        context = MapBuilder.createContext(CRS.decode("CRS:84"));
        env = new GeneralEnvelope(CRS.decode("CRS:84"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(800, 600), null),
                new SceneDef(context),
                new ViewDef(env));


    }

    @Test
    public void testFeatureRendering() throws Exception{
        for(FeatureCollection col : featureColls){
            final MapLayer layer = MapBuilder.createFeatureLayer(col, SF.style(SF.pointSymbolizer()));
            testRendering(layer);
        }        
    }

    @Test
    public void testCoverageRendering() throws Exception{
        for(GridCoverage2D col : coverages){
            final MapLayer layer = MapBuilder.createCoverageLayer(col, SF.style(SF.rasterSymbolizer()), "cov");
            testRendering(layer);
        }
    }

    @Test
    public void testCoverageNDRendering() throws Exception{
        //todo
    }

    @Test
    public void testLongitudeFirst() throws Exception{

        final int[] pixel = new int[4];
        final int[] red = new int[]{255,0,0,255};
        final int[] white = new int[]{255,255,255,255};

        final Hints hints = new Hints();
        hints.put(GO2Hints.KEY_COLOR_MODEL, ColorModel.getRGBdefault());


        

        //create a map context with a layer that will cover the entire area we will ask for
        final GeneralEnvelope covenv = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        covenv.setRange(0, -180, 180);
        covenv.setRange(1, -90, 90);
        final BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = img.createGraphics();
        g.setColor(Color.RED);
        g.fill(new Rectangle(0, 0, 360, 180));
        final GridCoverage2D coverage = GCF.create("test1", img, covenv);
        final MapLayer layer = MapBuilder.createCoverageLayer(coverage, SF.style(SF.rasterSymbolizer()), "");
        final MapContext context = MapBuilder.createContext();
        context.layers().add(layer);


        //sanity test, image should be a red vertical band in the middle
        final CoordinateReferenceSystem epsg4326 = CRS.decode("EPSG:4326");
        GeneralEnvelope env = new GeneralEnvelope(epsg4326);
        env.setRange(0, -180, 180);
        env.setRange(1, -180, 180);

        BufferedImage buffer = DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(360, 360), Color.WHITE),
                new SceneDef(context, hints),
                new ViewDef(env));
        //ImageIO.write(buffer, "png", new File("sanity.png"));
        assertEquals(360,buffer.getWidth());
        assertEquals(360,buffer.getHeight());

        WritableRaster raster = buffer.getRaster();
        raster.getPixel(0, 0, pixel);       assertArrayEquals(white, pixel);
        raster.getPixel(359, 0, pixel);     assertArrayEquals(white, pixel);
        raster.getPixel(359, 359, pixel);   assertArrayEquals(white, pixel);
        raster.getPixel(0, 359, pixel);     assertArrayEquals(white, pixel);
        raster.getPixel(180, 0, pixel);     assertArrayEquals(red, pixel);
        raster.getPixel(180, 359, pixel);   assertArrayEquals(red, pixel);
        raster.getPixel(0, 180, pixel);     assertArrayEquals(white, pixel);
        raster.getPixel(359, 180, pixel);   assertArrayEquals(white, pixel);



        //east=horizontal test, image should be a red horizontal band in the middle
        buffer = DefaultPortrayalService.portray(
                new CanvasDef(new Dimension(360, 360), Color.WHITE),
                new SceneDef(context, hints),
                new ViewDef(env).setLongitudeFirst());
        //ImageIO.write(buffer, "png", new File("flip.png"));
        assertEquals(360,buffer.getWidth());
        assertEquals(360,buffer.getHeight());

        raster = buffer.getRaster();
        raster.getPixel(0, 0, pixel);       assertArrayEquals(white, pixel);
        raster.getPixel(359, 0, pixel);     assertArrayEquals(white, pixel);
        raster.getPixel(359, 359, pixel);   assertArrayEquals(white, pixel);
        raster.getPixel(0, 359, pixel);     assertArrayEquals(white, pixel);
        raster.getPixel(180, 0, pixel);     assertArrayEquals(white, pixel);
        raster.getPixel(180, 359, pixel);   assertArrayEquals(white, pixel);
        raster.getPixel(0, 180, pixel);     assertArrayEquals(red, pixel);
        raster.getPixel(359, 180, pixel);   assertArrayEquals(red, pixel);



    }



    private void testRendering(MapLayer layer) throws TransformException, PortrayalException{
        final StopOnErrorMonitor monitor = new StopOnErrorMonitor();

        final MapContext context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
        context.layers().add(layer);
        assertEquals(1, context.layers().size());

        for(final Envelope env : envelopes){
            for(Date[] drange : dates){
                for(Double[] erange : elevations){
                    final Envelope cenv = GO2Utilities.combine(env, drange, erange);
                    final BufferedImage img = DefaultPortrayalService.portray(
                        new CanvasDef(new Dimension(800, 600), null),
                        new SceneDef(context),
                        new ViewDef(cenv,0,monitor));
                    assertNull(monitor.getLastException());
                    assertNotNull(img);
                }
            }
        }
    }

}
