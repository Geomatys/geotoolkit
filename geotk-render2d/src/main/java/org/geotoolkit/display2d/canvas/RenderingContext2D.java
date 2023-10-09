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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.internal.referencing.provider.Affine;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.DefaultDerivedCRS;
import org.apache.sis.referencing.operation.DefaultConversion;
import org.apache.sis.referencing.operation.builder.LinearTransformBuilder;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.util.Utilities;
import org.geotoolkit.display.canvas.CanvasUtilities;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.style.labeling.LabelRenderer;
import org.geotoolkit.display2d.style.labeling.decimate.DecimationLabelRenderer;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.resources.Errors;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.display.canvas.AbstractCanvas2D.toRectangle;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.Matrix;


/**
 * Rendering Context for Java2D.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class RenderingContext2D implements RenderingContext{

    /**
     * The operation method used by {@link #getDisplayCRS()}.
     * This is a temporary constant, as we will probably need to replace the creation
     * of a {@link DefaultDerivedCRS} by something else. After that replacement, this
     * constant will be removed.
     */
    private static final Affine DISPLAY_TO_OBJECTIVE_OPERATION = new Affine();

    /**
     * 50pixels ensure large strokes of graphics won't show on the map.
     * TODO : need to find a better way to reduce the geometry preserving length
     */
    private static final int CLIP_PIXEL_MARGIN = 50;

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.display2d.canvas");
    private static final int MAX_WRAP = 3;
    private static final Map<Font,FontMetrics> fontMetrics = new HashMap<>();

    private static final int DISPLAY_TRS = 0;
    private static final int OBJECTIVE_TRS = 1;
    private static final int OTHER_TRS = 2;
    private int current = DISPLAY_TRS;

    public final GeometryFactory GF = JTS.getFactory();

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
    private Class labelRendererClass = null;

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
     * Precalculated objective to display rotation.
     */
    private double rotation = 0.0;

    /**
     * Precaculated geographic scale calculated using OGC Symbology Encoding
     * Specification.
     * This is not the scale Objective to Display.
     * This is not an accurate geographic scale.
     * This is a fake average scale unproper for correct rendering.
     * It is used only to filter SE rules.
     */
    private double seScale = 1;

    private final Hints hints = new Hints();

    private final Date[] temporalRange = new Date[2];
    private final Double[] elevationRange = new Double[2];

    private Rectangle          canvasDisplaybounds  = null;
    private Envelope           canvasObjectiveBBox  = null;
    private Envelope2D         canvasObjectiveBBox2D  = null;

    private GridGeometry gridGeometry;
    private GridGeometry gridGeometry2d;

    //clipping geometries
    private Rectangle2D displayClipRect;
    private Polygon displayClip;

    private final GeometryCSTransformer objToDisplayTransformer =
            new GeometryCSTransformer(new CoordinateSequenceMathTransformer(null));
    /**
     * This envelope should be the painted area in ojective CRS,
     * but symbolizer may need to enlarge it because of symbols size.
     */
    public org.locationtech.jts.geom.Envelope objectiveJTSEnvelope = null;

    private final Map<CoordinateReferenceSystem, MathTransform> dataToDisplay = new HashMap<>();
    private final Map<CoordinateReferenceSystem, MathTransform> dataToObjective = new HashMap<>();


    /**
     * Constructs a new {@code RenderingContext} for the specified canvas.
     *
     * @param canvas        The canvas which creates this rendering context.
     */
    public RenderingContext2D(J2DCanvas canvas, GridGeometry gridGeometry, GridGeometry gridGeometry2d, final CanvasMonitor monitor, final double dpi) {
        ensureNonNull("Grid geometry", gridGeometry);
        ensureNonNull("Grid geometry 2D", gridGeometry2d);
        this.gridGeometry = gridGeometry;
        this.gridGeometry2d = gridGeometry2d;
        this.canvasObjectiveBBox= canvas.getVisibleEnvelope();
        this.objectiveCRS       = canvasObjectiveBBox.getCoordinateReferenceSystem();
        this.objectiveCRS2D     = canvas.getObjectiveCRS2D();
        this.displayCRS         = canvas.getDisplayCRS();

        this.canvasDisplaybounds = toRectangle(gridGeometry2d.getExtent());
        try {
            this.displayToObjective = (AffineTransform2D) MathTransforms.concatenate(
                    MathTransforms.translation(canvasDisplaybounds.x, canvasDisplaybounds.y),
                    gridGeometry2d.getGridToCRS(PixelInCell.CELL_CORNER));
            this.objectiveToDisplay = displayToObjective.inverse();
        } catch (NoninvertibleTransformException ex) {
            throw new IllegalArgumentException("2D Renderer support only grid geometries providing an invertible grid2Crs", ex);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("2D Renderer support only grid geometries whose grid to crs conversion is a 2D affine transform", ex);
        }

        this.monitor = monitor;
        ((CoordinateSequenceMathTransformer)this.objToDisplayTransformer.getCSTransformer())
                .setTransform(objectiveToDisplay);

        this.labelRenderer = null;
        this.labelRendererClass = (Class)canvas.getRenderingHint(GO2Hints.KEY_LABEL_RENDERER_CLASS);

        this.coeffs.clear();
        //set the Pixel coeff = 1
        this.coeffs.put(Units.POINT, 1f);


        //calculate canvas shape/bounds values ---------------------------------
        this.displayClipRect = (Rectangle2D) canvasDisplaybounds.clone();
        this.displayClipRect.setRect(
                displayClipRect.getX()-CLIP_PIXEL_MARGIN,
                displayClipRect.getY()-CLIP_PIXEL_MARGIN,
                displayClipRect.getWidth()+2*CLIP_PIXEL_MARGIN,
                displayClipRect.getHeight()+2*CLIP_PIXEL_MARGIN);
        this.displayClip = JTS.toGeometry(canvasDisplaybounds);

        //calculate the objective bbox with there temporal and elevation parameters ----
        this.canvasObjectiveBBox2D = new Envelope2D(gridGeometry2d.getEnvelope());

        //calculate the resolution -----------------------------------------------
        this.dpi = dpi;
        this.resolution = gridGeometry2d.getResolution(true); //-- explicitely exprime resolution only into multidimensional CRS horizontal 2D part
        adjustResolutionWithDPI(resolution);

        try {
            geoScale = canvas.getGeographicScale();
        } catch (TransformException ex) {
            //could not calculate the geographic scale.
            geoScale = 1;
            LOGGER.log(Level.WARNING, null, ex);
        }

        rotation = AffineTransforms2D.getRotation(objectiveToDisplay);

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
            DirectPosition[] wrapPoints = ReferencingUtilities.findWrapAround(objectiveCRS2D);

//            /*
//            Check if current envelope is within the wrap range
//            if so disable wrapping.
//            */
//            if (wrapPoints != null) {
//
//                //east-west wrapaound axis index, this is the none zero axis
//                final int horizontalIdx = (wrapPoints[0].getOrdinate(1) != 0 || wrapPoints[1].getOrdinate(1) != 0) ? 1 : 0;
//                final double wrapMin = wrapPoints[0].getOrdinate(horizontalIdx);
//                final double wrapMax = wrapPoints[1].getOrdinate(horizontalIdx);
//
//                final Envelope envelope = gridGeometry2d.getEnvelope();
//                final double[] lower = envelope.getLowerCorner().getCoordinate();
//                final double[] upper = envelope.getUpperCorner().getCoordinate();
//
//                if (lower[horizontalIdx] > upper[horizontalIdx]) {
//                    //crossing anti-meridian, reversed envelope
//                } else if (
//                        (lower[horizontalIdx] < wrapMin && upper[horizontalIdx] > wrapMin) ||
//                        (lower[horizontalIdx] < wrapMax && upper[horizontalIdx] > wrapMax)
//                        ) {
//                    //crossing anti-meridian by extension, can happen when envelope are build by hand
//                } else {
//                    //not crossing
//                    wrapPoints = null;
//                }
//            }

            // TODO: verify if a simple envelopes.normalize or using SIS wrap-around adjustement would be enough to replace this code
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
                wraps.wrapObjToDisp = this.objectiveToDisplay;

                //decreasing and increasing wraps
                wraps.wrapDecObjToDisp = new AffineTransform2D[nbLeft];
                wraps.wrapDecObj       = new AffineTransform2D[nbLeft];
                wraps.wrapIncObjToDisp = new AffineTransform2D[nbRight];
                wraps.wrapIncObj       = new AffineTransform2D[nbRight];

                final AffineTransform dif = new AffineTransform();
                final AffineTransform step = new AffineTransform(objectiveToDisplay);
                dif.setToTranslation(x2-x1,y2-y1);
                for(int i=0;i<nbRight;i++){
                    step.concatenate(dif);
                    wraps.wrapIncObj[i] = new AffineTransform2D(1, 0, 0, 1, (x2-x1)*(i+1), (y2-y1)*(i+1));
                    wraps.wrapIncObjToDisp[i] = new AffineTransform2D(step);
                }
                step.setTransform(objectiveToDisplay);
                dif.setToTranslation(x1-x2,y1-y2);
                for(int i=0;i<nbLeft;i++){
                    step.concatenate(dif);
                    wraps.wrapDecObj[i] = new AffineTransform2D(1, 0, 0, 1, (x1-x2)*(i+1), (y1-y2)*(i+1));
                    wraps.wrapDecObjToDisp[i] = new AffineTransform2D(step);
                }

                //build the wrap rectangle
                //Envelope domainOfValidity = CRS.getDomainOfValidity(objectiveCRS2D);
                final GeneralEnvelope env = new GeneralEnvelope(objectiveCRS2D);
                env.add(wrapPoints[0]);
                env.add(wrapPoints[1]);
                if(env.getSpan(0) == 0){
                    final double min = canvasObjectiveBBox2D.getMinimum(0);
                    final double max = canvasObjectiveBBox2D.getMaximum(0);
                    env.setRange(0, -Double.MAX_VALUE, Double.MAX_VALUE);
                    wraps.wrapDecLine = GF.createLineString(new Coordinate[]{
                        new Coordinate(min, env.getMinimum(1)),
                        new Coordinate(max, env.getMinimum(1))});
                    wraps.wrapIncLine = GF.createLineString(new Coordinate[]{
                        new Coordinate(min, env.getMaximum(1)),
                        new Coordinate(max, env.getMaximum(1))});
                }else{
                    final double min = canvasObjectiveBBox2D.getMinimum(1);
                    final double max = canvasObjectiveBBox2D.getMaximum(1);
                    env.setRange(1, -Double.MAX_VALUE, Double.MAX_VALUE);
                    wraps.wrapDecLine = GF.createLineString(new Coordinate[]{
                        new Coordinate(env.getMinimum(0), min),
                        new Coordinate(env.getMinimum(0), max)});
                    wraps.wrapIncLine = GF.createLineString(new Coordinate[]{
                        new Coordinate(env.getMaximum(0), min),
                        new Coordinate(env.getMaximum(0), max)});
                }
                wraps.wrapArea = (org.locationtech.jts.geom.Polygon)JTS.toGeometry(env);
                wraps.objectiveJTSEnvelope = new org.locationtech.jts.geom.Envelope(
                        canvasObjectiveBBox2D.getMinX(),canvasObjectiveBBox2D.getMaxX(),
                        canvasObjectiveBBox2D.getMinY(),canvasObjectiveBBox2D.getMaxY());

                //fix the envelopes, normalize them using wrap infos
                canvasObjectiveBBox = ReferencingUtilities.wrapNormalize(canvasObjectiveBBox, wrapPoints);
                canvasObjectiveBBox2D = new Envelope2D(ReferencingUtilities.wrapNormalize(canvasObjectiveBBox2D, wrapPoints));
            }

        } catch (TransformException ex) {
            LOGGER.log(Level.INFO, ex.getLocalizedMessage(), ex);
        }

        //TODO add a extra margin for large symbols
        //this dhould depend on symbol size, API is updating, for now hardcode this value
        if (wraps != null && wraps.objectiveJTSEnvelope != null) {
            this.objectiveJTSEnvelope = new org.locationtech.jts.geom.Envelope(wraps.objectiveJTSEnvelope);
            this.objectiveJTSEnvelope.expandBy(50);
        }

        this.hints.putAll(canvas.getHints(true));
    }

    public RenderingContext2D(GridGeometry gridGeometry, CanvasMonitor monitor) {
        this.gridGeometry = gridGeometry;

        //extract 2d grid
        final int[] space2d = gridGeometry.getExtent().getSubspaceDimensions(2);
        this.gridGeometry2d = gridGeometry.selectDimensions(space2d);

        this.canvasObjectiveBBox = gridGeometry.getEnvelope();
        this.objectiveCRS = gridGeometry.getCoordinateReferenceSystem();
        this.objectiveCRS2D = gridGeometry2d.getCoordinateReferenceSystem();
        this.displayToObjective = (AffineTransform2D) gridGeometry2d.getGridToCRS(PixelInCell.CELL_CENTER);
        try {
            this.objectiveToDisplay = displayToObjective.inverse();
        } catch (NoninvertibleTransformException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
        this.monitor = monitor;
        ((CoordinateSequenceMathTransformer)this.objToDisplayTransformer.getCSTransformer())
                .setTransform(objectiveToDisplay);

        //set the Pixel coeff = 1
        this.coeffs.put(Units.POINT, 1f);
        /*
         * TODO: will need a way to avoid the cast below. In my understanding, DerivedCRS may not be the appropriate
         *       CRS to create after all, because in ISO 19111 a DerivedCRS is more than just a base CRS with a math
         *       transform. A DerivedCRS may also "inherit" some characteritics of the base CRS. For example if the
         *       base CRS is a VerticalCRS, then the DerivedCRS may also implement VerticalCRS.
         *
         *       I'm not yet sure what should be the appropriate kind of CRS to create here. ImageCRS? EngineeringCRS?
         *       How to express the relationship to the base CRS is also not yet determined.
         */
        final SingleCRS objCRS2D = (SingleCRS) getObjectiveCRS2D();
        final Map<String,?> name = Collections.singletonMap(DefaultDerivedCRS.NAME_KEY, "Derived - "+objCRS2D.getName().toString());
        this.displayCRS = DefaultDerivedCRS.create(name, objCRS2D,
                new DefaultConversion(name, DISPLAY_TO_OBJECTIVE_OPERATION, getObjectiveToDisplay(), null),
                objCRS2D.getCoordinateSystem());

        //calculate canvas shape/bounds values ---------------------------------
        this.canvasDisplaybounds = toRectangle(gridGeometry2d.getExtent());
        this.displayClipRect = (Rectangle2D) canvasDisplaybounds.clone();
        this.displayClipRect.setRect(
                displayClipRect.getX()-CLIP_PIXEL_MARGIN,
                displayClipRect.getY()-CLIP_PIXEL_MARGIN,
                displayClipRect.getWidth()+2*CLIP_PIXEL_MARGIN,
                displayClipRect.getHeight()+2*CLIP_PIXEL_MARGIN);
        this.displayClip = JTS.toGeometry(canvasDisplaybounds);

        //calculate the objective bbox with there temporal and elevation parameters ----
        this.canvasObjectiveBBox2D = new Envelope2D(gridGeometry2d.getEnvelope());

        //calculate the resolution -----------------------------------------------
        this.dpi = 90;
        this.resolution = gridGeometry2d.getResolution(true); //-- explicitely exprime resolution only into multidimensional CRS horizontal 2D part
        adjustResolutionWithDPI(resolution);

        try {
            geoScale = computeGeographicScale(this.gridGeometry);
        } catch (TransformException ex) {
            //could not calculate the geographic scale.
            geoScale = 1;
            LOGGER.log(Level.WARNING, null, ex);
        }

        rotation = AffineTransforms2D.getRotation(objectiveToDisplay);

        //set temporal and elevation range--------------------------------------
        final Date[] temporal = computeTemporalRange(gridGeometry);
        if(temporal != null){
            temporalRange[0] = temporal[0];
            temporalRange[1] = temporal[1];
        }else{
            Arrays.fill(temporalRange, null);
        }

        final Double[] elevation = computeElevationRange(gridGeometry);
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
    }

    private static Point2D.Double toVector(Point2D.Double p0, Point2D.Double p1){
        return new Point2D.Double(p1.x-p0.x, p1.y-p0.y);
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
        this.displayToDevice = null;
        this.graphics = null;
        this.renderingHints = null;
        this.labelRenderer = null;
        this.monitor = null;
        this.canvasObjectiveBBox = null;
        this.objectiveCRS = null;
        this.objectiveToDevice = null;
        this.objectiveToDisplay = null;
        this.resolution = null;
        this.current = DISPLAY_TRS;
        this.rotation = 0.0;
    }

    public void dispose(){
        if(graphics != null){
            graphics.dispose();
        }
        reset();
    }

    @Override
    public Optional<?> getHint(RenderingHints.Key key) {
        return Optional.ofNullable(hints.get(key));
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
     * Returns an affine transform between two coordinate reference systems. This method is
     * equivalents to the following pseudo-code, except for the exception to be thrown if the
     * transform is not an instance of {@link AffineTransform}.
     *
     * <blockquote><pre>
     * return (AffineTransform) {@link #getMathTransform getMathTransform}(sourceCRS, targetCRS);
     * </pre></blockquote>
     *
     * @param sourceCRS The source coordinate reference system.
     * @param targetCRS The target coordinate reference system.
     * @return An affine transform from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException if the transform can't be created or is not affine.
     *
     * @see #getMathTransform
     * @see BufferedCanvas2D#getImplHint
     */
    private AffineTransform getAffineTransform(final CoordinateReferenceSystem sourceCRS,
                                              final CoordinateReferenceSystem targetCRS)
            throws FactoryException {
        final MathTransform mt =
                getMathTransform(sourceCRS, targetCRS);
        try {
            return (AffineTransform) mt;
        } catch (ClassCastException cause) {
            throw new FactoryException(Errors.format(Errors.Keys.NotAnAffineTransform), cause);
        }
    }

    /**
     * Returns a transform between two coordinate systems.
     * The arguments are usually (but not necessarily) one of the following pairs:
     *
     * <ul>
     *   <li><p><b>({@code graphicCRS}, {@linkplain #objectiveCRS}):</b><br>
     *       Arbitrary transform from the data CRS (used internally in a {@link GraphicPrimitive2D})
     *       to the objective CRS (set in {@link BufferedCanvas2D}).</p></li>
     *
     *   <li><p><b>({@link #objectiveCRS}, {@link #displayCRS}):</b><br>
     *       {@linkplain AffineTransform Affine transform} from the objective CRS in "real world"
     *       units (usually metres or degrees) to the display CRS in dots (usually 1/72 of inch).
     *       This transform changes every time the zoom (or map scale) changes.</p></li>
     * </ul>
     *
     * @param sourceCRS The source coordinate reference system.
     * @param targetCRS The target coordinate reference system.
     * @return A transform from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException if the transformation can't be created.
     *
     * @see #getAffineTransform
     * @see BufferedCanvas2D#getImplHint
     */
    private MathTransform getMathTransform(final CoordinateReferenceSystem sourceCRS,
                                          final CoordinateReferenceSystem targetCRS)
            throws FactoryException {
        return CRS.findOperation(sourceCRS, targetCRS, null).getMathTransform();
    }

    public synchronized MathTransform getDataToObjective(final CoordinateReferenceSystem dataCrs) throws FactoryException, NoninvertibleTransformException {
        MathTransform trs = dataToObjective.get(dataCrs);
        if (trs == null) {
            //trs = MathTransforms.concatenate(getDataToDisplay(dataCrs), displayToObjective);
            trs = getMathTransform(dataCrs, objectiveCRS2D);
            dataToObjective.put(dataCrs, trs);
        }
        return trs;
    }

    public synchronized MathTransform getDataToDisplay(final CoordinateReferenceSystem dataCrs) throws FactoryException, NoninvertibleTransformException {
        MathTransform trs = dataToDisplay.get(dataCrs);
        if (trs == null) {
            final MathTransform unoptimized = getMathTransform(dataCrs, displayCRS);
            trs = unoptimized;
            dataToDisplay.put(dataCrs, unoptimized);

            /*
            TODO : does not work as expected, provides exellent performance improvements
            especialy in reprojection but an offset appears with zooming in.
            try {
                trs = new GridOptimizedTransform(unoptimized, gridGeometry2d);
                dataToDisplay.put(dataCrs, trs);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                Logger.getLogger(RenderingContext2D.class.getName()).log(Level.SEVERE, null, ex);
            }
            */
        }
        return trs;
    }

    /**
     * Like the Graphics class, this create method makes a clone of the current
     * Rendering context. this may be use in multithread to avoid several object to work
     * on the same context.
     * @param g2d Graphics2D
     * @return RenderingContext2D
     */
    public RenderingContext2D create(final Graphics2D g2d) {
        final RenderingContext2D context = new RenderingContext2D(getGridGeometry(), monitor);
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
            if(labelRendererClass != null && LabelRenderer.class.isAssignableFrom(labelRendererClass)){
                try {
                    labelRenderer = (LabelRenderer) labelRendererClass.newInstance();
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

    public GridGeometry getGridGeometry() {
        return gridGeometry;
    }

    public GridGeometry getGridGeometry2D() {
        return gridGeometry2d;
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
     */
    public float getUnitCoefficient(final Unit<Length> uom){
        Float f = coeffs.get(uom);
        if (f == null) {
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
     * @return double[] of 2 dimensions
     */
    public double[] getResolution(final CoordinateReferenceSystem crs) {
        if (Utilities.equalsIgnoreMetadata(objectiveCRS, crs)) {
            return getResolution();
        } else {
            final double[] newRes = new double[2];

            assert resolution.length == 2 : "RenderingContext2D : Resolution array should have length equals to 2. Founded length : "+resolution.length;
            assert Utilities.equalsIgnoreMetadata(canvasObjectiveBBox2D.getCoordinateReferenceSystem(), objectiveCRS2D) : "RenderingContext2D : canvasObjectiveBBox2D should own same CRS than objectiveCRS2D";

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
     * Returns the geographic scale, like we can see in scalebar legends '1 : 200 000'
     * This is mainly used in style rules to check the minimum and maximum scales.
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
     */
    public double getSEScale() {
        return seScale;
    }

    /**
     *
     * @return objective to display rotation.
     */
    public double getRotation() {
        return rotation;
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

    public GeometryCSTransformer getObjectiveToDisplayGeometryTransformer() {
        return objToDisplayTransformer;
    }

    // Informations about the complete canvas area -----------------------------

    /**
     * {@inheritDoc }
     */
    public Rectangle getCanvasDisplayBounds() {
        return canvasDisplaybounds;
    }

    /**
     * {@inheritDoc }
     */
    public Envelope getCanvasObjectiveBounds2D() {
        return canvasObjectiveBBox2D;
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

    public Rectangle2D getDisplayClipRectangle() {
        return displayClipRect;
    }

    public Polygon getDisplayClipPolygon() {
        return displayClip;
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
        sb.append("Display Bounds = \n");
        sb.append(canvasDisplaybounds).append("\n");
        sb.append("Objective BBOX = \n");
        sb.append(canvasObjectiveBBox).append("\n");
        sb.append("Objective BBOX 2D = \n");
        sb.append(canvasObjectiveBBox2D).append("\n");

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


    /**
     * Returns the geographic scale, in a ground unit manner, relation between map display size
     * and real ground unit meters.
     *
     * @throws org.opengis.referencing.operation.TransformException
     * @throws IllegalStateException If the affine transform used for conversion is in
     *                               illegal state.
     */
    private final double computeGeographicScale(GridGeometry grid) throws TransformException {
        double[] pointOfInterest = grid.getExtent().getPointOfInterest(PixelInCell.CELL_CENTER);
        return CanvasUtilities.getGeographicScale(new Point2D.Double(pointOfInterest[0], pointOfInterest[1]),
                getObjectiveToDisplay(), getObjectiveCRS2D());
    }

    private static final Date[] computeTemporalRange(GridGeometry grid) {
        final int index = getTemporalAxisIndex(grid);
        if (index >= 0) {
            final Envelope envelope = grid.getEnvelope();
            final Date[] range = new Date[2];
            final double min = envelope.getMinimum(index);
            final double max = envelope.getMaximum(index);
            range[0] = Double.isInfinite(min) ? null : new Date((long)min);
            range[1] = Double.isInfinite(max) ? null : new Date((long)max);
            return range;
        }
        return null;
    }

    private static final Double[] computeElevationRange(GridGeometry grid) {
        final int index = getElevationAxisIndex(grid);
        if (index >= 0) {
            final Envelope envelope = grid.getEnvelope();
            return new Double[]{envelope.getMinimum(index), envelope.getMaximum(index)};
        }
        return null;
    }

    /**
     * Find the elevation axis index or -1 if there is none.
     */
    private static final int getElevationAxisIndex(GridGeometry grid) {
        final CoordinateReferenceSystem objCrs = grid.getCoordinateReferenceSystem();
        final CoordinateSystem cs = objCrs.getCoordinateSystem();
        for (int i = 0, n = cs.getDimension(); i < n; i++) {
            final AxisDirection direction = cs.getAxis(i).getDirection();
            final Unit unit = cs.getAxis(i).getUnit();
            if (direction == AxisDirection.UP || direction == AxisDirection.DOWN && (unit != null && unit.isCompatible(Units.METRE))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find the temporal axis index or -1 if there is none.
     */
    private static final int getTemporalAxisIndex(GridGeometry grid) {
        final CoordinateReferenceSystem objCrs = grid.getCoordinateReferenceSystem();
        final CoordinateSystem cs = objCrs.getCoordinateSystem();
        for (int i = 0, n = cs.getDimension(); i < n; i++) {
            final AxisDirection direction = cs.getAxis(i).getDirection();
            if (direction == AxisDirection.FUTURE || direction == AxisDirection.PAST) {
                return i;
            }
        }
        return -1;
    }
}
