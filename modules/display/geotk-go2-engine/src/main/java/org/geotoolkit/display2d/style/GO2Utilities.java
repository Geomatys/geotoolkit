/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2004-2008, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.display2d.style;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.OperationRegistry;
import javax.media.jai.registry.RIFRegistry;

import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.collection.Cache;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.GeometryJ2D;
import org.geotoolkit.display2d.primitive.GraphicCoverageJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display.primitive.ReferencedGraphic.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.raster.ShadedReliefCRIF;
import org.geotoolkit.display2d.style.raster.ShadedReliefDescriptor;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotools.filter.visitor.IsStaticExpressionVisitor;
import org.geotoolkit.style.MutableStyleFactory;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.expression.Expression;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Style;
import org.opengis.style.Rule;
import org.opengis.style.SemanticType;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GO2Utilities {

    private static final Cache<Symbolizer,CachedSymbolizer> CACHE = new Cache<Symbolizer, CachedSymbolizer>(50,50,true);

    private static final Map<Class<? extends CachedSymbolizer>,SymbolizerRenderer> RENDERERS =
            new HashMap<Class<? extends CachedSymbolizer>, SymbolizerRenderer>();

    public static final MutableStyleFactory STYLE_FACTORY;
    public static final FilterFactory2 FILTER_FACTORY;
    public static final float SELECTION_LOWER_ALPHA = 0.09f;
    public static final int SELECTION_PIXEL_MARGIN = 2;

    static{
        ServiceLoader<SymbolizerRenderer> loader = ServiceLoader.load(SymbolizerRenderer.class);
        for(SymbolizerRenderer renderer : loader){
            RENDERERS.put(renderer.getCachedSymbolizerClass(), renderer);
        }

        //Register the shadedrelief JAI operations
        //TODO this should be made automaticly using the META-INF/registryFile.jai
        OperationRegistry or = JAI.getDefaultInstance().getOperationRegistry();
        OperationDescriptor  fd = new ShadedReliefDescriptor();
        RenderedImageFactory rifJava = new ShadedReliefCRIF();
        try{
            or.registerDescriptor(fd);
            RIFRegistry.register(or, fd.getName(), "org.geotoolkit", rifJava);
        }catch(Exception ex){}


        final Hints hints = new Hints();
        hints.put(Hints.STYLE_FACTORY, MutableStyleFactory.class);
        hints.put(Hints.FILTER_FACTORY, FilterFactory2.class);
        STYLE_FACTORY = (MutableStyleFactory)FactoryFinder.getStyleFactory(hints);
        FILTER_FACTORY = (FilterFactory2) FactoryFinder.getFilterFactory(hints);
    }
    
    private GO2Utilities() {}
    
    public static void portray(final GraphicCoverageJ2D graphic, CachedSymbolizer symbol,
            RenderingContext2D context) throws PortrayalException {

        SymbolizerRenderer renderer = findRenderer(symbol);
        if(renderer != null){
            renderer.portray(graphic, symbol, context);
        }

    }

    public static boolean hit(final ProjectedFeature graphic, final CachedSymbolizer symbol,
            final RenderingContext2D context, final SearchArea mask, final VisitFilter filter){

        SymbolizerRenderer renderer = findRenderer(symbol);
        if(renderer != null){
            return renderer.hit(graphic, symbol, context, mask, filter);
        }

        return false;
    }

    public static boolean hit(final GraphicCoverageJ2D graphic, final CachedSymbolizer symbol,
            final RenderingContext2D renderingContext, final SearchArea mask, final VisitFilter filter) {
        SymbolizerRenderer renderer = findRenderer(symbol);
        if(renderer != null){
            return renderer.hit(graphic, symbol, renderingContext, mask, filter);
        }

        return false;
    }

    public static Rectangle2D estimate(final ProjectedFeature graphic, final CachedSymbolizer symbol,
            final RenderingContext2D context, Rectangle2D rect){

        SymbolizerRenderer renderer = findRenderer(symbol);
        if(renderer != null){
            return renderer.estimate(graphic, symbol, context, rect);
        }

        return XRectangle2D.INFINITY;
    }

    ////////////////////////////////////////////////////////////////////////////
    // renderers cache /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static SymbolizerRenderer findRenderer(CachedSymbolizer symbol){
        final Class<? extends CachedSymbolizer> type = symbol.getClass();
        SymbolizerRenderer candidate = RENDERERS.get(type);
        if (candidate != null) {
            return candidate;
        }
        candidate = findRendererForCachedClass(type.getSuperclass());
        if (candidate != null) {
            return candidate;
        }
        return null;
    }

    private static SymbolizerRenderer findRendererForCachedClass(Class<?> type) {
        while (type != null) {
            SymbolizerRenderer candidate = RENDERERS.get(type);
            if (candidate != null) {
//                synchronized (RENDERERS) {
//                    RENDERERS.put(type, candidate);
//                }
                return candidate;
            }
            // Checks interfaces implemented by this class.
            for (final Class<?> interf : type.getInterfaces()) {
                candidate = findRendererForCachedClass(interf);
                if (candidate != null) {
                    return candidate;
                }
            }
            type = type.getSuperclass();
        }
        return null;
    }

    public static SymbolizerRenderer findRenderer(Class<? extends Symbolizer> type){
        for(SymbolizerRenderer renderer : RENDERERS.values()){
            if(renderer.getSymbolizerClass().isAssignableFrom(type)){
                return renderer;
            }
        }
        return null;
    }

    /**
     * @param candidate class
     * @param references classes
     * @return closesest reference class or null if none match the candidate class
     */
    private static Class findClosestParent(Class candidate, Class ... references){

        int closestIndice = Integer.MAX_VALUE;
        Class<?> closest = null;

        for(final Class<?> reference : references){
            final int indice = findHierarchyLevel(candidate, reference);
            if(indice != -1 && indice <= closestIndice){
                closestIndice = indice;
                closest = reference;
            }
        }

        return closest;
    }

    /**
     * @param candidate class
     * @param reference class
     * @return -1 if reference is not an interface or parent of the candidate class
     *          0 if the parent class matches extacly the reference class
     *          >1 the class hierarchy level, the smaller is the number, the closer is
     *          the candidate class to the reference class
     */
    private static int findHierarchyLevel(final Class candidate, final Class reference){
        int level = 0;

        Class c = candidate;
        while(c != Object.class){

            //check the class
            if(c == reference){
                return level;
            }else{
                level += 1000;
            }

            //check it's interfaces
            for(final Class<?> i : c.getInterfaces()){
                if(i == reference){
                    return level;
                }else{
                    level += 1;
                }
            }

            c = c.getSuperclass();
        }

        return -1;
    }

    private static Collection<Class<?>> findMostSpecialize(Collection<Class<?>> classes) {
        final Set<Class<?>> specialized = new HashSet<Class<?>>();

        candidates :
        for(final Class candidate : classes){

            compare:
            for(final Class compared : classes){
                //continue if same class
                if(compared == candidate) continue compare;
                final Class result = findMostSpecialize(candidate, compared);

                //candidate is not much specialized
                if(result == compared) continue candidates;
            }

            specialized.add(candidate);
        }

        return specialized;
    }

    private static Class findMostSpecialize(final Class a, final Class b){
        boolean aisb = b.isAssignableFrom(a);
        boolean bisa = a.isAssignableFrom(b);

        if(aisb && !bisa){
            return a;
        }else if(!aisb && bisa){
            return b;
        }else{
            return null;
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // information about styles ////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static float[] validDashes(float[] dashes) {
        if (dashes == null || dashes.length == 0 || dashes.length == 1) {
            return null;
        } else {

            for (final float f : dashes) {
                if (f == 0) {
                    return null;
                }
            }

            if(dashes.length>2){
                return new float[]{dashes[0],dashes[1]};
            }

            return dashes;
        }
    }

    public static <T> T evaluate(Expression exp, Feature feature, Class<T> type, T defaultValue ){
        T value;
        if(exp == null || (value = exp.evaluate(feature, type)) == null){
            value = defaultValue;
        }
        return value;
    }

    public static Geometry getGeometry(final Feature feature, final String geomName){
        ///TODO use the correct geometrie, must wait for a better feature implementation
//        if (geomName != null && !geomName.trim().isEmpty() && feature.getProperty(geomName) != null) {
//            return (Geometry) feature.getProperty(geomName).getValue();
//        } else {
            return (Geometry) feature.getDefaultGeometryProperty().getValue();
//        }
    }

    public static Shape createShape(Geometry geom){
        final GeometryJ2D tempShape = new GeometryJ2D(null);
        tempShape.setGeometry(geom);
        return tempShape;
    }

    public static Collection<String> getRequieredAttributsName(final Expression exp, final Collection<String> collection){
        return (Collection<String>) exp.accept(ListingPropertyVisitor.VISITOR, collection);
    }

    public static boolean isStatic(Expression exp){
        if(exp == null) return true;
        return (Boolean) exp.accept(IsStaticExpressionVisitor.VISITOR, null);
    }

    /**
     * Returns the symbolizers that apply on the given feature.
     */
    public static List<CachedSymbolizer> getSymbolizer(Feature feature, Style style) {
        List<CachedSymbolizer> symbols = new ArrayList<CachedSymbolizer>();

        FeatureType ftype = feature.getType();
        String typeName = ftype.getName().toString();
        Collection<? extends FeatureTypeStyle> ftss = style.featureTypeStyles();

        for (FeatureTypeStyle fts : ftss) {

            //store "else" rules
            boolean doElse = true;
            List<Rule> elseRules = new ArrayList<Rule>();

            //test if the featutetype is valid
            if (true) {
//            if (typeName == null || (typeName.equalsIgnoreCase(fts.getFeatureTypeName())) ) {

                Collection<? extends Rule> rules = fts.rules();
                for (Rule rule : rules) {

                    //test if the rule is valid and is not a "else" rule
                    if (!rule.isElseFilter() && (rule.getFilter() == null || rule.getFilter().evaluate(feature))) {
                        doElse = false;
                        //append all the symbolizers
                        Collection<? extends Symbolizer> syms = rule.symbolizers();
                        for (Symbolizer sym : syms) {
                            CachedSymbolizer cache = getCached(sym);
                            symbols.add(cache);
                        }
                    } else {
                        elseRules.add(rule);
                    }
                }
            }

            //explore else rules if necessary
            if (doElse) {
                for (Rule rule : elseRules) {
                    //append all the symbolizers
                    Collection<? extends Symbolizer> syms = rule.symbolizers();
                    for (Symbolizer sym : syms) {
                        CachedSymbolizer cache = getCached(sym);
                        symbols.add(cache);
                    }
                }
            }
        }

        return symbols;
    }

    public static Set<String> propertiesCachedNames(final Collection<CachedRule> rules){
        final Set<String> atts = new HashSet<String>();
        for(final CachedRule r : rules){
            atts.addAll(r.getRequieredAttributsName());
        }
        return atts;
    }

    public static List<Rule> getValidRules(final Style style, final double scale, final Name typeName){
        final List<Rule> validRules = new ArrayList<Rule>();

        final List<? extends FeatureTypeStyle> ftss = style.featureTypeStyles();
        for(final FeatureTypeStyle fts : ftss){

            final Id ids = fts.getFeatureInstanceIDs();
            final Set<Name> names = fts.featureTypeNames();
            final Collection<SemanticType> semantics = fts.semanticTypeIdentifiers();

            //TODO filter correctly possibilities
            //test if the featutetype is valid
            //we move to next feature  type if not valid
            if (false) continue;
            //if (typeName != null && !(typeName.equalsIgnoreCase(fts.getFeatureTypeName())) ) continue;


            final List<? extends Rule> rules = fts.rules();
            for(final Rule rule : rules){
                //test if the scale is valid for this rule
                if(rule.getMinScaleDenominator() <= scale && rule.getMaxScaleDenominator() > scale){
                    validRules.add(rule);
                }
            }
        }

        return validRules;
    }

    public static List<Rule> getValidRules(final Style style, final double scale, SimpleFeatureType type) {
        final List<Rule> validRules = new ArrayList<Rule>();

        final List<? extends FeatureTypeStyle> ftss = style.featureTypeStyles();
        for(final FeatureTypeStyle fts : ftss){

            final Id ids = fts.getFeatureInstanceIDs();
            final Set<Name> names = fts.featureTypeNames();

            //check semantic
            final Collection<SemanticType> semantics = fts.semanticTypeIdentifiers();

            if(!semantics.isEmpty()){
                GeometryType gtype = type.getGeometryDescriptor().getType();
                Class ctype = gtype.getBinding();

                boolean valid = false;

                for(SemanticType semantic : semantics){
                    if(semantic == SemanticType.ANY){
                        valid = true;
                        break;
                    }else if(semantic == SemanticType.LINE){
                        if(ctype == LineString.class || ctype == MultiLineString.class){
                            valid = true;
                            break;
                        }
                    }else if(semantic == SemanticType.POINT){
                        if(ctype == Point.class || ctype == MultiPoint.class){
                            valid = true;
                            break;
                        }
                    }else if(semantic == SemanticType.POLYGON){
                        if(ctype == Polygon.class || ctype == MultiPolygon.class){
                            valid = true;
                            break;
                        }
                    }else if(semantic == SemanticType.RASTER){
                        // can not test this on feature datas
                    }else if(semantic == SemanticType.TEXT){
                        //no text type in JTS, that's a stupid thing this Text semantic
                    }
                }

                if(!valid) continue;

            }


            //TODO filter correctly possibilities
            //test if the featutetype is valid
            //we move to next feature  type if not valid
            if (false) continue;
            //if (typeName != null && !(typeName.equalsIgnoreCase(fts.getFeatureTypeName())) ) continue;


            final List<? extends Rule> rules = fts.rules();
            for(final Rule rule : rules){
                //test if the scale is valid for this rule
                if(rule.getMinScaleDenominator() <= scale && rule.getMaxScaleDenominator() > scale){
                    validRules.add(rule);
                }
            }
        }

        return validRules;
    }

    public static List<CachedRule> getValidCachedRules(final Style style, final double scale, SimpleFeatureType type) {
        final List<CachedRule> validRules = new ArrayList<CachedRule>();

        final List<? extends FeatureTypeStyle> ftss = style.featureTypeStyles();
        for(final FeatureTypeStyle fts : ftss){

            final Id ids = fts.getFeatureInstanceIDs();
            final Set<Name> names = fts.featureTypeNames();

            //check semantic
            final Collection<SemanticType> semantics = fts.semanticTypeIdentifiers();

            if(!semantics.isEmpty()){
                GeometryType gtype = type.getGeometryDescriptor().getType();
                Class ctype = gtype.getBinding();

                boolean valid = false;

                for(SemanticType semantic : semantics){
                    if(semantic == SemanticType.ANY){
                        valid = true;
                        break;
                    }else if(semantic == SemanticType.LINE){
                        if(ctype == LineString.class || ctype == MultiLineString.class){
                            valid = true;
                            break;
                        }
                    }else if(semantic == SemanticType.POINT){
                        if(ctype == Point.class || ctype == MultiPoint.class){
                            valid = true;
                            break;
                        }
                    }else if(semantic == SemanticType.POLYGON){
                        if(ctype == Polygon.class || ctype == MultiPolygon.class){
                            valid = true;
                            break;
                        }
                    }else if(semantic == SemanticType.RASTER){
                        // can not test this on feature datas
                    }else if(semantic == SemanticType.TEXT){
                        //no text type in JTS, that's a stupid thing this Text semantic
                    }
                }

                if(!valid) continue;

            }


            //TODO filter correctly possibilities
            //test if the featutetype is valid
            //we move to next feature  type if not valid
            if (false) continue;
            //if (typeName != null && !(typeName.equalsIgnoreCase(fts.getFeatureTypeName())) ) continue;


            final List<? extends Rule> rules = fts.rules();
            for(final Rule rule : rules){
                //test if the scale is valid for this rule
                if(rule.getMinScaleDenominator() <= scale && rule.getMaxScaleDenominator() > scale){
                    validRules.add(getCached(rule));
                }
            }
        }

        return validRules;
    }

    public static List<CachedRule> getValidCachedRules(final Style style, final double scale, final Name type) {
        final List<CachedRule> validRules = new ArrayList<CachedRule>();

        final List<? extends FeatureTypeStyle> ftss = style.featureTypeStyles();
        for(final FeatureTypeStyle fts : ftss){

            final Id ids = fts.getFeatureInstanceIDs();
            final Set<Name> names = fts.featureTypeNames();
            final Collection<SemanticType> semantics = fts.semanticTypeIdentifiers();

            //TODO filter correctly possibilities
            //test if the featutetype is valid
            //we move to next feature  type if not valid
            if (false) continue;
            //if (typeName != null && !(typeName.equalsIgnoreCase(fts.getFeatureTypeName())) ) continue;


            final List<? extends Rule> rules = fts.rules();
            for(final Rule rule : rules){
                //test if the scale is valid for this rule
                if(rule.getMinScaleDenominator() <= scale && rule.getMaxScaleDenominator() > scale){
                    validRules.add(getCached(rule));
                }
            }
        }

        return validRules;
    }

    ////////////////////////////////////////////////////////////////////////////
    // SYMBOLIZER CACHES ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static CachedRule getCached(Rule rule){
        CachedRule cr = new CachedRule(rule);
        return cr;
    }

    public static CachedSymbolizer getCached(Symbolizer symbol){

        CachedSymbolizer value = CACHE.peek(symbol);
        if (value == null) {
            Cache.Handler<CachedSymbolizer> handler = CACHE.lock(symbol);
            try {
                value = handler.peek();
                if (value == null) {
                    final SymbolizerRenderer renderer = findRenderer(symbol.getClass());
                    if(renderer != null){
                        value = renderer.createCachedSymbolizer(symbol);
                    }
                }
            } finally {
                handler.putAndUnlock(value);
            }
        }
        return value;
    }

    public static void clearCache(){
        CACHE.clear();
    }

}
