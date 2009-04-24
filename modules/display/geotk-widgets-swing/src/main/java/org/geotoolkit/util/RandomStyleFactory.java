/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

import org.geotools.data.FeatureSource;

import org.geotoolkit.factory.Factory;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
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
import org.opengis.style.Description;
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

    private MutableStyleFactory SF = new DefaultStyleFactory();
    private FilterFactory FF = FactoryFinder.getFilterFactory(null);
    private final String[] POINT_SHAPES = {"square", "circle", "triangle", "star", "cross", "x"};
    private final int[] SIZES = {8, 10, 12, 14, 16};
    private final int[] WIDTHS = {1, 2};
    private final Color[] COLORS = {
        Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY,
        Color.GRAY, Color.GREEN.darker(), Color.LIGHT_GRAY,
        Color.ORANGE, Color.RED, Color.YELLOW.darker()
    };

    public RandomStyleFactory() {
        
    }

    //------------------duplicates----------------------------------------------
    public MutableStyle duplicate(MutableStyle style) {
        return style;
//        DuplicatingStyleVisitor xerox = new DuplicatingStyleVisitor();
//        style.accept(xerox);
//        return (Style) xerox.getCopy();
    }

    public MutableFeatureTypeStyle duplicate(MutableFeatureTypeStyle fts) {
        return fts;
//        DuplicatingStyleVisitor xerox = new DuplicatingStyleVisitor();
//        fts.accept(xerox);
//        return (FeatureTypeStyle) xerox.getCopy();
    }

    public MutableRule duplicate(MutableRule rule) {
        return rule;
//        DuplicatingStyleVisitor xerox = new DuplicatingStyleVisitor();
//        rule.accept(xerox);
//        return (Rule) xerox.getCopy();
    }

    //----------------------creation--------------------------------------------
    public PointSymbolizer createPointSymbolizer() {
        
        final Unit uom = NonSI.PIXEL;
        final String geom = StyleConstants.DEFAULT_GEOM;
        final String name = null;
        final Description desc = SF.description("title", "abs");
        
        List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        
        Fill fill = SF.fill(SF.literal(randomColor()), FF.literal(0.6f) );
        Stroke stroke = SF.stroke(randomColor(), 1);
        Mark mark = SF.mark(FF.literal("square"), stroke, fill);
        symbols.add(mark);
        
        Expression opa = FF.literal(1);
        Expression size = FF.literal(randomPointSize());
        Expression rotation = FF.literal(0);
        AnchorPoint anchor = SF.anchorPoint(0, 0);
        Displacement displacement = SF.displacement(0, 0);
        
        Graphic gra = SF.graphic(symbols,opa,size,rotation,anchor,displacement);
        
        return SF.pointSymbolizer(name,geom,desc,uom,gra);
    }

    public LineSymbolizer createLineSymbolizer() {
        
        final Unit uom = NonSI.PIXEL;
        final String geom = StyleConstants.DEFAULT_GEOM;
        final String name = null;
        final Description desc = SF.description("title", "abs");
        
        Stroke stroke = SF.stroke(randomColor(), 1);
        Expression offset = FF.literal(0);
        
        return SF.lineSymbolizer(name,geom,desc,uom,stroke,offset);
    }

    public PolygonSymbolizer createPolygonSymbolizer() {
        
        final Unit uom = NonSI.PIXEL;
        final String geom = StyleConstants.DEFAULT_GEOM;
        final String name = null;
        final Description desc = SF.description("title", "abs");
        
        Fill fill = SF.fill(SF.literal(randomColor()), FF.literal(0.6f) );
        Stroke stroke = SF.stroke(randomColor(), 1);
        
        Displacement displacement = SF.displacement(0, 0);
        Expression offset = FF.literal(0);
        
        return SF.polygonSymbolizer(name,geom,desc,uom,stroke, fill,displacement,offset);
    }

    public RasterSymbolizer createRasterSymbolizer() {
        return SF.rasterSymbolizer();
    }

    public MutableStyle createPolygonStyle() {
        
        MutableStyle style = null;

        PolygonSymbolizer ps = createPolygonSymbolizer();

        style = SF.style();
        style.featureTypeStyles().add(SF.featureTypeStyle(ps));

        return style;
    }

    public MutableStyle createDefaultVectorStyle(FeatureSource<SimpleFeatureType, SimpleFeature> fs){
        MutableStyle style = null;

        Symbolizer ps = SF.polygonSymbolizer();  //createPolygonSymbolizer(randomColor(), randomWidth());

        try {
            FeatureType typ = fs.getSchema();
            AttributeDescriptor att = typ.getGeometryDescriptor();
            AttributeType type = att.getType();

            Class cla = type.getBinding();

            if (cla.equals(Polygon.class) || cla.equals(MultiPolygon.class)) {
                ps = SF.polygonSymbolizer();
            } else if (cla.equals(LineString.class) || cla.equals(MultiLineString.class)) {
                ps = SF.lineSymbolizer();
            } else if (cla.equals(Point.class) || cla.equals(MultiPoint.class)) {
                ps = SF.pointSymbolizer();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        style = SF.style();
        style.featureTypeStyles().add(SF.featureTypeStyle(ps));

        return style;
    }
    
    public MutableStyle createRandomVectorStyle(FeatureSource<SimpleFeatureType, SimpleFeature> fs) {
        MutableStyle style = null;

        Symbolizer ps = SF.polygonSymbolizer();  //createPolygonSymbolizer(randomColor(), randomWidth());

        try {
            FeatureType typ = fs.getSchema();
            AttributeDescriptor att = typ.getGeometryDescriptor();
            AttributeType type = att.getType();

            Class cla = type.getBinding();

            if (cla.equals(Polygon.class) || cla.equals(MultiPolygon.class)) {
                ps = createPolygonSymbolizer();
            } else if (cla.equals(LineString.class) || cla.equals(MultiLineString.class)) {
                ps = createLineSymbolizer();
            } else if (cla.equals(Point.class) || cla.equals(MultiPoint.class)) {
                ps = createPointSymbolizer();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        style = SF.style();
        style.featureTypeStyles().add(SF.featureTypeStyle(ps));

        return style;
    }

    public MutableStyle createRasterStyle() {
        MutableStyle style = null;

        RasterSymbolizer raster = SF.rasterSymbolizer();

        style = SF.style(new Symbolizer[]{raster});
        return style;
    }

    //-----------------------random---------------------------------------------
    private int randomPointSize() {
        return SIZES[((int) (Math.random() * SIZES.length))];
    }

    private int randomWidth() {
        return WIDTHS[((int) (Math.random() * WIDTHS.length))];
    }

    private String randomPointShape() {
        return POINT_SHAPES[((int) (Math.random() * POINT_SHAPES.length))];
    }

    private Color randomColor() {
        return COLORS[((int) (Math.random() * COLORS.length))];
    }
}
