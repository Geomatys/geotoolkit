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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.geotoolkit.display.canvas.CanvasController2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.style.labeling.LabelRenderer;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;

import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Rendering Context for Java2D.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface RenderingContext2D extends RenderingContext{

    /**
     * {@inheritDoc }
     */
    @Override
    J2DCanvas getCanvas();

    /**
     * Shortcut method which equals a call to :
     * context.setGraphicsCRS(context.getDisplayCRS);
     * whitout the need of a try catch.
     *
     * Optimization for a pretty common case.
     */
    void switchToDisplayCRS();

    /**
     * Shortcut method which equals a call to :
     * context.setGraphicsCRS(context.getObjectiveCRS);
     * whitout the need of a try catch.
     *
     * Optimization for a pretty common case.
     */
    void switchToObjectiveCRS();

    /**
     * Returns the graphics where painting occurs. The initial coordinate reference system is
     * {@link #getDisplayCRS()}, which maps the <cite>Java2D</cite> {@linkplain Graphics2D user space}.
     * For drawing shapes directly in terms of "real world" coordinates, users should invoke
     * <code>{@linkplain #setGraphicsCRS setGraphicsCRS}({@linkplain #getObjectiveCRS()})</code> or
     * {@linkplain #switchToDisplayCRS() } and {@linkplain #switchToObjectiveCRS() }.
     */
    Graphics2D getGraphics();

    /**
     * Use this methods rather send getGraphics().getRenderingHints.
     * This method cases the result for better performances.
     * @return rendering hints of the graphics 2D.
     */
    RenderingHints getRenderingHints();

    /**
     * Like the Graphics class, this create method makes a clone of the current
     * Rendering context. this may be use in multithread to avoid several object to work
     * on the same context.
     */
    RenderingContext2D create(Graphics2D g2d);
    
    /**
     * Get or Create a label renderer for this rendering context.
     * @param create : if true will create a label renderer if there is none.
     */
    LabelRenderer getLabelRenderer(boolean create);

    FontMetrics getFontMetrics(Font f);
    
    // Informations related to scale datas -------------------------------------
    /**
     * Find the coefficient between the given Unit and the Objective CRS.
     */
    float getUnitCoefficient(Unit<Length> unit);

    /**
     * Returns the current painting resolution. this may be used in the parameters
     * given to gridCoverageReaders to extract the best resolution grid coverage.
     * This resolution is between Objective and Display CRS.
     *
     * @return double[] of 2 dimensions
     */
    double[] getResolution();

    /**
     * Returns the current painting resolution. this may be used in the parameters
     * given to gridCoverageReaders to extract the best resolution grid coverage.
     * This resolution is between the given CRS and Display CRS.
     *
     * @return double[] of 2 dimensions
     */
    double[] getResolution(CoordinateReferenceSystem crs);

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
    double getScale();

    /**
     * Returns the geographic scale, like we can see in scalebar legends '1 : 200 000'
     * This is mainly used in style rules to check the minimum and maximum scales.
     */
    double getGeographicScale();

    /**
     * Geographic scale calculated using OGC Symbology Encoding specification.
     * This is not the scale Objective to Display.
     * This is not an accurate geographic scale.
     * This is a fake average scale unproper for correct rendering.
     * It is used only to filter SE rules.
     */
    double getSEScale();

    /**
     * @return affine transform from objective CRS to display CRS.
     */
    AffineTransform2D getObjectiveToDisplay();

    /**
     * @return affine transform from display CRS to objective CRS.
     */
    AffineTransform2D getDisplayToObjective();

    // Informations about the currently painted area ---------------------------

    Shape getPaintingDisplayShape();

    Rectangle getPaintingDisplayBounds();

    Shape getPaintingObjectiveShape();

    BoundingBox getPaintingObjectiveBounds2D();

    Envelope getPaintingObjectiveBounds();


    
    // Informations about the complete canvas area -----------------------------

    Shape getCanvasDisplayShape();
    
    Rectangle getCanvasDisplayBounds();
    
    Shape getCanvasObjectiveShape();

    BoundingBox getCanvasObjectiveBounds2D();
    
    Envelope getCanvasObjectiveBounds();
    
}
