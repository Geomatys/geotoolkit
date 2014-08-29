/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.style;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.AttributeType;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.style.AnchorPoint;
import org.opengis.style.Displacement;
import org.opengis.style.Fill;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Mark;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Stroke;
import org.opengis.style.Symbolizer;

/**
 * Random style builder. This is a convini class if you dont need special styles.
 * This class will provide you simple et good looking styles for your maps.
 *
 * @author Johann Sorel
 * @module pending
 */
public class RandomStyleBuilder extends Factory {

    private static final Literal[] POINT_SHAPES = {
                                    StyleConstants.MARK_SQUARE,
                                    StyleConstants.MARK_CIRCLE,
                                    StyleConstants.MARK_TRIANGLE,
                                    StyleConstants.MARK_STAR,
                                    StyleConstants.MARK_CROSS,
                                    StyleConstants.MARK_X};
    private static final int[] SIZES = {8, 10, 12, 14, 16};
    private static final int[] WIDTHS = {1, 2};
    private static final Color[] COLORS = {
        Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY,
        Color.GRAY, Color.GREEN.darker(), Color.LIGHT_GRAY,
        Color.ORANGE, Color.RED, Color.YELLOW.darker()
    };

    private static final MutableStyleFactory SF = (MutableStyleFactory)
            FactoryFinder.getStyleFactory(new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    private RandomStyleBuilder() {}


    public  static PointSymbolizer createRandomPointSymbolizer() {

        final Unit uom = NonSI.PIXEL;
        final String geom = StyleConstants.DEFAULT_GEOM;
        final String name = null;

        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();

        final Fill fill =  SF.fill( SF.literal(randomColor()), FF.literal(0.6f) );
        final Stroke stroke =  SF.stroke(randomColor(), 1);
        final Mark mark =  SF.mark(randomMarkShape(), stroke, fill);
        symbols.add(mark);

        final Expression opa = FF.literal(1);
        final Expression size = FF.literal(randomPointSize());
        final Expression rotation = FF.literal(0);
        final AnchorPoint anchor =  SF.anchorPoint(0, 0);
        final Displacement displacement =  SF.displacement(0, 0);

        final Graphic gra =  SF.graphic(symbols,opa,size,rotation,anchor,displacement);

        return  SF.pointSymbolizer(name,geom,StyleConstants.DEFAULT_DESCRIPTION,uom,gra);
    }

    public  static LineSymbolizer createRandomLineSymbolizer() {

        final Unit uom = NonSI.PIXEL;
        final String geom = StyleConstants.DEFAULT_GEOM;
        final String name = null;

        final Stroke stroke =  SF.stroke(randomColor(), 1);
        final Expression offset = FF.literal(0);

        return  SF.lineSymbolizer(name,geom,StyleConstants.DEFAULT_DESCRIPTION,uom,stroke,offset);
    }

    public  static PolygonSymbolizer createRandomPolygonSymbolizer() {

        final Unit uom = NonSI.PIXEL;
        final String geom = StyleConstants.DEFAULT_GEOM;
        final String name = null;

        final Fill fill =  SF.fill( SF.literal(randomColor()), FF.literal(0.6f) );
        final Stroke stroke =  SF.stroke(randomColor(), 1);

        final Displacement displacement =  SF.displacement(0, 0);
        final Expression offset = FF.literal(0);

        return  SF.polygonSymbolizer(name,geom,StyleConstants.DEFAULT_DESCRIPTION,uom,stroke, fill,displacement,offset);
    }

    public  static MutableStyle createDefaultVectorStyle(final FeatureType typ){

        final Symbolizer ps;

        final AttributeDescriptor att = typ.getGeometryDescriptor();
        // Can be null
        if (att == null) {
            return SF.style();
        }
        final AttributeType type = att.getType();

        final Class cla = type.getBinding();

        if (cla.equals(Polygon.class) || cla.equals(MultiPolygon.class)) {
            ps =  SF.polygonSymbolizer();
        } else if (cla.equals(LineString.class) || cla.equals(MultiLineString.class)) {
            ps =  SF.lineSymbolizer();
        } else if (cla.equals(Point.class) || cla.equals(MultiPoint.class)) {
            ps =  SF.pointSymbolizer();
        } else{
            //multiple types, create rules
            final MutableStyle style = SF.style();
            final MutableFeatureTypeStyle fts = SF.featureTypeStyle();

            final MutableRule rulePoint = SF.rule(StyleConstants.DEFAULT_POINT_SYMBOLIZER);
            rulePoint.setFilter(FF.or(
                                    FF.equals(FF.function("geometryType", FF.property("geometry")), FF.literal("Point")),
                                    FF.equals(FF.function("geometryType", FF.property("geometry")), FF.literal("MultiPoint"))
                                ));
            final MutableRule ruleLine = SF.rule(StyleConstants.DEFAULT_LINE_SYMBOLIZER);
            ruleLine.setFilter(FF.or(
                                    FF.equals(FF.function("geometryType", FF.property("geometry")), FF.literal("LineString")),
                                    FF.equals(FF.function("geometryType", FF.property("geometry")), FF.literal("MultiLineString"))
                                ));
            final MutableRule rulePolygon = SF.rule(StyleConstants.DEFAULT_POLYGON_SYMBOLIZER);
            rulePolygon.setFilter(FF.or(
                                    FF.equals(FF.function("geometryType", FF.property("geometry")), FF.literal("Polygon")),
                                    FF.equals(FF.function("geometryType", FF.property("geometry")), FF.literal("MultiPolygon"))
                                ));

            fts.rules().add(rulePoint);
            fts.rules().add(ruleLine);
            fts.rules().add(rulePolygon);
            style.featureTypeStyles().add(fts);
            return style;
        }

        final MutableStyle style =  SF.style();
        style.featureTypeStyles().add( SF.featureTypeStyle(ps));
        return style;
    }

    public  static MutableStyle createRandomVectorStyle(final FeatureType typ) {

        final Symbolizer ps;
        final AttributeDescriptor att = typ.getGeometryDescriptor();
        // Can be null
        if (att == null) {
            return SF.style();
        }
        final AttributeType type = att.getType();
        final Class cla = type.getBinding();

        if (cla.equals(Polygon.class) || cla.equals(MultiPolygon.class)) {
            ps = createRandomPolygonSymbolizer();
        } else if (cla.equals(LineString.class) || cla.equals(MultiLineString.class)) {
            ps = createRandomLineSymbolizer();
        } else if (cla.equals(Point.class) || cla.equals(MultiPoint.class)) {
            ps = createRandomPointSymbolizer();
        } else{
            //multiple types, create rules
            final MutableStyle style = SF.style();
            final MutableFeatureTypeStyle fts = SF.featureTypeStyle();

            final MutableRule rulePoint = SF.rule(createRandomPointSymbolizer());
            rulePoint.setFilter(FF.or(
                                    FF.equals(FF.function("geometryType", FF.property("geometry")), FF.literal("Point")),
                                    FF.equals(FF.function("geometryType", FF.property("geometry")), FF.literal("MultiPoint"))
                                ));
            final MutableRule ruleLine = SF.rule(createRandomLineSymbolizer());
            ruleLine.setFilter(FF.or(
                                    FF.equals(FF.function("geometryType", FF.property("geometry")), FF.literal("LineString")),
                                    FF.equals(FF.function("geometryType", FF.property("geometry")), FF.literal("MultiLineString"))
                                ));
            final MutableRule rulePolygon = SF.rule(createRandomPolygonSymbolizer());
            rulePolygon.setFilter(FF.or(
                                    FF.equals(FF.function("geometryType", FF.property("geometry")), FF.literal("Polygon")),
                                    FF.equals(FF.function("geometryType", FF.property("geometry")), FF.literal("MultiPolygon"))
                                ));

            fts.rules().add(rulePoint);
            fts.rules().add(ruleLine);
            fts.rules().add(rulePolygon);
            style.featureTypeStyles().add(fts);
            return style;
        }

        final MutableStyle style =  SF.style();
        style.featureTypeStyles().add( SF.featureTypeStyle(ps));
        return style;
    }

    public static MutableStyle createDefaultRasterStyle() {
        final RasterSymbolizer raster =  SF.rasterSymbolizer();
        return  SF.style(new Symbolizer[]{raster});
    }

    /**
     * @return random point size (8,10,12,14,16)
     */
    public  static int randomPointSize() {
        return SIZES[((int) (Math.random() * SIZES.length))];
    }

    /**
     * @return random width (1,2)
     */
    public  static int randomWidth() {
        return WIDTHS[((int) (Math.random() * WIDTHS.length))];
    }

    /**
     * @return random mark literal (circle, square, triangle, ...)
     */
    public static Literal randomMarkShape() {
        return POINT_SHAPES[((int) (Math.random() * POINT_SHAPES.length))];
    }

    /**
     * @return random color
     */
    public static Color randomColor() {
        return COLORS[((int) (Math.random() * COLORS.length))];
    }
}
