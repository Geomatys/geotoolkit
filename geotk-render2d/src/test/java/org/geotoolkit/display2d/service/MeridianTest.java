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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.map.MapLayer;
import org.apache.sis.map.MapLayers;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.privy.AffineTransform2D;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.geometry.GeometricUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.FilterFactory;
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

    private static final GeometryFactory GF = JTS.getFactory();
    private static final DefaultStyleFactory SF = DefaultStyleFactory.provider();
    private static final FilterFactory FF = FilterUtilities.FF;

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

        final MapLayers context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef();
        canvasDef.setDimension(new Dimension(360, 180));
        canvasDef.setBackground(Color.WHITE);
        canvasDef.setEnvelope(env);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);
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

        final MapLayers context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef();
        canvasDef.setEnvelope(env);
        canvasDef.setDimension(new Dimension(360, 180));
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);
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

        final MapLayers context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef();
        canvasDef.setEnvelope(env);
        canvasDef.setDimension(new Dimension(360, 180));
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);
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

        final MapLayers context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef();
        canvasDef.setEnvelope(env);
        canvasDef.setDimension(new Dimension(360, 180));
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);
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

        final MapLayers context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef();
        canvasDef.setEnvelope(env);
        canvasDef.setDimension(new Dimension(360, 180));
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);
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

        final MapLayers context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), env);
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);
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

        final MapLayers context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -540, +900); //1 on the left, 2 on the right
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360*4, 180), env);
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);
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

        final MapLayers context = createFeatureLayer(poly, Polygon.class);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -355, +725); //-175 on the left, +545 on the right
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(1080, 180), env);
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);
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

        final MapLayers context = createFeatureLayer(points);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), env);
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);

        ImageIO.write(image, "PNG", File.createTempFile("test", ".png"));

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

        final MapLayers context = createFeatureLayer(points);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), env);
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);

        ImageIO.write(image, "PNG", File.createTempFile("test", ".png"));

        checkImage(image, new Rectangle(349, 79, 2, 2),
                          new Rectangle(349, 99, 2, 2),
                          new Rectangle(  9, 79, 2, 2),
                          new Rectangle(  9, 99, 2, 2));

    }

    @Test
    public void testEnvelopeP170toP190() throws Exception{

        final GeneralEnvelope genv = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        genv.setRange(0, +170, +190);
        genv.setRange(1, -10, +10);

        final Geometry poly = GeometricUtilities.toJTSGeometry(genv, GeometricUtilities.WrapResolution.SPLIT);

        final MapLayers context = createFeatureLayer(poly, Geometry.class);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), env);
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);
        checkImage(image, new Rectangle(350, 80, 10, 20),
                          new Rectangle(0, 80, 10, 20));
    }


    @Test
    public void testEnvelopeN190toN170() throws Exception{

        final GeneralEnvelope genv = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        genv.setRange(0, -190, -170);
        genv.setRange(1, -10, +10);

        final Geometry poly = GeometricUtilities.toJTSGeometry(genv, GeometricUtilities.WrapResolution.SPLIT);

        final MapLayers context = createFeatureLayer(poly, Geometry.class);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), env);
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);
        checkImage(image, new Rectangle(350, 80, 10, 20),
                          new Rectangle(0, 80, 10, 20));
    }

    @Test
    public void testEnvelopeP170toN170() throws Exception{

        final GeneralEnvelope genv = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        genv.setRange(0, +170, -170);
        genv.setRange(1, -10, +10);

        final Geometry poly = GeometricUtilities.toJTSGeometry(genv, GeometricUtilities.WrapResolution.SPLIT);

        final MapLayers context = createFeatureLayer(poly, Geometry.class);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), env);
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);
        checkImage(image, new Rectangle(350, 80, 10, 20),
                          new Rectangle(0, 80, 10, 20));
    }

    @Test
    public void testEnvelopeLarge() throws Exception{

        final GeneralEnvelope genv = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        genv.setRange(0, -120, +140);
        genv.setRange(1, -70, +40);

        final Geometry poly = GeometricUtilities.toJTSGeometry(genv, GeometricUtilities.WrapResolution.SPLIT);

        final MapLayers context = createFeatureLayer(poly, Geometry.class);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), env);
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);
        checkImage(image, new Rectangle(60, 50, 260, 110));
    }

    @Test
    public void testEnvelopeWorld() throws Exception{

        final GeneralEnvelope genv = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        genv.setRange(0, -180, +180);
        genv.setRange(1, -90, +90);

        final Geometry poly = GeometricUtilities.toJTSGeometry(genv, GeometricUtilities.WrapResolution.SPLIT);

        final MapLayers context = createFeatureLayer(poly, Geometry.class);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), env);
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);
        checkImage(image, new Rectangle(0, 0, 360, 180));
    }

    /**
     * Test coverage that overlaps the +180 meridian.
     */
    @Test
    public void testImageCrossP170toP190() throws Exception{

        final GeneralEnvelope covEnv = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        covEnv.setRange(0, +170, +190);
        covEnv.setRange(1, -10, +10);

        final MapLayers context = createCoverageLayer(covEnv);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), env);
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);
        checkImage(image, new Rectangle(350, 80, 10, 20),
                          new Rectangle(0, 80, 10, 20));
    }

    /**
     * Test coverage that overlaps the -180 meridian.
     */
    @Test
    public void testImageCrossN190toN170() throws Exception{

        final GeneralEnvelope covEnv = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        covEnv.setRange(0, -190, -170);
        covEnv.setRange(1, -10, +10);

        final MapLayers context = createCoverageLayer(covEnv);
        final SceneDef sceneDef = new SceneDef(context);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), env);
        canvasDef.setBackground(Color.WHITE);

        final RenderedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef);
        checkImage(image, new Rectangle(350, 80, 10, 20),
                          new Rectangle(0, 80, 10, 20));
    }

    /**
     * Test the image content.
     * The image must be completely white and pixels inside the fille areas must be black.
     */
    private void checkImage(RenderedImage image, Rectangle ... fillAreas){

        final int white = Color.WHITE.getRGB();
        final int black = Color.RED.getRGB();

        ColorModel cm = image.getColorModel();
        Raster raster = (image instanceof BufferedImage) ? ((BufferedImage) image).getRaster() : image.getData();

        final int xmax = raster.getMinX() + raster.getWidth();
        final int ymax = raster.getMinY() + raster.getHeight();

        for (int y=raster.getMinY(); y<ymax; y++) {
            for (int x=raster.getMinX(); x < xmax; x++) {
                int rgb = cm.getRGB(raster.getDataElements(x, y, null));

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


    private static <T extends Geometry> MapLayers  createFeatureLayer(T geometry, Class<T> geomClass){

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(geomClass).setName("geom").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType type = ftb.build();

        final Feature feature = type.newInstance();
        JTS.setCRS(geometry, CommonCRS.WGS84.normalizedGeographic());
        feature.setPropertyValue("geom",geometry);
        final FeatureSet col = new InMemoryFeatureSet(type, Arrays.asList(feature));

        final PolygonSymbolizer symbol = SF.polygonSymbolizer(SF.stroke(Color.BLACK, 0), SF.fill(Color.RED), null);
        final MutableStyle style = SF.style(symbol);
        final MapLayer layer = MapBuilder.createLayer(col);
        layer.setStyle(style);

        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(layer);
        return context;
    }

    private static MapLayers createFeatureLayer(MultiPoint geometry){

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(MultiPoint.class).setName("geom").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType type = ftb.build();

        final Feature feature = type.newInstance();
        JTS.setCRS(geometry, CommonCRS.WGS84.normalizedGeographic());
        feature.setPropertyValue("geom",geometry);
        final FeatureSet col = new InMemoryFeatureSet(type, Arrays.asList(feature));

        final List<GraphicalSymbol> symbols = new ArrayList<>();
        symbols.add(SF.mark(StyleConstants.MARK_SQUARE, SF.fill(Color.RED), SF.stroke(Color.BLACK, 0)));
        final Graphic graphic = SF.graphic(symbols, StyleConstants.LITERAL_ONE_FLOAT, FF.literal(2), StyleConstants.LITERAL_ZERO_FLOAT, null, null);
        final PointSymbolizer ps = SF.pointSymbolizer(graphic, null);

        final MutableStyle style = SF.style(ps);
        final MapLayer layer = MapBuilder.createLayer(col);
        layer.setStyle(style);

        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(layer);
        return context;
    }

    private static MapLayers createCoverageLayer(Envelope env){

        final BufferedImage image = new BufferedImage((int)env.getSpan(0), (int)env.getSpan(1), BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setDomain(new GridGeometry(null, PixelInCell.CELL_CORNER, new AffineTransform2D(1, 0, 0, -1,
                env.getMinimum(0), env.getMaximum(1)), env.getCoordinateReferenceSystem()));
        gcb.setValues(image);
        final GridCoverage coverage = gcb.build();

        final RasterSymbolizer symbol = SF.rasterSymbolizer();
        final MutableStyle style = SF.style(symbol);
        final MapLayer layer = MapBuilder.createCoverageLayer(coverage, style,"test");

        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(layer);
        return context;
    }

}
