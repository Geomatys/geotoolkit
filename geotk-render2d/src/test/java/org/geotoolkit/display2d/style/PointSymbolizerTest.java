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
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.map.MapItem;
import org.apache.sis.map.MapLayer;
import org.apache.sis.map.MapLayers;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.factory.Hints;
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
