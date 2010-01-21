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
package org.geotoolkit.display2d.canvas;

import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.geotoolkit.display.canvas.ReferencedCanvas;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.style.labeling.DefaultLabelRenderer;
import org.geotoolkit.display2d.style.labeling.LabelRenderer;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.logging.Logging;

import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;


/**
 * Informations relative to a rendering in progress. A {@code RenderingContext} instance is
 * created by {@link J2DCanvas} at rendering time, which iterates over all graphic
 * objects and invokes the rendering process for each of them. The rendering context
 * is disposed once the rendering is completed. {@code RenderingContext} instances contain the
 * following informations:
 * <p>
 * <ul>
 *   <li>The {@link Graphics2D} handler to use for rendering.</li>
 *   <li>The coordinate reference systems in use and the transformations between them.</li>
 *   <li>The area rendered up to date. This information shall be updated by each
 *       {@link GraphicJ2D} while they are painting.</li>
 *   <li>The map scale.</li>
 * </ul>
 * <p>
 * A rendering usually implies the following transformations (names are
 * {@linkplain CoordinateReferenceSystem coordinate reference systems} and arrows
 * are {@linkplain MathTransform transforms}):
 * 
 * <p align="center">
 * &nbsp; {@code graphicCRS}    &nbsp; <img src="doc-files/right.png">
 * &nbsp; {@link #objectiveCRS} &nbsp; <img src="doc-files/right.png">
 * &nbsp; {@link #displayCRS}   &nbsp; <img src="doc-files/right.png">
 * &nbsp; {@code deviceCRS}
 * </p>
 * 
 * @module pending
 * @since 2.3
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @author Johann Sorel (Geomatys)
 */
public final class DefaultRenderingContext2D implements RenderingContext2D{

    private static final Logger LOGGER = Logging.getLogger(DefaultRenderingContext2D.class);

    private static final int DISPLAY_TRS = 0;
    private static final int OBJECTIVE_TRS = 1;
    private static final int OTHER_TRS = 2;
    private int current = DISPLAY_TRS;

    /**
     * The originating canvas.
     */
    private final ReferencedCanvas2D canvas;

    /**
     * The graphics handle to use for painting. This graphics is set by {@link BufferedCanvas2D}
     * when a new painting in underway. It is reset to {@code null} once the rendering is finished.
     *
     * @see #getGraphics
     */
    private Graphics2D graphics = null;

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
     * The affine transform from {@link #objectiveCRS} to {@code deviceCRS}. Used by
     * {@link #setGraphicsCRS} when the CRS is {@link #objectiveCRS}. This is a pretty common case,
     * and unfortunatly one that is badly optimized by {@link ReferencedCanvas#getMathTransform}.
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
    private final Map<Unit<Length>,Float> coeffs = new HashMap<Unit<Length>, Float>();
    
    /**
     * Precalculated resolution, avoid graphics to recalculate it since
     */
    private final double[] resolution = new double[2];

    /**
     * Precalculated geographic scale, avoid graphics to recalculate it.
     */
    private double geoScale = 1;

    private final Date[] temporalRange = new Date[2];
    private final Double[] elevationRange = new Double[2];



    private Shape              paintingDisplayShape   = null;
    private Rectangle          paintingDisplaybounds  = null;
    private Shape              paintingObjectiveShape = null;
    private GeneralEnvelope    paintingObjectiveBBox  = null;
    private Envelope           paintingObjectiveBBox2D  = null;

    private Shape              canvasDisplayShape   = null;
    private Rectangle          canvasDisplaybounds  = null;
    private Shape              canvasObjectiveShape = null;
    private GeneralEnvelope    canvasObjectiveBBox  = null;
    private Envelope           canvasObjectiveBBox2D  = null;
    

    /**
     * Constructs a new {@code RenderingContext} for the specified canvas.
     *
     * @param canvas        The canvas which creates this rendering context.
     */
    public DefaultRenderingContext2D(final ReferencedCanvas2D canvas) {
        this.canvas = canvas;
    }

    public void initParameters(final AffineTransform2D objToDisp, final CanvasMonitor monitor,
            final Shape paintingDisplayShape, final Shape paintingObjectiveShape,
            final Shape canvasDisplayShape, final Shape canvasObjectiveShape, Date[] temporal, Double[] elevation){
        this.objectiveCRS       = canvas.getObjectiveCRS();
        this.objectiveCRS2D = canvas.getObjectiveCRS2D();
        this.displayCRS         = canvas.getDisplayCRS();
        this.objectiveToDisplay = objToDisp;
        try {
            this.displayToObjective = (AffineTransform2D) objToDisp.inverse();
        } catch (NoninvertibleTransformException ex) {
            Logging.getLogger(DefaultRenderingContext2D.class).log(Level.SEVERE, null, ex);
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
        this.resolution[0] = canvasObjectiveBounds.getWidth()/canvasDisplayBounds.getWidth();
        this.resolution[1] = canvasObjectiveBounds.getHeight()/canvasDisplayBounds.getHeight();
//        canvasObjectiveBounds.setRect(canvasObjectiveBounds.getX()-d0,
//                                      canvasObjectiveBounds.getY()-d1,
//                                      canvasObjectiveBounds.getWidth()+2*d0,
//                                      canvasObjectiveBounds.getHeight()+2*d1);
        this.canvasObjectiveBBox2D = new Envelope2D(objectiveCRS2D,canvasObjectiveBounds);
        try {
            this.canvasObjectiveBBox = GO2Utilities.combine(objectiveCRS, canvasObjectiveBounds, temporal, elevation);
        } catch (TransformException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        //calculate painting shape/bounds values -------------------------------
        this.paintingDisplayShape = paintingDisplayShape;
        final Rectangle2D paintingDisplayBounds = paintingDisplayShape.getBounds2D();
        this.paintingDisplaybounds = paintingDisplayBounds.getBounds();
        this.paintingObjectiveShape = paintingObjectiveShape;

        final Rectangle2D paintingObjectiveBounds = paintingObjectiveShape.getBounds2D();
        this.paintingObjectiveBBox2D = new Envelope2D(objectiveCRS2D,paintingObjectiveBounds);
        try {
            this.paintingObjectiveBBox = GO2Utilities.combine(objectiveCRS, paintingObjectiveBounds, temporal, elevation);
        } catch (TransformException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        geoScale = canvas.getController().getGeographicScale();

        //set temporal and elevation range
        if(temporal != null){
            temporalRange[0] = temporal[0];
            temporalRange[1] = temporal[1];
        }else{
            temporalRange[0] = null;
            temporalRange[1] = null;
        }
        if(elevation != null){
            elevationRange[0] = elevation[0];
            elevationRange[1] = elevation[1];
        }else{
            elevationRange[0] = null;
            elevationRange[1] = null;
        }

    }

    public void initGraphic(final Graphics2D graphics){
        this.graphics           = graphics;
        this.displayToDevice    = (graphics != null) ? graphics.getTransform() : null;
        this.objectiveToDevice  = (displayToDevice != null) ? new AffineTransform(displayToDevice) : new AffineTransform();
        this.objectiveToDevice.concatenate(objectiveToDisplay);
    }

    public void reset(){
        this.coeffs.clear();
        this.canvasDisplaybounds = null;
        this.displayCRS = null;
        this.canvasDisplayShape = null;
        this.displayToDevice = null;
        this.graphics = null;
        this.labelRenderer = null;
        this.monitor = null;
        this.canvasObjectiveBBox = null;
        this.objectiveCRS = null;
        this.canvasObjectiveShape = null;
        this.objectiveToDevice = null;
        this.objectiveToDisplay = null;
        this.resolution[0] = 1;
        this.resolution[1] = 1;
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
    public ReferencedCanvas2D getCanvas(){
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
     * {@inheritDoc }
     */
    @Override
    public final Graphics2D getGraphics() {
        return graphics;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void switchToDisplayCRS() {
        if(current != DISPLAY_TRS){
            graphics.setTransform(displayToDevice);
            current = DISPLAY_TRS;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
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
                        Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM), e);
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
                        DefaultRenderingContext2D.class, "getAffineTransform");
        try {
            return (AffineTransform) mt;
        } catch (ClassCastException cause) {
            throw new FactoryException(Errors.format(Errors.Keys.NOT_AN_AFFINE_TRANSFORM), cause);
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
                DefaultRenderingContext2D.class, "getMathTransform");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public RenderingContext2D create(final Graphics2D g2d){
        final DefaultRenderingContext2D context = new DefaultRenderingContext2D(canvas);
        context.initParameters(objectiveToDisplay, monitor,
                               paintingDisplayShape, paintingObjectiveShape,
                               canvasDisplayShape, canvasObjectiveShape,temporalRange,elevationRange);
        context.initGraphic(g2d);
        g2d.setRenderingHints(this.graphics.getRenderingHints());
        context.labelRenderer = getLabelRenderer(true);
        return context;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public LabelRenderer getLabelRenderer(final boolean create) {
        if(labelRenderer == null && create){
            Class candidate = (Class)canvas.getRenderingHint(GO2Hints.KEY_LABEL_RENDERER_CLASS);

            if(candidate != null && LabelRenderer.class.isAssignableFrom(candidate)){
                try {
                    labelRenderer = (LabelRenderer) candidate.newInstance();
                    labelRenderer.setRenderingContext(this);
                } catch (InstantiationException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }else{
                labelRenderer = new DefaultLabelRenderer();
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
    /**
     * {@inheritDoc }
     */
    @Override
    public float getUnitCoefficient(final Unit<Length> uom){
        Float f = coeffs.get(uom);
        if(f==null){
            f = GO2Utilities.calculateScaleCoefficient(this,uom);
            coeffs.put(uom, f);
        }

        return f;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double[] getResolution() {
        return resolution.clone();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double[] getResolution(CoordinateReferenceSystem crs) {
        if(CRS.equalsIgnoreMetadata(objectiveCRS2D, crs)){
            return getResolution();
        }else{
            final double[] res = getResolution();

            final Envelope env;
            try {
                env = CRS.transform(canvasObjectiveBBox, crs);
                final Rectangle2D canvasCRSBounds = new Rectangle2D.Double(0, 0, env.getSpan(0), env.getSpan(1));
                res[0] = Math.abs(canvasCRSBounds.getWidth()/canvasDisplaybounds.getWidth());
                res[1] = Math.abs(canvasCRSBounds.getHeight()/canvasDisplaybounds.getHeight());
            } catch (TransformException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
            
            return res;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getScale() {
        return canvas.getController().getScale();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getGeographicScale() {
        return geoScale;
    }

    // Informations about the currently painted area ---------------------------
    /**
     * {@inheritDoc }
     */
    @Override
    public Shape getPaintingDisplayShape(){
        return paintingDisplayShape;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Rectangle getPaintingDisplayBounds(){
        return paintingDisplaybounds;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Shape getPaintingObjectiveShape(){
        return paintingObjectiveShape;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BoundingBox getPaintingObjectiveBounds2D(){
        return new DefaultBoundingBox(paintingObjectiveBBox2D);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getPaintingObjectiveBounds(){
        return paintingObjectiveBBox;
    }

    // Informations about the complete canvas area -----------------------------
    /**
     * {@inheritDoc }
     */
    @Override
    public Shape getCanvasDisplayShape() {
        return canvasDisplayShape;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Rectangle getCanvasDisplayBounds() {
        return canvasDisplaybounds;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Shape getCanvasObjectiveShape() {
        return canvasObjectiveShape;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BoundingBox getCanvasObjectiveBounds2D() {
        return new DefaultBoundingBox(canvasObjectiveBBox2D);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getCanvasObjectiveBounds() {
        return canvasObjectiveBBox;
    }

    @Override
    public AffineTransform2D getObjectiveToDisplay() {
        return objectiveToDisplay;
    }

    @Override
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

}
