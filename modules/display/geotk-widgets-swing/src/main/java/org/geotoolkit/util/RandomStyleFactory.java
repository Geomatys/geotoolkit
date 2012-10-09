/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.FilterFactory;
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

import static org.geotoolkit.style.StyleConstants.*;
import org.opengis.filter.Filter;
import org.opengis.filter.MatchAction;

/**
 * Random style factory. This is a convini class if you dont need special styles.
 * This class will provide you simple et good looking styles for your maps.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class RandomStyleFactory extends Factory {

    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    private static final String[] POINT_SHAPES = {"square", "circle", "triangle", "star", "cross", "x"};
    private static final int[] SIZES = {8, 10, 12, 14, 16};
    private static final int[] WIDTHS = {1, 2};
    private static final Color[] COLORS = {
        Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY,
        Color.GRAY, Color.GREEN.darker(), Color.LIGHT_GRAY,
        Color.ORANGE, Color.RED, Color.YELLOW.darker()
    };

    private RandomStyleFactory() {
    }

    //----------------------creation--------------------------------------------
    public static PointSymbolizer createPointSymbolizer() {
        return createPointSymbolizer(randomColor());
    }

    public static LineSymbolizer createLineSymbolizer() {  
        return createLineSymbolizer(randomColor());
    }

    public static PolygonSymbolizer createPolygonSymbolizer() {        
        return createPolygonSymbolizer(randomColor());
    }
    
    private static PointSymbolizer createPointSymbolizer(Color color) {
                
        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();        
        final Fill fill = SF.fill(SF.literal(color), FF.literal(0.6f) );
        final Stroke stroke = SF.stroke(color, 1);
        final Mark mark = SF.mark(MARK_SQUARE, stroke, fill);
        symbols.add(mark);
                
        final Graphic gra = SF.graphic(
                symbols,
                LITERAL_ONE_FLOAT,
                FF.literal(randomPointSize()),
                LITERAL_ZERO_FLOAT,
                DEFAULT_ANCHOR_POINT,
                DEFAULT_DISPLACEMENT);
        
        return SF.pointSymbolizer(
                null,
                DEFAULT_GEOM,
                DEFAULT_DESCRIPTION,
                DEFAULT_UOM,
                gra);
    }

    private static LineSymbolizer createLineSymbolizer(Color color) {
        final Stroke stroke = SF.stroke(color, 1);
        return SF.lineSymbolizer(
                null,
                DEFAULT_GEOM,
                DEFAULT_DESCRIPTION,
                DEFAULT_UOM,
                stroke,
                LITERAL_ZERO_FLOAT);
    }

    private static PolygonSymbolizer createPolygonSymbolizer(Color color) {        
        final Fill fill = SF.fill(SF.literal(color), FF.literal(0.6f) );
        final Stroke stroke = SF.stroke(Color.DARK_GRAY, 1);
        return SF.polygonSymbolizer(
                null,
                DEFAULT_GEOM,
                DEFAULT_DESCRIPTION,
                DEFAULT_UOM,
                stroke,
                fill,
                DEFAULT_DISPLACEMENT,
                LITERAL_ZERO_FLOAT);
    }
    
    public static RasterSymbolizer createRasterSymbolizer() {
        return SF.rasterSymbolizer();
    }

    public static MutableStyle createDefaultVectorStyle(final FeatureCollection<SimpleFeature> fs){

        final Symbolizer ps;

        final FeatureType typ = fs.getFeatureType();
        final AttributeDescriptor att = typ.getGeometryDescriptor();
        if(att == null){
            return SF.style();
        }
        
        final AttributeType type = att.getType();

        final Class cla = type.getBinding();

        if (cla.equals(Polygon.class) || cla.equals(MultiPolygon.class)) {
            ps = SF.polygonSymbolizer();
        } else if (cla.equals(LineString.class) || cla.equals(MultiLineString.class)) {
            ps = SF.lineSymbolizer();
        } else if (cla.equals(Point.class) || cla.equals(MultiPoint.class)) {
            ps = SF.pointSymbolizer();
        } else{
            //geometry can be anything, create a style with a rule for each type
            final MutableRule mrpt = SF.rule(SF.pointSymbolizer());
            mrpt.setFilter(
                    FF.or(
                        FF.equal(FF.function("geometryType", FF.property(att.getLocalName())),FF.literal("Point"),false,MatchAction.ANY),
                        FF.equal(FF.function("geometryType", FF.property(att.getLocalName())),FF.literal("MultiPoint"),false,MatchAction.ANY)
                    ));
            
            final MutableRule mrl = SF.rule(SF.lineSymbolizer());
            mrl.setFilter(
                    FF.or(
                        FF.equal(FF.function("geometryType", FF.property(att.getLocalName())),FF.literal("LineString"),false,MatchAction.ANY),
                        FF.equal(FF.function("geometryType", FF.property(att.getLocalName())),FF.literal("MultiLineString"),false,MatchAction.ANY)
                    ));
            final MutableRule mrpo = SF.rule(SF.polygonSymbolizer());
            mrpo.setFilter(
                    FF.or(
                        FF.equal(FF.function("geometryType", FF.property(att.getLocalName())),FF.literal("Polygon"),false,MatchAction.ANY),
                        FF.equal(FF.function("geometryType", FF.property(att.getLocalName())),FF.literal("MultiPolygon"),false,MatchAction.ANY)
                    ));
            
            final MutableStyle style = SF.style();
            final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
            fts.rules().add(mrpt);
            fts.rules().add(mrl);
            fts.rules().add(mrpo);
            style.featureTypeStyles().add(fts);            
            return style;
        }

        final MutableStyle style = SF.style();
        style.featureTypeStyles().add(SF.featureTypeStyle(ps));
        return style;
    }
    
    public static MutableStyle createRandomVectorStyle(final FeatureCollection fs) {
        
        final Symbolizer ps;
        final FeatureType typ = fs.getFeatureType();
        final AttributeDescriptor att = typ.getGeometryDescriptor();

        if(att == null){
            return SF.style();
        }

        final AttributeType type = att.getType();
        final Class cla = type.getBinding();
        final Color color = randomColor();

        if (cla.equals(Polygon.class) || cla.equals(MultiPolygon.class)) {
            ps = createPolygonSymbolizer(color);
        } else if (cla.equals(LineString.class) || cla.equals(MultiLineString.class)) {
            ps = createLineSymbolizer(color);
        } else if (cla.equals(Point.class) || cla.equals(MultiPoint.class)) {
            ps = createPointSymbolizer(color);
        } else {
            //geometry can be anything, create a style with a rule for each type
            final MutableRule mrpt = SF.rule(createPointSymbolizer(color));
            mrpt.setFilter(
                    FF.or(
                        FF.equal(FF.function("geometryType", FF.property(att.getLocalName())),FF.literal("Point"),false,MatchAction.ANY),
                        FF.equal(FF.function("geometryType", FF.property(att.getLocalName())),FF.literal("MultiPoint"),false,MatchAction.ANY)
                    ));
            
            final MutableRule mrl = SF.rule(createLineSymbolizer(color));
            mrl.setFilter(
                    FF.or(
                        FF.equal(FF.function("geometryType", FF.property(att.getLocalName())),FF.literal("LineString"),false,MatchAction.ANY),
                        FF.equal(FF.function("geometryType", FF.property(att.getLocalName())),FF.literal("MultiLineString"),false,MatchAction.ANY)
                    ));
            final MutableRule mrpo = SF.rule(createPolygonSymbolizer(color));
            mrpo.setFilter(
                    FF.or(
                        FF.equal(FF.function("geometryType", FF.property(att.getLocalName())),FF.literal("Polygon"),false,MatchAction.ANY),
                        FF.equal(FF.function("geometryType", FF.property(att.getLocalName())),FF.literal("MultiPolygon"),false,MatchAction.ANY)
                    ));
            
            final MutableStyle style = SF.style();
            final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
            fts.rules().add(mrpt);
            fts.rules().add(mrl);
            fts.rules().add(mrpo);
            style.featureTypeStyles().add(fts);            
            return style;
        }

        final MutableStyle style = SF.style();
        style.featureTypeStyles().add(SF.featureTypeStyle(ps));
        return style;
    }

    public static MutableStyle createRasterStyle() {
        final RasterSymbolizer raster = SF.rasterSymbolizer();
        return SF.style(new Symbolizer[]{raster});
    }

    //-----------------------random---------------------------------------------
    private static int randomPointSize() {
        return SIZES[((int) (Math.random() * SIZES.length))];
    }

    private static int randomWidth() {
        return WIDTHS[((int) (Math.random() * WIDTHS.length))];
    }

    private static String randomPointShape() {
        return POINT_SHAPES[((int) (Math.random() * POINT_SHAPES.length))];
    }

    private static Color randomColor() {
        return COLORS[((int) (Math.random() * COLORS.length))];
    }
}
