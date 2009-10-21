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
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;


/**
 * A graphic implementation with specialized support for two-dimensional CRS. This
 * default implementation uses <cite>Java2D</cite> geometry objects like {@link Shape},
 * which are somewhat lightweight objects. There is no dependency toward AWT toolkit in
 * this class, which means that this class can be used as a basis for SWT renderer as well.
 *
 * @module pending
 * @since 2.3
 * @source $URL$
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 */
public interface ReferencedGraphic2D extends ReferencedGraphic {
    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * canvas {@linkplain ReferencedCanvas2D#getDisplayBounds display bounds} changed.
     */
    public static final String DISPLAY_BOUNDS_PROPERTY = "displayBounds";
    
    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain AbstractGraphic#getZOrderHint z order hint} changed.
     */
    public static final String Z_ORDER_HINT_PROPERTY = "zOrderHint";
    
    @Override
    ReferencedCanvas2D getCanvas();
    
    /**
     * Returns the <var>z</var> order hint value for this graphic. Graphics with highest
     * <var>z</var> order will be painted on top of graphics with lowest <var>z</var> order.
     * The default value is {@link Double#POSITIVE_INFINITY}.
     */
    double getZOrderHint();

    /**
     * Sets the <var>z</var> order hint value for this graphic. Graphics with highest
     * <var>z</var> order will be painted on top of graphics with lowest <var>z</var> order.
     * <p>
     * This method fires a {@value org.geotools.display.canvas.DisplayObject#Z_ORDER_HINT_PROPERTY}
     * property change event.
     */
    void setZOrderHint(final double zOrderHint);
    
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
    Shape getDisplayBounds();

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
    void refresh();

    /**
     * Advises that some region need to be repainted. This graphic will not be repainted
     * immediately, but at some later time depending on the widget implementation (e.g.
     * <cite>Swing</cite>). This {@code refrech(...)} method can be invoked from any thread;
     * it doesn't need to be the <cite>Swing</cite> thread.
     *
     * @param bounds The dirty region to refreshed, in the "real world" {@linkplain #getObjectiveCRS
     *        objective coordinate reference system}. A {@code null} value refresh everything.
     */
    void refresh(final Rectangle2D bounds);

}
