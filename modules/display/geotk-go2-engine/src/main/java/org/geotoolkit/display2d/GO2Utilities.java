/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.OperationRegistry;
import javax.media.jai.registry.RIFRegistry;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.collection.Cache;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.jts.JTSGeometryJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.primitive.iso.ISOGeometryJ2D;
import org.geotoolkit.display2d.primitive.jts.DecimateJTSGeometryJ2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.raster.ShadedReliefCRIF;
import org.geotoolkit.display2d.style.raster.ShadedReliefDescriptor;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.filter.visitor.IsStaticExpressionVisitor;
import org.geotoolkit.filter.visitor.ListingPropertyVisitor;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.style.MutableStyleFactory;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Style;
import org.opengis.style.Rule;
import org.opengis.style.SemanticType;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GO2Utilities {

    private static final GeometryFactory JTS_FACTORY = new GeometryFactory();

    private static final Cache<Symbolizer,CachedSymbolizer> CACHE = new Cache<Symbolizer, CachedSymbolizer>(50,50,true);

    private static final Map<Class<? extends CachedSymbolizer>,SymbolizerRenderer> RENDERERS =
            new HashMap<Class<? extends CachedSymbolizer>, SymbolizerRenderer>();

    public static final MutableStyleFactory STYLE_FACTORY;
    public static final FilterFactory2 FILTER_FACTORY;
    public static final float SELECTION_LOWER_ALPHA = 0.09f;
    public static final int SELECTION_PIXEL_MARGIN = 2;

    static{
        final ServiceLoader<SymbolizerRenderer> loader = ServiceLoader.load(SymbolizerRenderer.class);
        for(SymbolizerRenderer renderer : loader){
            RENDERERS.put(renderer.getCachedSymbolizerClass(), renderer);
        }

        //Register the shadedrelief JAI operations
        //TODO this should be made automaticly using the META-INF/registryFile.jai
        final OperationRegistry or = JAI.getDefaultInstance().getOperationRegistry();
        final OperationDescriptor  fd = new ShadedReliefDescriptor();
        final RenderedImageFactory rifJava = new ShadedReliefCRIF();
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

    public static void portray(final ProjectedFeature feature, CachedSymbolizer symbol,
            RenderingContext2D context) throws PortrayalException{
        final SymbolizerRenderer renderer = findRenderer(symbol);
        if(renderer != null){
            renderer.portray(feature, symbol, context);
        }
    }

    public static void portray(final ProjectedCoverage graphic, CachedSymbolizer symbol,
            RenderingContext2D context) throws PortrayalException {
        final SymbolizerRenderer renderer = findRenderer(symbol);
        if(renderer != null){
            renderer.portray(graphic, symbol, context);
        }
    }

    public static boolean hit(final ProjectedFeature graphic, final CachedSymbolizer symbol,
            final RenderingContext2D context, final SearchAreaJ2D mask, final VisitFilter filter){
        final SymbolizerRenderer renderer = findRenderer(symbol);
        if(renderer != null){
            return renderer.hit(graphic, symbol, context, mask, filter);
        }
        return false;
    }

    public static boolean hit(final ProjectedCoverage graphic, final CachedSymbolizer symbol,
            final RenderingContext2D renderingContext, final SearchAreaJ2D mask, final VisitFilter filter) {
        final SymbolizerRenderer renderer = findRenderer(symbol);
        if(renderer != null){
            return renderer.hit(graphic, symbol, renderingContext, mask, filter);
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // geometries operations ///////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    public static Shape toJava2D(Geometry geom){
        return new JTSGeometryJ2D(geom);
    }

    public static Shape toJava2D(Geometry geom, double[] resolution){
        return new DecimateJTSGeometryJ2D(geom,resolution);
    }

    public static Shape toJava2D(org.opengis.geometry.Geometry geom){
        if(geom instanceof JTSGeometry){
            final JTSGeometry geo = (JTSGeometry) geom;
            return toJava2D(geo.getJTSGeometry());
        }else{
            return new ISOGeometryJ2D(geom);
        }
    }

    public static Geometry toJTS(Shape candidate){
        final PathIterator ite = candidate.getPathIterator(null);
        final List<Coordinate> coords = new ArrayList<Coordinate>();

        final float[] xy = new float[2];
        while(!ite.isDone()){
            ite.currentSegment(xy);
            coords.add(new Coordinate(xy[0], xy[1]));
            ite.next();
        }
        coords.add(coords.get(0));

        final LinearRing ring = JTS_FACTORY.createLinearRing(coords.toArray(new Coordinate[coords.size()]));
        return JTS_FACTORY.createPolygon(ring, new LinearRing[0]);
    }

    public static boolean testHit(VisitFilter filter, Geometry left, Geometry right){

        switch(filter){
            case INTERSECTS :
                return left.intersects(right);
            case WITHIN :
                return left.contains(right);
        }

        return false;
    }

    public static boolean testHit(VisitFilter filter, org.opengis.geometry.Geometry left, org.opengis.geometry.Geometry right){

        switch(filter){
            case INTERSECTS :
                return left.intersects(right);
            case WITHIN :
                return left.contains(right);
        }

        return false;
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
        final boolean aisb = b.isAssignableFrom(a);
        final boolean bisa = a.isAssignableFrom(b);

        if(aisb && !bisa){
            return a;
        }else if(!aisb && bisa){
            return b;
        }else{
            return null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // some scale utility methods //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Calculate the coefficient between the objective unit and the given one.
     */
    public static float calculateScaleCoefficient(RenderingContext2D context, Unit<Length> symbolUnit){
        final CoordinateReferenceSystem objectiveCRS = context.getObjectiveCRS();
        
        if(symbolUnit == null || objectiveCRS == null){
            throw new NullPointerException("symbol unit and objectiveCRS cant be null");
        }

        //we have a special unit we must adjust the coefficient

        final CoordinateSystem cs = objectiveCRS.getCoordinateSystem();
        final int dimension = cs.getDimension();
        final List<Double> converters = new ArrayList<Double>();

        //go throw each dimension and append valid converters
        for (int i=0; i<dimension; i++){
            final CoordinateSystemAxis axis = cs.getAxis(i);
            final Unit axisUnit = axis.getUnit();
            if (axisUnit.isCompatible(symbolUnit)){
                final UnitConverter converter = axisUnit.getConverterTo(symbolUnit);

                if(!converter.isLinear()){
                    throw new UnsupportedOperationException("Cannot convert nonlinear units yet");
                }else{
                    converters.add(converter.convert(1) - converter.convert(0));
                }
            }else if(axisUnit == NonSI.DEGREE_ANGLE){
                //calculate coefficient at center of the screen.
                final Rectangle rect = context.getCanvasDisplayBounds();
                final AffineTransform2D trs = context.getDisplayToObjective();
                Point2D pt = new Point2D.Double(rect.getCenterX(), rect.getCenterY());
                pt = trs.transform(pt,pt);

                //TODO not correct yet, I'm not sure how to select the correct
                //axis for calculation
                if(!axis.getDirection().equals(AxisDirection.NORTH)) continue;

                final GeographicCRS crs = (GeographicCRS) objectiveCRS;

                final double a = crs.getDatum().getEllipsoid().getSemiMajorAxis();
                final double b = crs.getDatum().getEllipsoid().getSemiMinorAxis();
                final double e2 = 1 - Math.pow((b/a),2);

                //TODO not sure of this neither
//                System.out.println(i);
                final double phi = Math.toRadians((i==0)? pt.getY() : pt.getX());
                double s = a * (Math.cos(phi)) / Math.sqrt( 1 - e2 * Math.pow(Math.sin(phi),2) );

                s = Math.toRadians(s);

                final Unit ellipsoidUnit = crs.getDatum().getEllipsoid().getAxisUnit();
                final UnitConverter converter = ellipsoidUnit.getConverterTo(symbolUnit);
                s = converter.convert(s) - converter.convert(0);

                converters.add(s);
            }
        }

        final float coeff;

        //calculate coefficient
        if(converters.isEmpty()){
            coeff = 1;
        }else if(converters.size() == 1){
            //only one valid converter
            coeff = converters.get(0).floatValue();
        }else{
            double sum = 0;
            for(final Double coef : converters){
                sum += coef*coef ;
            }
            coeff = (float) Math.sqrt( sum/2d );
        }

        return 1/coeff;
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

    public static Geometry getGeometry(final SimpleFeature feature, final String geomName){
        if (geomName != null && !geomName.trim().isEmpty()) {
            return (Geometry) feature.getAttribute(geomName);
        } else {
            return (Geometry) feature.getDefaultGeometry();
        }
    }

    public static Geometry getGeometry(final Feature feature, final String geomName){
        if (geomName != null && !geomName.trim().isEmpty()) {
            final Property prop = feature.getProperty(geomName);
            if(prop != null){
                final Object obj = prop.getValue();
                if(obj == null || obj instanceof Geometry){
                    return (Geometry)obj;
                }
            }
            return null;
        } else {
            return (Geometry) feature.getDefaultGeometryProperty().getValue();
        }
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
        final List<CachedSymbolizer> symbols = new ArrayList<CachedSymbolizer>();

        final FeatureType ftype = feature.getType();
        final String typeName = ftype.getName().toString();
        final Collection<? extends FeatureTypeStyle> ftss = style.featureTypeStyles();

        for (FeatureTypeStyle fts : ftss) {

            //store "else" rules
            boolean doElse = true;
            final List<Rule> elseRules = new ArrayList<Rule>();

            //test if the featutetype is valid
            if (true) {
//            if (typeName == null || (typeName.equalsIgnoreCase(fts.getFeatureTypeName())) ) {

                final Collection<? extends Rule> rules = fts.rules();
                for (final Rule rule : rules) {

                    //test if the rule is valid and is not a "else" rule
                    if (!rule.isElseFilter() && (rule.getFilter() == null || rule.getFilter().evaluate(feature))) {
                        doElse = false;
                        //append all the symbolizers
                        final Collection<? extends Symbolizer> syms = rule.symbolizers();
                        for (Symbolizer sym : syms) {
                            symbols.add(getCached(sym));
                        }
                    } else {
                        elseRules.add(rule);
                    }
                }
            }

            //explore else rules if necessary
            if (doElse) {
                for (final Rule rule : elseRules) {
                    //append all the symbolizers
                    final Collection<? extends Symbolizer> syms = rule.symbolizers();
                    for (final Symbolizer sym : syms) {
                        symbols.add(getCached(sym));
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

    public static Set<String> propertiesCachedNames(final CachedRule[] rules){
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
                final GeometryType gtype = type.getGeometryDescriptor().getType();
                final Class ctype = gtype.getBinding();

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

    public static CachedRule[] getValidCachedRules(final Style style, final double scale, SimpleFeatureType type) {
        final List<CachedRule> validRules = new ArrayList<CachedRule>();

        final List<? extends FeatureTypeStyle> ftss = style.featureTypeStyles();
        for(final FeatureTypeStyle fts : ftss){

            final Id ids = fts.getFeatureInstanceIDs();
            final Set<Name> names = fts.featureTypeNames();

            //check semantic
            final Collection<SemanticType> semantics = fts.semanticTypeIdentifiers();

            if(!semantics.isEmpty()){
                final GeometryType gtype = type.getGeometryDescriptor().getType();
                final Class ctype = gtype.getBinding();

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

        return validRules.toArray(new CachedRule[validRules.size()]);
    }

    public static CachedRule[] getValidCachedRules(final Style style, final double scale, final Name type) {
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

        return validRules.toArray(new CachedRule[validRules.size()]);
    }

    ////////////////////////////////////////////////////////////////////////////
    // SYMBOLIZER CACHES ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static CachedRule getCached(Rule rule){
        return new CachedRule(rule);
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
