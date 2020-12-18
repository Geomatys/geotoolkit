/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2013, Geomatys
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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Date;
import java.util.Optional;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Informations relative to a rendering in progress. A {@code RenderingContext} instance is
 * created by {@link AWTDirectRenderer2D#paint} at rendering time, which iterates over all graphic
 * objects and invokes {@link GraphicPrimitive2D#paint} for each of them. The rendering context
 * is disposed once the rendering is completed. {@code RenderingContext} instances contain the
 * following informations:
 * <p>
 * <ul>
 *   <li>The {@link Graphics2D} handler to use for rendering.</li>
 *   <li>The coordinate reference systems in use and the transformations between them.</li>
 *   <li>The area rendered up to date. This information shall be updated by each
 *       {@link GraphicPrimitive2D} while they are painting.</li>
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
 * @module
 * @since 2.3
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @author Johann Sorel (Geomatys)
 *
 */
public interface RenderingContext {

    /**
     * Search internal hints for a value associated to provided key.
     * @param key The key to get a value for. Must not be null
     * @return Value found for given key, if any.
     */
    Optional<?> getHint(final RenderingHints.Key key);

    /**
     * Returns the rendering objective CRS. this may not be at all time
     * the same crs as the canvas, because the canvas might be update while rendering.
     * @return Objective CRS
     */
    CoordinateReferenceSystem getObjectiveCRS();

    /**
     * Returns only the 2D component of the objective CRS.
     * @return Objective CRS 2D
     */
    CoordinateReferenceSystem getObjectiveCRS2D();

    /**
     * Returns the rendering display CRS. this may not be at all time
     * the same CRS as the canvas, because the canvas might be update while rendering.
     * @return Display CRS
     */
    CoordinateReferenceSystem getDisplayCRS();

    /**
     * Sets the coordinate reference system in use for rendering in {@link Graphics2D}. Invoking
     * this method do not alter the current state of any canvas or GO-2 graphic objects. It is
     * only a convenient way to {@linkplain Graphics2D#setTransform set the affine transform} in
     * the current <cite>Java2D</cite> {@link Graphics2D} handle, for example in order to alternate
     * rendering mode between geographic features and labels. The specified coordinate reference
     * system (the {@code crs} argument) is usually (but not limited to) one of
     * {@link #objectiveCRS} or {@link #displayCRS} values.
     *
     * @param  crs The CRS for the {@link #getGraphics() Java2D graphics handle}.
     * @throws TransformException if this method failed to find an affine transform from the
     *         specified CRS to the device CRS.
     *
     * @see #getGraphics
     * @see #getAffineTransform
     * @see Graphics2D#setTransform
     */
    void setGraphicsCRS(CoordinateReferenceSystem crs) throws TransformException;

    /**
     * Get the canvas monitor.
     *
     * @return CanvasMonitor, can not be null.
     */
    CanvasMonitor getMonitor();

    /**
     * Extract the date range from the canvas objective envelope (first temporal CRS).
     * This array is never null but it's values can be null.
     *
     * @return array of two dates for the temporal range, never null.
     */
    Date[] getTemporalRange();

    /**
     * Extract the elevation range from the canvas objective envelope (first vertical CRS).
     * This array is never null but it's values can be null.
     *
     * @return array of two dates for the vertical range, never null.
     */
    Double[] getElevationRange();

}
