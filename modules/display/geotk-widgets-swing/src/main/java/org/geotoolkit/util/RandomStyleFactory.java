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
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import org.geotoolkit.data.FeatureSource;

import org.geotoolkit.factory.Factory;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.MutableStyleFactory;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
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
 * Random style factory. This is a convini class if you dont need special styles.
 * This class will provide you simple et good looking styles for your maps.
 * 
 * @author Johann Sorel (Puzzle-GIS)
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
        
        final Unit uom = NonSI.PIXEL;
        final String geom = StyleConstants.DEFAULT_GEOM;
        final String name = null;
        
        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        
        final Fill fill = SF.fill(SF.literal(randomColor()), FF.literal(0.6f) );
        final Stroke stroke = SF.stroke(randomColor(), 1);
        final Mark mark = SF.mark(FF.literal("square"), stroke, fill);
        symbols.add(mark);
        
        final Expression opa = FF.literal(1);
        final Expression size = FF.literal(randomPointSize());
        final Expression rotation = FF.literal(0);
        final AnchorPoint anchor = SF.anchorPoint(0, 0);
        final Displacement displacement = SF.displacement(0, 0);
        
        final Graphic gra = SF.graphic(symbols,opa,size,rotation,anchor,displacement);
        
        return SF.pointSymbolizer(name,geom,StyleConstants.DEFAULT_DESCRIPTION,uom,gra);
    }

    public static LineSymbolizer createLineSymbolizer() {
        
        final Unit uom = NonSI.PIXEL;
        final String geom = StyleConstants.DEFAULT_GEOM;
        final String name = null;
        
        final Stroke stroke = SF.stroke(randomColor(), 1);
        final Expression offset = FF.literal(0);
        
        return SF.lineSymbolizer(name,geom,StyleConstants.DEFAULT_DESCRIPTION,uom,stroke,offset);
    }

    public static PolygonSymbolizer createPolygonSymbolizer() {
        
        final Unit uom = NonSI.PIXEL;
        final String geom = StyleConstants.DEFAULT_GEOM;
        final String name = null;
        
        final Fill fill = SF.fill(SF.literal(randomColor()), FF.literal(0.6f) );
        final Stroke stroke = SF.stroke(randomColor(), 1);
        
        final Displacement displacement = SF.displacement(0, 0);
        final Expression offset = FF.literal(0);
        
        return SF.polygonSymbolizer(name,geom,StyleConstants.DEFAULT_DESCRIPTION,uom,stroke, fill,displacement,offset);
    }

    public static RasterSymbolizer createRasterSymbolizer() {
        return SF.rasterSymbolizer();
    }

    public static MutableStyle createPolygonStyle() {
        final PolygonSymbolizer ps = createPolygonSymbolizer();
        final MutableStyle style = SF.style();
        style.featureTypeStyles().add(SF.featureTypeStyle(ps));

        return style;
    }

    public static MutableStyle createDefaultVectorStyle(FeatureSource<SimpleFeatureType, SimpleFeature> fs){

        final Symbolizer ps;

        final FeatureType typ = fs.getSchema();
        final AttributeDescriptor att = typ.getGeometryDescriptor();
        final AttributeType type = att.getType();

        final Class cla = type.getBinding();

        if (cla.equals(Polygon.class) || cla.equals(MultiPolygon.class)) {
            ps = SF.polygonSymbolizer();
        } else if (cla.equals(LineString.class) || cla.equals(MultiLineString.class)) {
            ps = SF.lineSymbolizer();
        } else if (cla.equals(Point.class) || cla.equals(MultiPoint.class)) {
            ps = SF.pointSymbolizer();
        } else{
            ps = SF.polygonSymbolizer();
        }

        final MutableStyle style = SF.style();
        style.featureTypeStyles().add(SF.featureTypeStyle(ps));
        return style;
    }
    
    public static MutableStyle createRandomVectorStyle(FeatureSource<SimpleFeatureType, SimpleFeature> fs) {
        
        final Symbolizer ps;
        final FeatureType typ = fs.getSchema();
        final AttributeDescriptor att = typ.getGeometryDescriptor();
        final AttributeType type = att.getType();
        final Class cla = type.getBinding();

        if (cla.equals(Polygon.class) || cla.equals(MultiPolygon.class)) {
            ps = createPolygonSymbolizer();
        } else if (cla.equals(LineString.class) || cla.equals(MultiLineString.class)) {
            ps = createLineSymbolizer();
        } else if (cla.equals(Point.class) || cla.equals(MultiPoint.class)) {
            ps = createPointSymbolizer();
        } else{
            ps = SF.polygonSymbolizer();
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
