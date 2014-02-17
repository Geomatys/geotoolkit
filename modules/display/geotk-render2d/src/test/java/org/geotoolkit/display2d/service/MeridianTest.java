/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.filter.DefaultFilterFactory2;
import org.geotoolkit.geometry.GeometricUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;

/**
 * Test renderer support for datas crossing a wrap around axis.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class MeridianTest {
    
    private static final GeometryFactory GF = new GeometryFactory();
    private static final DefaultStyleFactory SF = new DefaultStyleFactory();
    private static final FilterFactory2 FF = new DefaultFilterFactory2();
    
    /**
     * Sanity test.
     * If this test fail, don't even bother looking at the others.
     */
    @Test
    public void testSanity() throws Exception{
        
        final Polygon poly = GF.createPolygon(new Coordinate[]{
            new Coordinate( 0,  0),
            new Coordinate( 0, 10),
            new Coordinate(20, 10),
            new Coordinate(20,  0),
            new Coordinate( 0,  0)
        });
        
        final MapContext context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(180, 80, 20, 10));
    }
    
    /**
     * Test crossing the +180 meridian.
     */
    @Test
    public void testCrossP170toP190() throws Exception{
        
        final Polygon poly = GF.createPolygon(new Coordinate[]{
            new Coordinate(+170, +10),
            new Coordinate(+190, +10),
            new Coordinate(+190, -10),
            new Coordinate(+170, -10),
            new Coordinate(+170, +10)
        });
        
        final MapContext context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(350, 80, 10, 20), 
                          new Rectangle(0, 80, 10, 20));
    }
    
    /**
     * Test crossing the -180 meridian.
     */
    @Test
    public void testCrossN170toN190() throws Exception{
        
        final Polygon poly = GF.createPolygon(new Coordinate[]{
            new Coordinate(-170, +10),
            new Coordinate(-190, +10),
            new Coordinate(-190, -10),
            new Coordinate(-170, -10),
            new Coordinate(-170, +10)
        });
        
        final MapContext context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(350, 80, 10, 20), 
                          new Rectangle(0, 80, 10, 20));
    }
    
    /**
     * Test loop around the +180 meridian.
     */
    @Test
    public void testLoopP170toN170() throws Exception{
        
        final Polygon poly = GF.createPolygon(new Coordinate[]{
            new Coordinate(+170, +10),
            new Coordinate(-170, +10),
            new Coordinate(-170, -10),
            new Coordinate(+170, -10),
            new Coordinate(+170, +10)
        });
        
        final MapContext context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(350, 80, 10, 20), 
                          new Rectangle(0, 80, 10, 20));
    }
    
    /**
     * Test loop around the -180 meridian.
     */
    @Test
    public void testLoopN170toP170() throws Exception{
        
        final Polygon poly = GF.createPolygon(new Coordinate[]{
            new Coordinate(-170, +10),
            new Coordinate(+170, +10),
            new Coordinate(+170, -10),
            new Coordinate(-170, -10),
            new Coordinate(-170, +10)
        });
        
        final MapContext context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(350, 80, 10, 20), 
                          new Rectangle(0, 80, 10, 20));
    }
    
    /**
     * Test a geometry which makes a full width wrap around.
     * Some geometry at the poles often have a line segment which makes a complete
     * world wrap.
     */
    @Test
    public void testFullWrapAroundGeometry() throws Exception{
        
        final Polygon poly = GF.createPolygon(new Coordinate[]{
            new Coordinate(-180, +10),
            new Coordinate(+180, +10),
            new Coordinate(+180, -10),
            new Coordinate(-180, -10),
            new Coordinate(-180, +10)
        });
        
        final MapContext context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(0, 80, 360, 20));
    }
    
    /**
     * Test duplicated on left and right.
     */
    @Test
    public void testDuplicateExact() throws Exception{
        
        final Polygon poly = GF.createPolygon(new Coordinate[]{
            new Coordinate( 0,  0),
            new Coordinate( 0, 10),
            new Coordinate(20, 10),
            new Coordinate(20,  0),
            new Coordinate( 0,  0)
        });
        
        final MapContext context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -540, +900); //1 on the left, 2 on the right
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360*4, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(180     , 80, 20, 10),
                          new Rectangle(180+ 360, 80, 20, 10),
                          new Rectangle(180+ 720, 80, 20, 10),
                          new Rectangle(180+1080, 80, 20, 10));
    }
    
    /**
     * Test partial duplicated on left and right.
     */
    @Test
    public void testDuplicatePartial() throws Exception{
        
        final Polygon poly = GF.createPolygon(new Coordinate[]{
            new Coordinate( 0,  0),
            new Coordinate( 0, 10),
            new Coordinate(20, 10),
            new Coordinate(20,  0),
            new Coordinate( 0,  0)
        });
        
        final MapContext context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -355, +725); //-175 on the left, +545 on the right
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(1080, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(-5      , 80, 20, 10),
                          new Rectangle(-5 + 360, 80, 20, 10),
                          new Rectangle(-5 + 720, 80, 20, 10),
                          new Rectangle(-5 +1080, 80, 20, 10));
    }
    
    @Test
    public void testMultiPointCrossP170toP190() throws Exception{
        
        final MultiPoint points = GF.createMultiPoint(new Coordinate[]{
            new Coordinate(+170, +10),
            new Coordinate(+190, +10),
            new Coordinate(+190, -10),
            new Coordinate(+170, -10)
        });
        
        final MapContext context = createFeatureLayer(points);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        
        ImageIO.write(image, "PNG", new File("/home/husky/test.png"));
        
        checkImage(image, new Rectangle(349, 79, 2, 2), 
                          new Rectangle(349, 99, 2, 2),
                          new Rectangle(  9, 79, 2, 2),
                          new Rectangle(  9, 99, 2, 2));
        
    }
    
    @Test
    public void testMultiPointWideSpan() throws Exception{
        
        final MultiPoint points = GF.createMultiPoint(new Coordinate[]{
            new Coordinate(+170, +10),
            new Coordinate(-170, +10),
            new Coordinate(-170, -10),
            new Coordinate(+170, -10)
        });
        
        final MapContext context = createFeatureLayer(points);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        
        ImageIO.write(image, "PNG", new File("/home/husky/test.png"));
        
        checkImage(image, new Rectangle(349, 79, 2, 2), 
                          new Rectangle(349, 99, 2, 2),
                          new Rectangle(  9, 79, 2, 2),
                          new Rectangle(  9, 99, 2, 2));
        
    }
    
    @Test
    public void testEnvelopeP170toP190() throws Exception{
        
        final GeneralEnvelope genv = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        genv.setRange(0, +170, +190);
        genv.setRange(1, -10, +10);
        
        final Geometry poly = GeometricUtilities.toJTSGeometry(genv, GeometricUtilities.WrapResolution.SPLIT);
        
        final MapContext context = createFeatureLayer(poly, Geometry.class);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(350, 80, 10, 20), 
                          new Rectangle(0, 80, 10, 20));        
    }
    
    
    @Test
    public void testEnvelopeN190toN170() throws Exception{
        
        final GeneralEnvelope genv = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        genv.setRange(0, -190, -170);
        genv.setRange(1, -10, +10);
        
        final Geometry poly = GeometricUtilities.toJTSGeometry(genv, GeometricUtilities.WrapResolution.SPLIT);
        
        final MapContext context = createFeatureLayer(poly, Geometry.class);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(350, 80, 10, 20), 
                          new Rectangle(0, 80, 10, 20));        
    }
    
    @Test
    public void testEnvelopeP170toN170() throws Exception{
        
        final GeneralEnvelope genv = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        genv.setRange(0, +170, -170);
        genv.setRange(1, -10, +10);
        
        final Geometry poly = GeometricUtilities.toJTSGeometry(genv, GeometricUtilities.WrapResolution.SPLIT);
        
        final MapContext context = createFeatureLayer(poly, Geometry.class);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(350, 80, 10, 20), 
                          new Rectangle(0, 80, 10, 20));        
    }
    
    @Test
    public void testEnvelopeLarge() throws Exception{
        
        final GeneralEnvelope genv = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        genv.setRange(0, -120, +140);
        genv.setRange(1, -70, +40);
        
        final Geometry poly = GeometricUtilities.toJTSGeometry(genv, GeometricUtilities.WrapResolution.SPLIT);
        
        final MapContext context = createFeatureLayer(poly, Geometry.class);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(60, 50, 260, 110));        
    }
    
    @Test
    public void testEnvelopeWorld() throws Exception{
        
        final GeneralEnvelope genv = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        genv.setRange(0, -180, +180);
        genv.setRange(1, -90, +90);
        
        final Geometry poly = GeometricUtilities.toJTSGeometry(genv, GeometricUtilities.WrapResolution.SPLIT);
        
        final MapContext context = createFeatureLayer(poly, Geometry.class);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(0, 0, 360, 180));        
    }
    
    
    /**
     * Test coverage that overlaps the +180 meridian.
     */
    @Test
    public void testImageCrossP170toP190() throws Exception{
        
        final GeneralEnvelope covEnv = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        covEnv.setRange(0, +170, +190);
        covEnv.setRange(1, -10, +10);
        
        final MapContext context = createCoverageLayer(covEnv);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(350, 80, 10, 20), 
                          new Rectangle(0, 80, 10, 20));
        
    }
    
    /**
     * Test coverage that overlaps the -180 meridian.
     */
    @Test
    public void testImageCrossN190toN170() throws Exception{
        
        final GeneralEnvelope covEnv = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        covEnv.setRange(0, -190, -170);
        covEnv.setRange(1, -10, +10);
        
        final MapContext context = createCoverageLayer(covEnv);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(350, 80, 10, 20), 
                          new Rectangle(0, 80, 10, 20));
        
    }
    
    /**
     * Test the image content.
     * The image must be completely white and pixels inside the fille areas must be black.
     * @param image
     * @param fillAreas 
     */
    private void checkImage(BufferedImage image, Rectangle ... fillAreas){
        
        final int white = Color.WHITE.getRGB();
        final int black = Color.BLACK.getRGB();
        
        final int width = image.getWidth();
        final int height = image.getHeight();
        
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                int rgb = image.getRGB(x, y);
                
                boolean inside = false;
                for(Rectangle rect : fillAreas){
                    if(rect.contains(x, y)){
                        inside = true;
                        break;
                    }
                }
                
                Assert.assertEquals("Wrong value at ("+x+","+y+")", inside?black:white, rgb);
            }
        }
        
    }
    
    
    private static <T extends Geometry> MapContext  createFeatureLayer(T geometry, Class<T> geomClass){
        
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("geom", geomClass, DefaultGeographicCRS.WGS84);
        final FeatureType type = ftb.buildFeatureType();
        
        final Feature feature = FeatureUtilities.defaultFeature(type, "0");
        JTS.setCRS(geometry, DefaultGeographicCRS.WGS84);
        feature.getProperty("geom").setValue(geometry);
        final FeatureCollection col = FeatureStoreUtilities.collection(feature);
        
        final PolygonSymbolizer symbol = SF.polygonSymbolizer(SF.stroke(Color.BLACK, 0), SF.fill(Color.BLACK), null);
        final MutableStyle style = SF.style(symbol);
        final MapLayer layer = MapBuilder.createFeatureLayer(col, style);
        
        final MapContext context = MapBuilder.createContext();
        context.layers().add(layer);
        return context;
    }
    
    private static MapContext createFeatureLayer(MultiPoint geometry){
        
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("geom", MultiPoint.class, DefaultGeographicCRS.WGS84);
        final FeatureType type = ftb.buildFeatureType();
        
        final Feature feature = FeatureUtilities.defaultFeature(type, "0");
        JTS.setCRS(geometry, DefaultGeographicCRS.WGS84);
        feature.getProperty("geom").setValue(geometry);
        final FeatureCollection col = FeatureStoreUtilities.collection(feature);
        
        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        symbols.add(SF.mark(StyleConstants.MARK_SQUARE, SF.fill(Color.BLACK), SF.stroke(Color.BLACK, 0)));
        final Graphic graphic = SF.graphic(symbols, StyleConstants.LITERAL_ONE_FLOAT, FF.literal(2), StyleConstants.LITERAL_ZERO_FLOAT, null, null);
        final PointSymbolizer ps = SF.pointSymbolizer(graphic, null);
        
        final MutableStyle style = SF.style(ps);
        final MapLayer layer = MapBuilder.createFeatureLayer(col, style);
        
        final MapContext context = MapBuilder.createContext();
        context.layers().add(layer);
        return context;
    }
    
    private static MapContext createCoverageLayer(Envelope env){
     
        final BufferedImage image = new BufferedImage((int)env.getSpan(0), (int)env.getSpan(1), BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());        
        
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setCoordinateReferenceSystem(env.getCoordinateReferenceSystem());
        gcb.setRenderedImage(image);
        gcb.setGridToCRS(new AffineTransform(1, 0, 0, -1, env.getMinimum(0), env.getMaximum(1)));
        gcb.setPixelAnchor(PixelInCell.CELL_CORNER);
        final GridCoverage2D coverage = (GridCoverage2D) gcb.build();
        
        final RasterSymbolizer symbol = SF.rasterSymbolizer();
        final MutableStyle style = SF.style(symbol);
        final MapLayer layer = MapBuilder.createCoverageLayer(coverage, style,"test");
        
        final MapContext context = MapBuilder.createContext();
        context.layers().add(layer);
        return context;
    }
    
}
