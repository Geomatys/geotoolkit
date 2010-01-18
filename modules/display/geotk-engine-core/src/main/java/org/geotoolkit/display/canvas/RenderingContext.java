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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Date;

import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.factory.Hints;

import org.opengis.display.canvas.Canvas;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.CoordinateOperationFactory;

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
 * @module pending
 * @since 2.3
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @author Johann Sorel (Geomatys)
 *
 */
public interface RenderingContext {

    /**
     * Returns the canvas who created this renderingContext.
     *
     * @return current rendering canvas
     */
    Canvas getCanvas();

    /**
     * Returns the rendering objective CRS. this may not be at all time
     * the same crs as the canvas, because the canvas might be update while rendering.
     * @return Objective CRS
     */
    CoordinateReferenceSystem getObjectiveCRS();

    /**
     * Returns only the 2D composant of the objective crs.
     * @return Objective CRS 2D
     */
    CoordinateReferenceSystem getObjectiveCRS2D();

    /**
     * Returns the rendering display CRS. this may not be at all time
     * the same crs as the canvas, because the canvas might be update while rendering.
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
     * @see Hints#COORDINATE_OPERATION_FACTORY
     */
    AffineTransform getAffineTransform(final CoordinateReferenceSystem sourceCRS,
                                              final CoordinateReferenceSystem targetCRS)
                                              throws FactoryException;

    /**
     * Returns a transform between two coordinate systems. If a {@link
     * Hints#COORDINATE_OPERATION_FACTORY} has been provided to the {@link BufferedCanvas2D},
     * then the specified {@linkplain CoordinateOperationFactory coordinate operation factory}
     * will be used. The arguments are usually (but not necessarily) one of the following pairs:
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
     * @see Hints#COORDINATE_OPERATION_FACTORY
     */
    MathTransform getMathTransform(final CoordinateReferenceSystem sourceCRS,
                                          final CoordinateReferenceSystem targetCRS)
                                          throws FactoryException;
    
    /**
     * Get the canvas monitor.
     * 
     * @return CanvasMonitor, can not be null.
     */
    CanvasMonitor getMonitor();

    Date[] getTemporalRange();

    Double[] getElevationRange();

}
