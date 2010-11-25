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

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.geotoolkit.referencing.operation.transform.AffineTransform2D;

import org.opengis.display.canvas.CanvasController;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;

/**
 * Default canvas 2D controller methods. 
 * This interface sum up the common 2D controls needed to correctly
 * navigate in the canvas.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface CanvasController2D extends CanvasController{

    public static final String TEMPORAL_PROPERTY = "temporal";

    public static final String ELEVATION_PROPERTY = "elevation";

    /**
     * Reinitializes the affine transform {@link #transform(java.awt.geom.AffineTransform)}
     * in order to cancel any zoom, rotation or translation.
     * The default implementation initializes the affine transform in
     * order to make the <var>y</var> axis point upwards and make the whole of the region covered
     * by the {@link #setVisibleArea(org.opengis.geometry.Envelope) } logical coordinates appear in the panel.
     * <p>
     */
    void reset() throws NoninvertibleTransformException;
    
    void repaint();

    Point2D getDisplayCenter();

    /**
     * Returns the center of the canvas in objective CRS.
     *
     * @return DirectPosition : center of the canvas
     *
     * @throws IllegalStateException if the affine transform used for conversion is in
     *                               illegal state, non invertible.
     */
    DirectPosition getCenter() throws NoninvertibleTransformException;

    Envelope getVisibleArea() throws TransformException;

    void setAutoRepaint(boolean auto);
    
    boolean isAutoRepaint();
    
    /**
     * Set the proportions support between X and Y axis.
     * if prop = Double.NaN then no correction will be applied
     * if prop = 1 then one unit in X will be equal to one unit in Y
     * else value will mean that prop*Y will be used
     */
    void setAxisProportions(double prop);
    
    /**
     * 
     * @return the X/Y proportion
     */
    double getAxisProportions();
    
    /**
     *
     * @return a snapshot objective To display transform.
     */
    AffineTransform2D getTransform();
    
    // Relative position operations --------------------------------------------
    
    void rotate(double r) throws NoninvertibleTransformException;    

    void rotate(double r, Point2D center) throws NoninvertibleTransformException;    
    
    /**
     * Change scale by a precise amount.
     *
     * @param s : multiplication scale factor
     */
    void scale(double s) throws NoninvertibleTransformException;

    /**
     *
     * @param s
     * @param center in Display CRS
     * @throws NoninvertibleTransformException
     */
    void scale(double s, Point2D center) throws NoninvertibleTransformException;
    
    /**
     * Translate of x and y amount in display units.
     *
     * @param x : translation against the X axy
     * @param y : translation against the Y axy
     */
    void translateDisplay(double x, double y) throws NoninvertibleTransformException;

    void translateObjective(double x, double y) throws NoninvertibleTransformException;
    
    /**
     * Changes the {@linkplain AffineTransform} by applying a concatenate affine transform.
     * The {@code change} transform
     * must express a change in logical units, for example, a translation in metres. 
     *
     * @param  change The affine transform change, as an affine transform in logical coordinates. If
     *         {@code change} is the identity transform, then this method does nothing and
     *         listeners are not notified.
     */
    void transform(AffineTransform change);

    /**
     * Changes the {@linkplain #AffineTransform} by applying an affine transform. The {@code change} transform
     * must express a change in pixel units, for example, a scrolling of 6 pixels toward right.
     *
     * @param  change The zoom change, as an affine transform in pixel coordinates. If
     *         {@code change} is the identity transform, then this method does nothing
     *         and listeners are not notified.
     *
     * @since 2.1
     */
    void transformPixels(final AffineTransform change);
    
    // Absolute position operations --------------------------------------------
    
    void setRotation(double r) throws NoninvertibleTransformException;

    double getRotation();

    void setScale(double newScale) throws NoninvertibleTransformException;

    /**
     * Returns the current scale factor. A value of 1/100 means that 100 metres
     * are displayed as 1 pixel (provided that the logical coordinates of {@code #getArea} are
     * expressed in metres). Scale factors for X and Y axes can be computed separately using the
     * following equations:
     *
     * <table cellspacing=3><tr>
     * <td width=50%><IMG src="doc-files/scaleX.png"></td>
     * <td width=50%><IMG src="doc-files/scaleY.png"></td>
     * </tr></table>
     *
     */
    double getScale();
    
    void setDisplayVisibleArea(Rectangle2D dipsEnv);

    void setVisibleArea(Envelope env) throws NoninvertibleTransformException,TransformException;

    /**
     * Defines the limits of the visible part, in logical coordinates.  This method will modify the
     * zoom and the translation in order to display the specified region. If {@link #zoom} contains
     * a rotation, this rotation will not be modified.
     *
     * @param  logicalBounds Logical coordinates of the region to be displayed.
     * @throws IllegalArgumentException if {@code source} is empty.
     */
    void setVisibleArea(final Rectangle2D logicalBounds) 
            throws IllegalArgumentException, NoninvertibleTransformException;

    /**
     * Set the scale, in a ground unit manner, relation between map display size
     * and real ground unit meters;
     * @param scale
     */
    void setGeographicScale(double scale) throws TransformException;

    /**
     * Returns the geographic scale, in a ground unit manner, relation between map display size
     * and real ground unit meters.
     *
     * @throws IllegalStateException If the affine transform used for conversion is in
     *                               illegal state.
     */
    double getGeographicScale() throws TransformException;


    //TODO need to handle this more correctly with a 4D BBox

    // Temporal dimension ------------------------------------------------------

    void setTemporalRange(Date startDate, Date endDate);

    Date[] getTemporalRange();

    // Elevation dimension -----------------------------------------------------

    void setElevationRange(Double min, Double max, Unit<Length> unit);

    Double[] getElevationRange();

    Unit<Length> getElevationUnit();
    
}
