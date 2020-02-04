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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.measure.Unit;
import org.apache.sis.feature.Features;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.measure.Units;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
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
import org.opengis.style.StyleFactory;
import org.opengis.style.Symbolizer;

/**
 * Random style builder. This is a convini class if you dont need special styles.
 * This class will provide you simple et good looking styles for your maps.
 *
 * @author Johann Sorel
 * @module
 */
public class RandomStyleBuilder {

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

    private static final MutableStyleFactory SF = (MutableStyleFactory) DefaultFactories.forBuildin(StyleFactory.class);
    private static final FilterFactory FF = DefaultFactories.forBuildin(FilterFactory.class);

    private RandomStyleBuilder() {}


    public  static PointSymbolizer createRandomPointSymbolizer() {

        final Unit uom = Units.POINT;
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
        final AnchorPoint anchor =  SF.anchorPoint(0.5, 0.5);
        final Displacement displacement =  SF.displacement(0, 0);

        final Graphic gra =  SF.graphic(symbols,opa,size,rotation,anchor,displacement);

        return  SF.pointSymbolizer(name,geom,StyleConstants.DEFAULT_DESCRIPTION,uom,gra);
    }

    public  static LineSymbolizer createRandomLineSymbolizer() {

        final Unit uom = Units.POINT;
        final String geom = StyleConstants.DEFAULT_GEOM;
        final String name = null;

        final Stroke stroke =  SF.stroke(randomColor(), 1);
        final Expression offset = FF.literal(0);

        return  SF.lineSymbolizer(name,geom,StyleConstants.DEFAULT_DESCRIPTION,uom,stroke,offset);
    }

    public  static PolygonSymbolizer createRandomPolygonSymbolizer() {

        final Unit uom = Units.POINT;
        final String geom = StyleConstants.DEFAULT_GEOM;
        final String name = null;

        final Fill fill =  SF.fill( SF.literal(randomColor()), FF.literal(0.6f) );
        final Stroke stroke =  SF.stroke(randomColor(), 1);

        final Displacement displacement =  SF.displacement(0, 0);
        final Expression offset = FF.literal(0);

        return  SF.polygonSymbolizer(name,geom,StyleConstants.DEFAULT_DESCRIPTION,uom,stroke, fill,displacement,offset);
    }

    public  static MutableStyle createDefaultVectorStyle(final FeatureType typ) {
        return createVectorStyle(
                typ,
                SF::polygonSymbolizer,
                SF::pointSymbolizer,
                SF::lineSymbolizer
        );
    }

    public  static MutableStyle createRandomVectorStyle(final FeatureType typ) {
        return createVectorStyle(
                typ,
                RandomStyleBuilder::createRandomPolygonSymbolizer,
                RandomStyleBuilder::createRandomPointSymbolizer,
                RandomStyleBuilder::createRandomLineSymbolizer
        );
    }

    public  static MutableStyle createVectorStyle(final FeatureType typ, Supplier<PolygonSymbolizer> polygonSymbol, Supplier<PointSymbolizer> pointSymbol, Supplier<LineSymbolizer> lineSymbol) {
        final Symbolizer ps;
        final PropertyType defAtt;
        try{
            defAtt = typ.getProperty(AttributeConvention.GEOMETRY_PROPERTY.toString());
        }catch(PropertyNotFoundException ex){
            return SF.style();
        }
        final AttributeType type = Features.toAttribute(defAtt)
                .orElse(null);
        if (type == null) return SF.style();
        final Class cla = type.getValueClass();

        if (cla.equals(Polygon.class) || cla.equals(MultiPolygon.class)) {
            ps = polygonSymbol.get();
        } else if (cla.equals(LineString.class) || cla.equals(MultiLineString.class)) {
            ps = lineSymbol.get();
        } else if (cla.equals(Point.class) || cla.equals(MultiPoint.class)) {
            ps = pointSymbol.get();
        } else {
            //multiple types, create rules
            final MutableStyle style = SF.style();
            final MutableFeatureTypeStyle fts = SF.featureTypeStyle();

            final MutableRule rulePoint = SF.rule(pointSymbol.get());
            rulePoint.setFilter(FF.or(
                                    FF.equals(FF.function("geometryType", FF.property(type.getName().toString())), FF.literal("Point")),
                                    FF.equals(FF.function("geometryType", FF.property(type.getName().toString())), FF.literal("MultiPoint"))
                                ));
            final MutableRule ruleLine = SF.rule(lineSymbol.get());
            ruleLine.setFilter(FF.or(
                                    FF.equals(FF.function("geometryType", FF.property(type.getName().toString())), FF.literal("LineString")),
                                    FF.equals(FF.function("geometryType", FF.property(type.getName().toString())), FF.literal("MultiLineString"))
                                ));
            final MutableRule rulePolygon = SF.rule(polygonSymbol.get());
            rulePolygon.setFilter(FF.or(
                                    FF.equals(FF.function("geometryType", FF.property(type.getName().toString())), FF.literal("Polygon")),
                                    FF.equals(FF.function("geometryType", FF.property(type.getName().toString())), FF.literal("MultiPolygon"))
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
