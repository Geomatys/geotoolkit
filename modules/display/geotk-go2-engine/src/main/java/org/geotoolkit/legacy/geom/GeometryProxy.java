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
package org.geotoolkit.legacy.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

import org.geotoolkit.math.Statistics;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;


/**
 * A geometry wrapping an existing {@linkplain Geometry geometry} object with a different
 * {@linkplain Style style}. Every calls except <code>get/setStyle</code> are forwarded
 * to the wrapped geometry. Consequently, <strong>changes in this geometry will impact
 * on the wrapped geometry</strong>, and conversely.
 *
 * @version $Id: GeometryProxy.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 * @module pending
 */
public final class GeometryProxy extends Geometry {
    /**
     * Serial number for compatibility with previous versions.
     */
    private static final long serialVersionUID = 7024656664286763717L;

    /**
     * The wrapped geometry object.
     */
    private Geometry geometry;

    /**
     * Construct a geographic shape wrapping the given geometry. The new geometry will
     * initially shares the same style than the given geometry.
     *
     * @param geometry The geometry to wrap.
     */
    public GeometryProxy(Geometry geometry) {
        super(geometry);
        while (geometry instanceof GeometryProxy) {
            geometry = ((GeometryProxy) geometry).geometry;
        }
        this.geometry = geometry;
    }

    /**
     * Returns the localized name for this geometry, or <code>null</code> if none.
     * This method forwards the call to the wrapped geometry.
     *
     * @param  locale The desired locale. If no name is available
     *         for this locale, a default locale will be used.
     * @return The geometry's name, localized if possible.
     */
    @Override
    public String getName(final Locale locale) {
        return geometry.getName(locale);
    }

    /**
     * Returns the geometry's coordinate system, or <code>null</code> if unknow.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return geometry.getCoordinateReferenceSystem();
    }

    /**
     * Set the geometry's coordinate system.
     * It will changes the coordinate system of the wrapped geometry.
     */
    @Override
    public void setCoordinateReferenceSystem(final CoordinateReferenceSystem coordinateSystem)
            throws TransformException
    {
        geometry.setCoordinateReferenceSystem(coordinateSystem);
    }

    /**
     * Returns the user object attached to this geometry, which is the same
     * than the user object for the wrapped geometry.
     */
    @Override
    public Object getUserObject() {
        return geometry.getUserObject();
    }

    /**
     * Set the user object for for the wrapped geometry. The user object for this proxy will
     * always stay null (for avoiding memory leak), but the user should never realize that.
     */
    @Override
    public void setUserObject(final Object userObject) {
        geometry.setUserObject(userObject);
    }

    /**
     * Determines whetever this geometry is empty.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    public boolean isEmpty() {
        return geometry.isEmpty();
    }

    /**
     * Add to the specified collection all {@link Polyline} objects making the wrapped geometry.
     */
    @Override
    void getPolylines(final Collection polylines) {
        geometry.getPolylines(polylines);
    }

    /**
     * Return the number of points in this geometry.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    public int getPointCount() {
        return geometry.getPointCount();
    }

    /**
     * Returns an estimation of memory usage in bytes.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    long getMemoryUsage() {
        return geometry.getMemoryUsage() + 4;
    }

    /**
     * Returns the smallest bounding box containing {@link #getBounds2D}.
     * This method forwards the call to the wrapped geometry.
     *
     * @deprecated This method is required by the {@link Shape} interface,
     *             but it doesn't provide enough precision for most cases.
     *             Use {@link #getBounds2D()} instead.
     */
    @Override
    public Rectangle getBounds() {
        return geometry.getBounds();
    }

    /**
     * Returns the bounding box of this geometry.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    public Rectangle2D getBounds2D() {
        return geometry.getBounds2D();
    }

    /**
     * Tests if the specified coordinates are inside the boundary of this geometry.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    public boolean contains(final double x, final double y) {
        return geometry.contains(x, y);
    }

    /**
     * Tests if a specified {@link Point2D} is inside the boundary of this geometry.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    public boolean contains(final Point2D point) {
        return geometry.contains(point);
    }

    /**
     * Test if the interior of this geometry entirely contains the given rectangle.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    public boolean contains(double x, double y, double width, double height) {
        return geometry.contains(x, y, width, height);
    }

    /**
     * Tests if the interior of this geometry entirely contains the given rectangle.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    public boolean contains(final Rectangle2D rectangle) {
        return geometry.contains(rectangle);
    }

    /**
     * Test if the interior of this geometry entirely contains the given shape.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    public boolean contains(final Shape shape) {
        return geometry.contains(shape);
    }

    /**
     * Tests if the interior of the geometry intersects the interior of a specified rectangle.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    public boolean intersects(double x, double y, double width, double height) {
        return geometry.intersects(x, y, width, height);
    }

    /**
     * Tests if the interior of the geometry intersects the interior of a specified rectangle.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    public boolean intersects(final Rectangle2D rectangle) {
        return geometry.intersects(rectangle);
    }

    /**
     * Tests if the interior of the geometry intersects the interior of a specified shape.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    public boolean intersects(final Shape shape) {
        return geometry.intersects(shape);
    }

    /**
     * Returns an geometry approximately equal to this geometry clipped to the specified
     * bounds. This method clip the wrapped geometry, and wrap the result in a new
     * <code>GeometryProxy</code> instance with the same {@linkplain Style style} than
     * the current one.
     */
    @Override
    public Geometry clip(final Clipper clipper) {
        Geometry clipped = geometry.clip(clipper);
        if (clipped == geometry) {
            freeze();
            return this;
        }
        if (clipped != null) {
            clipped = new GeometryProxy(clipped);
        }
        return clipped;
    }

    /**
     * Compress the wrapped geometry.
     *
     * @param  level The compression level (or algorithm) to use.
     * @return A <em>estimation</em> of the compression rate.
     * @throws TransformException If an error has come up during a cartographic projection.
     */
    @Override
    public float compress(final CompressionLevel level) throws TransformException, FactoryException {
        return geometry.compress(level);
    }

    /**
     * Returns the geometry's resolution.
     * This method forwards the call to the wrapped geometry.
     *
     * @return Statistics about the resolution, or <code>null</code>
     *         if this geometry doesn't contains any point.
     */
    @Override
    public Statistics getResolution() {
        return geometry.getResolution();
    }

    /**
     * Set the geometry's resolution. It will changes the resolution of the wrapped geometry.
     *
     * @param  resolution Desired resolution, in the same linear units than {@link #getResolution}.
     * @throws TransformException If some coordinate transformations were needed and failed.
     *         There is no guaranteed on geometry's state in case of failure.
     */
    @Override
    public void setResolution(final double resolution) throws TransformException, FactoryException {
        geometry.setResolution(resolution);
    }

    /**
     * Returns the rendering resolution.
     * This method forwards the call to the wrapped geometry.
     *
     * @return The rendering resolution in units of this geometry's {@linkplain #getCoordinateSystem
     *         coordinate system} (linear or angular units), or 0 if the finest available
     *         resolution should be used.
     */
    @Override
    public float getRenderingResolution() {
        return geometry.getRenderingResolution();
    }

    /**
     * Hints this geometry that the specified resolution is sufficient for rendering.
     * It will changes the rendering resolution of the wrapped geometry.
     *
     * @param resolution The resolution to use at rendering time, in units of this geometry's
     *        {@linkplain #getCoordinateSystem coordinate system} (linear or angular units).
     */
    @Override
    public void setRenderingResolution(float resolution) {
        geometry.setRenderingResolution(resolution);
    }

    /**
     * Returns an iterator object that iterates along the shape boundary and provides access to
     * the geometry of the shape outline. This method forwards the call to the wrapped geometry.
     */
    @Override
    public PathIterator getPathIterator(final AffineTransform transform) {
        return geometry.getPathIterator(transform);
    }

    /**
     * Returns a flattened path iterator for this geometry.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    public PathIterator getPathIterator(final AffineTransform transform, final double flatness) {
        return geometry.getPathIterator(transform, flatness);
    }

    /**
     * Returns <code>true</code> if {@link #getPathIterator} returns a flattened iterator.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    boolean isFlattenedShape() {
        return geometry.isFlattenedShape();
    }

    /**
     * Deletes all the information that was kept in an internal cache.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    void clearCache() {
        geometry.clearCache();
    }

    /**
     * Freeze the wrapped geometry.
     */
    @Override
    final void freeze() {
        geometry.freeze();
    }

    /**
     * Returns <code>true</code> if we are not allowed to change this geometry.
     * This method forwards the call to the wrapped geometry.
     */
    @Override
    final boolean isFrozen() {
        return geometry.isFrozen();
    }

    /**
     * Return a clone of this geometry. The returned geometry will have a deep copy semantic.
     * This method is <code>final</code> for implementation reason.
     */
    @Override
    public final Object clone() {
        /*
         * This <code>clone()</code> method needs to be final because user's implementation would be
         * ignored, since we override <code>clone(Map)</code> in a way which do not call this method
         * anymore. It have to call <code>super.clone()</code> instead.
         */
        return clone(new IdentityHashMap());
    }

    /**
     * Clone this geometry, trying to avoid cloning twice the wrapped geometry.
     */
    @Override
    Object clone(final Map alreadyCloned) {
        final GeometryProxy copy = (GeometryProxy) super.clone();
        copy.geometry = (Geometry) geometry.resolveClone(alreadyCloned);
        return copy;
    }

    /**
     * Compares the specified object with this geometry for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object==this) {
            // Slight optimization
            return true;
        }
        if (super.equals(object)) {
            return geometry.equals(((GeometryProxy)object).geometry);
        }
        return false;
    }

    /**
     * Returns a hash value for this geometry.
     */
    @Override
    public int hashCode() {
        return geometry.hashCode() ^ (int)serialVersionUID;
    }

}
