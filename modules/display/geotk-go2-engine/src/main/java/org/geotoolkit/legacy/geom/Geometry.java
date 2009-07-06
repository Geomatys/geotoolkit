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

import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.text.Format;
import java.text.NumberFormat;
import java.text.FieldPosition;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.Map;
import java.util.Locale;
import java.util.logging.Level;
import java.io.Serializable;

import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.util.Cloneable;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;

import org.geotoolkit.measure.Latitude;
import org.geotoolkit.measure.Longitude;
import org.geotoolkit.measure.AngleFormat;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.math.Statistics;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.logging.Logging;
import org.opengis.referencing.operation.CoordinateOperation;


/**
 * Base class for {@linkplain Shape geometric shape} to be rendered in a given
 * {@link CoordinateSystem coordinate system}.  Those classes are not designed
 * for spatial analysis or topology: they are <strong>not</strong> a replacement
 * for <A HREF="http://www.vividsolutions.com/JTS/jts_frame.htm">JTS</A>, neither
 * an implementation of ISO-19107. They are rather a wrapper around arbitrary source
 * of (<var>x</var>,<var>y</var>) coordinates to be rendered. With the rendering goal
 * in mind, this class implements the {@link Shape} interface for interoperability with
 * <A HREF="http://java.sun.com/products/java-media/2D/">Java2D</A>. But it provides also
 * some more capabilities. For example, <code>contains</code> and <code>intersects</code>
 * methods accepts arbitrary shapes instead of rectangle only. <code>Geometry</code> objects
 * can have arbitrary two-dimensional coordinate system, which can be
 * {@linkplain #setCoordinateSystem changed at any time} (i.e. the geometry can be reprojected).
 * {@linkplain #setRenderingResolution Decimation} can be applied at rendering time. Futhermore,
 * <code>Geometry</code>s can {@linkplain #compress compress} and share their internal data in
 * order to reduce memory footprint.
 *
 * @version $Id: Geometry.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 */
public abstract class Geometry implements Shape, Cloneable, Serializable {
    /**
     * Serial number for compatibility with previous versions.
     */
    private static final long serialVersionUID = -1274472236517648668L;

    /**
     * The logger for the renderer module.
     */
    protected static final Logger LOGGER = Logging.getLogger(Geometry.class);

    /**
     * The default coordinate system for all geometries. This is the coordinate
     * system used if no CS were explicitly specified at construction time. The
     * default implementation uses a two-dimensional cartesian coordinate system
     * with {@linkplain AxisInfo#X x},{@linkplain AxisInfo#Y y} axis in
     * {@linkplain Unit#METRE metres}. This coordinate system is treated specially
     * by the default {@linkplain org.geotoolkit.ct.CoordinateTransformationFactory
     * coordinate transformation factory} with loose transformation rules: if no
     * transformation path were found, then the transformation from this CS to any
     * CS with a compatible number of dimensions is assumed to be the identity transform.
     *
     * @see LocalCoordinateSystem#PROMISCUOUS
     * @see LocalCoordinateSystem#CARTESIAN
     * @see GeographicCoordinateSystem#WGS84
     */
    public static final CoordinateReferenceSystem DEFAULT_COORDINATE_SYSTEM = DefaultEngineeringCRS.GENERIC_2D;

    /**
     * A user object. This is often the {@linkplain Feature feature} where this geometry
     * come from, or an ID for this feature as a {@linkplain String character string}.
     * This information is stored here in order to allow faster retrieval of the
     * {@linkplain Feature feature} under the mouse cursor.
     */
    private Object userObject;
    
    /**
     * A generic identifier for this geometry.
     */
    private String ID;

    /**
     * Construct an empty geographic shape.
     */
    public Geometry() {
    }

    /**
     * Construct a geographic shape with the same data than the specified geometry.
     *
     * @param geometry The geometry to copy data from.
     */
    protected Geometry(final Geometry geometry) {
        this.userObject = geometry.userObject;
    }

    /**
     * Returns the localized name for this geometry, or <code>null</code> if none.
     * The default implementation returns always <code>null</code>.
     *
     * @param  locale The desired locale. If no name is available
     *         for this locale, a default locale will be used.
     * @return The geometry's name, localized if possible.
     */
    public String getName(final Locale locale) {
        return null;
    }
    
    /**
     * Returns the geometry ID. The ID is string that identifies the current
     * geometry and its application specific, that is, the renderer won't use it
     * in any special way.
     *
     * @return The geometry ID, or <code>null</code> if none.
     */
    public String getID() {
        return ID;
    }

    /**
     * Sets the geometry ID.
     *
     * @param The geometry ID.
     */
    public void setID(final String ID) {
        this.ID = ID;
    }

    /**
     * Returns the user object attached to this geometry. This is often the
     * {@linkplain Feature feature} where this geometry come from, or an ID
     * for this feature as a {@linkplain String character string}.
     */
    public Object getUserObject() {
    	return userObject;
    }

    /**
     * Set the user object for this geometry. This is often the {@linkplain Feature feature}
     * where this geometry come from, or an ID for this geometry as a {@linkplain String
     * character string}. The renderer will not use this information in any way. It is stored
     * here mostly for faster retrieval of the {@linkplain Feature feature} under the mouse cursor.
     */
    public void setUserObject(final Object userObject) {
    	this.userObject = userObject;
    }

    /**
     * Returns the geometry's coordinate system, or <code>null</code> if unknow.
     */
    public abstract CoordinateReferenceSystem getCoordinateReferenceSystem();

    /**
     * Set the geometry's coordinate system. Calling this method is equivalents
     * to reproject all geometry's points from the old coordinate system to the
     * new one.
     *
     * @param  coordinateSystem The new coordinate system. A <code>null</code> value reset
     *         the default coordinate system (usually the one that best fits internal data).
     * @throws TransformException If a transformation failed. In case of failure,
     *         the state of this object will stay unchanged, as if this method has
     *         never been invoked.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    public abstract void setCoordinateReferenceSystem(final CoordinateReferenceSystem coordinateSystem)
            throws TransformException, UnmodifiableGeometryException;

    /**
     * Check if two coordinate system are equivalents, ignoring attributes like the CS name.
     */
    static boolean equivalents(final CoordinateReferenceSystem cs1, final CoordinateReferenceSystem cs2) {
        if (cs1 == cs2) return true;
        return cs1!=null && cs1.equals(cs2);
    }

    /**
     * Construct a transform from two coordinate systems.
     *
     * @param  sourceCS The source coordinate system.
     * @param  targetCS The target coordinate system.
     * @return A transformation from <code>sourceCS</code> to <code>targetCS</code>.
     */
    static CoordinateOperation getCoordinateTransformation(final CoordinateReferenceSystem sourceCS,
                                                                final CoordinateReferenceSystem targetCS) 
                                                                throws OperationNotFoundException, FactoryException {
        return CRS.getCoordinateOperationFactory(true).createOperation(sourceCS, targetCS);
    }

    /**
     * Retourne une transformation identitée pour le système de coordonnées
     * spécifié, ou <code>null</code> si <code>coordinateSystem</code> est nul.
     *
     * @param  coordinateSystem The coordinate system, or <code>null</code>.
     * @return An identity transformation from and to <code>coordinateSystem</code>,
     *         or <code>null</code>.
     */
    static CoordinateOperation getIdentityTransform(final CoordinateReferenceSystem coordinateSystem) {
        if (coordinateSystem != null) 
            try {
            return getCoordinateTransformation(coordinateSystem, coordinateSystem);
        } catch (OperationNotFoundException exception) {
            // Should not happen; we are just asking for an identity transform!
            Logger.getLogger(Geometry.class.toString()).log(Level.WARNING,exception.getMessage());
        }catch (FactoryException exception) {
            // Should not happen; we are just asking for an identity transform!
            Logger.getLogger(Geometry.class.toString()).log(Level.WARNING,exception.getMessage());
        }
        return null;
    }

    /**
     * Determines whetever this geometry is empty.
     */
    public boolean isEmpty() {
        // To be overriden by subclasses with a more efficient implementation.
        return getPointCount() == 0;
    }

    /**
     * Add to the specified collection all non-empty {@link Polyline} objects making this
     * geometry. This method is used by {@link GeometryCollection#getPathIterator} and
     * {@link PolygonAssembler} only.
     */
    void getPolylines(final Collection polylines) {
    }

    /**
     * Return the number of points in this geometry.
     */
    public abstract int getPointCount();

    /**
     * Returns an estimation of memory usage in bytes. This method is for information
     * purposes only. The memory really used by two geometries may be lower than the sum
     * of their  <code>getMemoryUsage()</code>  return values,  since geometries try to
     * share their data when possible. Furthermore, this method does not take into account
     * the extra bytes generated by Java Virtual Machine for each object.
     *
     * @return An <em>estimation</em> of memory usage in bytes.
     */
    long getMemoryUsage() {
        // To be overriden by subclasses.
        return getPointCount()*8;
    }

    /**
     * Returns the smallest bounding box containing {@link #getBounds2D}.
     *
     * @deprecated This method is required by the {@link Shape} interface,
     *             but it doesn't provide enough precision for most cases.
     *             Use {@link #getBounds2D()} instead.
     */
    @Override
    public Rectangle getBounds() {
        final Rectangle rect = new Rectangle();
        rect.setRect(getBounds2D()); // Perform the appropriate rounding.
        return rect;
    }

    /**
     * Returns the bounding box of this geometry. The rectangle's coordinates will be expressed
     * in this geometry's coordinate system (as returned by {@link #getCoordinateSystem}).
     *
     * @return The bounding box of this geometry.
     */
    @Override
    public abstract Rectangle2D getBounds2D();

    /**
     * Tests if the specified coordinates are inside the boundary of this geometry.
     *
     * @param  x the specified <var>x</var> coordinates in this geometry coordinate system.
     * @param  y the specified <var>y</var> coordinates in this geometry coordinate system.
     * @return <code>true</code> if the specified coordinates are inside 
     *         the geometry boundary; <code>false</code> otherwise.
     */
    @Override
    public boolean contains(double x, double y) {
        // To be overriden by subclasses with a more efficient implementation.
        return contains(new Point2D.Double(x,y));
    }

    /**
     * Tests if a specified {@link Point2D} is inside the boundary of this geometry.
     *
     * @param  point the specified point in this geometry coordinate system.
     * @return <code>true</code> if the specified point is inside 
     *         the geometry boundary; <code>false</code> otherwise.
     */
    @Override
    public abstract boolean contains(Point2D point);

    /**
     * Test if the interior of this geometry entirely contains the given rectangle.
     * The rectangle's coordinates must expressed in this geometry's coordinate
     * system (as returned by {@link #getCoordinateSystem}).
     */
    @Override
    public boolean contains(double x, double y, double width, double height) {
        return contains(new Rectangle2D.Double(x, y, width, height));
    }

    /**
     * Tests if the interior of this geometry entirely contains the given rectangle.
     * The rectangle's coordinates must expressed in this geometry's coordinate
     * system (as returned by {@link #getCoordinateSystem}).
     */
    @Override
    public boolean contains(final Rectangle2D rectangle) {
        // To be overriden by subclasses with a more efficient implementation.
        return contains((Shape)rectangle);
    }

    /**
     * Test if the interior of this geometry entirely contains the given shape.
     * The coordinate system for the specified <code>shape</code> argument
     * must be the same than this <code>Geometry</code> object, as returned
     * by {@link #getCoordinateSystem}.
     */
    public abstract boolean contains(final Shape shape);

    /**
     * Tests if the interior of the geometry intersects the interior of a specified rectangle.
     * The rectangle's coordinates must expressed in this geometry's coordinate
     * system (as returned by {@link #getCoordinateSystem}).
     */
    @Override
    public boolean intersects(double x, double y, double width, double height) {
        return intersects(new Rectangle2D.Double(x, y, width, height));
    }

    /**
     * Tests if the interior of the geometry intersects the interior of a specified rectangle.
     * The rectangle's coordinates must expressed in this geometry's coordinate
     * system (as returned by {@link #getCoordinateSystem}).
     */
    @Override
    public boolean intersects(final Rectangle2D rectangle) {
        // To be overriden by subclasses with a more efficient implementation.
        return intersects((Shape)rectangle);
    }

    /**
     * Tests if the interior of the geometry intersects the interior of a specified shape.
     * The coordinate system for the specified <code>shape</code> argument
     * must be the same than this <code>Geometry</code> object, as returned
     * by {@link #getCoordinateSystem}.
     */
    public abstract boolean intersects(final Shape shape);

    /**
     * Returns an geometry approximately equal to this geometry clipped to the specified bounds.
     * The clip is only approximate in that the resulting geometry may extend outside the clip
     * area. However, it is guaranteed that the returned geometry contains at least all the
     * interior of the clip area.
     *
     * If this method can't perform the clip, or if it believes that it isn't worth doing a clip,
     * it returns <code>this</code>. If this geometry doesn't intersect the clip area, then this
     * method returns <code>null</code>. Otherwise, a new geometry is created and returned. The new
     * geometry will try to share as much internal data as possible with <code>this</code> in order
     * to keep memory footprint low.
     *
     * @param  clipper The clipping area.
     * @return <code>null</code> if this geometry doesn't intersect the clip, <code>this</code>
     *         if no clip has been performed, or a new clipped geometry otherwise.
     */
    public Geometry clip(final Clipper clipper) {
        // Subclasses will overrides this method with a more efficient implementation.
        if (equivalents(clipper.mapCRS, getCoordinateReferenceSystem())) {
            return XRectangle2D.intersectInclusive(clipper.mapClip, getBounds2D()) ? this : null;
        }
        return this;
    }

    /**
     * Compress this geometry. The <code>level</code> argument specify the algorithm,
     * which may be desctructive (i.e. data may loose precision). Compressing geometry
     * may help to reduce memory usage, providing that there is no reference to the
     * (<var>x</var>,<var>y</var>) coordinate points outside this geometry (otherwise
     * the garbage collector will not reclaim the old data).
     *
     * @param  level The compression level (or algorithm) to use. See the {@link CompressionLevel}
     *         javadoc for an explanation of available algorithms.
     * @return A <em>estimation</em> of the compression rate. For example a value of 0.2
     *         means that the new geometry use <em>approximatively</em> 20% less memory.
     *         Warning: this value may be inacurate, for example if the old geometry was
     *         used to shares its data with an other geometry, compressing one geometry
     *         may actually increase memory usage since the two geometries will no longer
     *         share their data.
     * @throws TransformException If an error has come up during a cartographic projection.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    public abstract float compress(final CompressionLevel level)
            throws FactoryException,TransformException, UnmodifiableGeometryException;

    /**
     * Returns the geometry's resolution. The mean resolution is the mean distance between
     * every pair of consecutive points in this geometry (ignoring "extra" points used for
     * drawing a border, if there is one). This method try to express the resolution in
     * linear units (usually meters) no matter if the coordinate systems is actually a
     * {@linkplain ProjectedCoordinateSystem projected} or a
     * {@linkplain GeographicCoordinateSystem geographic} one.
     * More specifically:
     * <ul>
     *   <li>If the coordinate system is a {@linkplain GeographicCoordinateSystem geographic}
     *       one, then the resolution is expressed in units of the underlying
     *       {@linkplain Ellipsoid#getAxisUnit ellipsoid's axis length}.</li>
     *   <li>Otherwise (especially if the coordinate system is a {@linkplain
     *       ProjectedCoordinateSystem projected} one), the resolution is expressed in
     *       {@linkplain ProjectedCoordinateSystem#getUnits units of the coordinate system}.</li>
     * </ul>
     *
     * @return Statistics about the resolution, or <code>null</code> if this geometry doesn't
     *         contains any point. If non-null, the statistics object contains
     *         {@linkplain Statistics#minimum minimum},
     *         {@linkplain Statistics#maximum maximum},
     *         {@linkplain Statistics#mean mean},
     *         {@linkplain Statistics#rms root mean square} and
     *         {@linkplain Statistics#standardDeviation standard deviation}
     *         always in linear units.
     */
    public abstract Statistics getResolution();

    /**
     * Set the geometry's resolution. This method interpolates new points in such a way
     * that every point is spaced by exactly <code>resolution</code> units (usually meters)
     * from the previous one. Consequently, the {@linkplain #getResolution resolution} after
     * this call will have a {@linkplain Statistics#standardDeviation standard deviation}
     * close to 0.
     * <br><br>
     * Calling this method with a large resolution may help to reduce memory footprint if
     * a fine resolution is not needed (note that {@link #compress} provides an alternative
     * way to reduce memory footprint).
     *
     * This method is irreversible. Invoking <code>setResolution</code> with a finner
     * resolution will increase memory consumption with no real resolution improvement.
     *
     * @param  resolution Desired resolution, in the same linear units than {@link #getResolution}.
     * @throws TransformException If some coordinate transformations were needed and failed.
     *         There is no guaranteed on geometry's state in case of failure.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    public abstract void setResolution(final double resolution)
            throws FactoryException,TransformException, UnmodifiableGeometryException;

    /**
     * Returns the rendering resolution. This is the spatial resolution used by
     * {@link PathIterator} only; it has no effect on the underyling data. Note
     * that at the difference of {@link #getResolution}, the units are not always
     * linear; they may be angular if the underlying coordinate system is {@linkplain
     * GeographicCoordinateSystem geographic}. Resolution in angular units is not very
     * meaningful for computation purpose (since the length of longitude degrees vary
     * with latitude), but is what the user see if the map is unprojected.
     * The <em>rendering</em> resolution is about what the user see.
     *
     * @return The rendering resolution in units of this geometry's {@linkplain #getCoordinateSystem
     *         coordinate system} (linear or angular units), or 0 if the finest available
     *         resolution should be used.
     */
    public float getRenderingResolution() {
        return 0;
    }

    /**
     * Hints this geometry that the specified resolution is sufficient for rendering.
     * Value 0 ask for the best available resolution. If a value greater than 0 is provided,
     * then the {@link PathIterator} will skip as many points as it can while preserving a
     * distance equals or smaller than <code>resolution</code> between two consecutive points.
     * Note that this method affect the <code>PathIterator</code> behavior only; it has no impact
     * on the underlying data. This method is non-destructive; it is possible to set a finer
     * resolution after a large one.
     *
     * @param resolution The resolution to use at rendering time, in units of this geometry's
     *        {@linkplain #getCoordinateSystem coordinate system} (linear or angular units,
     *        see {@link #getRenderingResolution} for a discussion).
     */
    public void setRenderingResolution(float resolution) {
        // The default implementation ignore this call, since this method is just a hint.
        // Subclasses will do the real work.
    }

    /**
     * Returns an iterator object that iterates along the shape boundary and provides access to
     * the geometry of the shape outline. If an optional {@link AffineTransform} is specified,
     * the coordinates returned in the iteration are transformed accordingly. The iterator may
     * not iterate through all internal data. If a {@linkplain #getRenderingResolution rendering
     * resolution} has been specified, then some points may be skipped during the iteration.
     */
    @Override
    public abstract PathIterator getPathIterator(AffineTransform transform);

    /**
     * Returns a flattened path iterator for this geometry.
     */
    @Override
    public PathIterator getPathIterator(final AffineTransform transform, final double flatness) {
        PathIterator iterator = getPathIterator(transform);
        if (!isFlattenedShape()) {
            iterator = new FlatteningPathIterator(iterator, flatness);
        }
        return iterator;
    }

    /**
     * Returns <code>true</code> if {@link #getPathIterator} returns a flattened iterator.
     * In this case, there is no need to wrap it into a {@link FlatteningPathIterator}.
     */
    boolean isFlattenedShape() {
        // Will be overriden by subclasses.
        return false;
    }

    /**
     * Deletes all the information that was kept in an internal cache. This method can be
     * called when we know that this geometry will no longer be used before a long time.
     * It does not cause the loss of any information, but will make subsequent uses of this
     * geometry slower (the time the internal caches take to be reconstructed, after which the
     * geometry will resume its normal speed).
     */
    void clearCache() {
    }

    /**
     * Freeze this geometry. A frozen geometry can't change its internal data anymore.
     * Invoking methods like {@link #setCoordinateSystem setCoordinateSystem(...)} or
     * {@link #setResolution setResolution(...)} on a frozen geometry will result in a
     * {@link UnmodifiableGeometryException} to be thrown. However, the following methods
     * still allowed:
     * <br><br>
     * <ul>
     *   <li>{@link #setStyle}</li>
     *   <li>{@link #setRenderingResolution}</li>
     * </ul>
     * <br><br>
     * This is because those methods affect the way the geometry is rendered, but has no
     * negative impact on the container that own this geometry.
     *
     * A frozen geometry can never been unfrozen, because we never know if there is not
     * some {@link GeometryProxy} left which still included in the container. To modify
     * a frozen geometry, {@linkplain #clone clone} it first.
     */
    void freeze() {
        // Will be overriden by subclasses.
    }

    /**
     * Returns <code>true</code> if we are not allowed to change this geometry. Containers
     * like {@link GeometryCollection} will test this flag in order to clone a child only
     * if needed.
     */
    boolean isFrozen() {
        return true;
    }

    /**
     * Return a clone of this geometry. The returned geometry will have
     * a deep copy semantic. However, subclasses should overrides this
     * method in such a way that both shapes will share as much internal
     * arrays as possible, even if they use differents coordinate systems.
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
            // Should never happen, since we are cloneable.
            throw new AssertionError(exception);
        }
    }

    /**
     * Clone this geometry. This method should never been invoked directly excepted only once
     * in the final <code>clone()</code> method. Invoke {@link #resolveClone(Map)} instead.
     * The <code>alreadyCloned</code> argument should not be modified inside this method,
     * but should be passed to all invocation of {@link #resolveClone(Map)}.
     *
     * This method needs to be overriden only if the clone operation requires cloning of
     * child geometries. In such case, the {@link #clone()} method should be overriden
     * and declared final as below:
     *
     * <blockquote><pre>
     * public final Object clone() {
     *     return clone(new IdentityHashMap());
     * }
     * </pre></blockquote>
     *
     * This <code>clone()</code> method needs to be final because user's implementation would be
     * ignored, since we override <code>clone(Map)</code> in a way which do not call this method
     * anymore (it usually call <code>super.clone()</code> instead).
     */
    Object clone(final Map alreadyCloned) {
        return clone();
    }

    /**
     * Clone this geometry only if it was not already cloned. This implementation is used in order
     * to avoid duplicate clones when a {@link GeometryCollection} contains {@link GeometryProxy}.
     *
     * @param alreadyCloned Maps the original geometries with their clones.
     *        This map should be an instance of {@link java.util.IdentityHashMap}.
     */
    final Object resolveClone(final Map alreadyCloned) {
        Object copy = alreadyCloned.get(this);
        if (copy != null) {
            return copy;
        }
        copy = clone(alreadyCloned);
        alreadyCloned.put(this, copy);
        return copy;
    }

    /**
     * Compares the specified object with this geometry for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object!=null && object.getClass().equals(getClass())) {
            return true;
        }
        return false;
    }

    /**
     * Returns a hash value for this geometry.
     */
    @Override
    public int hashCode() {
        // To be overriden by subclass with a more efficient implementation.
        final String name = getName(null);
        return (name!=null) ? name.hashCode() : 0;
    }

    /**
     * Return a string representation of this geometry for debugging purpose.
     * The returned string will look like
     * "<code>Polygon["polygon name", 44�30'N-51�59'N  70�59'W-54�59'W (56 pts)]</code>".
     */
    @Override
    public String toString() {
        final Format format;
        final Rectangle2D  bounds = getBounds2D();
        final CoordinateReferenceSystem cs = getCoordinateReferenceSystem();
        Object minX,minY,maxX,maxY;
        if (cs instanceof GeographicCRS) {
            minX   = new Longitude(bounds.getMinX());
            minY   = new Latitude (bounds.getMinY());
            maxX   = new Longitude(bounds.getMaxX());
            maxY   = new Latitude (bounds.getMaxY());
            format = new AngleFormat();
        } else {
            minX   = new Double(bounds.getMinX());
            minY   = new Double(bounds.getMinY());
            maxX   = new Double(bounds.getMaxX());
            maxY   = new Double(bounds.getMaxY());
            format = NumberFormat.getNumberInstance();
        }
        final String         name = getName(Locale.getDefault());
        final FieldPosition dummy = new FieldPosition(0);
        final StringBuffer buffer = new StringBuffer(Classes.getShortClassName(this));
        buffer.append('[');
        if (name != null) {
            buffer.append('"');
            buffer.append(name);
            buffer.append("\", ");
        }
        if (cs != null) {
            buffer.append("cs=\"");
            buffer.append(cs.getName());
            buffer.append("\", ");
        }
        buffer.append("x={");
        format.format(minX, buffer, dummy).append(" \u2026 ");
        format.format(maxX, buffer, dummy).append("}, y={");
        format.format(minY, buffer, dummy).append(" \u2026 ");
        format.format(maxY, buffer, dummy).append("} (");
        buffer.append(getPointCount()); buffer.append(" pts)");
        
        buffer.append(']');
        return buffer.toString();
    }
}
