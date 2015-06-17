/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2014, Geomatys
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
package org.geotoolkit.display2d.canvas;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display.canvas.CanvasUtilities;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.style.labeling.LabelRenderer;
import org.geotoolkit.display2d.style.labeling.decimate.DecimationLabelRenderer;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.geotoolkit.resources.Errors;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 * Rendering Context for Java2D.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class RenderingContext2D implements RenderingContext{

    private static final Logger LOGGER = Logging.getLogger(RenderingContext2D.class);
    private static final int MAX_WRAP = 3;
    private static final Map<Font,FontMetrics> fontMetrics = new HashMap<>();

    private static final int DISPLAY_TRS = 0;
    private static final int OBJECTIVE_TRS = 1;
    private static final int OTHER_TRS = 2;
    private int current = DISPLAY_TRS;
    
    public final GeometryFactory GF = new GeometryFactory();

    /**
     * The originating canvas.
     */
    private final J2DCanvas canvas;

    /**
     * The graphics handle to use for painting. This graphics is set by {@link BufferedCanvas2D}
     * when a new painting in underway. It is reset to {@code null} once the rendering is finished.
     *
     * @see #getGraphics
     */
    private Graphics2D graphics = null;

    /*
     * cache of the Graphics2D rendering hints.
     */
    private RenderingHints renderingHints = null;

    private double dpi = 90;

    /**
     * A snapshot of {@link ReferencedCanvas#getObjectiveCRS} at the time of painting. This is the
     * "real world" coordinate reference system that the user will see on the screen. Data from all
     * {@link GraphicPrimitive2D} must be transformed to this CRS before to be painted. Units are
     * usually "real world" metres.
     * <p>
     * This coordinate system is usually set once for a given {@link BufferedCanvas2D} and do not
     * change anymore, except if the user wants to change the projection see on screen.
     *
     * @see #displayCRS
     * @see #setGraphicsCRS
     * @see ReferencedCanvas#getObjectiveCRS
     */
    private CoordinateReferenceSystem objectiveCRS = null;
    private CoordinateReferenceSystem objectiveCRS2D = null;

    /**
     * A snapshot of {@link ReferencedCanvas#getDisplayCRS} at the time of painting. This CRS maps
     * the {@linkplain Graphics2D user space} in terms of <cite>Java2D</cite>: each "unit" is a dot
     * (about 1/72 of inch). <var>x</var> values increase toward the right of the screen and
     * <var>y</var> values increase toward the bottom of the screen. This CRS is appropriate
     * for rendering text and labels.
     * <p>
     * This coordinate system may be different between two different renderings,
     * especially if the zoom (or map scale) has changed since the last rendering.
     *
     * @see #objectiveCRS
     * @see #setGraphicsCRS
     * @see ReferencedCanvas#getDisplayCRS
     */
    private CoordinateReferenceSystem displayCRS = null;

    private CanvasMonitor monitor = null;

    private AffineTransform2D objectiveToDisplay = null;
    private AffineTransform2D displayToObjective = null;

    /**
     * Multiple repetition if there is a world wrap
     */
    public RenderingWrapParams wraps = null;

    /**
     * The affine transform from {@link #objectiveCRS} to {@code deviceCRS}. Used by
     * {@link #setGraphicsCRS} when the CRS is {@link #objectiveCRS}. This is a pretty common case,
     * and unfortunately one that is badly optimized by {@link ReferencedCanvas#getMathTransform}.
     */
    private AffineTransform objectiveToDevice = null;

    /**
     * The affine transform from {@link #displayCRS} to {@code deviceCRS}.
     * Used by {@link #setGraphicsCRS} when the CRS is {@link #displayCRS}.
     */
    private AffineTransform displayToDevice = null;

    /**
     * The label renderer. Shall be created only once.
     */
    private LabelRenderer labelRenderer = null;

    /**
     * List of coefficients from "Unit" to Objective CRS.
     */
    private final Map<Unit<Length>,Float> coeffs = new IdentityHashMap<>();

    /**
     * Precalculated resolution, avoid graphics to recalculate it since
     */
    private double[] resolution;

    /**
     * Precalculated geographic scale, avoid graphics to recalculate it.
     */
    private double geoScale = 1;

    /**
     * Precaculated geographic scale calculated using OGC Symbology Encoding
     * Specification.
     * This is not the scale Objective to Display.
     * This is not an accurate geographic scale.
     * This is a fake average scale unproper for correct rendering.
     * It is used only to filter SE rules.
     */
    private double seScale = 1;

    private final Date[] temporalRange = new Date[2];
    private final Double[] elevationRange = new Double[2];


    private Shape              paintingDisplayShape   = null;
    private Rectangle          paintingDisplaybounds  = null;
    private Shape              paintingObjectiveShape = null;
    private Envelope           paintingObjectiveBBox  = null;
    private Envelope           paintingObjectiveBBox2D  = null;
    private BoundingBox        paintingObjectiveBBox2DB  = null;

    private Shape              canvasDisplayShape   = null;
    private Rectangle          canvasDisplaybounds  = null;
    private Shape              canvasObjectiveShape = null;
    private Envelope           canvasObjectiveBBox  = null;
    private Envelope           canvasObjectiveBBox2D  = null;
    private BoundingBox        canvasObjectiveBBox2DB  = null;


    /**
     * Constructs a new {@code RenderingContext} for the specified canvas.
     *
     * @param canvas        The canvas which creates this rendering context.
     */
    public RenderingContext2D(final J2DCanvas canvas) {
        this.canvas = canvas;
    }

    public void initParameters(final AffineTransform2D objToDisp, final CanvasMonitor monitor,
            final Shape paintingDisplayShape, final Shape paintingObjectiveShape,
            final Shape canvasDisplayShape, final Shape canvasObjectiveShape, final double dpi){
        this.canvasObjectiveBBox= canvas.getVisibleEnvelope();
        this.objectiveCRS       = canvasObjectiveBBox.getCoordinateReferenceSystem();
        this.objectiveCRS2D     = canvas.getObjectiveCRS2D();
        this.displayCRS         = canvas.getDisplayCRS();
        this.objectiveToDisplay = objToDisp;
        try {
            this.displayToObjective = (AffineTransform2D) objToDisp.inverse();
        } catch (NoninvertibleTransformException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
        this.monitor = monitor;

        this.labelRenderer = null;

        this.coeffs.clear();
        //set the Pixel coeff = 1
        this.coeffs.put(NonSI.PIXEL, 1f);


        //calculate canvas shape/bounds values ---------------------------------
        this.canvasDisplayShape = canvasDisplayShape;
        final Rectangle2D canvasDisplayBounds = canvasDisplayShape.getBounds2D();
        this.canvasDisplaybounds = canvasDisplayBounds.getBounds();
        this.canvasObjectiveShape = canvasObjectiveShape;

        final Rectangle2D canvasObjectiveBounds = canvasObjectiveShape.getBounds2D();

        //calculate the objective bbox with there temporal and elevation parameters ----
        this.canvasObjectiveBBox2D = new Envelope2D(objectiveCRS2D,canvasObjectiveBounds);
        this.canvasObjectiveBBox2DB = new DefaultBoundingBox(canvasObjectiveBBox2D);

        //calculate the resolution -----------------------------------------------
        this.dpi = dpi;
        this.resolution = new double[2]; //-- explicitely exprime resolution only into multidimensional CRS horizontal 2D part 
        this.resolution[0] = canvasObjectiveBounds.getWidth()/canvasDisplayBounds.getWidth();
        this.resolution[1] = canvasObjectiveBounds.getHeight()/canvasDisplayBounds.getHeight();
        adjustResolutionWithDPI(resolution);

        //calculate painting shape/bounds values -------------------------------
        this.paintingDisplayShape = paintingDisplayShape;
        final Rectangle2D paintingDisplayBounds = paintingDisplayShape.getBounds2D();
        this.paintingDisplaybounds = paintingDisplayBounds.getBounds();
        this.paintingObjectiveShape = paintingObjectiveShape;

        final Rectangle2D paintingObjectiveBounds = paintingObjectiveShape.getBounds2D();
        this.paintingObjectiveBBox2D = new Envelope2D(objectiveCRS2D,paintingObjectiveBounds);
        this.paintingObjectiveBBox2DB = new DefaultBoundingBox(paintingObjectiveBBox2D);
        this.paintingObjectiveBBox = new GeneralEnvelope(canvasObjectiveBBox);
        ((GeneralEnvelope)this.paintingObjectiveBBox).setRange(0, paintingObjectiveBounds.getMinX(), paintingObjectiveBounds.getMaxX());
        ((GeneralEnvelope)this.paintingObjectiveBBox).setRange(1, paintingObjectiveBounds.getMinY(), paintingObjectiveBounds.getMaxY());

        try {
            geoScale = canvas.getGeographicScale();
        } catch (TransformException ex) {
            //could not calculate the geographic scale.
            geoScale = 1;
            LOGGER.log(Level.WARNING, null, ex);
        }

        //set temporal and elevation range--------------------------------------
        final Date[] temporal = canvas.getTemporalRange();
        if(temporal != null){
            temporalRange[0] = temporal[0];
            temporalRange[1] = temporal[1];
        }else{
            Arrays.fill(temporalRange, null);
        }

        final Double[] elevation = canvas.getElevationRange();
        if(elevation != null){
            elevationRange[0] = elevation[0];
            elevationRange[1] = elevation[1];
        }else{
            Arrays.fill(elevationRange, null);
        }

        //calculate the symbology encoding scale -------------------------------
        seScale = CanvasUtilities.computeSEScale(
            getCanvasObjectiveBounds2D(),
            getObjectiveToDisplay(),
            getCanvasDisplayBounds());

        //prepare informations for possible map repetition ---------------------
        wraps = null;
        try {
            //test if wrap is possible
            final DirectPosition[] wrapPoints = ReferencingUtilities.findWrapAround(objectiveCRS2D);

            if(wrapPoints != null){
                //search the multiples transformations
                wraps = new RenderingWrapParams();
                wraps.wrapPoints = wrapPoints;

                //project the 4 canvas bounds points on the wrap line
                final double[] projs = new double[8];
                projs[0] = canvasDisplaybounds.x;                           projs[1] = canvasDisplaybounds.y;
                projs[2] = canvasDisplaybounds.x+canvasDisplaybounds.width; projs[3] = canvasDisplaybounds.y;
                projs[4] = canvasDisplaybounds.x+canvasDisplaybounds.width; projs[5] = canvasDisplaybounds.y+canvasDisplaybounds.height;
                projs[6] = canvasDisplaybounds.x;                           projs[7] = canvasDisplaybounds.y+canvasDisplaybounds.height;
                displayToObjective.transform(projs, 0, projs, 0, 4);

                final double x1 = wrapPoints[0].getOrdinate(0);
                final double y1 = wrapPoints[0].getOrdinate(1);
                final double x2 = wrapPoints[1].getOrdinate(0);
                final double y2 = wrapPoints[1].getOrdinate(1);
                nearestColinearPoint(x1, y1, x2, y2, projs, 0);
                nearestColinearPoint(x1, y1, x2, y2, projs, 2);
                nearestColinearPoint(x1, y1, x2, y2, projs, 4);
                nearestColinearPoint(x1, y1, x2, y2, projs, 6);

                final Point2D.Double p0 = new Point2D.Double(x1, y1);
                final Point2D.Double p1 = new Point2D.Double(x2, y2);

                //test projected points
                final Point2D.Double refVector = toVector(p0, p1);
                double kLeft = 0;
                double kRight = 0;
                for(int i=0;i<8;i+=2){
                    final Point2D.Double candidate = new Point2D.Double(projs[i], projs[i+1]);
                    final Point2D.Double candidateVector = toVector(p0, candidate);

                    double k = refVector.x*candidateVector.x + refVector.y*candidateVector.y;
                    k /= (refVector.x*refVector.x) + (refVector.y*refVector.y);

                    if(k<0){
                        if(kLeft==0){
                            kLeft = -k;
                        }else{
                            kLeft = Math.max(kLeft,-k);
                        }
                    }else if(k>1){
                        k -=1;
                        if(kRight==0){
                            kRight = k;
                        }else{
                            kRight = Math.max(kRight,k);
                        }
                    }
                }
                int nbLeft = (int) Math.ceil(kLeft);
                int nbRight = (int) Math.ceil(kRight);
                if(nbLeft<0)nbLeft=0;
                if(nbRight<0)nbRight=0;
                if(nbLeft >MAX_WRAP) nbLeft  = MAX_WRAP;
                if(nbRight>MAX_WRAP) nbRight = MAX_WRAP;
                wraps.wrapDecNb = nbLeft;
                wraps.wrapIncNb = nbRight;
                
                //increment by one for possible geometry overlaping the meridian
                //those will need and extra repetition
                nbLeft++;
                nbRight++;
                
                //normal transforms
                wraps.wrapObj = new AffineTransform2D(new AffineTransform());
                wraps.wrapObjToDisp = objToDisp;
                
                //decreasing and increasing wraps
                wraps.wrapDecObjToDisp = new AffineTransform2D[nbLeft];
                wraps.wrapDecObj       = new AffineTransform2D[nbLeft];
                wraps.wrapIncObjToDisp = new AffineTransform2D[nbRight];
                wraps.wrapIncObj       = new AffineTransform2D[nbRight];
                
                final AffineTransform dif = new AffineTransform();
                final AffineTransform step = new AffineTransform(objToDisp);
                dif.setToTranslation(x2-x1,y2-y1);
                for(int i=0;i<nbRight;i++){
                    step.concatenate(dif);
                    wraps.wrapIncObj[i] = new AffineTransform2D(1, 0, 0, 1, (x2-x1)*(i+1), (y2-y1)*(i+1));
                    wraps.wrapIncObjToDisp[i] = new AffineTransform2D(step);
                }
                step.setTransform(objToDisp);
                dif.setToTranslation(x1-x2,y1-y2);
                for(int i=0;i<nbLeft;i++){
                    step.concatenate(dif);
                    wraps.wrapDecObj[i] = new AffineTransform2D(1, 0, 0, 1, (x1-x2)*(i+1), (y1-y2)*(i+1));
                    wraps.wrapDecObjToDisp[i] = new AffineTransform2D(step);
                }

                //build the wrap rectangle
                final GeneralEnvelope env = new GeneralEnvelope(objectiveCRS2D);
                env.add(wrapPoints[0]);
                env.add(wrapPoints[1]);
                if(env.getSpan(0) == 0){
                    final double min = canvasObjectiveBBox2D.getMinimum(0);
                    final double max = canvasObjectiveBBox2D.getMaximum(0);
                    env.setRange(0, min, max);
                    wraps.wrapDecLine = GF.createLineString(new Coordinate[]{
                        new Coordinate(min, env.getMinimum(1)), 
                        new Coordinate(max, env.getMinimum(1))});
                    wraps.wrapIncLine = GF.createLineString(new Coordinate[]{
                        new Coordinate(min, env.getMaximum(1)), 
                        new Coordinate(max, env.getMaximum(1))});
                }else{
                    final double min = canvasObjectiveBBox2D.getMinimum(1);
                    final double max = canvasObjectiveBBox2D.getMaximum(1);
                    env.setRange(1, min, max);
                    wraps.wrapDecLine = GF.createLineString(new Coordinate[]{
                        new Coordinate(env.getMinimum(0), min), 
                        new Coordinate(env.getMinimum(0), max)});
                    wraps.wrapIncLine = GF.createLineString(new Coordinate[]{
                        new Coordinate(env.getMaximum(0), min), 
                        new Coordinate(env.getMaximum(0), max)});
                }
                wraps.wrapArea = (com.vividsolutions.jts.geom.Polygon)JTS.toGeometry(env);
                wraps.objectiveJTSEnvelope = new com.vividsolutions.jts.geom.Envelope(
                        canvasObjectiveBounds.getMinX(),canvasObjectiveBounds.getMaxX(),
                        canvasObjectiveBounds.getMinY(),canvasObjectiveBounds.getMaxY());

                //fix the envelopes, normalize them using wrap infos
                canvasObjectiveBBox = ReferencingUtilities.wrapNormalize(canvasObjectiveBBox, wrapPoints);
                canvasObjectiveBBox2D = ReferencingUtilities.wrapNormalize(canvasObjectiveBBox2D, wrapPoints);
                canvasObjectiveBBox2DB = new DefaultBoundingBox(canvasObjectiveBBox2D);

                paintingObjectiveBBox = ReferencingUtilities.wrapNormalize(paintingObjectiveBBox, wrapPoints);
                paintingObjectiveBBox2D = ReferencingUtilities.wrapNormalize(paintingObjectiveBBox2D, wrapPoints);
                paintingObjectiveBBox2DB = new DefaultBoundingBox(paintingObjectiveBBox2D);
            }

        } catch (TransformException ex) {
            LOGGER.log(Level.INFO, ex.getLocalizedMessage(), ex);
        }

    }

    private static Point2D.Double toVector(Point2D.Double p0, Point2D.Double p1){
        return new Point2D.Double(p1.x-p0.x, p1.y-p1.y);
    }

    public static void nearestColinearPoint(final double x1, final double y1,
                                            final double x2, final double y2,
                                            final double[] point, int offset) {
        double x = point[offset];
        double y = point[offset+1];
        final double slope = (y2-y1) / (x2-x1);
        if (!Double.isInfinite(slope)) {
            final double y0 = (y2 - slope*x2);
            x = ((y-y0)*slope+x) / (slope*slope+1);
            y = x*slope + y0;
        } else {
            x = x2;
        }
        point[offset] = x;
        point[offset+1] = y;
    }


    public void initGraphic(final Graphics2D graphics){
        this.graphics           = graphics;
        this.renderingHints     = graphics.getRenderingHints();
        this.displayToDevice    = (graphics != null) ? graphics.getTransform() : null;
        this.objectiveToDevice  = (displayToDevice != null) ? new AffineTransform(displayToDevice) : new AffineTransform();
        this.objectiveToDevice.concatenate(objectiveToDisplay);
        this.current = DISPLAY_TRS;
    }

    public void reset(){
        this.coeffs.clear();
        this.canvasDisplaybounds = null;
        this.displayCRS = null;
        this.canvasDisplayShape = null;
        this.displayToDevice = null;
        this.graphics = null;
        this.renderingHints = null;
        this.labelRenderer = null;
        this.monitor = null;
        this.canvasObjectiveBBox = null;
        this.objectiveCRS = null;
        this.canvasObjectiveShape = null;
        this.objectiveToDevice = null;
        this.objectiveToDisplay = null;
        this.resolution = null;
        this.current = DISPLAY_TRS;
    }

    public void dispose(){
        if(graphics != null){
            graphics.dispose();
        }
        reset();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public J2DCanvas getCanvas(){
        return canvas;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoordinateReferenceSystem getObjectiveCRS() {
        return objectiveCRS;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoordinateReferenceSystem getObjectiveCRS2D() {
        return objectiveCRS2D;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoordinateReferenceSystem getDisplayCRS() {
        return displayCRS;
    }

    /**
     * Returns the graphics where painting occurs. The initial coordinate reference system is
     * {@link #getDisplayCRS()}, which maps the <cite>Java2D</cite> {@linkplain Graphics2D user space}.
     * For drawing shapes directly in terms of "real world" coordinates, users should invoke
     * <code>{@linkplain #setGraphicsCRS setGraphicsCRS}({@linkplain #getObjectiveCRS()})</code> or
     * {@linkplain #switchToDisplayCRS() } and {@linkplain #switchToObjectiveCRS() }.
     * @return Graphics2D
     */
    public final Graphics2D getGraphics() {
        return graphics;
    }

    /**
     * Shortcut method which equals a call to :
     * context.setGraphicsCRS(context.getDisplayCRS);
     * without the need of a try catch.
     *
     * Optimization for a pretty common case.
     */
    public void switchToDisplayCRS() {
        if(current != DISPLAY_TRS){
            graphics.setTransform(displayToDevice);
            current = DISPLAY_TRS;
        }
    }

    /**
     * Shortcut method which equals a call to :
     * context.setGraphicsCRS(context.getObjectiveCRS);
     * whitout the need of a try catch.
     *
     * Optimization for a pretty common case.
     */
    public void switchToObjectiveCRS() {
        if(current != OBJECTIVE_TRS){
            graphics.setTransform(objectiveToDevice);
            current = OBJECTIVE_TRS;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setGraphicsCRS(CoordinateReferenceSystem crs) throws TransformException {

        if (crs == displayCRS) {
            switchToDisplayCRS();
        }else if (crs == objectiveCRS || crs == objectiveCRS2D) {
            switchToObjectiveCRS();
        } else try {
            crs = CRSUtilities.getCRS2D(crs);
            AffineTransform at = getAffineTransform(crs, displayCRS);
            at.preConcatenate(displayToDevice);
            current = OTHER_TRS;
            graphics.setTransform(at);
        } catch (FactoryException e) {
            throw new TransformException(Errors.format(
                        Errors.Keys.IllegalCoordinateReferenceSystem), e);
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AffineTransform getAffineTransform(final CoordinateReferenceSystem sourceCRS,
                                              final CoordinateReferenceSystem targetCRS)
            throws FactoryException {
        final MathTransform mt =
                canvas.getMathTransform(sourceCRS, targetCRS,
                        RenderingContext2D.class, "getAffineTransform");
        try {
            return (AffineTransform) mt;
        } catch (ClassCastException cause) {
            throw new FactoryException(Errors.format(Errors.Keys.NotAnAffineTransform), cause);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MathTransform getMathTransform(final CoordinateReferenceSystem sourceCRS,
                                          final CoordinateReferenceSystem targetCRS)
            throws FactoryException {
        return canvas.getMathTransform(sourceCRS, targetCRS,
                RenderingContext2D.class, "getMathTransform");
    }

    /**
     * Like the Graphics class, this create method makes a clone of the current
     * Rendering context. this may be use in multithread to avoid several object to work
     * on the same context.
     * @param g2d Graphics2D
     * @return RenderingContext2D
     */
    public RenderingContext2D create(final Graphics2D g2d){
        final RenderingContext2D context = new RenderingContext2D(canvas);
        context.initParameters(objectiveToDisplay, monitor,
                               paintingDisplayShape, paintingObjectiveShape,
                               canvasDisplayShape, canvasObjectiveShape, dpi);
        context.initGraphic(g2d);
        g2d.setRenderingHints(this.graphics.getRenderingHints());
        context.labelRenderer = getLabelRenderer(true);
        return context;
    }

    /**
     * Get or Create a label renderer for this rendering context.
     * @param create : if true will create a label renderer if there is none.
     * @return FontMetrics
     */
    public LabelRenderer getLabelRenderer(final boolean create) {
        if(labelRenderer == null && create){
            Class candidate = (Class)canvas.getRenderingHint(GO2Hints.KEY_LABEL_RENDERER_CLASS);

            if(candidate != null && LabelRenderer.class.isAssignableFrom(candidate)){
                try {
                    labelRenderer = (LabelRenderer) candidate.newInstance();
                    labelRenderer.setRenderingContext(this);
                } catch (InstantiationException | IllegalAccessException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
            }else{
                labelRenderer = new DecimationLabelRenderer();
                labelRenderer.setRenderingContext(this);
            }
        }
        return labelRenderer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CanvasMonitor getMonitor() {
        return monitor;
    }



    // Informations related to scale datas -------------------------------------

    public FontMetrics getFontMetrics(Font f) {
        FontMetrics fm = fontMetrics.get(f);
        if(fm == null){
            fm = getGraphics().getFontMetrics(f);
            fontMetrics.put(f, fm);
        }
        return fm;
    }

    /**
     * Find the coefficient between the given Unit and the Objective CRS.
     * @param unit
     * @return float
     */
    public float getUnitCoefficient(final Unit<Length> uom){
        Float f = coeffs.get(uom);
        if(f==null){
            f = GO2Utilities.calculateScaleCoefficient(this,uom);
            coeffs.put(uom, f);
        }

        return f;
    }

    public double getDPI() {
        return dpi;
    }

    /**
     * Returns the current painting resolution. this may be used in the parameters
     * given to gridCoverageReaders to extract the best resolution grid coverage.
     * This resolution is between Objective and Display CRS.
     *
     * @return double[] of 2 dimensions
     */
    public double[] getResolution() {
        return resolution.clone();
    }

    /**
     * Returns the current painting resolution. this may be used in the parameters
     * given to gridCoverageReaders to extract the best resolution grid coverage.
     * This resolution is between the given CRS and Display CRS.
     *
     * @param crs
     * @return double[] of 2 dimensions
     */
    public double[] getResolution(final CoordinateReferenceSystem crs) {
        if (CRS.equalsIgnoreMetadata(objectiveCRS, crs)) {
            return getResolution();
        } else {
            final double[] newRes = new double[2];
            
            assert resolution.length == 2 : "RenderingContext2D : Resolution array should have length equals to 2. Founded length : "+resolution.length;
            assert CRS.equalsIgnoreMetadata(canvasObjectiveBBox2D.getCoordinateReferenceSystem(), objectiveCRS2D) : "RenderingContext2D : canvasObjectiveBBox2D should own same CRS than objectiveCRS2D"; 
            
            try {
                final CoordinateReferenceSystem target2DCRS = CRSUtilities.getCRS2D(crs);
                ReferencingUtilities.convertResolution(canvasObjectiveBBox2D, resolution, target2DCRS, newRes);

            } catch (TransformException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
            return adjustResolutionWithDPI(newRes);
        }
    }

    /**
     * Adjust the resolution relative to 90 DPI.
     * a dpi under 90 with raise the resolution level while
     * a bigger spi will lower the resolution level.
     */
    private double[] adjustResolutionWithDPI(final double[] res){
        res[0] = (90/dpi) * res[0];
        res[1] = (90/dpi) * res[1];
        return res;
    }

    /**
     * Returns the scale factor, or {@link Double#NaN NaN} if the scale is unknow. The scale factor
     * is usually smaller than 1. For example for a 1:1000 scale, the scale factor will be 0.001.
     * This scale factor takes in account the physical size of the rendering device (e.g. the
     * screen size) if such information is available. Note that this scale can't be more accurate
     * than the {@linkplain java.awt.GraphicsConfiguration#getNormalizingTransform() information
     * supplied by the underlying system}.
     *
     * @return The rendering scale factor as a number between 0 and 1, or {@link Double#NaN}.
     * @see CanvasController2D#getScale()
     */
    public double getScale() {
        return canvas.getScale();
    }

    /**
     * Returns the geographic scale, like we can see in scalebar legends '1 : 200 000'
     * This is mainly used in style rules to check the minimum and maximum scales.
     * @return
     */
    public double getGeographicScale() {
        return geoScale;
    }

    /**
     * Geographic scale calculated using OGC Symbology Encoding specification.
     * This is not the scale Objective to Display.
     * This is not an accurate geographic scale.
     * This is a fake average scale unproper for correct rendering.
     * It is used only to filter SE rules.
     * @return
     */
    public double getSEScale() {
        return seScale;
    }

    /**
     * @return affine transform from objective CRS to display CRS.
     */
    public AffineTransform2D getObjectiveToDisplay() {
        return objectiveToDisplay;
    }

    /**
     * @return affine transform from display CRS to objective CRS.
     */
    public AffineTransform2D getDisplayToObjective() {
        return displayToObjective;
    }

    @Override
    public Date[] getTemporalRange() {
        return temporalRange;
    }

    @Override
    public Double[] getElevationRange() {
        return elevationRange;
    }

    // Informations about the currently painted area ---------------------------

    /**
     * {@inheritDoc }
     */
    public Shape getPaintingDisplayShape(){
        return paintingDisplayShape;
    }

    /**
     * {@inheritDoc }
     */
    public Rectangle getPaintingDisplayBounds(){
        return paintingDisplaybounds;
    }

    /**
     * {@inheritDoc }
     */
    public Shape getPaintingObjectiveShape(){
        return paintingObjectiveShape;
    }

    /**
     * {@inheritDoc }
     */
    public BoundingBox getPaintingObjectiveBounds2D(){
        return paintingObjectiveBBox2DB;
    }

    /**
     * {@inheritDoc }
     */
    public Envelope getPaintingObjectiveBounds(){
        return paintingObjectiveBBox;
    }

    // Informations about the complete canvas area -----------------------------

    /**
     * {@inheritDoc }
     */
    public Shape getCanvasDisplayShape() {
        return canvasDisplayShape;
    }

    /**
     * {@inheritDoc }
     */
    public Rectangle getCanvasDisplayBounds() {
        return canvasDisplaybounds;
    }

    /**
     * {@inheritDoc }
     */
    public Shape getCanvasObjectiveShape() {
        return canvasObjectiveShape;
    }

    /**
     * {@inheritDoc }
     */
    public BoundingBox getCanvasObjectiveBounds2D() {
        return canvasObjectiveBBox2DB;
    }

    /**
     * {@inheritDoc }
     */
    public Envelope getCanvasObjectiveBounds() {
        return canvasObjectiveBBox;
    }

    /**
     * Use this methods rather send getGraphics().getRenderingHints.
     * This method cases the result for better performances.
     * @return rendering hints of the graphics 2D.
     */
    public RenderingHints getRenderingHints() {
        return renderingHints;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("========== Rendering Context 2D ==========\n");

        sb.append("---------- Coordinate Reference Systems ----------\n");
        sb.append("Objective CRS = \n");
        sb.append(objectiveCRS).append("\n");
        sb.append("Objective CRS 2D = \n");
        sb.append(objectiveCRS2D).append("\n");
        sb.append("Display CRS = \n");
        sb.append(displayCRS).append("\n");

        if(resolution != null){
            sb.append("Resolution = ");
            for(double d : resolution){
                sb.append(d).append("   ");
            }
        }

        sb.append("\n");
        sb.append("Geographic Scale = ");
        sb.append(geoScale).append("\n");
        sb.append("OGC SE Scale = ");
        sb.append(seScale).append("\n");
        sb.append("Temporal range = ");
        sb.append(temporalRange[0]).append("  to  ").append(temporalRange[1]).append("\n");
        sb.append("Elevation range = ");
        sb.append(elevationRange[0]).append("  to  ").append(elevationRange[1]).append("\n");

        sb.append("\n---------- Canvas Geometries ----------\n");
        sb.append("Display Shape = \n");
        sb.append(canvasDisplayShape).append("\n");
        sb.append("Display Bounds = \n");
        sb.append(canvasDisplaybounds).append("\n");
        sb.append("Objective Shape = \n");
        sb.append(canvasObjectiveShape).append("\n");
        sb.append("Objective BBOX = \n");
        sb.append(canvasObjectiveBBox).append("\n");
        sb.append("Objective BBOX 2D = \n");
        sb.append(canvasObjectiveBBox2D).append("\n");

        sb.append("\n---------- Painting Geometries (dirty area) ----------\n");
        sb.append("Display Shape = \n");
        sb.append(paintingDisplayShape).append("\n");
        sb.append("Display Bounds = \n");
        sb.append(paintingDisplaybounds).append("\n");
        sb.append("Objective Shape = \n");
        sb.append(paintingObjectiveShape).append("\n");
        sb.append("Objective BBOX = \n");
        sb.append(paintingObjectiveBBox).append("\n");
        sb.append("Objective BBOX 2D = \n");
        sb.append(paintingObjectiveBBox2D).append("\n");

        sb.append("\n---------- Transforms ----------\n");
        sb.append("Objective to Display = \n");
        sb.append(objectiveToDisplay).append("\n");
        sb.append("Display to Objective = \n");
        sb.append(displayToObjective).append("\n");


        sb.append("\n---------- Rendering Hints ----------\n");
        if(renderingHints != null){
            for(Entry<Object,Object> entry : renderingHints.entrySet()){
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }
        }

        sb.append("========== Rendering Context 2D ==========\n");
        return sb.toString();
    }

}
