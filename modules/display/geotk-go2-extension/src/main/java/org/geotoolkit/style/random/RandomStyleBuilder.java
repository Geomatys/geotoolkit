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
package org.geotoolkit.style.random;

import org.geotoolkit.style.*;
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
import org.geotoolkit.data.FeatureCollection;

import org.geotoolkit.factory.Factory;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
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
 * Random style factory. This is a convini class if you dont need special styles.
 * This class will provide you simple et good looking styles for your maps.
 * 
 * @author Johann Sorel (Puzzle-GIS)
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

    private final MutableStyleFactory sf;
    private final FilterFactory ff;
    
    public RandomStyleBuilder() {
        this(null,null);
    }

    public RandomStyleBuilder(MutableStyleFactory styleFactory, FilterFactory filterFactory){
        if(styleFactory == null){
             sf = (MutableStyleFactory) FactoryFinder.getStyleFactory(new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));
        }else{
            sf = styleFactory;
        }

        if(filterFactory == null){
             ff = FactoryFinder.getFilterFactory(null);
        }else{
            ff = filterFactory;
        }

    }

    //----------------------creation--------------------------------------------
    public PointSymbolizer createPointSymbolizer() {
        
        final Unit uom = NonSI.PIXEL;
        final String geom = StyleConstants.DEFAULT_GEOM;
        final String name = null;
        
        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        
        final Fill fill =  sf.fill( sf.literal(randomColor()), ff.literal(0.6f) );
        final Stroke stroke =  sf.stroke(randomColor(), 1);
        final Mark mark =  sf.mark(randomPointShape(), stroke, fill);
        symbols.add(mark);
        
        final Expression opa = ff.literal(1);
        final Expression size = ff.literal(randomPointSize());
        final Expression rotation = ff.literal(0);
        final AnchorPoint anchor =  sf.anchorPoint(0, 0);
        final Displacement displacement =  sf.displacement(0, 0);
        
        final Graphic gra =  sf.graphic(symbols,opa,size,rotation,anchor,displacement);
        
        return  sf.pointSymbolizer(name,geom,StyleConstants.DEFAULT_DESCRIPTION,uom,gra);
    }

    public LineSymbolizer createLineSymbolizer() {
        
        final Unit uom = NonSI.PIXEL;
        final String geom = StyleConstants.DEFAULT_GEOM;
        final String name = null;
        
        final Stroke stroke =  sf.stroke(randomColor(), 1);
        final Expression offset = ff.literal(0);
        
        return  sf.lineSymbolizer(name,geom,StyleConstants.DEFAULT_DESCRIPTION,uom,stroke,offset);
    }

    public PolygonSymbolizer createPolygonSymbolizer() {
        
        final Unit uom = NonSI.PIXEL;
        final String geom = StyleConstants.DEFAULT_GEOM;
        final String name = null;
        
        final Fill fill =  sf.fill( sf.literal(randomColor()), ff.literal(0.6f) );
        final Stroke stroke =  sf.stroke(randomColor(), 1);
        
        final Displacement displacement =  sf.displacement(0, 0);
        final Expression offset = ff.literal(0);
        
        return  sf.polygonSymbolizer(name,geom,StyleConstants.DEFAULT_DESCRIPTION,uom,stroke, fill,displacement,offset);
    }

    public RasterSymbolizer createRasterSymbolizer() {
        return  sf.rasterSymbolizer();
    }

    public MutableStyle createPolygonStyle() {
        final PolygonSymbolizer ps = createPolygonSymbolizer();
        final MutableStyle style =  sf.style();
        style.featureTypeStyles().add( sf.featureTypeStyle(ps));

        return style;
    }

    public MutableStyle createDefaultVectorStyle(FeatureCollection<SimpleFeature> fs){

        final Symbolizer ps;

        final FeatureType typ = fs.getSchema();
        final AttributeDescriptor att = typ.getGeometryDescriptor();
        final AttributeType type = att.getType();

        final Class cla = type.getBinding();

        if (cla.equals(Polygon.class) || cla.equals(MultiPolygon.class)) {
            ps =  sf.polygonSymbolizer();
        } else if (cla.equals(LineString.class) || cla.equals(MultiLineString.class)) {
            ps =  sf.lineSymbolizer();
        } else if (cla.equals(Point.class) || cla.equals(MultiPoint.class)) {
            ps =  sf.pointSymbolizer();
        } else{
            ps =  sf.polygonSymbolizer();
        }

        final MutableStyle style =  sf.style();
        style.featureTypeStyles().add( sf.featureTypeStyle(ps));
        return style;
    }
    
    public MutableStyle createRandomVectorStyle(FeatureCollection<SimpleFeature> fs) {
        
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
            ps =  sf.polygonSymbolizer();
        }

        final MutableStyle style =  sf.style();
        style.featureTypeStyles().add( sf.featureTypeStyle(ps));
        return style;
    }

    public MutableStyle createRasterStyle() {
        final RasterSymbolizer raster =  sf.rasterSymbolizer();
        return  sf.style(new Symbolizer[]{raster});
    }

    //-----------------------random---------------------------------------------
    public int randomPointSize() {
        return SIZES[((int) (Math.random() * SIZES.length))];
    }

    public int randomWidth() {
        return WIDTHS[((int) (Math.random() * WIDTHS.length))];
    }

    public Literal randomPointShape() {
        return POINT_SHAPES[((int) (Math.random() * POINT_SHAPES.length))];
    }

    public Color randomColor() {
        return COLORS[((int) (Math.random() * COLORS.length))];
    }
}
