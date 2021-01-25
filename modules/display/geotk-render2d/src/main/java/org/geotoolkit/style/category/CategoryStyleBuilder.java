/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.sis.feature.Features;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.storage.feature.query.QueryBuilder;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.interval.RandomPalette;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
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
import org.opengis.style.StyleFactory;
import org.opengis.style.Symbolizer;

/**
 * Random style factory. This is a convini class if you dont need special styles.
 * This class will provide you simple et good looking styles for your maps.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
public class CategoryStyleBuilder {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.style");

    private final MutableStyleFactory sf;
    private final FilterFactory ff;

    private final MutableFeatureTypeStyle fts;
    private final List<PropertyName> properties = new ArrayList<PropertyName>();
    private Class<? extends Symbolizer> expectedType = null;
    private boolean other = false;
    private PropertyName currentProperty = null;
    private MapLayer layer;

    private Symbolizer template;
    private RandomPalette palette;

    public CategoryStyleBuilder() {
        this(null,null);
    }

    public CategoryStyleBuilder(final MutableStyleFactory styleFactory, final FilterFactory filterFactory){
        if(styleFactory == null){
             sf = (MutableStyleFactory) DefaultFactories.forBuildin(StyleFactory.class);
        }else{
            sf = styleFactory;
        }

        if(filterFactory == null){
             ff = DefaultFactories.forBuildin(FilterFactory.class);
        }else{
            ff = filterFactory;
        }

        fts = sf.featureTypeStyle();

    }

    public void analyze(final MapLayer layer){
        Resource resource = layer.getData();
        if (!(resource instanceof FeatureSet)) {
            throw new IllegalArgumentException("Layer resource must be a FeatureSet");
        }

        this.layer = layer;
        fts.rules().clear();

        properties.clear();
        if (layer != null) {
            FeatureType schema;
            try {
                schema = ((FeatureSet) resource).getType();
            } catch (DataStoreException ex) {
                throw new FeatureStoreRuntimeException(ex.getMessage(), ex);
            }

            for(PropertyType desc : schema.getProperties(true)){
                if(desc instanceof AttributeType){
                    Class<?> type = ((AttributeType)desc).getValueClass();

                    if(!Geometry.class.isAssignableFrom(type)){
                        properties.add(ff.property(desc.getName().tip().toString()));
                    }
                }
            }

            //find the geometry class for template
            Class<?> geoClass = null;
            try{
                PropertyType geo = FeatureExt.getDefaultGeometry(schema);
                geoClass = Features.toAttribute(geo)
                        .map(AttributeType::getValueClass)
                        .orElse(null);
            }catch(PropertyNotFoundException ex){
                LOGGER.log(Level.FINE, "No sis:geometry property found", ex);
            }

            if(geoClass==null){
                return;
            }

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
                List<GraphicalSymbol> symbols = new ArrayList<>();
                symbols.add(sf.mark(StyleConstants.MARK_CIRCLE, fill, stroke));
                Graphic gra = sf.graphic(symbols, ff.literal(1), ff.literal(12), ff.literal(0), sf.anchorPoint(), sf.displacement());
                template = sf.pointSymbolizer(gra, null);
                expectedType = PointSymbolizer.class;
            }


            //try to rebuild the previous analyze if it was one
            List<MutableFeatureTypeStyle> ftss = layer.getStyle().featureTypeStyles();

            if(ftss.size() == 1){
                MutableFeatureTypeStyle fts = ftss.get(0);

                //defensive copy avoid synchronization
                List<MutableRule> candidateRules = new ArrayList<>(fts.rules());

                for(Rule r : candidateRules){
                    //defensive copy avoid synchronization
                    List<? extends Symbolizer> candidateSymbols = new ArrayList<>(r.symbolizers());

                    if(candidateSymbols.size() != 1) break;

                    Symbolizer symbol = candidateSymbols.get(0);
                    if(expectedType.isInstance(symbol)){

                        if(r.isElseFilter()){
                            //it looks like it's a valid classification "other" rule
                            this.fts.rules().add((MutableRule) r);
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
                                        this.fts.rules().add((MutableRule) r);
                                        template = symbol;
                                        currentProperty = (PropertyName) exp1;
                                    }else{
                                        //property is not in the schema
                                        break;
                                    }
                                }else if(exp2 instanceof PropertyName && exp1 instanceof Literal){
                                    if(properties.contains(exp2)){
                                        //it looks like it's a valid classification property rule
                                        this.fts.rules().add((MutableRule) r);
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

    public void setTemplate(final Symbolizer template) {
        this.template = template;
    }

    public RandomPalette getPalette() {
        return palette;
    }

    public void setPalette(final RandomPalette palette) {
        this.palette = palette;
    }

    public MutableFeatureTypeStyle getFeatureTypeStyle() {
        return fts;
    }

    public List<PropertyName> getProperties() {
        return properties;
    }

    public void setCurrentProperty(final PropertyName currentProperty) {
        this.currentProperty = currentProperty;
    }

    public PropertyName getCurrentProperty() {
        return currentProperty;
    }

    public boolean isOther() {
        return other;
    }

    public void setOther(final boolean other) {
        this.other = other;
    }

    public List<MutableRule> create(){
        //search the different values
        final Set<Object> differentValues = new HashSet<>();
        final PropertyName property = currentProperty;
        final FeatureSet resource = (FeatureSet) layer.getData();

        final QueryBuilder builder = new QueryBuilder();
        try {
            builder.setTypeName(resource.getType().getName());
        } catch (DataStoreException ex) {
            LOGGER.log(Level.FINE, "Error while accessing data", ex);
        }
        builder.setProperties(new String[]{property.getPropertyName()});
        final Query query = builder.buildQuery();

        try (Stream<Feature> stream = resource.subset(query).features(false)){
            final Iterator<Feature> features = stream.iterator();
            while(features.hasNext()){
                final Feature feature = features.next();
                differentValues.add(property.evaluate(feature));
            }
        } catch (DataStoreException|FeatureStoreRuntimeException ex) {
            LOGGER.log(Level.FINE, "Error while accessing data", ex);
        }

        //generate the different rules
        fts.rules().clear();

        for(Object obj : differentValues){
            fts.rules().add(createRule(property, obj));
        }

        //generate the other rule if asked
        if(other){
            MutableRule r = sf.rule(createSymbolizer());
            r.setElseFilter(true);
            r.setDescription(sf.description("other", "other"));
            fts.rules().add(r);
        }

        return fts.rules();
    }


    public Symbolizer createSymbolizer(){
        return derivateSymbolizer(template, palette.next());
    }

    /**
     * Derivate a symbolizer with a new color.
     */
    public Symbolizer derivateSymbolizer(final Symbolizer symbol, final Color color){

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

    public MutableRule createRule(final PropertyName property, final Object obj){
        MutableRule r = sf.rule(createSymbolizer());
        r.setFilter(ff.equals(property, ff.literal(obj)));
        r.setDescription(sf.description(obj.toString(), obj.toString()));
        return r;
    }
}
