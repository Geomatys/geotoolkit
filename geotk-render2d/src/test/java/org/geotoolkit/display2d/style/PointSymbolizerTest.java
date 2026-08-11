/*
 * (C) 2022, Geomatys
 */
package org.geotoolkit.display2d.style;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.sis.feature.FeatureOperations;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.geometry.wrapper.jts.Factory;
import org.apache.sis.geometry.wrapper.jts.JTS;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.map.MapItem;
import org.apache.sis.map.MapLayer;
import org.apache.sis.map.MapLayers;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.SingleAttributeTypeBuilder;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Expression;
import org.opengis.filter.FilterFactory;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.PointSymbolizer;

import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PointSymbolizerTest {

    private static final GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();
    private static final MutableStyleFactory SF = DefaultStyleFactory.provider();
    protected static final FilterFactory FF = FilterUtilities.FF;

    /**
     * Render triangles and check rotation is applied.
     */
    @Test
    public void rotationTest() throws Exception{

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(Point.class).setName("geom").setCRS(CommonCRS.defaultGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType type = ftb.build();
        final Feature feature = type.newInstance();
        feature.setPropertyValue("geom",GF.createPoint(new Coordinate(0, 0)));
        final FeatureSet collection = new InMemoryFeatureSet(type, Arrays.asList(feature));

        final MapLayer layer = MapBuilder.createLayer(collection);

        final int WHITE = Color.WHITE.getRGB();
        final int BLACK = Color.BLACK.getRGB();

        { //test no rotation
            layer.setStyle(createRotatedTriangle(0));
            final RenderedImage buffer = createWorldImage(layer);
            Assert.assertEquals(WHITE, getRGB(buffer, 170, 80));
            Assert.assertEquals(WHITE, getRGB(buffer, 170, 85));
            Assert.assertEquals(BLACK, getRGB(buffer, 170, 100));
            Assert.assertEquals(BLACK, getRGB(buffer, 180, 80));
            Assert.assertEquals(BLACK, getRGB(buffer, 180, 85));
            Assert.assertEquals(BLACK, getRGB(buffer, 180, 100));
            Assert.assertEquals(WHITE, getRGB(buffer, 190, 80));
            Assert.assertEquals(WHITE, getRGB(buffer, 190, 85));
            Assert.assertEquals(BLACK, getRGB(buffer, 190, 100));
        }

        { //test right rotation
            layer.setStyle(createRotatedTriangle(45));
            final RenderedImage buffer = createWorldImage(layer);
            Assert.assertEquals(WHITE, getRGB(buffer, 170, 80));
            Assert.assertEquals(BLACK, getRGB(buffer, 170, 85));
            Assert.assertEquals(BLACK, getRGB(buffer, 170, 100));
            Assert.assertEquals(WHITE, getRGB(buffer, 180, 80));
            Assert.assertEquals(BLACK, getRGB(buffer, 180, 85));
            Assert.assertEquals(BLACK, getRGB(buffer, 180, 100));
            Assert.assertEquals(WHITE, getRGB(buffer, 190, 80));
            Assert.assertEquals(WHITE, getRGB(buffer, 190, 85));
            Assert.assertEquals(WHITE, getRGB(buffer, 190, 100));
        }

        { //test left rotation
            layer.setStyle(createRotatedTriangle(-45));
            final RenderedImage buffer = createWorldImage(layer);
            Assert.assertEquals(BLACK, getRGB(buffer, 170, 80));
            Assert.assertEquals(WHITE, getRGB(buffer, 170, 85));
            Assert.assertEquals(WHITE, getRGB(buffer, 170, 100));
            Assert.assertEquals(WHITE, getRGB(buffer, 180, 80));
            Assert.assertEquals(BLACK, getRGB(buffer, 180, 85));
            Assert.assertEquals(BLACK, getRGB(buffer, 180, 100));
            Assert.assertEquals(WHITE, getRGB(buffer, 190, 80));
            Assert.assertEquals(BLACK, getRGB(buffer, 190, 85));
            Assert.assertEquals(BLACK, getRGB(buffer, 190, 100));
        }
    }

    @Test
    public void renderComputedGeometry() throws Exception {
        final var gf = Factory.INSTANCE.factory(false);
        var ftb = new FeatureTypeBuilder().setName("computed");
        ftb.addAttribute(Double.class).setName("longitude");
        ftb.addAttribute(Double.class).setName("latitude");
        ftb.addProperty(FeatureOperations.function(Map.of("name", "sis:geometry"),
                f -> {
                    var pt = gf.createPoint(new Coordinate((Double) f.getPropertyValue("longitude"), (Double) f.getPropertyValue("latitude")));
                    JTS.setCoordinateReferenceSystem(pt, CommonCRS.defaultGeographic());
                    return pt;
                },
                new SingleAttributeTypeBuilder().setName("geometry").setValueClass(Point.class).setCRS(CommonCRS.defaultGeographic()).build()));
        var fType = ftb.build();

        final var f = fType.newInstance();
        f.setPropertyValue("longitude", 5d);
        f.setPropertyValue("latitude", 44d);

        var layer = MapBuilder.createLayer(new InMemoryFeatureSet(fType, List.of(f)));
        var ctx = MapBuilder.createContext(CommonCRS.defaultGeographic());
        ctx.getComponents().add(layer);

        final SceneDef scenedef = new SceneDef(ctx);
        final CanvasDef canvasdef = new CanvasDef(new Dimension(4, 4), new Envelope2D(CommonCRS.defaultGeographic(), 4, 43, 2, 2));
        canvasdef.setBackground(Color.BLACK);

        var image = DefaultPortrayalService.portray(canvasdef, scenedef);
        var pxIt = PixelIterator.create(image);
        var allBlack = true;
        while (pxIt.next()) {
            allBlack &= pxIt.getSample(0) == 0;
        }

        assertFalse(allBlack);
    }


    @Test
    public void renderFilteredLayer() throws Exception {
        final var gf = Factory.INSTANCE.factory(false);
        var ftb = new FeatureTypeBuilder().setName("computed");
        ftb.addAttribute(Boolean.class).setName("disabled");
        ftb.addAttribute(Point.class).setName("geometry");
        var fType = ftb.build();

        final var f = fType.newInstance();
        final var pt = gf.createPoint(new Coordinate(5d, 44d));
        JTS.setCoordinateReferenceSystem(pt, CommonCRS.defaultGeographic());
        f.setPropertyValue("geometry", pt);
        f.setPropertyValue("disabled", false);

        var layer = MapBuilder.createLayer(new InMemoryFeatureSet(fType, List.of(f)));
        final var q = new FeatureQuery();
        q.setSelection(FilterUtilities.FF.equal(FilterUtilities.FF.property("disabled"), FF.literal(false)));
        layer.setQuery(q);
        var ctx = MapBuilder.createContext(CommonCRS.defaultGeographic());
        ctx.getComponents().add(layer);

        final SceneDef scenedef = new SceneDef(ctx);
        final CanvasDef canvasdef = new CanvasDef(new Dimension(4, 4), new Envelope2D(CommonCRS.defaultGeographic(), 4, 43, 2, 2));
        canvasdef.setBackground(Color.BLACK);

        var image = DefaultPortrayalService.portray(canvasdef, scenedef);
        var pxIt = PixelIterator.create(image);
        var allBlack = true;
        while (pxIt.next()) {
            allBlack &= pxIt.getSample(0) == 0;
        }

        assertFalse(allBlack);

        f.setPropertyValue("disabled", true);

        image = DefaultPortrayalService.portray(canvasdef, scenedef);
        pxIt = PixelIterator.create(image);
        allBlack = true;
        while (pxIt.next()) {
            allBlack &= pxIt.getSample(0) == 0;
        }

        assertTrue(allBlack);
    }

    /**
     * Verify that two rules using different filters and geometries are displayed as designed
     */
    @Test
    public void renderTwoRules() throws Exception {
        final var gf = Factory.INSTANCE.factory(false);
        var ftb = new FeatureTypeBuilder().setName("render-two-rules");
        ftb.addAttribute(String.class).setName("rule");
        ftb.addAttribute(Point.class)
                .setName("geometry")
                .setCRS(CommonCRS.defaultGeographic())
                .addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Point.class)
                .setName("the_geom");
        var fType = ftb.build();

        final var f = fType.newInstance();
        final var upperLeftPt = gf.createPoint(new Coordinate(4.5d, 44.5d));
        JTS.setCoordinateReferenceSystem(upperLeftPt, CommonCRS.defaultGeographic());
        f.setPropertyValue("the_geom", upperLeftPt);

        final var lowerRightPt = gf.createPoint(new Coordinate(5.5d, 43.5d));
        JTS.setCoordinateReferenceSystem(upperLeftPt, CommonCRS.defaultGeographic());
        f.setPropertyValue("geometry", lowerRightPt);

        f.setPropertyValue("rule", "1");

        var layer = MapBuilder.createLayer(new InMemoryFeatureSet(fType, List.of(f)));
        layer.setStyle(SF.style("two rules", SF.description(), true, List.of(SF.featureTypeStyle(
                "two rules", SF.description(), null, Set.of(fType.getName()), null, List.of(
                        SF.rule(
                                "1", SF.description(), null, 0, Double.MAX_VALUE,
                                List.of(
                                    SF.pointSymbolizer(SF.graphic(List.of(SF.mark(FF.literal("square"), SF.stroke(Color.YELLOW, 0), SF.fill(Color.RED))), FF.literal(1), FF.literal(2), null, SF.anchorPoint(0.5, 0.5), SF.displacement()), null)
                                ),
                                FF.equal(FF.property("rule"), FF.literal("1"))
                        ),
                        SF.rule(
                                "2", SF.description(), null, 0, Double.MAX_VALUE,
                                List.of(
                                        SF.pointSymbolizer(SF.graphic(List.of(SF.mark(FF.literal("square"), SF.stroke(Color.YELLOW, 0), SF.fill(Color.GREEN))), FF.literal(1), FF.literal(2), null, SF.anchorPoint(0.5, 0.5), SF.displacement()), "the_geom")
                                ),
                                FF.equal(FF.property("rule"), FF.literal("2"))
                        )
                ))), null));

        var ctx = MapBuilder.createContext(CommonCRS.defaultGeographic());
        ctx.getComponents().add(layer);

        final SceneDef scenedef = new SceneDef(ctx);
        final CanvasDef canvasdef = new CanvasDef(new Dimension(4, 4), new Envelope2D(CommonCRS.defaultGeographic(), 4, 43, 2, 2));
        canvasdef.setBackground(Color.BLACK);

        var image = DefaultPortrayalService.portray(canvasdef, scenedef);
        var pixels = image.getTile(0, 0).getPixels(0, 0, 4, 4, new int[4*4*3]);
        assertArrayEquals(
                "Expects only first rule to be rendered, displaying a red square in lower right corner",
                new int[] {
                        // Black square       - Black square
                        0, 0, 0,  0, 0, 0,      0, 0, 0,  0, 0, 0,
                        0, 0, 0,  0, 0, 0,      0, 0, 0,  0, 0, 0,
                        // Black square       - Red square
                        0, 0, 0,  0, 0, 0,      255, 0, 0,  255, 0, 0,
                        0, 0, 0,  0, 0, 0,      255, 0, 0,  255, 0, 0,
                },
                pixels);

        f.setPropertyValue("rule", "2");

        image = DefaultPortrayalService.portray(canvasdef, scenedef);
        pixels = image.getTile(0, 0).getPixels(0, 0, 4, 4, new int[4*4*3]);
        assertArrayEquals(
                "Expects only second rule to be rendered, displaying a green square in upper left corner",
                new int[] {
                        // Green square         - Black square
                        0, 255, 0,  0, 255, 0,    0, 0, 0,  0, 0, 0,
                        0, 255, 0,  0, 255, 0,    0, 0, 0,  0, 0, 0,
                        // Black square         - Red square
                        0, 0, 0,  0, 0, 0,        0, 0, 0,  0, 0, 0,
                        0, 0, 0,  0, 0, 0,        0, 0, 0,  0, 0, 0,
                },
                pixels);
    }

    /**
     * Verify that two symbols using different geometries are properly displayed, using the right associated geometry.
     */
    @Test
    public void renderTwoSymbols() throws Exception {
        final var gf = Factory.INSTANCE.factory(false);
        var ftb = new FeatureTypeBuilder().setName("render-two-symbols");
        ftb.addAttribute(Point.class)
           .setName("geometry")
           .setCRS(CommonCRS.defaultGeographic())
           .addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Point.class)
           .setName("the_geom");
        var fType = ftb.build();

        final var f = fType.newInstance();
        final var upperLeftPt = gf.createPoint(new Coordinate(4.5d, 44.5d));
        JTS.setCoordinateReferenceSystem(upperLeftPt, CommonCRS.defaultGeographic());
        f.setPropertyValue("the_geom", upperLeftPt);

        final var lowerRightPt = gf.createPoint(new Coordinate(5.5d, 43.5d));
        JTS.setCoordinateReferenceSystem(upperLeftPt, CommonCRS.defaultGeographic());
        f.setPropertyValue("geometry", lowerRightPt);

        var layer = MapBuilder.createLayer(new InMemoryFeatureSet(fType, List.of(f)));
        layer.setStyle(SF.style(
                SF.pointSymbolizer(SF.graphic(List.of(SF.mark(FF.literal("square"), SF.stroke(Color.YELLOW, 0), SF.fill(Color.RED))), FF.literal(1), FF.literal(2), null, SF.anchorPoint(0.5, 0.5), SF.displacement()), null),
                SF.pointSymbolizer(SF.graphic(List.of(SF.mark(FF.literal("square"), SF.stroke(Color.YELLOW, 0), SF.fill(Color.GREEN))), FF.literal(1), FF.literal(2), null, SF.anchorPoint(0.5, 0.5), SF.displacement()), "the_geom")
        ));
        var ctx = MapBuilder.createContext(CommonCRS.defaultGeographic());
        ctx.getComponents().add(layer);

        final SceneDef scenedef = new SceneDef(ctx);
        final CanvasDef canvasdef = new CanvasDef(new Dimension(4, 4), new Envelope2D(CommonCRS.defaultGeographic(), 4, 43, 2, 2));
        canvasdef.setBackground(Color.BLACK);

        var image = DefaultPortrayalService.portray(canvasdef, scenedef);
        var pixels = image.getTile(0, 0).getPixels(0, 0, 4, 4, new int[4*4*3]);
        assertArrayEquals(
                "Expects a green square in upper-left corner, and a red square in lower right corner",
                new int[] {
                        // Green square         - Black square
                        0, 255, 0,  0, 255, 0,    0, 0, 0,  0, 0, 0,
                        0, 255, 0,  0, 255, 0,    0, 0, 0,  0, 0, 0,
                        // Black square         - Red square
                        0, 0, 0,  0, 0, 0,        255, 0, 0,  255, 0, 0,
                        0, 0, 0,  0, 0, 0,        255, 0, 0,  255, 0, 0,
                },
                pixels);

    }

    /*
     * Current implementation assume that we have a buffered image.
     * TODO: update this method if this is no longer the case.
     */
    private static int getRGB(RenderedImage image, int x, int y) {
        return ((BufferedImage) image).getRGB(x, y);
    }

    /**
     * Generate a black and white image.
     */
    private static RenderedImage createWorldImage(MapItem item) throws PortrayalException {
        final MapLayers context;
        if (item instanceof MapLayers) {
            context = (MapLayers) item;
        } else {
            context = MapBuilder.createContext();
            context.getComponents().add(item);
        }

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.defaultGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);

        final ColorModel cm = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY).getColorModel();
        final Hints hints = new Hints();
        hints.put(GO2Hints.KEY_COLOR_MODEL, cm);
        hints.put(Hints.KEY_ANTIALIASING, Hints.VALUE_ANTIALIAS_OFF);

        final SceneDef scenedef = new SceneDef(context, hints);
        final CanvasDef canvasdef = new CanvasDef(new Dimension(360, 180), env);
        canvasdef.setBackground(Color.WHITE);

        return DefaultPortrayalService.portray(canvasdef, scenedef);
    }

    private static MutableStyle createRotatedTriangle(double r) {
        final GraphicalSymbol gs = SF.mark(FF.literal("triangle"), SF.stroke(Color.BLUE, 0), SF.fill(Color.RED));
        final Expression opacity = FF.literal(1);
        final Expression size = FF.literal(40);
        final Expression rotation = FF.literal(r);
        final Graphic graphic = SF.graphic(Arrays.asList(gs), opacity, size, rotation, null, null);
        final PointSymbolizer symbol = SF.pointSymbolizer(graphic, null);
        return SF.style(symbol);
    }
}
