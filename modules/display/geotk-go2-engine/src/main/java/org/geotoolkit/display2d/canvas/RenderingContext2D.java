/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.style.renderer.LabelRenderer;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;


/**
 * Rendering Context for Java2D.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface RenderingContext2D extends RenderingContext{

    /**
     * {@inheritDoc }
     */
    @Override
    ReferencedCanvas2D getCanvas();

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
     * {@link #displayCRS}, which maps the <cite>Java2D</cite> {@linkplain Graphics2D user space}.
     * For drawing shapes directly in terms of "real world" coordinates, users should invoke
     * <code>{@linkplain #setGraphicsCRS setGraphicsCRS}({@linkplain #objectiveCRS})</code>.
     */
    Graphics2D getGraphics();
    
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

    // Informations related to scale datas -------------------------------------
    /**
     * Find the coefficient between the given Unit and the Objective CRS.
     */
    float getUnitCoefficient(Unit<Length> unit);

    /**
     * Returns the current painting resolution. this may be used in the parameters
     * given to gridCoverageReaders to extract the best resolution grid coverage.
     *
     * @return double[] of 2 dimensions
     */
    double[] getResolution();

    /**
     * Returns the scale factor, or {@link Double#NaN NaN} if the scale is unknow. The scale factor
     * is usually smaller than 1. For example for a 1:1000 scale, the scale factor will be 0.001.
     * This scale factor takes in account the physical size of the rendering device (e.g. the
     * screen size) if such information is available. Note that this scale can't be more accurate
     * than the {@linkplain java.awt.GraphicsConfiguration#getNormalizingTransform() information
     * supplied by the underlying system}.
     *
     * @return The rendering scale factor as a number between 0 and 1, or {@link Double#NaN}.
     * @see BufferedCanvas2D#getScale
     */
    double getScale();

    /**
     * @return affine transform from objective CRS to Display CRS.
     */
    AffineTransform getObjectiveToDisplay();

    // Informations about the currently painted area ---------------------------

    Shape getPaintingDisplayShape();

    Rectangle getPaintingDisplayBounds();

    Shape getPaintingObjectiveShape();

    BoundingBox getPaintingObjectiveBounds();


    
    // Informations about the complete canvas area -----------------------------

    Shape getCanvasDisplayShape();
    
    Rectangle getCanvasDisplayBounds();
    
    Shape getCanvasObjectiveShape();
    
    BoundingBox getCanvasObjectiveBounds();
    
}
