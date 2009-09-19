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
package org.geotoolkit.style.category;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geotoolkit.data.DefaultQuery;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.collection.FeatureIterator;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.interval.RandomPalette;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.style.Fill;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Mark;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.Stroke;
import org.opengis.style.Symbolizer;

/**
 * Random style factory. This is a convini class if you dont need special styles.
 * This class will provide you simple et good looking styles for your maps.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class CategoryStyleBuilder extends Factory {

    private final MutableStyleFactory sf;
    private final FilterFactory ff;

    private final List<Rule> rules = new ArrayList<Rule>();
    private final List<PropertyName> properties = new ArrayList<PropertyName>();
    private Class<? extends Symbolizer> expectedType = null;
    private boolean other = false;
    private PropertyName currentProperty = null;
    private FeatureMapLayer layer;

    private Symbolizer template;
    private RandomPalette palette;

    public CategoryStyleBuilder() {
        this(null,null);
    }

    public CategoryStyleBuilder(MutableStyleFactory styleFactory, FilterFactory filterFactory){
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

    public void analyze(FeatureMapLayer layer){
        this.layer = layer;
        rules.clear();

        properties.clear();
        if(layer != null){
            SimpleFeatureType schema = layer.getFeatureSource().getSchema();

            for(PropertyDescriptor desc : schema.getDescriptors()){
                Class<?> type = desc.getType().getBinding();

                if(!Geometry.class.isAssignableFrom(type)){
                    properties.add(ff.property(desc.getName().getLocalPart()));
                }
            }

            //find the geometry class for template
            GeometryDescriptor geo = schema.getGeometryDescriptor();
            Class<?> geoClass = geo.getType().getBinding();

            if(Polygon.class.isAssignableFrom(geoClass) || MultiPolygon.class.isAssignableFrom(geoClass)){
                Stroke stroke = sf.stroke(Color.BLACK, 1);
                Fill fill = sf.fill(Color.BLUE);
                template = sf.polygonSymbolizer(stroke,fill,null);
                expectedType = PolygonSymbolizer.class;
            }else if(LineString.class.isAssignableFrom(geoClass) || MultiLineString.class.isAssignableFrom(geoClass)){
                Stroke stroke = sf.stroke(Color.BLUE, 2);
                template = sf.lineSymbolizer(stroke,null);
                expectedType = LineSymbolizer.class;
            }else{
                Stroke stroke = sf.stroke(Color.BLACK, 1);
                Fill fill = sf.fill(Color.BLUE);
                List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
                symbols.add(sf.mark(StyleConstants.MARK_CIRCLE, fill, stroke));
                Graphic gra = sf.graphic(symbols, ff.literal(1), ff.literal(12), ff.literal(0), sf.anchorPoint(), sf.displacement());
                template = sf.pointSymbolizer(gra, null);
                expectedType = PointSymbolizer.class;
            }


            //try to rebuild the previous analyze if it was one
            List<MutableFeatureTypeStyle> ftss = layer.getStyle().featureTypeStyles();

            if(ftss.size() == 1){
                MutableFeatureTypeStyle fts = ftss.get(0);

                List<MutableRule> rules = fts.rules();

                for(Rule r : rules){
                    List<? extends Symbolizer> symbols = r.symbolizers();

                    if(symbols.size() != 1) break;

                    Symbolizer symbol = symbols.get(0);
                    if(expectedType.isInstance(symbol)){

                        if(r.isElseFilter()){
                            //it looks like it's a valid classification "other" rule
                            rules.add((MutableRule) r);
                            template = symbol;
                            other = true;
                        }else{
                            Filter f = r.getFilter();
                            if(f != null && f instanceof PropertyIsEqualTo){
                                PropertyIsEqualTo equal = (PropertyIsEqualTo) f;
                                Expression exp1 = equal.getExpression1();
                                Expression exp2 = equal.getExpression2();

                                if(exp1 instanceof PropertyName && exp2 instanceof Literal){
                                    if(properties.contains(exp1)){
                                        //it looks like it's a valid classification property rule
                                        rules.add((MutableRule) r);
                                        template = symbol;
                                        currentProperty = (PropertyName) exp1;
                                    }else{
                                        //property is not in the schema
                                        break;
                                    }
                                }else if(exp2 instanceof PropertyName && exp1 instanceof Literal){
                                    if(properties.contains(exp2)){
                                        //it looks like it's a valid classification property rule
                                        rules.add((MutableRule) r);
                                        template = symbol;
                                        currentProperty = (PropertyName) exp2;
                                    }else{
                                        //property is not in the schema
                                        break;
                                    }
                                }else{
                                    //mismatch analyze structure
                                    break;
                                }
                            }
                        }

                    }else{
                        break;
                    }

                }

            }
        }
    }

    public Symbolizer getTemplate() {
        return template;
    }

    public void setTemplate(Symbolizer template) {
        this.template = template;
    }

    public RandomPalette getPalette() {
        return palette;
    }

    public void setPalette(RandomPalette palette) {
        this.palette = palette;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public List<PropertyName> getProperties() {
        return properties;
    }

    public void setCurrentProperty(PropertyName currentProperty) {
        this.currentProperty = currentProperty;
    }

    public PropertyName getCurrentProperty() {
        return currentProperty;
    }

    public boolean isOther() {
        return other;
    }

    public void setOther(boolean other) {
        this.other = other;
    }

    public List<Rule> create(){
        //search the different values
        final Set<Object> differentValues = new HashSet<Object>();
        final PropertyName property = currentProperty;
        final DefaultQuery query = new DefaultQuery();
        query.setPropertyNames(new String[]{property.getPropertyName()});

        FeatureIterator<SimpleFeature> features = null;
        try{
            features = layer.getFeatureSource().getFeatures(query).features();
            while(features.hasNext()){
                final SimpleFeature feature = features.next();
                differentValues.add(property.evaluate(feature));
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }finally{
            if(features != null){
                features.close();
            }
        }

        //generate the different rules
        rules.clear();

        for(Object obj : differentValues){
            rules.add(createRule(property, obj));
        }

        //generate the other rule if asked
        if(other){
            MutableRule r = sf.rule(createSymbolizer());
            r.setElseFilter(true);
            r.setDescription(sf.description("other", "other"));
            rules.add(r);
        }

        return rules;
    }


    public Symbolizer createSymbolizer(){
        return derivateSymbolizer(template, palette.next());
    }

    /**
     * Derivate a symbolizer with a new color.
     */
    public Symbolizer derivateSymbolizer(Symbolizer symbol, Color color){

        if(symbol instanceof PolygonSymbolizer){
            PolygonSymbolizer ps = (PolygonSymbolizer)symbol;
            Fill fill = sf.fill(sf.literal(color),ps.getFill().getOpacity());
            return sf.polygonSymbolizer(ps.getName(), ps.getGeometryPropertyName(),
                    ps.getDescription(), ps.getUnitOfMeasure(),
                    ps.getStroke(),fill,ps.getDisplacement(),ps.getPerpendicularOffset());
        }else if(symbol instanceof LineSymbolizer){
            LineSymbolizer ls = (LineSymbolizer) symbol;
            Stroke oldStroke = ls.getStroke();
            Stroke stroke = sf.stroke(sf.literal(color),oldStroke.getOpacity(),oldStroke.getWidth(),
                    oldStroke.getLineJoin(),oldStroke.getLineCap(),oldStroke.getDashArray(),oldStroke.getDashOffset());
            return sf.lineSymbolizer(ls.getName(), ls.getGeometryPropertyName(),
                    ls.getDescription(), ls.getUnitOfMeasure(), stroke, ls.getPerpendicularOffset());
        }else if(symbol instanceof PointSymbolizer){
            PointSymbolizer ps = (PointSymbolizer) symbol;
            Graphic oldGraphic = ps.getGraphic();
            Mark oldMark = (Mark) oldGraphic.graphicalSymbols().get(0);
            Fill fill = sf.fill(sf.literal(color),oldMark.getFill().getOpacity());
            List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
            symbols.add(sf.mark(oldMark.getWellKnownName(), fill, oldMark.getStroke()));
            Graphic graphic = sf.graphic(symbols, oldGraphic.getOpacity(),oldGraphic.getSize(),
                    oldGraphic.getRotation(),oldGraphic.getAnchorPoint(),oldGraphic.getDisplacement());
            return sf.pointSymbolizer(graphic,ps.getGeometryPropertyName());
        }else{
            throw new IllegalArgumentException("unexpected symbolizer type : " + symbol);
        }

    }

    public Rule createRule(PropertyName property, Object obj){
        MutableRule r = sf.rule(createSymbolizer());
        r.setFilter(ff.equals(property, ff.literal(obj)));
        r.setDescription(sf.description(obj.toString(), obj.toString()));
        return r;
    }
}
