/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display.canvas;

import java.awt.Component;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.measure.unit.Unit;
import javax.measure.unit.SI;
import javax.measure.unit.NonSI;
import javax.measure.converter.ConversionException;

import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.DirectPosition;

import org.geotoolkit.display.container.AbstractContainer;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.referencing.operation.matrix.AffineMatrix3;
import org.geotoolkit.util.Utilities;

/**
 * A canvas implementation with default support for two-dimensional CRS management. This
 * default implementation uses <cite>Java2D</cite> geometry objects like {@link Shape} and
 * {@link AffineTransform}, which are somewhat lightweight objects. There is no dependency
 * toward AWT toolkit in this class (which means that this class can be used as a basis for
 * SWT renderer as well), and this class does not assume a rectangular widget.
 *
 * @module pending
 * @since 2.3
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @author Johann Sorel (Geomatys)
 */
public abstract class ReferencedCanvas2D extends ReferencedCanvas {
        
    /**
     * The affine transform from the {@linkplain #getObjectiveCRS objective CRS} to the
     * {@linkplain #getDisplayCRS display CRS}. This transform is zoom dependent, but device
     * independent.
     * <p>
     * If a subclass changes the values provided in this matrix, then it should invokes
     * <code>{@linkplain #setObjectiveToDisplayTransform(org.opengis.referencing.operation.Matrix)
     * setObjectiveToDisplayTransform}(objectiveToDisplay)</code> in order to reflect those changes
     * into this canvas CRS, and <code>{@linkplain #zoomChanged zoomChanged}(change)</code> in
     * order to notify listeners.
     *
     * @see #getObjectiveCRS
     * @see #getDisplayCRS
     * @see #getObjectiveToDisplayTransform
     * @see #zoomChanged
     */
    AffineMatrix3 objectiveToDisplay = new AffineMatrix3();

    protected final AffineMatrix3 previousObjectiveToDisplay = new AffineMatrix3();

    /**
     * The affine transform from the {@linkplain #getDisplayCRS display CRS} to the {@linkplain
     * #getDeviceCRS device CRS}. This transform is set as if no clipping were performed by
     * <cite>Swing</cite>. When the output device is the screen, this transform contains
     * only <var>x</var> and <var>y</var> translation terms, which are the {@link Rectangle#x}
     * and {@link Rectangle#y} values of the {@linkplain #getDisplayBounds display bounds}
     * respectively. This transform is often the identity transform.
     * <p>
     * If a subclass changes the values provided in this matrix, then it should invokes
     * <code>{@linkplain #setDisplayToDeviceTransform(org.opengis.referencing.operation.Matrix)
     * setDisplayToDeviceTransform}(displayToDevice)</code> in order to reflect those changes
     * into this canvas CRS.
     *
     * @see #getDisplayCRS
     * @see #getDeviceCRS
     */
    protected AffineMatrix3 displayToDevice = new AffineMatrix3();

    private final AffineMatrix3 previousDisplayToDevice = new AffineMatrix3();

    /**
     * The affine transform from the units used in the {@linkplain #getObjectiveCRS objective CRS}
     * to "dots" units. A dots is equals to 1/72 of inch. This transform is basically nothing else
     * than an unit conversion; This transform is used as a convenient step in the computation of a
     * realistic {@linkplain #getScale scale factor}.
     * <p>
     * This affine transform is computed automatically when {@link #setObjectiveCRS} is invoked.
     * Users don't need to change its value.
     *
     * @see #updateNormalizationFactor
     */
    protected final AffineTransform normalizeToDots = new AffineTransform();

    /**
     * If different than 1, then calls to the {@linkplain #zoomChanged} method will be performed
     * with an affine transform expanded by this factor. This is used in order to avoid rounding
     * error in calculation of widget {@link java.awt.geom.Area} that need to be refreshed.
     */
    private static final double SCALE_ZOOM_CHANGE = 1.000001;

    /**
     * The display bounds. Initially set to an infinite rectangle.
     * This field should never be null.
     */
    private Shape displayBounds = XRectangle2D.INFINITY;

    /**
     * The widget area (in {@linkplain #getDisplayCRS display coordinates}) enqueued for painting,
     * or {@code null} if no painting is in process. This field is set indirectly at the begining
     * of {@link BufferedCanvas2D#paint}, and reset to {@code null} as soon as this canvas has been
     * painted. This information is used by {@link #repaint} in order to avoid repainting twice the
     * same area.
     */
    private transient Shape dirtyArea;

    /**
     * Creates an initially empty canvas with a default CRS.
     *
     * @param renderer Renderer to use with this canvas
     */
    protected ReferencedCanvas2D(CoordinateReferenceSystem crs) {
        this(crs,null);
    }
    
    /**
     * Creates an initially empty canvas with a default CRS.
     *
     * @param renderer Renderer to use with this canvas
     */
    protected ReferencedCanvas2D(CoordinateReferenceSystem crs,Hints hints) {
        super(crs,hints);
        // The following must be invoked here instead than in super-class because
        // 'normalizeToDots' is not yet assigned when the super-class constructor
        // is run.
        updateNormalizationFactor(getObjectiveCRS());
    }

    @Override
    public void setContainer(AbstractContainer renderer) {
        if(!(renderer instanceof AbstractContainer2D)){
            throw new IllegalArgumentException("ReferencedCanvas2D needs a AbstractContainer2D.");
        }
        super.setContainer(renderer);
    }

    @Override
    public AbstractContainer2D getContainer() {
        return (AbstractContainer2D) super.getContainer();
    }

    @Override
    public abstract CanvasController2D getController();
    
    /**
     * Returns the display bounds in terms of {@linkplain #getDisplayCRS display CRS}.
     * The display shape doesn't need to be {@linkplain Rectangle rectangular}. The
     * display bounds is often {@link java.awt.Component#getBounds()}.
     * <p>
     * If the display bounds is unknown, then this method returns a shape with infinite extends.
     * This method should never returns {@code null}.
     *
     * @return
     */
    public Shape getDisplayBounds() {
        return displayBounds;
    }

    public synchronized Shape getObjectiveBounds() throws TransformException{
        final MathTransform2D transform = (MathTransform2D) getDisplayToObjectiveTransform();
        final Shape bounds = getDisplayBounds();
        return transform.createTransformedShape(bounds);
    }
    
    /**
     * Sets the display bounds in terms of {@linkplain #getDisplayCRS display CRS}.
     * The display shape is usually {@linkplain Rectangle rectangular}, but this is not mandatory.
     * <p>
     * The display bounds could be the {@linkplain #getEnvelope envelope} of all graphics
     * {@linkplain #objectiveToDisplay transformed} from objective to display CRS, in which
     * case it would be zoom-dependent. But more often, this is rather the
     * {@linkplain java.awt.Component#getBounds() widget bounds}, which is zoom-independent
     * (instead, the content displayed in the widget changes). In the later case,
     * {@code setDisplayBounds} is usually not invoked after {@link #setDisplayCRS}.
     * <p>
     * This method fires a {@value org.geotools.display.canvas.DisplayObject#DISPLAY_BOUNDS_PROPERTY}
     * property change event.
     *
     * @param bounds The new canvas bounds in display coordinates.
     */
    public synchronized void setDisplayBounds(Shape bounds) {
        if (bounds == null) {
            bounds = XRectangle2D.INFINITY;
        }
        final Shape old;
        old = displayBounds;
        displayBounds = bounds;
        propertyListeners.firePropertyChange(DISPLAY_BOUNDS_PROPERTY, old, bounds);
    }

    /**
     * Transforms the specified rectangle from the {@linkplain #getObjectiveCRS objective CRS} to
     * the {@linkplain #getDisplayCRS display CRS} used by <cite>Java2D</cite>. The transformation
     * used is the {@link #objectiveToDisplay} affine transform, which is usually the transform
     * used the last time that the {@link BufferedCanvas2D#paint} method was invoked.
     *
     * @param  bounds The rectangle in terms of {@linkplain #getObjectiveCRS objective CRS}.
     * @return The rectangle in terms of {@linkplain #getDisplayCRS display CRS}.
     */
    public final Rectangle objectiveToDisplay(final Rectangle2D bounds) {
//        assert Thread.holdsLock(this);
        return (Rectangle) XAffineTransform.transform(previousObjectiveToDisplay, bounds, new Rectangle());
    }

    /**
     * Advises that at least a portion of this canvas need to be repainted. This canvas will not be
     * repainted immediately, but at some later time depending on the widget implementation. This
     * {@code repaint(...)} method can be invoked from any thread; it doesn't need to be the
     * <cite>Swing</cite> thread.
     * <p>
     * Usually only one of {@code objectiveArea} and {@code displayArea} arguments is provided. If
     * both arguments are non-null, then this method repaint the {@linkplain Rectangle#add union}
     * of those rectangles in display coordinates.
     * <p>
     * This method is invoked by {@link ReferencedGraphic2D#refresh()} and usually don't need to be
     * invoked directly.
     *
     * @param graphic The graphic to repaint, or {@code null} if unknown.
     * @param objectiveArea The dirty region to repaint in terms of
     *        {@linkplain #getObjectiveCRS objective CRS}, or {@code null}.
     * @param displayArea The dirty region to repaint in terms of
     *        {@linkplain #getDisplayCRS display CRS}, or {@code null}.
     */
    public abstract void repaint(Shape displayArea);

    /**
     * Returns {@code true} if the specified area is scheduled for painting. More specifically,
     * returns {@code true} if {@link #paintStarted} has been invoked, {@link #paintFinished} has
     * not yet been invoked, and the dirty area given to {@code paintStarted} encloses completly
     * the area given to this {@code isDirtyArea} method.
     *
     * @param  area The area to test, in terms of {@linkplain #getDisplayCRS display CRS}.
     * @return {@code true} if the specified area is already in process of being painted.
     */
    protected final boolean isDirtyArea(final Rectangle area) {
//        assert Thread.holdsLock(this);
        if (dirtyArea == null) {
            return true;
        }
        if (area == null) {
            return dirtyArea.equals(XRectangle2D.INFINITY);
        }
        return dirtyArea.contains(area);
    }

    /**
     * Call when a repaint is needed.
     */
    public abstract void repaint();
        
    /**
     * Returns {@code true} if the given coordinate is visible on this {@code Canvas}. The default
     * implementation checks if the coordinate (transformed in terms of {@linkplain #getDisplayCRS
     * display CRS}) is inside the {@linkplain #getDisplayBounds display bounds}.
     */
    @Override
    public synchronized boolean isVisible(final DirectPosition coordinate) {
        final GeneralDirectPosition position;
        try {
            position = toDisplayPosition(coordinate);
        } catch (TransformException e) {
            /*
             * A typical reason for transformation failure is a coordinate point outside the area
             * of validity. If the specified point is outside the area of validity of the CRS used
             * by this canvas, then we can reasonably assume that it is outside the canvas envelope
             * as well.
             */
            return false;
        }
        return getDisplayBounds().contains(position.ordinates[0], position.ordinates[1]);
    }

    /**
     * Updates {@link #normalizeToDots} affine transform for the specified
     * {@linkplain #getObjectiveCRS objective coordinate reference system}.
     * This method is invoked automatically by {@link #setObjectiveCRS}.
     * Users don't need to invoke this method directly, but subclasses may
     * override it.
     *
     * @param crs The new objective CRS.
     */
    @Override
    protected void updateNormalizationFactor(final CoordinateReferenceSystem crs) {
        super.updateNormalizationFactor(crs);
        final Ellipsoid ellipsoid = CRSUtilities.getHeadGeoEllipsoid(crs);
        final CoordinateSystem cs = crs.getCoordinateSystem();
        final Unit<?>       unit0 = cs.getAxis(0).getUnit();
        final Unit<?>       unit1 = cs.getAxis(1).getUnit();
        final boolean    sameUnit = Utilities.equals(unit0, unit1);
        normalizeToDots.setToScale(getNormalizationFactor(unit0, ellipsoid, true),
                                   getNormalizationFactor(unit1, ellipsoid, !sameUnit));
    }

    /**
     * Returns the amount of "dots" in one unit of the specified unit. There is 72 dots in one
     * inch, and 2.54/100 inchs in one metre.  The {@code unit} argument must be a linear or
     * an angular unit.
     *
     * @param unit The unit. If {@code null}, then the unit will be assumed to be metres or
     *        degrees depending of whatever {@code ellipsoid} is {@code null} or not.
     * @param ellipsoid The ellipsoid if the CRS is geographic, or {@code null} otherwise.
     * @param log {@code true} if this method is allowed to log a warning in case of failure.
     *        This is used in order to avoid logging the same message twice.
     */
    private double getNormalizationFactor(Unit<?> unit, final Ellipsoid ellipsoid, final boolean log) {
        double m = 1;
        try {
            if (ellipsoid != null) {
                if (unit == null) {
                    unit = NonSI.DEGREE_ANGLE;
                }
                /*
                 * Converts an angular unit to a linear one.   An ellipsoid has two axis that we
                 * could use. For the WGS84 ellipsoid, the semi-major axis results in a nautical
                 * mile of 1855.32 metres  while  the semi-minor axis results in a nautical mile
                 * of 1849.10 metres. The average of semi-major and semi-minor axis results in a
                 * nautical mile of 1852.21 metres, which is pretty close to the internationaly
                 * agreed length (1852 metres). This is consistent with the definition of nautical
                 * mile, which is the length of an angle of 1 minute along the meridian at 45° of
                 * latitude.
                 */
                m = unit.getConverterTo(SI.RADIAN).convert(m) *
                        0.5*(ellipsoid.getSemiMajorAxis() + ellipsoid.getSemiMinorAxis());
                unit = ellipsoid.getAxisUnit();
            }
            if (unit != null) {
                m = unit.getConverterTo(SI.METER).convert(m);
            }
        } catch (ConversionException exception) {
            /*
             * A unit conversion failed. Since this normalizing factor is used only for computing a
             * scale, it is not crucial to the renderer working. Log a warning message and continue.
             * We keep the m value computed so far, which will be assumed to be a length in metres.
             */
            if (log) {
                final LogRecord record;
                record = Loggings.getResources(getLocale()).getLogRecord(
                        Level.WARNING, Loggings.Keys.UNEXPECTED_UNIT_$1, unit);
                record.setSourceClassName(ReferencedCanvas2D.class.getName());
                record.setSourceMethodName("setObjectiveCRS");
                record.setThrown(exception);
                getLogger().log(record);
            }
        }
        return 7200/2.54 * m;
    }

    /**
     * Invoked when the {@linkplain #objectiveToDisplay objective to display transform} changed.
     * This method updates cached informations like the envelope in every graphics.
     *
     * @param change The zoom <strong>change</strong> in terms of {@linkplain #getDisplayCRS
     *        display CRS}, or {@code null} if unknown. If {@code null}, then all graphics will
     *        be fully redrawn during the next rendering (i.e. all offscreen buffers are flushed).
     *
     * @see GraphicPrimitive2D#zoomChanged
     *
     * @todo Rename as {@code scaleChanged} and expect a {@code ScaleChangeEvent} argument with
     *       old and new scale, affine transform change and affine transform change scaled.
     */
    protected void zoomChanged(final AffineTransform change) {
        if (change!=null && change.isIdentity()) {
            return;
        }
        //should call a zoom change event that will make each graphic fire a change event
    }

    /**
     * Invoked when the display bounds may have changed as a result of component resizing.
     */
    protected void displayBoundsChanged(Shape oldBounds, Shape newBounds) {
        propertyListeners.firePropertyChange(DISPLAY_BOUNDS_PROPERTY, oldBounds, newBounds);
    }

    protected AffineMatrix3 setObjectiveToDisplayTransform(Rectangle clipBounds) throws TransformException{

        final Rectangle displayBounds = getDisplayBounds().getBounds();

        /*
         * If the zoom has changed, send a notification to all graphics before to start the
         * rendering. Graphics will update their cache, which is used in order to decide if
         * a graphic needs to be repainted or not. Note that some graphics may change their
         * state, which may results in a new 'paint' event to be fired.  But because of the
         * 'dirtyArea' flag above, some 'paint' event will be intercepted in order to avoid
         * repainting the same area twice.
         */
        if (!previousObjectiveToDisplay.equals(objectiveToDisplay)) {
            /*
             * Computes the change as an affine transform, and send the notification.
             * Optionnaly scale slightly the change in order to avoid rounding errors
             * in calculation of widget area that need to be refreshed.
             */
            try {
                final AffineTransform change = previousObjectiveToDisplay.createInverse();
                change.preConcatenate(objectiveToDisplay);
                if (SCALE_ZOOM_CHANGE != 1) {
                    final double centerX = displayBounds.getCenterX();
                    final double centerY = displayBounds.getCenterY();
                    change.translate(      centerX,           centerY);
                    change.scale(SCALE_ZOOM_CHANGE, SCALE_ZOOM_CHANGE);
                    change.translate(     -centerX,          -centerY);
                }
                zoomChanged(change);
            } catch (NoninvertibleTransformException exception) {
                /*
                 * Should not happen. If it happen anyway, declare that everything must be
                 * repainted. It will be slower, but will not prevent the renderer to work.
                 */
                handleException(ReferencedCanvas2D.class, "paint", exception);
                zoomChanged(null);
            }
            try {
                /*
                 * Computes the new scale factor. This scale factor takes in account the real
                 * size of the rendering device (e.g. the screen), but is only as accurate as
                 * the information supplied by the underlying system.
                 */
                final AffineTransform normalize = objectiveToDisplay.createInverse();
                normalize.concatenate(displayToDevice);
                normalize.preConcatenate(normalizeToDots);
//                setScale(1 / XAffineTransform.getScale(normalize)); -----------called a scale change on all graphics using obsolet method zoomChanged
            } catch (NoninvertibleTransformException exception) {
                handleException(ReferencedCanvas2D.class, "paint", exception);
            }
            /*
             * Now takes in account the zoom change. The 'displayCRS' must be recreated. Failure
             * to create this CRS will make the rendering process impossible. In such case, we
             * will paint the stack trace right into the component and exit from this method.
             */
            previousObjectiveToDisplay.setTransform(objectiveToDisplay);
            setObjectiveToDisplayTransform(previousObjectiveToDisplay);

        }
        /*
         * If the device changed, then the 'deviceCRS' must be recreated. Failure to create this
         * CRS will make the rendering process impossible. In such case, we will paint the stack
         * trace right into the component and exit from this method.
         */
        // TODO: concatenate with the information provided in config. Check if changed since last call.
        previousDisplayToDevice.setToTranslation(-displayBounds.x, -displayBounds.y);
        setDisplayToDeviceTransform(previousDisplayToDevice);


        return previousObjectiveToDisplay;
    }

    public abstract Image getSnapShot();

}
