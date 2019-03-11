/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Length;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.measure.Units;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.util.ArgumentChecks;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.NullArgumentException;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.collection.Cache;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.grid.GridCoverage;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.processing.coverage.resample.CannotReprojectException;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.shape.TransformedShape;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.primitive.iso.ISOGeometryJ2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.filter.visitor.IsStaticExpressionVisitor;
import org.geotoolkit.filter.visitor.ListingPropertyVisitor;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.jts.awt.DecimateJTSGeometryJ2D;
import org.geotoolkit.geometry.jts.awt.JTSGeometryJ2D;
import org.geotoolkit.image.jai.FloodFill;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.math.XMath;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.coverage.resample.ResampleDescriptor;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.renderer.style.WKMMarkFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.visitor.PrepareStyleVisitor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Fill;
import org.opengis.style.Mark;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.SelectedChannelType;
import org.opengis.style.SemanticType;
import org.opengis.style.Stroke;
import org.opengis.style.Style;
import org.opengis.style.StyleVisitor;
import org.opengis.style.Symbolizer;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class GO2Utilities {

    public static final GeometryFactory JTS_FACTORY = new GeometryFactory();

    private static final Cache<Symbolizer,CachedSymbolizer> CACHE = new Cache<Symbolizer, CachedSymbolizer>(50,50,true);

    private static final Map<Class<? extends CachedSymbolizer>,SymbolizerRendererService> RENDERERS =
            new HashMap<Class<? extends CachedSymbolizer>, SymbolizerRendererService>();

    private static final double SE_EPSILON = 1e-6;

    public static final MutableStyleFactory STYLE_FACTORY;
    public static final FilterFactory2 FILTER_FACTORY;
    public static final float SELECTION_LOWER_ALPHA = 0.09f;
    public static final int SELECTION_PIXEL_MARGIN = 2;
    public static final AlphaComposite ALPHA_COMPOSITE_0F = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
    public static final AlphaComposite ALPHA_COMPOSITE_1F = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
    //returned interior point when geometry in unvalid
    private static final Coordinate INVALID_INTERIOR_POINT = new Coordinate(-0.5, -0.5, Double.NaN);

    public static final Shape GLYPH_LINE;
    public static final Shape GLYPH_POLYGON;
    public static final Point2D GLYPH_POINT;
    public static final Shape GLYPH_TEXT;

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.display2d");
    /**
     * A tolerance value for black color. Used in {@linkplain #removeBlackBorder(java.awt.image.WritableRenderedImage)}
     * to define an applet of black colors to replace with alpha data.
     */
    private static final int COLOR_TOLERANCE = 13;

    /**
     * Palette of black colors samples computed with {@link #COLOR_TOLERANCE}.
     * Used in {@linkplain #removeBlackBorder(java.awt.image.WritableRenderedImage)}.
     */
    private static final double[][] BLACK_COLORS;

    static{

        List<double[]> blackColorsList = new ArrayList<>();
        fillColorToleranceTable(0, 2, blackColorsList, new double[]{0, 0, 0, 255}, COLOR_TOLERANCE);
        BLACK_COLORS = blackColorsList.toArray(new double[0][]);

        final ServiceLoader<SymbolizerRendererService> loader = ServiceLoader.load(SymbolizerRendererService.class);
        for(SymbolizerRendererService renderer : loader){
            RENDERERS.put(renderer.getCachedSymbolizerClass(), renderer);
        }

        final Hints hints = new Hints();
        hints.put(Hints.STYLE_FACTORY, MutableStyleFactory.class);
        hints.put(Hints.FILTER_FACTORY, FilterFactory2.class);
        STYLE_FACTORY = (MutableStyleFactory)FactoryFinder.getStyleFactory(hints);
        FILTER_FACTORY = (FilterFactory2) FactoryFinder.getFilterFactory(hints);

        //LINE -----------------------------------------------------------------
        final float x2Points[] = {0,    0.4f,   0.6f,   1f};
        final float y2Points[] = {0.2f, 0.6f,   0.4f,   0.8f};
        final GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, x2Points.length);

        polyline.moveTo (x2Points[0], y2Points[0]);
        for (int index = 1; index < x2Points.length; index++) {
                 polyline.lineTo(x2Points[index], y2Points[index]);
        }
        GLYPH_LINE = polyline;

        //POLYGON --------------------------------------------------------------
        final float x1Points[] = {0.2f,     0.4f,   1f,     1f,     0.2f};
        final float y1Points[] = {1f,       0.4f,   0.2f,   1f,     1f};
        final GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, x1Points.length);

        polygon.moveTo(x1Points[0], y1Points[0]);
        for (int index = 1; index < x1Points.length; index++) {
                polygon.lineTo(x1Points[index], y1Points[index]);
        }
        GLYPH_POLYGON = polygon;

        //POINT ----------------------------------------------------------------
        GLYPH_POINT = new Point2D.Float(0.5f,0.5f);

        //TEXT -----------------------------------------------------------------
        final float xtPoints[] = {0.1f,     0.3f,   0.2f,   0.2f};
        final float ytPoints[] = {0.6f,     0.6f,   0.6f,   0.9f};
        final GeneralPath textLine = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xtPoints.length);

        textLine.moveTo (xtPoints[0], ytPoints[0]);
        for (int index = 1; index < xtPoints.length; index++) {
                 textLine.lineTo(xtPoints[index], ytPoints[index]);
        }
        GLYPH_TEXT = textLine;

    }

    private GO2Utilities() {}

    /**
     * @return true if some datas has been rendered
     */
    public static boolean portray(final ProjectedFeature feature, final CachedSymbolizer symbol,
            final RenderingContext2D context) throws PortrayalException{
        final SymbolizerRendererService renderer = findRenderer(symbol);
        if(renderer != null){
            return renderer.portray(feature, symbol, context);
        }
        return false;
    }

    /**
     * @return true if some datas has been rendered
     */
    public static boolean portray(final ProjectedCoverage graphic, final CachedSymbolizer symbol,
            final RenderingContext2D context) throws PortrayalException {
        final SymbolizerRendererService renderer = findRenderer(symbol);
        if(renderer != null){
            return renderer.portray(graphic, symbol, context);
        }
        return false;
    }

    /**
     * @return true if some datas has been rendered
     */
    public static boolean portray(final RenderingContext2D renderingContext, GridCoverage2D dataCoverage) throws PortrayalException{
        final CanvasMonitor monitor = renderingContext.getMonitor();
        final Graphics2D g2d = renderingContext.getGraphics();

        final CoordinateReferenceSystem coverageCRS = dataCoverage.getCoordinateReferenceSystem();
        boolean sameCRS = true;
        try{
            final CoordinateReferenceSystem candidate2D = CRSUtilities.getCRS2D(coverageCRS);
            if(!Utilities.equalsIgnoreMetadata(candidate2D,renderingContext.getObjectiveCRS2D()) ){
                sameCRS = false;
                dataCoverage = GO2Utilities.resample(dataCoverage.view(ViewType.NATIVE),renderingContext.getObjectiveCRS2D());

                if(dataCoverage != null){
                    dataCoverage = dataCoverage.view(ViewType.RENDERED);
                }
            }
        } catch (CannotReprojectException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return false;
        } catch(Exception ex){
            //several kind of errors can happen here, we catch anything to avoid blocking the map component.
            monitor.exceptionOccured(
                new IllegalStateException("Coverage is not in the requested CRS, found : " +
                "\n"+ coverageCRS +
                " was expecting : \n" +
                renderingContext.getObjectiveCRS() +
                "\nOriginal Cause:"+ ex.getMessage(), ex), Level.WARNING);
            return false;
        }

        if(dataCoverage == null){
            monitor.exceptionOccured(new NullArgumentException("GO2Utilities : Reprojected coverage is null."),Level.WARNING);
            return false;
        }

        //we must switch to objectiveCRS for grid coverage
        renderingContext.switchToObjectiveCRS();

        RenderedImage img = dataCoverage.getRenderedImage();

        if(!sameCRS){
            //will be reprojected, we must check that image has alpha support
            //otherwise we will have black borders after reprojection
            if(!img.getColorModel().hasAlpha()){
                //ensure we have a bufferedImage for floodfill operation
                final BufferedImage buffer;
                if(img instanceof BufferedImage){
                    buffer = (BufferedImage) img;
                }else{
                    buffer = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    buffer.createGraphics().drawRenderedImage(img, new AffineTransform());
                }

                //remove black borders+
                FloodFill.fill(buffer, new Color[]{Color.BLACK}, new Color(0f,0f,0f,0f),
                        new java.awt.Point(0,0),
                        new java.awt.Point(buffer.getWidth()-1,0),
                        new java.awt.Point(buffer.getWidth()-1,buffer.getHeight()-1),
                        new java.awt.Point(0,buffer.getHeight()-1)
                        );
                img = buffer;
            }
        }

        final MathTransform2D trs2D = dataCoverage.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT);
        if(trs2D instanceof AffineTransform){
            g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
            g2d.drawRenderedImage(img, (AffineTransform)trs2D);
            return true;
        }else if (trs2D instanceof LinearTransform) {
            final LinearTransform lt = (LinearTransform) trs2D;
            final int col = lt.getMatrix().getNumCol();
            final int row = lt.getMatrix().getNumRow();
            //TODO using only the first parameters of the linear transform
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass());
        }else{
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass() );
        }

    }

    public static boolean hit(final ProjectedFeature graphic, final CachedSymbolizer symbol,
            final RenderingContext2D context, final SearchAreaJ2D mask, final VisitFilter filter){
        final SymbolizerRendererService renderer = findRenderer(symbol);
        if(renderer != null){
            return renderer.hit(graphic, symbol, context, mask, filter);
        }
        return false;
    }

    public static boolean hit(final ProjectedCoverage graphic, final CachedSymbolizer symbol,
            final RenderingContext2D renderingContext, final SearchAreaJ2D mask, final VisitFilter filter) {
        final SymbolizerRendererService renderer = findRenderer(symbol);
        if(renderer != null){
            return renderer.hit(graphic, symbol, renderingContext, mask, filter);
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Glyph utils /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Paint a mark.
     *
     * @param mark : mark to paint
     * @param size : expected mark size
     * @param target : Graphics2D
     */
    public static void renderGraphic(final Mark mark, final float size, final Graphics2D target){
        final Expression wkn = mark.getWellKnownName();

        final Shape shape;

        if(StyleConstants.MARK_CIRCLE.equals(wkn)){
            shape = WKMMarkFactory.CIRCLE;
        }else if(StyleConstants.MARK_CROSS.equals(wkn)){
            shape = WKMMarkFactory.CROSS;
        }else if(StyleConstants.MARK_SQUARE.equals(wkn)){
            shape = WKMMarkFactory.SQUARE;
        }else if(StyleConstants.MARK_STAR.equals(wkn)){
            shape = WKMMarkFactory.STAR;
        }else if(StyleConstants.MARK_TRIANGLE.equals(wkn)){
            shape = WKMMarkFactory.TRIANGLE;
        }else if(StyleConstants.MARK_X.equals(wkn)){
            shape = WKMMarkFactory.X;
        }else{
            shape = null;
        }

        if(shape != null){
            final TransformedShape trs = new TransformedShape();
            trs.setOriginalShape(shape);
            trs.scale(size, size);
            renderFill(trs, mark.getFill(), target);
            renderStroke(trs, mark.getStroke(), Units.METRE, target);
        }

    }

    /**
     * Paint a stroked shape.
     *
     * @param shape : java2d shape
     * @param stroke : sld stroke
     * @param uom
     * @param target : Graphics2D
     */
    public static void renderStroke(final Shape shape, final Stroke stroke, final Unit uom, final Graphics2D target){
        final Expression expColor = stroke.getColor();
        final Expression expOpa = stroke.getOpacity();
        final Expression expCap = stroke.getLineCap();
        final Expression expJoin = stroke.getLineJoin();
        final Expression expWidth = stroke.getWidth();

        Paint color;
        final float width;
        final float opacity;
        final int cap;
        final int join;
        final float[] dashes;

        if(GO2Utilities.isStatic(expColor)){
            color = expColor.evaluate(null, Color.class);
        }else{
            color = Color.RED;
        }

        if(color == null){
            color = Color.RED;
        }

        if(expOpa != null && GO2Utilities.isStatic(expOpa)){
            Number num = expOpa.evaluate(null, Number.class);
            if(num != null){
                opacity = XMath.clamp(num.floatValue(),0f,1f);
            }else{
                opacity = 0.6f;
            }

        }else{
            opacity = 0.6f;
        }

        if(GO2Utilities.isStatic(expCap)){
            if(StyleConstants.STROKE_CAP_ROUND.equals(expCap)){
                cap = BasicStroke.CAP_ROUND;
            }else if(StyleConstants.STROKE_CAP_SQUARE.equals(expCap)){
                cap = BasicStroke.CAP_SQUARE;
            }else {
                cap = BasicStroke.CAP_BUTT;
            }
        }else{
            cap = BasicStroke.CAP_BUTT;
        }

        if(GO2Utilities.isStatic(expJoin)){
            if(StyleConstants.STROKE_JOIN_ROUND.equals(expJoin)){
                join = BasicStroke.JOIN_ROUND;
            }else if(StyleConstants.STROKE_JOIN_MITRE.equals(expJoin)){
                join = BasicStroke.JOIN_MITER;
            }else {
                join = BasicStroke.JOIN_BEVEL;
            }
        }else{
            join = BasicStroke.JOIN_BEVEL;
        }

        if(Units.POINT.equals(uom) && GO2Utilities.isStatic(expWidth)){
            width = expWidth.evaluate(null, Number.class).floatValue();

            if(stroke.getDashArray() != null && stroke.getDashArray().length >0){
                dashes = stroke.getDashArray();
            }else{
                dashes = null;
            }

        }else{
            width = 1f;
            dashes = null;
        }

        target.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        target.setPaint(color);

        if(dashes != null){
            target.setStroke(new BasicStroke(width, cap, join,1,dashes,0));
        }else{
            target.setStroke(new BasicStroke(width, cap, join));
        }

        target.draw(shape);
    }

    /**
     * Paint a filled shape.
     *
     * @param shape : java2d shape
     * @param fill : sld fill
     * @param target : Graphics2D
     */
    public static void renderFill(final Shape shape, final Fill fill, final Graphics2D target){
        if(fill == null){
            return;
        }
        final Expression expColor = fill.getColor();
        final Expression expOpa = fill.getOpacity();

        Paint color;
        final float opacity;

        if(GO2Utilities.isStatic(expColor)){
            color = expColor.evaluate(null, Color.class);
        }else{
            color = Color.RED;
        }

        if(color == null){
            color = Color.RED;
        }

        if(GO2Utilities.isStatic(expOpa)){
            opacity = XMath.clamp(expOpa.evaluate(null, Number.class).floatValue(),0f,1f);
        }else{
            opacity = 0.6f;
        }

        target.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        target.setPaint(color);

        target.fill(shape);
    }

    ////////////////////////////////////////////////////////////////////////////
    // geometries operations ///////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static Shape toJava2D(final Geometry geom){
        return new JTSGeometryJ2D(geom);
    }

    public static Shape toJava2D(final Geometry geom, final double[] resolution){
        return new DecimateJTSGeometryJ2D(geom,resolution);
    }

    public static Shape toJava2D(final org.opengis.geometry.Geometry geom){
        if(geom instanceof JTSGeometry){
            final JTSGeometry geo = (JTSGeometry) geom;
            return toJava2D(geo.getJTSGeometry());
        }else{
            return new ISOGeometryJ2D(geom);
        }
    }

    public static Geometry toJTS(final Shape candidate){
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

    public static boolean testHit(final VisitFilter filter, final Geometry left, final Geometry right){

        switch(filter){
            case INTERSECTS :
                return left.intersects(right);
            case WITHIN :
                return left.contains(right);
        }

        return false;
    }

    public static boolean testHit(final VisitFilter filter, final org.opengis.geometry.Geometry left, final org.opengis.geometry.Geometry right){

        switch(filter){
            case INTERSECTS :
                return left.intersects(right);
            case WITHIN :
                return left.contains(right);
        }

        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // work on envelope ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Calculate the most accurate pixel resolution for the given envelope.
     *
     * @param context2D
     * @param wanted
     * @return double, in envelope crs unit by pixel
     * @throws TransformException
     */
    public static double pixelResolution(final RenderingContext2D context2D, final Envelope wanted) throws TransformException{
        final Dimension dim = context2D.getCanvasDisplayBounds().getSize();
        final double[] resolution = context2D.getResolution(context2D.getDisplayCRS());

        //resolution contain dpi adjustments, to obtain an image of the correct dpi
        //we raise the request dimension so that when we reduce it it will have the
        //wanted dpi.
        dim.width /= resolution[0];
        dim.height /= resolution[1];

        final MathTransform objToDisp = context2D.getObjectiveToDisplay();

        Envelope cropped = wanted;
        if(!CRS.equalsApproximatively(context2D.getCanvasObjectiveBounds2D(), wanted.getCoordinateReferenceSystem())){
            cropped = Envelopes.transform(wanted, context2D.getObjectiveCRS2D());
        }

        cropped = Envelopes.transform(objToDisp, cropped);

        //we assume we only have a regular
        return wanted.getSpan(0) / cropped.getSpan(0);
    }

    ////////////////////////////////////////////////////////////////////////////
    // renderers cache /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static SymbolizerRendererService findRenderer(final CachedSymbolizer symbol){
        final Class<? extends CachedSymbolizer> type = symbol.getClass();
        SymbolizerRendererService candidate = RENDERERS.get(type);
        if (candidate != null) {
            return candidate;
        }
        candidate = findRendererForCachedClass(type.getSuperclass());
        if (candidate != null) {
            return candidate;
        }
        return null;
    }

    private static SymbolizerRendererService findRendererForCachedClass(Class<?> type) {
        while (type != null) {
            SymbolizerRendererService candidate = RENDERERS.get(type);
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

    public static SymbolizerRendererService findRenderer(final Class<? extends Symbolizer> type){
        for(SymbolizerRendererService renderer : RENDERERS.values()){
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
    private static Class findClosestParent(final Class candidate, final Class ... references){

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

    private static Collection<Class<?>> findMostSpecialize(final Collection<Class<?>> classes) {
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
    // rewrite coverage read param  ////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static GridCoverage2D resample(final GridCoverage dataCoverage, final CoordinateReferenceSystem targetCRS) throws ProcessException{
        final ProcessDescriptor desc = ResampleDescriptor.INSTANCE;
        final Parameters params = Parameters.castOrWrap(desc.getInputDescriptor().createValue());
        params.getOrCreate(ResampleDescriptor.IN_COVERAGE).setValue(dataCoverage);
        params.getOrCreate(ResampleDescriptor.IN_COORDINATE_REFERENCE_SYSTEM).setValue(targetCRS);

        final org.geotoolkit.process.Process process = desc.createProcess(params);
        final ParameterValueGroup result = process.call();
        return (GridCoverage2D) result.parameter("result").getValue();
    }

    ////////////////////////////////////////////////////////////////////////////
    // some scale utility methods //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Calculate the coefficient between the objective unit and the given one.
     */
    public static float calculateScaleCoefficient(final RenderingContext2D context, final Unit<Length> symbolUnit){
        final CoordinateReferenceSystem objectiveCRS = context.getObjectiveCRS();

        ensureNonNull("symbol unit", symbolUnit);
        ensureNonNull("objective crs", objectiveCRS);

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

                if (!converter.isLinear()) {
                    throw new UnsupportedOperationException("Cannot convert nonlinear units yet");
                }else{
                    converters.add(converter.convert(1) - converter.convert(0));
                }
            }else if(axisUnit == Units.DEGREE){
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

    /**
     * Compute Euclidean distance between a point and a line define by 2 points (ptA, ptB).
     *
     * @param point
     * @param ptA
     * @param ptB
     */
    public static double euclidianDistance(final double[] point, final double[] ptA, final double[] ptB) {
        ArgumentChecks.ensureNonNull("point", point);
        ArgumentChecks.ensureNonNull("dp1", ptA);
        ArgumentChecks.ensureNonNull("dp2", ptB);
        final int dimension = point.length;
        if (ptA.length != dimension || ptB.length != dimension)
            throw new IllegalArgumentException("All points should have same dimension.");
        if (dimension == 2) {
            final double u0 = ptB[0] - ptA[0];
            final double u1 = ptB[1] - ptA[1];
            return Math.abs((point[0]-ptA[0])*u1 - (point[1]-ptB[1])*u0)/ (u0*u0+u1*u1);
        } else {
            double dist = 0;
            double normU = 0;
            int prodCursorMin = 0;
            int prodCursorMax = 1;
            for (int i = 0; i < dimension; i++) {
                if (++prodCursorMax == dimension) prodCursorMax = 0;
                if (++prodCursorMin == dimension) prodCursorMin = 0;
                final double ui = ptB[i] - ptA[i];
                normU += ui * ui;
                final double uCMax = ptB[prodCursorMax]    - ptA[prodCursorMax];
                final double uCMin = ptB[prodCursorMin]    - ptA[prodCursorMin];
                final double vMAMax = point[prodCursorMax] - ptA[prodCursorMax];
                final double vMAMin = point[prodCursorMin] - ptA[prodCursorMin];
                final double di = Math.abs(vMAMin*uCMax - vMAMax*uCMin);
                dist += di*di;
            }
            return Math.sqrt(dist / normU);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // information about styles ////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static float[] validDashes(final float[] dashes) {
        if (dashes == null || dashes.length == 0) {
            return null;
        } else {
            return dashes;
        }
    }

    public static <T> T evaluate(final Expression exp, final Object candidate, final Class<T> type, final T defaultValue ){
        if(exp==null) return defaultValue;
        T value;
        try{
            value = exp.evaluate(candidate, type);
            if(value == null){
                value = defaultValue;
            }
        }catch(IllegalArgumentException ex){
            //if functions or candidate do not have the proper field we will have a IllegalArgumentException
            value = defaultValue;
        }
        return value;
    }

    public static Float evaluate(final Expression exp, final Object candidate,
            final float defaultValue, final float min, final float max){
        if(exp==null) return defaultValue;
        Float value;
        try{
            value = exp.evaluate(candidate, Float.class);
            if(value == null){
                value = defaultValue;
            }else{
                //ensure min/max
                value = XMath.clamp(value, min, max);
            }
        }catch(IllegalArgumentException ex){
            //if functions or candidate do not have the proper field we will have a IllegalArgumentException
            value = defaultValue;
        }
        return value;
    }

    public static Geometry getGeometry(final Object obj, final Expression geomExp){
        return geomExp.evaluate(obj, Geometry.class);
    }

    public static Class getGeometryClass(final FeatureType featuretype, final String geomName){
        final PropertyType prop;
        if (geomName != null && !geomName.trim().isEmpty()) {
            prop = featuretype.getProperty(geomName);
        }else if(featuretype != null){
            prop = featuretype.getProperty(AttributeConvention.GEOMETRY_PROPERTY.toString());
        }else{
            prop = null;
        }

        if(prop instanceof AttributeType){
            return ((AttributeType)prop).getValueClass();
        }else{
            return Geometry.class;
        }
    }

    public static Geometry getGeometry(final Feature feature, final Expression geomExp){
        if (isNullorEmpty(geomExp)) {
            return (Geometry) FeatureExt.getDefaultGeometryValue(feature).orElse(null);
        } else {
            return geomExp.evaluate(feature, Geometry.class);
        }
    }

    public static Collection<String> getRequieredAttributsName(final Expression exp, final Collection<String> collection){
        return (Collection<String>) exp.accept(ListingPropertyVisitor.VISITOR, collection);
    }

    public static boolean isStatic(final Expression exp){
        if(exp == null) return true;
        return (Boolean) exp.accept(IsStaticExpressionVisitor.VISITOR, null);
    }

    /**
     * Test if an expression is :
     * - null
     * - Expression.NIL
     * - PropertyName with null or empty name
     *
     * @param exp
     * @return true if empty
     */
    public static boolean isNullorEmpty(Expression exp){
        if(exp==null || exp==Expression.NIL){
            return true;
        }else if(exp instanceof PropertyName){
            final PropertyName pn = (PropertyName) exp;
            final String str = pn.getPropertyName();
            if(str==null || str.trim().isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the symbolizers that apply on the given feature.
     */
    public static List<CachedSymbolizer> getSymbolizer(final Feature feature, final Style style) {
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
                            symbols.add(getCached(sym,ftype));
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
                        symbols.add(getCached(sym,ftype));
                    }
                }
            }
        }

        return symbols;
    }

    public static Set<String> propertiesNames(final Collection<? extends Rule> rules){
        org.geotoolkit.style.visitor.ListingPropertyVisitor visitor = new org.geotoolkit.style.visitor.ListingPropertyVisitor();
        final Set<String> names = new HashSet<>();
        for(Rule r : rules){
            visitor.visit(r, names);
        }
        return names;
    }

    public static Set<String> propertiesCachedNames(final Collection<CachedRule> rules){
        final Set<String> atts = new HashSet<>();
        for(final CachedRule r : rules){
            r.getRequieredAttributsName(atts);
        }
        return atts;
    }

    public static Set<String> propertiesCachedNames(final CachedRule[] rules){
        final Set<String> atts = new HashSet<>();
        for(final CachedRule r : rules){
            r.getRequieredAttributsName(atts);
        }
        return atts;
    }

    /**
     * This information can be used to determinate if geometries smaller then a pixel
     * can be ignore when rendering.
     *
     * @return true if there is a visible margin used in this symbols.
     */
    public static boolean visibleMargin(final CachedRule[] rules, final float minMargin, final RenderingContext2D context){
        for(CachedRule r : rules){
            for(CachedSymbolizer s : r.symbolizers()){
                final float m = s.getMargin(null, context);
                if(Float.isNaN(m) || m>=minMargin){
                    //margin can not be evaluate or is bigger
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Rule> getValidRules(final Style style, final double scale, final FeatureType type) {
        final List<Rule> validRules = new ArrayList<Rule>();

        final List<? extends FeatureTypeStyle> ftss = style.featureTypeStyles();
        for(final FeatureTypeStyle fts : ftss){

            final Id ids = fts.getFeatureInstanceIDs();
            final Set<GenericName> names = fts.featureTypeNames();

            //check semantic, only if we have a feature type
            if(type != null){
                final Collection<SemanticType> semantics = fts.semanticTypeIdentifiers();
                if(!semantics.isEmpty()){
                    Class ctype;
                    try {
                        ctype = FeatureExt.castOrUnwrap(FeatureExt.getDefaultGeometry(type))
                                .map(AttributeType::getValueClass)
                                .orElse(null);
                    } catch (PropertyNotFoundException e) {
                          ctype = null;
                    }

                    boolean valid = false;
                    for(SemanticType semantic : semantics){
                        if(semantic == SemanticType.ANY){
                            valid = true;
                            break;
                        }else if(semantic == SemanticType.LINE){
                            if(ctype == LineString.class || ctype == MultiLineString.class || ctype == Geometry.class ){
                                valid = true;
                                break;
                            }
                        }else if(semantic == SemanticType.POINT){
                            if(ctype == Point.class || ctype == MultiPoint.class || ctype == Geometry.class){
                                valid = true;
                                break;
                            }
                        }else if(semantic == SemanticType.POLYGON){
                            if(ctype == Polygon.class || ctype == MultiPolygon.class || ctype == Geometry.class){
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
            }


            //TODO filter correctly possibilities
            //test if the featutetype is valid
            //we move to next feature  type if not valid
            if (false) continue;
            //if (typeName != null && !(typeName.equalsIgnoreCase(fts.getFeatureTypeName())) ) continue;


            final List<? extends Rule> rules = fts.rules();
            for(final Rule rule : rules){
                //test if the scale is valid for this rule
                if(rule.getMinScaleDenominator()-SE_EPSILON <= scale && rule.getMaxScaleDenominator()+SE_EPSILON > scale){
                    validRules.add(rule);
                }
            }
        }

        return validRules;
    }

    public static CachedRule[] getValidCachedRules(final Style style, final double scale, final FeatureType type) {
        final List<CachedRule> validRules = new ArrayList<CachedRule>();

        final List<? extends FeatureTypeStyle> ftss = style.featureTypeStyles();
        for(final FeatureTypeStyle fts : ftss){

            final Id ids = fts.getFeatureInstanceIDs();
            final Set<GenericName> names = fts.featureTypeNames();

            //check semantic, only if we have a feature type
            if(type != null){
                final Collection<SemanticType> semantics = fts.semanticTypeIdentifiers();
                if(!semantics.isEmpty()){
                    Class ctype;
                    try {
                        ctype = FeatureExt.castOrUnwrap(FeatureExt.getDefaultGeometry(type))
                                .map(AttributeType::getValueClass)
                                .orElse(null);
                    } catch (PropertyNotFoundException e) {
                          ctype = null;
                    }

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
            }


            //TODO filter correctly possibilities
            //test if the featutetype is valid
            //we move to next feature  type if not valid
            //if (false) continue;
            //if (typeName != null && !(typeName.equalsIgnoreCase(fts.getFeatureTypeName())) ) continue;


            final List<? extends Rule> rules = fts.rules();
            for(final Rule rule : rules){
                //test if the scale is valid for this rule
                if(rule.getMinScaleDenominator()-SE_EPSILON <= scale && rule.getMaxScaleDenominator()+SE_EPSILON > scale){
                    validRules.add(getCached(rule,type));
                }
            }
        }

        return validRules.toArray(new CachedRule[validRules.size()]);
    }

    public static CachedRule[] getValidCachedRules(final Style style, final double scale, final GenericName type, final FeatureType expected) {
        final List<CachedRule> validRules = new ArrayList<>();

        final List<? extends FeatureTypeStyle> ftss = style.featureTypeStyles();
        for(final FeatureTypeStyle fts : ftss){

            final Id ids = fts.getFeatureInstanceIDs();
            final Set<GenericName> names = fts.featureTypeNames();
            final Collection<SemanticType> semantics = fts.semanticTypeIdentifiers();

            //TODO filter correctly possibilities
            //test if the featutetype is valid
            //we move to next feature  type if not valid
            if (false) continue;
            //if (typeName != null && !(typeName.equalsIgnoreCase(fts.getFeatureTypeName())) ) continue;


            final List<? extends Rule> rules = fts.rules();
            for(final Rule rule : rules){
                //test if the scale is valid for this rule
                if(rule.getMinScaleDenominator()-SE_EPSILON <= scale && rule.getMaxScaleDenominator()+SE_EPSILON > scale){
                    validRules.add(getCached(rule,expected));
                }
            }
        }

        return validRules.toArray(new CachedRule[validRules.size()]);
    }

    /**
     * Return true if this raster symbolizer define the original data style.
     * That means no rendering operations need to be applied on the coverage
     * before painting.
     */
    public static boolean isDefaultRasterSymbolizer(final Symbolizer symbolizer){
        if(!(symbolizer instanceof RasterSymbolizer)){
            return false;
        }
        final RasterSymbolizer rs = (RasterSymbolizer)symbolizer;

        if(rs.getShadedRelief() != null &&
           rs.getShadedRelief().getReliefFactor().evaluate(null, Float.class) != 0){
            return false;
        }
        if(rs.getOpacity() != null && rs.getOpacity().evaluate(null, Float.class) != 1f){
            return false;
        }
        if(rs.getImageOutline() != null){
            return false;
        }
        if(rs.getContrastEnhancement() != null &&
           rs.getContrastEnhancement().getGammaValue().evaluate(null, Float.class) != 1f){
           return false;
        }
        if(rs.getColorMap() != null && rs.getColorMap().getFunction() != null){
            return false;
        }
        final SelectedChannelType[] bands = (rs.getChannelSelection()==null) ? null
                                            : rs.getChannelSelection().getRGBChannels();
        if(bands != null){
            if(bands.length != 3){
                return false;
            }
            //todo should check each band
        }

        return true;
    }

    /**
     * Try to get the most representative point of a a geometry.
     * This is used by PointSymbolizer and TextSymbiolizer.
     *
     * @param geom, not null
     * @return
     */
    public static Point getBestPoint(Geometry geom){
        Point pt = null;

        // 1 : try to get an interior point
        //NOTE : this sometimes fails with TopololyException or IllegalArgumentException
        try{
            pt = geom.getInteriorPoint();
            if(pt.isValid() && !pt.getCoordinate().equals2D(INVALID_INTERIOR_POINT)) return pt;
        }catch(Throwable ex){
            //JTS error sometimes happen
        }
        // 2 : fallback on centroid
        //NOTE : even for valid geometries, the centroid happened to be NaN
        try{
            pt = geom.getCentroid();
            if(pt.isValid()) return pt;
        }catch(Throwable ex){
            //JTS error sometimes happen
        }

        // 3 : extract from envelope
        final org.locationtech.jts.geom.Envelope env = geom.getEnvelopeInternal();
        pt = JTS_FACTORY.createPoint(new Coordinate(
                (env.getMaxX()+env.getMinX())/2.0,
                (env.getMaxY()+env.getMinY())/2.0));

        return pt.isValid() ? pt : null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // SYMBOLIZER CACHES ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static CachedRule getCached(final Rule rule,final FeatureType expected){
        return new CachedRule(rule,expected);
    }

    public static CachedSymbolizer getCached(Symbolizer symbol,final FeatureType expected){
        CachedSymbolizer value;

        if(expected != null){
            //optimize the symbolizer before caching it
            final StyleVisitor sv = new PrepareStyleVisitor(Feature.class, expected);
            symbol = (Symbolizer)symbol.accept(sv, null);
        }

        final SymbolizerRendererService renderer = findRenderer(symbol.getClass());
        if(renderer != null){
            value = renderer.createCachedSymbolizer(symbol);
        } else {
            throw new IllegalStateException("No renderer for the style "+ symbol);
        }
        return value;


//        CachedSymbolizer value = CACHE.peek(symbol);
//        if (value == null) {
//            Cache.Handler<CachedSymbolizer> handler = CACHE.lock(symbol);
//            try {
//                value = handler.peek();
//                if (value == null) {
//                    final SymbolizerRendererService renderer = findRenderer(symbol.getClass());
//                    if(renderer != null){
//                        value = renderer.createCachedSymbolizer(symbol,expected);
//                    } else {
//                        throw new IllegalStateException("No renderer for the style "+ symbol);
//                    }
//                }
//            } finally {
//                handler.putAndUnlock(value);
//            }
//        }
//        return value;
    }

    public static void clearCache(){
        CACHE.clear();
    }

    ////////////////////////////////////////////////////////////////////////////
    // OTHER UTILS /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Merge colors, the first color is placed at the back.
     * The first color is expected to be opaque.
     *
     * @return Opaque color resulting from the merge
     * @throws IllegalArgumentException if first color is not opaque
     */
    public static Color mergeColors(final Color c1, final Color c2) throws IllegalArgumentException{

        if(c1.getAlpha() != 255){
            throw new IllegalArgumentException("First color must be opaque");
        }

        final float alpha = (float)c2.getAlpha()/255f;
        final int r = Math.min(c1.getRed(), c2.getRed())        + (int) (Math.abs(c1.getRed()-c2.getRed())*alpha);
        final int g = Math.min(c1.getGreen(), c2.getGreen())    + (int) (Math.abs(c1.getGreen()-c2.getGreen())*alpha);
        final int b = Math.min(c1.getBlue(), c2.getBlue())      + (int) (Math.abs(c1.getBlue()-c2.getBlue())*alpha);

        return new Color(r, g, b);
    }

    public static void removeNaN(GeneralEnvelope env){
        //we definitly do not want some NaN values
        if(Double.isNaN(env.getMinimum(0))){ env.setRange(0, Double.NEGATIVE_INFINITY, env.getMaximum(0));  }
        if(Double.isNaN(env.getMaximum(0))){ env.setRange(0, env.getMinimum(0), Double.POSITIVE_INFINITY);  }
        if(Double.isNaN(env.getMinimum(1))){ env.setRange(1, Double.NEGATIVE_INFINITY, env.getMaximum(1));  }
        if(Double.isNaN(env.getMaximum(1))){ env.setRange(1, env.getMinimum(1), Double.POSITIVE_INFINITY);  }
    }

    //-- Some utility methods for any Renderer.

    /**
     * Remove black border of an ARGB image to replace them with transparent pixels.
     *
     * @param toFilter Image to remove black border from.
     */
    public static void removeBlackBorder(final WritableRenderedImage toFilter) {
        // remove black border only on image larger than 1x1 pixels
        if (toFilter.getHeight() > 1 && toFilter.getWidth() > 1) {
            FloodFill.fill(toFilter, BLACK_COLORS, new double[]{0d, 0d, 0d, 0d},
                    new java.awt.Point(0, 0),
                    new java.awt.Point(toFilter.getWidth() - 1, 0),
                    new java.awt.Point(toFilter.getWidth() - 1, toFilter.getHeight() - 1),
                    new java.awt.Point(0, toFilter.getHeight() - 1)
            );
        } else {
            LOGGER.log(Level.FINER, "Ignoring black border removal, because image is too small (image < 1x1)");
        }
    }

    /**
     * Add an alpha band to the image and remove any black border if asked.
     *
     * TODO, this could be done more efficiently by adding an ImageLayout hints
     * when doing the coverage reprojection. but hints can not be passed currently.
     */
    public static RenderedImage forceAlpha(RenderedImage img) {
        if (!img.getColorModel().hasAlpha()) {
            //Add alpha channel
            final BufferedImage buffer = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            buffer.createGraphics().drawRenderedImage(img, new AffineTransform());
            img = buffer;
        }
        return img;
    }

    private static void fillColorToleranceTable(int index, int maxIndex, List<double[]> container, double[] baseColor, int tolerance) {
        for (int j = 0 ; j < tolerance; j++) {
            final double[] color = new double[baseColor.length];
            System.arraycopy(baseColor, 0, color, 0, baseColor.length);
            color[index] += j;
            if (index >= maxIndex) {
                container.add(color);
            } else {
                for (int i = index +1 ; i <= maxIndex ; i++) {
                    fillColorToleranceTable(i, maxIndex, container, color, tolerance);
                }
            }
        }
    }

}
