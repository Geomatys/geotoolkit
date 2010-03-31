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
package org.geotoolkit.display.primitive;

import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Locale;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.resources.Errors;


/**
 * A graphic implementation with specialized support for two-dimensional CRS. This
 * default implementation uses <cite>Java2D</cite> geometry objects like {@link Shape},
 * which are somewhat lightweight objects. There is no dependency toward AWT toolkit in
 * this class, which means that this class can be used as a basis for SWT renderer as well.
 *
 * @module pending
 * @since 2.3
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 */
public abstract class AbstractReferencedGraphic2D extends AbstractReferencedGraphic implements ReferencedGraphic2D{
       
    /**
     * The default {@linkplain #getZOrderHint z-order}.
     */
    private static final double DEFAULT_Z_ORDER = Double.POSITIVE_INFINITY;
    
    /**
     * The z value for this graphic.
     *
     * @see #getZOrderHint
     * @see #setZOrderHint
     */
    protected double zOrder = DEFAULT_Z_ORDER;
    
    /**
     * The format used during the last call to {@link #getName}. We use only one instance for
     * all graphics, since an application is likely to use only one locale. However, more locales
     * are allowed; it will just be slower.
     */
    private static Format format;
    
    /**
     * Convenience class for {@link RenderedLayer#getName}.
     * This class should be immutable and thread-safe.
     */
    private static final class Format {
        /** The locale of the {@link #format}. */
        public final Locale locale;
        
        /** The format in the {@link #locale}. */
        public final NumberFormat format;

        /** Construct a format for the given locale. */
        public Format(final Locale locale) {
            this.locale = locale;
            this.format = NumberFormat.getNumberInstance(locale);
        }
    }
        
    /**
     * A geometric shape that fully contains the area painted during the last
     * {@linkplain GraphicPrimitive2D#paint rendering}. This shape must be in terms of the
     * {@linkplain ReferencedCanvas2D#getDisplayCRS display CRS}. A {@link XRectangle2D#INFINITY}
     * value means that the whole canvas area may have been affected. This field should never
     * be null.
     */
    protected transient Shape displayBounds = XRectangle2D.INFINITY;

    /**
     * {@code true} if this canvas or graphic has
     * {@value org.geotoolkit.display.canvas.DisplayObject#DISPLAY_BOUNDS_PROPERTY} properties
     * listeners. Used in order to reduce the amount of {@link PropertyChangeEvent} objects created
     * in the common case where no listener have interest in this property. This optimisation may
     * be worth since a those change event may be sent every time a graphic is painted.
     *
     * @see #listenersChanged
     */
    private boolean hasBoundsListeners;

    /**
     * Constructs a new graphic with a default {@linkplain DefaultEngineeringCRS#GENERIC_2D
     * generic CRS}.
     *
     * @see #setObjectiveCRS
     * @see #setEnvelope
     * @see #setTypicalCellDimension
     * @see #setZOrderHint
     */
    protected AbstractReferencedGraphic2D(final ReferencedCanvas2D canvas) {
        this(canvas,DefaultEngineeringCRS.GENERIC_2D);
    }

    /**
     * Constructs a new graphic using the specified objective CRS.
     *
     * @param  crs The objective coordinate reference system.
     * @throws IllegalArgumentException if {@code crs} is null or has an incompatible number of
     *         dimensions.
     *
     * @see #setObjectiveCRS
     * @see #setEnvelope
     * @see #setTypicalCellDimension
     * @see #setZOrderHint
     */
    protected AbstractReferencedGraphic2D(final ReferencedCanvas2D canvas, final CoordinateReferenceSystem crs)
            throws IllegalArgumentException
    {
        super(canvas,to2D(crs));
    }

    @Override
    public ReferencedCanvas2D getCanvas() {
        return (ReferencedCanvas2D) super.getCanvas();
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static CoordinateReferenceSystem to2D(final CoordinateReferenceSystem crs) {
        try {
            return CRSUtilities.getCRS2D(crs);
        } catch (TransformException e) {
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
    }

//    /**
//     * Sets the objective coordinate refernece system for this graphic.
//     * If the specified CRS has more than two dimensions, then it must be a
//     * {@linkplain org.opengis.referencing.crs.CompoundCRS compound CRS} with
//     * a two dimensional head.
//     * @throws TransformException 
//     */
//    @Override
//    protected void setObjectiveCRS(final CoordinateReferenceSystem newCRS, final CoordinateReferenceSystem oldCRS) throws TransformException {
//        super.setObjectiveCRS(CRSUtilities.getCRS2D(newCRS), oldCRS);
//    }

    /**
     * Set the envelope for this graphic. Subclasses should invokes this method as soon as they
     * known their envelope.
     */
    @Override
    protected void setEnvelope(final Envelope envelope) throws TransformException {
        synchronized (getTreeLock()) {
            super.setEnvelope(CRS.transform(envelope, getCanvas().getObjectiveCRS2D()));
            displayBounds = XRectangle2D.INFINITY;
        }
    }

    /**
     * {@inheritDoc }
     * If no name were {@linkplain #setName explicitly set}, then this method returns a default
     * name built from the {@linkplain #getZOrderHint z order}.
     * 
     * @return specified name or z order if not specified
     */
    @Override
    public String getName() {
        final String name = super.getName();
        
        if(name != null){
            return name;
        }
        
        final Locale locale = getLocale();
        Format f = format; // Avoid the need for synchronization.

        if (f == null || !f.locale.equals(locale)) {
            format = f = new Format(locale);
        }
        final StringBuffer buffer = new StringBuffer("z=");
        return f.format.format(getZOrderHint(), buffer, new FieldPosition(0)).toString();
        
    }
    
    /**
     * Returns the <var>z</var> order hint value for this graphic. Graphics with highest
     * <var>z</var> order will be painted on top of graphics with lowest <var>z</var> order.
     * The default value is {@link Double#POSITIVE_INFINITY}.
     */
    @Override
    public double getZOrderHint() {
        synchronized (getTreeLock()) {
            return zOrder;
        }
    }

    /**
     * Sets the <var>z</var> order hint value for this graphic. Graphics with highest
     * <var>z</var> order will be painted on top of graphics with lowest <var>z</var> order.
     * <p>
     * This method fires a {@value org.geotoolkit.display.canvas.DisplayObject#Z_ORDER_HINT_PROPERTY}
     * property change event.
     */
    @Override
    public void setZOrderHint(final double zOrderHint) {
        if (Double.isNaN(zOrderHint)) {
            throw new IllegalArgumentException(Errors.getResources(getLocale()).getString(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, "zOrderHint", zOrderHint));
        }
        final double oldZOrder;
        synchronized (getTreeLock()) {
            oldZOrder = this.zOrder;
            if (zOrderHint == oldZOrder) {
                return;
            }
            this.zOrder = zOrderHint;
        }
        propertyListeners.firePropertyChange(Z_ORDER_HINT_PROPERTY, oldZOrder, zOrderHint);
    }
    
    /**
     * Returns a geometric shape that fully contains the display area painted during the last
     * {@linkplain GraphicPrimitive2D#paint rendering}. This shape must be in terms of the
     * {@linkplain ReferencedCanvas2D#getDisplayCRS display CRS}. It may be clipped to the
     * {@linkplain ReferencedCanvas2D#getDisplayBounds canvas display bounds}.
     * <p>
     * Note that there is no guarantee that the returned shape is the smallest shape that encloses
     * the graphic display area, only that the graphic display area (possibly clipped to the canvas
     * display area) lies entirely within the indicated shape. More specifically, the returned shape
     * may have infinite extends if the actual graphic bounds are unknown.
     * <p>
     * This method never returns {@code null}.
     */
    @Override
    public final Shape getDisplayBounds() {
        return displayBounds;
    }

    /**
     * Sets the display bounds in terms of {@linkplain ReferencedCanvas2D#getDisplayCRS display CRS}.
     * The display may be approximative, as long as it completely encloses the display area (possibly
     * clipped to the {@linkplain ReferencedCanvas2D#getDisplayBounds canvas display bounds}.
     * Simple shapes with fast {@code contains(...)} and {@code intersects(...)} methods are
     * encouraged.
     * <p>
     * Some canvas implementations will invoke this method automatically in their
     * {@linkplain org.geotoolkit.display.canvas.BufferedCanvas2D rendering method}.
     * <p>
     * This method fires a {@value org.geotoolkit.display.canvas.DisplayObject#DISPLAY_BOUNDS_PROPERTY}
     * property change event.
     */
    protected final void setDisplayBounds(Shape bounds) {
        if (bounds == null) {
            bounds = XRectangle2D.INFINITY;
        }
        final Shape old;
        synchronized (getTreeLock()) {
            old = displayBounds;
            displayBounds = bounds;
            if (hasBoundsListeners) {
                propertyListeners.firePropertyChange(DISPLAY_BOUNDS_PROPERTY, old, bounds);
            }
        }
    }

    /**
     * Advises that this graphic need to be repainted. The graphic will not be repainted
     * immediately, but at some later time depending on the widget implementation (e.g.
     * <cite>Swing</cite>). This {@code refresh()} method can be invoked from any thread;
     * it doesn't need to be the <cite>Swing</cite> thread.
     * <p>
     * Note that this method repaint only the area painted during the last {@linkplain
     * GraphicPrimitive2D#paint rendering}. If this graphic now cover a wider area, then the
     * area to repaint must be specified with a call to {@link #refresh(Rectangle2D)} instead.
     */
    @Override
    public void refresh() {
        synchronized (getTreeLock()) {
            if (displayBounds.equals(XRectangle2D.INFINITY)) {
                refresh(null, displayBounds.getBounds());
            } else {
                refresh(XRectangle2D.INFINITY, null);
            }
        }
    }

    /**
     * Advises that some region need to be repainted. This graphic will not be repainted
     * immediately, but at some later time depending on the widget implementation (e.g.
     * <cite>Swing</cite>). This {@code refrech(...)} method can be invoked from any thread;
     * it doesn't need to be the <cite>Swing</cite> thread.
     *
     * @param bounds The dirty region to refreshed, in the "real world" {@linkplain #getObjectiveCRS
     *        objective coordinate reference system}. A {@code null} value refresh everything.
     */
    @Override
    public void refresh(final Rectangle2D bounds) {
        synchronized (getTreeLock()) {
            refresh(bounds!=null ? bounds : XRectangle2D.INFINITY, null);
        }
    }

    /**
     * Advises that at least a portion of this graphic need to be repainted. This method
     * can been invoked from any thread (may or may not be the <cite>Swing</cite> thread).
     *
     * TODO: implements me. For now, do nothing.
     *
     * @param objectiveArea The dirty region to repaint in terms of
     *        {@linkplain #getObjectiveCRS objective CRS}, or {@code null}.
     * @paral displayArea The dirty region to repaint in terms of
     *        {@linkplain #getDisplayCRS display CRS}, or {@code null}.
     */
    private void refresh(final Rectangle2D objectiveArea, final Rectangle displayArea) {
        final ReferencedCanvas2D canvas = getCanvas();
        //TODO this should be an event, not a direct call to canvas.repaint();
//        try {
//            canvas.repaint(this, objectiveArea, displayArea);
//        } catch (PortrayalException ex) {
//            Logging.getLogger(ReferencedGraphic2D.class).log(Level.SEVERE, null, ex);
//        }
        
    }

    /**
     * Invoked when a property change listener has been {@linkplain #addPropertyChangeListener
     * added} or {@linkplain #removePropertyChangeListener removed}.
     */
    @Override
    protected void listenersChanged() {
        super.listenersChanged();
        hasBoundsListeners = propertyListeners.hasListeners(DISPLAY_BOUNDS_PROPERTY);
    }

}
