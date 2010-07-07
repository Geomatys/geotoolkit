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
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.opengis.util.FactoryException;
import org.opengis.util.ProgressListener;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.math.Statistics;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.util.Utilities;


/**
 * A collection of geometry shapes. Included geometries may be {@link Polyline}s, {@link Polygon}s
 * or others {@link GeometryCollection}. Regrouping related polygons in a single collection
 * help to speed up the rendering. Polygons can be regrouped on a spatial basis (European polygons,
 * African polygons, etc.) or on a value basis (50 meters isobath, 100 meters isobath, etc.).
 * <br><br>
 * A <code>GeometryCollection</code> is initially built with a {@linkplain CoordinateSystem
 * coordinate system}. An arbitrary amount of {@linkplain Geometry geometries} can be added
 * after construction using {@link #add(Geometry)} or {@link #add(float[],int,int)}. Geometries
 * will be rendered in the order they were added. If polygons are broken in many pieces, then
 * the {@link #assemble(Shape,float[],ProgressListener) assemble(...)} method may help
 * to assemble them before rendering.
 * <br><br>
 * <strong>Note:</strong> this class has a natural ordering that is inconsistent with equals.
 * The {@link #compareTo} method compares only the collection's {@linkplain #setValue(Comparable)
 * value}, while {@link #equals} compares also all coordinate points. The natural ordering for
 * <code>GeometryCollection</code> is convenient for sorting collections in alphabetical order
 * or isobaths in increasing order of altitude.
 *
 * @version $Id: GeometryCollection.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 *
 * @todo     : Add a 'getTree(boolean)' method returning a TreeNode. Would be usefull for debugging.
 *             Node contains GeometryCollection only if boolean argument is false, GeometryCollection
 *             and Polygons if true (not Polylines). Node.toString returns Geometry.getName().
 *
 * @see Polyline
 * @see Polygon
 * @module pending
 */
public class GeometryCollection extends Geometry implements Comparable {
    /**
     * Version number for compatibility with geometry created with previous versions.
     */
    private static final long serialVersionUID = -2265970934035650026L;

    /**
     * A common value for {@link #value}. Declared in order to reduce
     * the amount of identical {@link Float} objects to be created.
     */
    private static final Float ZERO = new Float(0);

    /**
     * The value or name for this collection, or <code>null</code> if none.
     * For isobaths, the value is the altitude as a {@link Float} object.
     */
    private Comparable value;

    /**
     * Coordinate system.
     */
    private CoordinateReferenceSystem coordinateReferenceSystem;

    /**
     * Collection of geometries making up this <code>GeometryCollection</code> object.
     * Geometries will be rendered in the order they were added.
     */
    private Geometry[] geometries;

    /**
     * Number of valid elements in <code>geometries</code>.
     */
    private int count;

    /**
     * View of {@link #geometries} as an immutable collection.
     */
    private transient Collection asCollection;

    /**
     * Rectangle completely enclosing this collection. This rectangle is 
     * calculated just once and kept in an internal cache to accelerate
     * certain checks.
     */
    private UnmodifiableRectangle bounds;

    /**
     * <code>true</code> if this collection has been frozen (see <code>freeze()</code>).
     * Invoking a mutator method like {@link #setResolution} on a frozen geometry
     * will thrown a {@link UnmodifiableGeometryException}.
     */
    private boolean frozen;

    /**
     * <code>true</code> if {@link #getPathIterator} returns a flattened iterator.
     * In this case, there is no need to wrap it into a {@link FlatteningPathIterator}.
     */
    private transient boolean flattened = true;

    /**
     * The statistics about resolution, or <code>null</code> if none.
     * This object is computed when first requested and cached for subsequent uses.
     * It is also serialized if available, since it is somewhat heavy to compute.
     */
    private Statistics resolution;

    /**
     * Construct an initially empty collection using the
     * {@linkplain #DEFAULT_COORDINATE_SYSTEM default coordinate system}.
     * Polygons can be added using one of the <code>add(...)</code> methods.
     *
     * @see #add(float[],int,int)
     * @see #add(Shape)
     * @see #add(Geometry)
     */
    public GeometryCollection() {
        this(DEFAULT_COORDINATE_SYSTEM);
    }

    /**
     * Construct an initially empty collection.
     * Polygons can be added using one of the <code>add(...)</code> methods.
     *
     * @param coordinateSystem The coordinate system to use for all
     *        points in this collection, or <code>null</code> if unknown.
     *
     * @see #DEFAULT_COORDINATE_SYSTEM
     * @see LocalCoordinateSystem#PROMISCUOUS
     * @see LocalCoordinateSystem#CARTESIAN
     * @see GeographicCoordinateSystem#WGS84
     * @see #add(float[],int,int)
     * @see #add(Shape)
     * @see #add(Geometry)
     */
    public GeometryCollection(final CoordinateReferenceSystem coordinateSystem) {
        this.coordinateReferenceSystem = coordinateSystem;
    }

    /**
     * Construct a collection with the same data as the specified collection.
     * The new collection will have a copy semantic, but the underlying arrays
     * of (<var>x</var>,<var>y</var>) points will be shared.
     */
    public GeometryCollection(final GeometryCollection geometry) {
        this.coordinateReferenceSystem = geometry.coordinateReferenceSystem;
        this.value            = geometry.value;
        this.count            = geometry.count;
        this.bounds           = geometry.bounds;
        this.asCollection     = geometry.asCollection;
        this.geometries       = new Geometry[count];
        for (int i=0; i<count; i++) {
            // TODO: Consider using an IdentityHashMap
            geometries[i] = (Geometry) geometry.geometries[i].clone();
        }
        flattened = checkFlattenedShape();
    }

    /**
     * Returns the localized name for this geometry, or <code>null</code> if none.
     *
     * @param  locale The desired locale. If no name is available
     *         for this locale, a default locale will be used.
     * @return The geometry's name, localized if possible.
     *
     * @todo     : We should find a way to avoid the creation of Format object at each
     *             invocation.
     */
    @Override
    public String getName(final Locale locale) {
        final Comparable value = this.value; // Avoid the need for synchronisation.
        if (locale != null) {
            if (value instanceof Number) {
                return NumberFormat.getInstance(locale).format(value);
            }
            if (value instanceof Date) {
                return DateFormat.getDateTimeInstance(DateFormat.LONG,
                                                      DateFormat.SHORT, locale).format(value);
            }
        }
        return (value!=null) ? value.toString() : null;
    }

    /**
     * Returns the value for this collection, or <code>NaN</code> if none.
     * If this collection is an isobath, then the value is typically the isobath altitude.
     */
    public float getValue() {
        final Comparable value = this.value; // Avoid the need for synchronisation.
        return (value instanceof Number) ? ((Number) value).floatValue() : Float.NaN;
    }

    /**
     * Set the value for this geometry. If this geometry is an isobath,
     * then the value is typically the isobath altitude.
     */
    public void setValue(final float value) {
        setValue(value==0 ? ZERO : new Float(value));
    }

    /**
     * Set the value for this geometry. It may be a {@link String} (for example "Africa"),
     * or a value as a {@link Number} object (for example <code>{@link Float}(-50)</code>
     * for the -50 meters isobath). There is two advantages in using <code>Number</code>
     * instead of <code>String</code> for values:
     * <ul>
     *   <li>Better ordering with {@link #compareTo}.</li>
     *   <li>Locale-dependent formatting with {@link #getName}.</li>
     * </ul>
     *
     * @param value The value of value for this geometry.
     */
    public void setValue(final Comparable value) {
        this.value = ZERO.equals(value) ? ZERO : value;
    }

    /**
     * Returns the geometry's coordinate system, or <code>null</code> if unknown.
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return coordinateReferenceSystem;
    }

    /**
     * Set the geometry's coordinate system. Calling this method is equivalent
     * to reprojecting all geometries from the old coordinate system to the new one.
     *
     * @param  coordinateSystem The new coordinate system. A <code>null</code> value
     *         resets the coordinate system given at construction time.
     * @throws TransformException If a transformation failed. In case of failure,
     *         the state of this object will remain unchanged (as if this method has
     *         never been invoked).
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    @Override
    public synchronized void setCoordinateReferenceSystem(final CoordinateReferenceSystem crs)
            throws TransformException, UnmodifiableGeometryException
    {
        if (frozen) {
            throw new UnmodifiableGeometryException((Locale)null);
        }
        final CoordinateReferenceSystem oldCoordinateSystem = this.coordinateReferenceSystem;
        if (count==0 || Utilities.equals(oldCoordinateSystem, crs)) {
            return;
        }
        final Geometry[] projected = getModifiableGeometries();
        int i=count;
        try {
            while (--i >= 0) {
                projected[i].setCoordinateReferenceSystem(crs);
            }
        } catch (TransformException exception) {
            /*
             * If a map projection failed, roll back to the original coordinate system.
             */
            while (++i < count) {
                if (projected[i] == geometries[i]) try {
                    projected[i].setCoordinateReferenceSystem(oldCoordinateSystem);
                } catch (TransformException unexpected) {
                    // Should not happen, since the old coordinate system is supposed to be ok.
                    LineString.unexpectedException(Classes.getShortClassName(projected[i]),
                                                   "setCoordinateSystem", unexpected);
                }
            }
            throw exception;
        }
        this.coordinateReferenceSystem = crs;
        this.geometries       = projected;
        this.bounds           = null;
        this.resolution       = null;
        this.asCollection     = null;
        this.flattened        = checkFlattenedShape();
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////                                                                       ////////////
    ////////////          M O D I F I E R S :   add / remove   M E T H O D S           ////////////
    ////////////                                                                       ////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Adds points to this collection. The points must be stored as (<var>x</var>,<var>y</var>)
     * pairs in this {@link #getCoordinateSystem geometry's coordinate system}. <code>NaN</code>
     * values will be considered as disjoint lines.
     *
     * @param  array Coordinate array (may contain NaNs). These data will be copied. Consequently,
     *         any modification on <code>data</code> will have no impact on the geometries created
     *         by this method.
     * @param  lower Index of the first <var>x</var> ordinate to add to the polyline.
     * @param  upper Index after of the last <var>y</var> ordinate to add to the polyline.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    public synchronized void add(final float[] array, final int lower, final int upper)
            throws UnmodifiableGeometryException
    {
        final Polyline[] toAdd = Polyline.getInstances(array, lower, upper, coordinateReferenceSystem);
        for (int i=0; i<toAdd.length; i++) {
            addImpl(toAdd[i]);
        }
    }

    /**
     * Add geometries from the specified shape. Shape's coordinates must be
     * express in this {@link #getCoordinateSystem geometry's coordinate system}.
     *
     * @param  shape The shape to add.
     * @throws IllegalArgumentException if the specified shape can't be added. This error may
     *         occur if <code>shape</code> is an instance of {@link Geometry} and uses an
     *         incompatible coordinate system.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    public synchronized void add(final Shape shape)
            throws IllegalArgumentException, UnmodifiableGeometryException
    {
        if (shape instanceof Geometry) try {
            add((Geometry) shape);
            return;
        } catch (TransformException exception) {
            // TODO: localize this message, if it is worth it.
            final IllegalArgumentException e = new IllegalArgumentException("Incompatible CS");
            e.initCause(exception);
            throw e;
        }
        final Polyline[] toAdd = Polyline.getInstances(shape, coordinateReferenceSystem);
        for (int i=0; i<toAdd.length; i++) {
            addImpl(toAdd[i]);
        }
    }

    /**
     * Add a geometry to this collection.
     *
     * @param  toAdd Geometry to add.
     * @throws TransformException if the specified geometry can't
     *         be transformed in this collection coordinate system.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    public synchronized Geometry add(Geometry toAdd)
            throws TransformException, UnmodifiableGeometryException
    {
        if (toAdd != null) {
            if (toAdd.isFrozen()) {
                toAdd = (Geometry) toAdd.clone();
            }
            if (coordinateReferenceSystem != null) {
                toAdd.setCoordinateReferenceSystem(coordinateReferenceSystem);
            } else {
                coordinateReferenceSystem = toAdd.getCoordinateReferenceSystem();
                if (coordinateReferenceSystem != null) {
                    setCoordinateReferenceSystem(coordinateReferenceSystem);
                }
            }
            toAdd.freeze();
            addImpl(toAdd);
            
            return toAdd;
        }
        return null;
    }

    /**
     * Add a geometry to this collection. This method doesn't freeze the geometry (which will
     * allow modifications without cloning it) and doesn't set the coordinate system.
     *
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    private void addImpl(final Geometry toAdd) throws UnmodifiableGeometryException {
        assert Thread.holdsLock(this);
        if (frozen) {
            throw new UnmodifiableGeometryException((Locale)null);
        }
        if (!toAdd.isEmpty()) {
            if (geometries == null) {
                geometries = new Geometry[16];
            }
            if (count >= geometries.length) {
                geometries = (Geometry[])XArrays.resize(geometries, count+Math.min(count, 256));
            }
            geometries[count++] = toAdd;
            bounds              = null;
            asCollection        = null;
            if (flattened) {
                // May changes from 'true' to 'false'.
                flattened = toAdd.isFlattenedShape();
            }
        }
    }

    /**
     * Removes a geometry from this collection.
     *
     * @param toRemove The geometry to remove.
     * @return <code>true</code> if the geometry has been removed.
     *
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    public synchronized boolean remove(final Geometry toRemove)
            throws UnmodifiableGeometryException
    {
        if (frozen) {
            throw new UnmodifiableGeometryException((Locale)null);
        }
        boolean removed = false;
        for (int i=count; --i>=0;) {
            if (geometries[i].equals(toRemove)) {
                remove(i);
                removed = true;
            }
        }
        return removed;
        // No change to sorting order.
    }

    /**
     * Remove the geometry at the specified index.
     */
    private void remove(final int index) {
        assert Thread.holdsLock(this) && !frozen : frozen;
        bounds       = null;
        asCollection = null;
        System.arraycopy(geometries, index+1, geometries, index, count-(index+1));
        geometries[--count] = null;
        if (!flattened) {
            // May changes from 'false' to 'true'.
            flattened = checkFlattenedShape();
        }
    }

    /**
     * Remove all geometries from this geometry.
     *
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    public synchronized void removeAll() throws UnmodifiableGeometryException {
        if (frozen) {
            throw new UnmodifiableGeometryException((Locale)null);
        }
        geometries = null;
        count = 0;
        clearCache();
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////                                                                       ////////////
    ////////////               A S S E M B L A G E   /   C L I P P I N G               ////////////
    ////////////                                                                       ////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Assemble all {@linkplain Polyline polylines} in order to create closed {@linkplain Polygon
     * polygons} for proper rendering. This method analyses all available polylines and merges
     * together the polylines that look like parts of the same polygons. It can also complete the
     * polygons that were cut by the map border.
     *
     * This method is useful in the context of geometries digitalized from many consecutive
     * maps (for example the GEBCO digital atlas). It is not possible to fill polygons with
     * <A HREF="http://java.sun.com/products/java-media/2D/">Java2D</A> if the polygons are
     * broken in many pieces, as in the figure below.
     *
     * <p align="center"><img src="doc-files/splitted.png"></p>
     *
     * <P>Running this method <strong>once</strong> for a given collection of geometries before
     * renderering helps to repair them. The algorithm is:</P>
     * <ol>
     *   <li>A list of all possible pairs of polylines is built.</li>
     *   <li>For any pair of polylines, the shortest distance between their extremities is
     *       computed. All combinations between the beginning and the end of a polyline with
     *       the beginning or end of the other polyline are taken into account.</li>
     *   <li>The pair with the shortest distance are identified. When the shortest distance
     *       from one polyline's extremity is the other extremity of the same polyline, then
     *       the polyline is identified as a closed polygon (e.g. an island or a lake).
     *       Otherwise, the closest polylines are merged together.</li>
     *   <li>The loop is reexecuted from step 1 until no more polylines have been merged.</li>
     * </ol>
     *
     * This method will produces better results if this collection contains other
     * <code>GeometryCollection</code> objects (one for each isobath) with {@linkplain #setValue
     * value set} to the bathymetric value (for example <code>-50</code> for the -50 meters
     * isobath). The <code>toComplete</code> argument tells which isobaths to complete with
     * the map border provided by the <code>mapBounds</code> argument.
     *
     * @param  mapBounds The bounded shape of the map, or <code>null</code> for assuming a
     *         rectangular map inferred from this geometry. This is the bounding shape of
     *         the software that created the polylines, not an arbitrary clip that the
     *         application would like.
     * @param  toComplete {@link #setValue value} of collections to complete with map border,
     *         or <code>null</code> if none.
     * @param  progress An optional progress listener (<code>null</code> in none). This is an
     *         optional but recommanded argument, since the computation may be very long.
     * @throws TransformException if a transformation was required and failed.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    public synchronized void assemble(Shape mapBounds, float[] toComplete,
                                      final ProgressListener progress)
            throws TransformException, UnmodifiableGeometryException
    {
        if (frozen) {
            throw new UnmodifiableGeometryException((Locale)null);
        }
        if (mapBounds == null) {
            mapBounds = getBounds2D();
        }
        if (toComplete == null) {
            toComplete = new float[0];
        }
        final PolygonAssembler assembler = new PolygonAssembler(mapBounds, progress);
        assembler.assemble(this, toComplete);
        clearCache();
    }

    /**
     * Assemble all {@linkplain Polyline polylines} with default setting. This convenience
     * method will complete the map border only for the 0 meters isobath.
     *
     * @param  progress An optional progress listener (<code>null</code> in none). This is an
     *         optional but recommanded argument, since the computation may be very long.
     * @throws TransformException if a transformation was required and failed.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    public void assemble(final ProgressListener progress)
            throws TransformException, UnmodifiableGeometryException
    {
        assemble(null, new float[]{-0f,0f}, progress);
    }

    /**
     * Returns an geometry approximately equal to this geometry clipped to the specified bounds.
     * The clip is only approximate in that the resulting geometry may extend outside the clip
     * area. However, it is guaranteed that the resulting geometry contains at least all the interior
     * of the clip area.
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
    @Override
    public synchronized Geometry clip(final Clipper clipper) {
        Geometry[] clips = new Geometry[count];
        int    clipCount = 0;
        boolean  changed = false;
        /*
         * Clip all geometries, discarding geometries outside the clip.
         */
        for (int i=0; i<count; i++) {
            final Geometry toClip  = geometries[i];
            final Geometry clipped = toClip.clip(clipper);
            if (clipped!=null && !clipped.isEmpty()) {
                assert Utilities.equals(toClip.getUserObject(), clipped.getUserObject()) : clipped;
                clips[clipCount++] = clipped;
                if (toClip != clipped) {
                    changed = true;
                }
            } else {
                changed = true;
            }
        }
        if (clipCount == 1) {
            // If there is only one geometry left, returns this particular geometry as
            // an optimisation.  It force us to copy the style, otherwise the geometry
            // will not be correctly rendered. REVISIT: is it the right thing to do?
            final Geometry clipped = clips[0];
            return clipped;
        }
        if (!changed) {
            freeze();
            return this;
        }
        final GeometryCollection geometry = new GeometryCollection(coordinateReferenceSystem);
        geometry.geometries = (Geometry[]) XArrays.resize(clips, clipCount);
        geometry.count      = clipCount;
        geometry.value      = this.value;
        if (coordinateReferenceSystem.equals(clipper.mapCRS)) {
            geometry.bounds = new UnmodifiableRectangle(bounds.createIntersection(clipper.mapClip));
            // Note: Bounds computed above may be bigger than the bounds usually computed
            //       by 'getBounds2D()'.  However, these bigger bounds conform to Shape
            //       specification and are also desirable.  If the bounds were smaller than
            //       the clip, the rendering code would wrongly believe that the clipped
            //       geometry is inappropriate for the clipping area. It would slow down the
            //       rendering, but would not affect the visual result.
        }
        return geometry;
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////                                                                       ////////////
    ////////////         A C C E S S O R S :   'getGeometries'   M E T H O D S         ////////////
    ////////////                                                                       ////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Remove all {@link GeometryCollection} from this collection and returns
     * them in a separated list. This method is used by {@link PolygonAssembler}.
     *
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    final List extractCollections() throws UnmodifiableGeometryException {
        assert Thread.holdsLock(this);
        if (frozen) {
            throw new UnmodifiableGeometryException((Locale)null);
        }
        int newCount = 0;
        final List collections = new ArrayList();
        for (int i=0; i<count; i++) {
            final Geometry geometry = geometries[i];
            if (geometry instanceof GeometryCollection) {
                collections.add(geometry);
            } else {
                geometry.freeze();
                geometries[newCount++] = geometry;
            }
        }
        count = newCount;
        trimToSize();
        return collections;
    }

    /**
     * Add to the specified collection all {@link Polyline} objects making this
     * geometry. This method is used by {@link GeometryCollection#getPathIterator}
     * and {@link PolygonAssembler} only.
     */
    @Override
    synchronized void getPolylines(final Collection polylines) {
        for (int i=0; i<count; i++) {
            geometries[i].getPolylines(polylines);
        }
    }

    /**
     * Returns a copy of {@link #geometries} with modifiable geometries.
     * All frozen geometries will have be cloned.
     */
    private Geometry[] getModifiableGeometries() {
        assert Thread.holdsLock(this);
        final Geometry[] copy = new Geometry[count];
        if (geometries != null) {
            System.arraycopy(geometries, 0, copy, 0, count);
        }
        Map alreadyCloned = null;
        for (int i=0; i<copy.length; i++) {
            if (copy[i].isFrozen()) {
                if (alreadyCloned == null) {
                    alreadyCloned = new IdentityHashMap();
                }
                copy[i] = (Geometry) copy[i].clone(alreadyCloned);
            }
            assert !copy[i].isFrozen() : copy[i];
        }
        return copy;
    }

    /**
     * Returns the collection of {@link Geometry} objects. The collection will
     * contains geometries in the order they were {@linkplain #add(Geometry) added}.
     *
     * @return A collection of {@link Geometry} objects.
     */
    public synchronized Collection getGeometries() {
        if (asCollection == null) {
            trimToSize();
            if (count == 0) {
                asCollection = Collections.EMPTY_LIST;
            } else {
                for (int i=0; i<count; i++) {
                    geometries[i].freeze();
                }
                asCollection = Collections.unmodifiableList(Arrays.asList(geometries));
            }
        }
        return asCollection;
    }

    /**
     * Returns the collection of geometries containing the specified point.
     * The collection will contains geometries in the reverse order, i.e.
     * geometries {@linkplain #add(Geometry) added} last will be returned first.
     * This convention make it easier to find the smallest geometry contained in
     * bigger geometry.
     *
     * @param  point The coordinates to look at in this
     *               {@linkplain #getCoordinateSystem geometry's coordinate system}.
     * @return The collection of geometries under the specified point.
     */
    public synchronized Collection getGeometries(final Point2D point) {
        if (getCachedBounds().contains(point)) {
            return new Filtered(this) {
                protected boolean accept(final Geometry geometry) {
                    return geometry.contains(point);
                }
            };
        }
        return Collections.EMPTY_SET;
    }

    /**
     * Returns the collection of geometries containing the specified shape.
     * The collection will contains geometries in the reverse order, i.e.
     * geometries {@linkplain #add(Geometry) added} last will be returned first.
     * This convention make it easier to find the smallest geometry contained in
     * bigger geometry.
     *
     * @param  shape A shape with coordinates expressed according to {@link #getCoordinateSystem}.
     * @return The collection of geometries containing the specified shape.
     */
    public synchronized Collection getGeometriesContaining(final Shape shape) {
        if (XRectangle2D.intersectInclusive(shape, getCachedBounds())) {
            return new Filtered(this) {
                protected boolean accept(final Geometry geometry) {
                    return geometry.contains(shape);
                }
            };
        }
        return Collections.EMPTY_SET;
    }

    /**
     * Returns the collection of geometries intersecting the specified shape.
     * The collection will contains geometries in the reverse order, i.e.
     * geometries {@linkplain #add(Geometry) added} last will be returned first.
     * This convention make it easier to find the smallest geometry contained in
     * bigger geometry.
     *
     * @param  shape A shape with coordinates expressed according to {@link #getCoordinateSystem}.
     * @return The collection of geometries intersecting the specified shape.
     */
    public synchronized Collection getGeometriesIntersecting(final Shape shape) {
        if (XRectangle2D.intersectInclusive(shape, getCachedBounds())) {
            return new Filtered(this) {
                protected boolean accept(final Geometry geometry) {
                    return geometry.intersects(shape);
                }
            };
        }
        return Collections.EMPTY_SET;
    }

    /**
     * Returns the name of the smallest {@linkplain Polygon polygon} at the given location.
     * This method is usefull for formatting tooltip text when the mouse cursor moves over
     * the map.
     *
     * @param  point The coordinates to look at in this
     *               {@linkplain #getCoordinateSystem geometry's coordinate system}.
     * @param  locale The desired locale for the geometry name.
     * @return The geometry name at the given location, or <code>null</code> if there is none.
     */
    public synchronized String getPolygonName(final Point2D point, final Locale locale) {
        if (getCachedBounds().contains(point)) {
            String name;
            for (int i=0; i<count; i++) {
                final Geometry polygon = geometries[i];
                if (polygon instanceof GeometryCollection) {
                    name = ((GeometryCollection) polygon).getPolygonName(point, locale);
                    if (name != null) {
                        return name;
                    }
                } else {
                    name = polygon.getName(locale);
                    if (name!=null && polygon.contains(point)) {
                        return name;
                    }
                }
            }
        }
        return null;
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////                                                                       ////////////
    ////////////                S H A P E   I M P L E M E N T A T I O N                ////////////
    ////////////            getBounds2D() / contains(...) / intersects(...)            ////////////
    ////////////                                                                       ////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Determines whetever the collection is empty.
     */
    @Override
    public synchronized boolean isEmpty() {
        for (int i=count; --i>=0;) {
            if (!geometries[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns an estimation of memory usage in bytes. This method is for information
     * purposes only. The memory really used by two geometries may be lower than the sum
     * of their  <code>getMemoryUsage()</code>  return values,  since geometries try to
     * share their data when possible. Furthermore, this method does not take into account
     * the extra bytes generated by Java Virtual Machine for each object.
     *
     * @return An <em>estimation</em> of memory usage in bytes.
     */
    @Override
    final synchronized long getMemoryUsage() {
        long total = 48;
        if (geometries != null) {
            total += 4*geometries.length;
        }
        for (int i=count; --i>=0;) {
            total += geometries[i].getMemoryUsage();
        }
        return total;
    }

    /**
     * Returns the number of points in this geometry.
     */
    @Override
    public synchronized int getPointCount() {
        int n = 0;
        for (int i=count; --i>=0;) {
            n += geometries[i].getPointCount();
        }
        return n;
    }

    /**
     * Return the bounding box of this geometry.
     */
    private Rectangle2D getCachedBounds() {
        assert frozen || Thread.holdsLock(this) : frozen;
        if (bounds == null) {
            Rectangle2D bounds = null;
            for (int i=count; --i>=0;) {
                final Geometry polygon = geometries[i];
                if (!polygon.isEmpty()) {
                    final Rectangle2D polygonBounds = polygon.getBounds2D();
                    if (bounds == null) {
                        bounds = new XRectangle2D(polygonBounds);
                    } else {
                        bounds.add(polygonBounds);
                    }
                }
            }
            this.bounds = new UnmodifiableRectangle(bounds);
        }
        return bounds;
    }

    /**
     * Return the bounding box of this geometry, including its possible
     * borders. This method uses a cache, such that after a first call,
     * the following calls should be fairly quick.
     *
     * @return A bounding box of this geometry. Changes to this rectangle
     *         will not affect the cache.
     */
    @Override
    public Rectangle2D getBounds2D() {
        if (!frozen) {
            // synchronize only if the shape is mutable.
            synchronized (this) {
                return getCachedBounds();
            }
        }
        return getCachedBounds(); // Immutable instance
    }

    /**
     * Indicates whether the specified (<var>x</var>,<var>y</var>) point is inside this geometry.
     * The point coordinates must be expressed in the geometry's coordinate system, that is
     * {@link #getCoordinateSystem()}.
     */
    @Override
    public synchronized boolean contains(final double x, final double y) {
        if (getCachedBounds().contains(x,y)) {
            for (int i=0; i<count; i++) {
                if (geometries[i].contains(x,y)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Indicates whether the specified point is inside this geometry.
     * The point coordinates must be expressed in the geometry's coordinate system,
     * that is {@link #getCoordinateSystem()}.
     */
    @Override
    public synchronized boolean contains(final Point2D point) {
        if (getCachedBounds().contains(point)) {
            for (int i=0; i<count; i++) {
                if (geometries[i].contains(point)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the specified rectangle is entirely contained within this geometry.
     * The rectangle's coordinates should be expressed in the geometry's coordinate system,
     * that is {link #getCoordinateSystem()}.
     */
    @Override
    public synchronized boolean contains(final Rectangle2D rect) {
        if (getCachedBounds().contains(rect)) {
            final Polygon shape = new Polygon(rect, getCoordinateReferenceSystem());
            for (int i=0; i<count; i++) {
                if (geometries[i].contains(shape)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the specified shape is entirely contained within this geometry.
     * The shape's coordinates must be expressed in the geometry's coordinate system, 
     * that is {@link #getCoordinateSystem()}.
     */
    @Override
    public synchronized boolean contains(final Shape shape) {
        if (getCachedBounds().contains(shape.getBounds2D())) {
            for (int i=0; i<count; i++) {
                if (geometries[i].contains(shape)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Tests whether the specified rectangle intersects the interior of this geometry.
     */
    @Override
    public synchronized boolean intersects(final Rectangle2D rect) {
        if (getCachedBounds().intersects(rect)) {
            final Polygon shape = new Polygon(rect, getCoordinateReferenceSystem());
            for (int i=0; i<count; i++) {
                if (geometries[i].intersects(shape)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Tests whether the specified shape intersects the interior of this geometry.
     */
    @Override
    public synchronized boolean intersects(final Shape shape) {
        if (getCachedBounds().intersects(shape.getBounds2D())) {
            for (int i=0; i<count; i++) {
                if (geometries[i].intersects(shape)) {
                    return true;
                }
            }
        }
        return false;
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////                                                                       ////////////
    ////////////    C O M P R E S S I O N   /   R E S O L U T I O N   S E T T I N G    ////////////
    ////////////                                                                       ////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Trim the {@link #geometries} array to its minimal size.
     */
    private void trimToSize() {
        assert Thread.holdsLock(this);
        geometries = (Geometry[]) XArrays.resize(geometries, count);
    }

    /**
     * Compress all geometries in this collection. The <code>level</code> argument specify the
     * algorithm, which may be desctructive (i.e. data may loose precision). For example, the
     * compression may replaces direct positions by relative positions, as in the figures below:
     *
     *       <table cellspacing='12'><tr>
     *       <td><p align="center"><img src="doc-files/uncompressed.png"></p></td>
     *       <td><p align="center"><img src="doc-files/compressed.png"></p></td>
     *       </tr></table>
     *
     * @param  level The compression level (or algorithm) to use. See the {@link CompressionLevel}
     *         javadoc for an explanation of available algorithms.
     * @return A <em>estimation</em> of the compression rate. For example a value of 0.2
     *         means that the new polygon uses <em>approximately</em> 20% less memory.
     * @throws TransformException If an error has occurred during a cartographic projection.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    @Override
    public synchronized float compress(final CompressionLevel level)
            throws TransformException, UnmodifiableGeometryException
    {
        if (frozen) {
            throw new UnmodifiableGeometryException((Locale)null);
        }
        bounds       = null;
        asCollection = null;
        int newCount = 0;
        geometries   = getModifiableGeometries();
        final long memoryUsage = getMemoryUsage();
        for (int i=0; i<count; i++) {
            final Geometry polygon = geometries[i];
            
            try {
                polygon.compress(level);
            } catch (FactoryException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
            
            
            if (!polygon.isEmpty()) {
                geometries[newCount++] = polygon;
            }
        }
        count = newCount;
        trimToSize();
        clearCache();
        return (float) (memoryUsage - getMemoryUsage()) / (float) memoryUsage;
    }

    /**
     * Returns the geometry's resolution. The mean resolution is the mean distance between
     * every pair of consecutive points in this geometry. This method tries to express the
     * resolution in linear units (usually meters) no matter whether the coordinate systems
     * is actually a {@linkplain ProjectedCoordinateSystem projected} or a
     * {@linkplain GeographicCoordinateSystem geographic} one.
     */
    @Override
    public synchronized Statistics getResolution() {
        if (resolution == null) {
            for (int i=count; --i>=0;) {
                final Statistics toAdd = geometries[i].getResolution();
                if (resolution == null) {
                    resolution = toAdd;
                } else {
                    resolution.add(toAdd);
                }
            }
        }
        if (resolution == null) {
            return new Statistics();
        }
        return (Statistics) resolution.clone();
    }

    /**
     * Set the geometry's resolution. This method tries to interpolate new points in such a way
     * that every point is spaced by exactly <code>resolution</code> units (usually meters)
     * from the previous one.
     *
     * @param  resolution Desired resolution, in the same units as {@link #getResolution}.
     * @throws TransformException If some coordinate transformations were needed and failed.
     *         There is no guarantee on contour's state in case of failure.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    @Override
    public synchronized void setResolution(final double resolution)
            throws TransformException, UnmodifiableGeometryException
    {
        if (frozen) {
            throw new UnmodifiableGeometryException((Locale)null);
        }
        bounds       = null;
        asCollection = null;
        geometries   = getModifiableGeometries();
        for (int i=count; --i>=0;) {
            final Geometry polygon = geometries[i];
            
            try {
                polygon.setResolution(resolution);
            } catch (FactoryException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
            
            if (polygon.isEmpty()) {
                remove(i);
            }
        }
    }

    /**
     * Returns the rendering resolution. This is the spatial resolution used by
     * {@link PathIterator} only; it has no effect on the underyling data.
     *
     * @return The rendering resolution in units of this geometry's
     *         {@linkplain #getCoordinateSystem coordinate system} (linear or angular units),
     *         or 0 if the finest available resolution should be used.
     */
    @Override
    public synchronized float getRenderingResolution() {
        float resolution = 0;
        for (int i=count; --i>=0;) {
            final float candidate = geometries[i].getRenderingResolution();
            if (candidate > resolution) {
                resolution = candidate;
            }
        }
        return resolution;
    }

    /**
     * Hints this geometry that the specified resolution is sufficient for rendering.
     * Value 0 ask for the best available resolution. If a value greater than 0 is provided,
     * then the {@link PathIterator} will skip as many points as it can while preserving a
     * distance equals or smaller than <code>resolution</code> between two consecutive points.
     *
     * @param resolution The resolution to use at rendering time, in units of this geometry's
     *        {@linkplain #getCoordinateSystem coordinate system} (linear or angular units).
     */
    @Override
    public synchronized void setRenderingResolution(float resolution) {
        for (int i=count; --i>=0;) {
            geometries[i].setRenderingResolution(resolution);
        }
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////                                                                       ////////////
    ////////////       P A T H   I T E R A T O R   /   M I S C E L L A N E O U S       ////////////
    ////////////                                                                       ////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a path iterator for this geometry.
     */
    @Override
    public PathIterator getPathIterator(final AffineTransform transform) {
        final List list = new ArrayList(count);
        getPolylines(list);
        return new PolygonPathIterator(null, list.iterator(), transform);
    }

    /**
     * Returns <code>true</code> if {@link #getPathIterator} returns a flattened iterator.
     * In this case, there is no need to wrap it into a {@link FlatteningPathIterator}.
     */
    @Override
    final boolean isFlattenedShape() {
        assert flattened == checkFlattenedShape() : flattened;
        return flattened;
    }

    /**
     * Returns <code>true</code> if {@link #getPathIterator} returns a flattened iterator.
     * In this case, there is no need to wrap it into a {@link FlatteningPathIterator}.
     */
    private boolean checkFlattenedShape() {
        for (int i=count; --i>=0;) {
            if (!geometries[i].isFlattenedShape()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Deletes all the information that was kept in an internal cache. This method can be
     * called when we know that this geometry will no longer be used before a particular time.
     * It does not cause the loss of any information, but will make subsequent uses of this
     * geometry slower (the time the internal caches take to be reconstructed, after which the
     * geometry will resume its normal speed).
     */
    @Override
    final synchronized void clearCache() {
        bounds        = null;
        resolution    = null;
        asCollection  = null;
        for (int i=count; --i>=0;) {
            geometries[i].clearCache();
        }
        flattened = checkFlattenedShape();
        super.clearCache();
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////                                                                       ////////////
    ////////////           C L O N E   /   E Q U A L S   /   H A S H C O D E           ////////////
    ////////////                                                                       ////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Freeze this collection. Once this method is invoked, no more geometry can
     * be added to the collection and the coordinate system can't be changed.
     */
    @Override
    synchronized void freeze() {
        trimToSize();
        frozen = true;
    }

    /**
     * Returns <code>true</code> if we are not allowed to change this collection.
     */
    @Override
    final boolean isFrozen() {
        return frozen;
    }

    /**
     * Return a copy of this geometry. The clone has a deep copy semantic,
     * but will share many internal arrays with the original geometry.
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
     * Return a copy of this geometry. The clone has a deep copy semantic,
     * but will share many internal arrays with the original geometry.
     */
    @Override
    synchronized Object clone(final Map alreadyCloned) {
        // Note: we can't use the 'GeometryCollection(GeometryCollection)' constructor,
        //       because the user way have subclassed this geometry.
        final GeometryCollection geometry = (GeometryCollection) super.clone();
        geometry.frozen = false;
        geometry.geometries = new Geometry[count];
        for (int i=geometry.geometries.length; --i>=0;) {
            geometry.geometries[i] = (Geometry) geometries[i].resolveClone(alreadyCloned);
        }
        return geometry;
    }

    /**
     * Compare this geometry with the specified object for order. Note that this method is
     * inconsistent with <code>equals</code>. The method <code>compareTo</code> compares only the
     * {@linkplain #setValue(Comparable) value}, while <code>equals</code> compares all coordinate
     * points. The natural ordering for <code>GeometryCollection</code> is convenient for sorting
     * geometries in alphabetical order or isobaths in increasing order of altitude. Geometries
     * without value are sorted last.
     *
     * @param  object The geometry to compare value with.
     * @return <ul>
     *    <li>+1 if this geometry's value is greater than the value for the specified geometry.</li>
     *    <li>-1 if this geometry's value is less than the value for the specified geometry.</li>
     *    <li> 0 if both geometries have the same value or value.</li>
     *  </ul>
     */
    @Override
    public int compareTo(final Object object) {
        final GeometryCollection that = (GeometryCollection) object;
        if (this.value == that.value) return 0;
        if (this.value == null)       return +1;
        if (that.value == null)       return -1;
        try {
            return value.compareTo(that.value);
        } catch (ClassCastException exception) {
            // Values not comparable. Check for numbers.
            if (this.value instanceof Number && that.value instanceof Number) {
                return Double.compare(((Number)this.value).doubleValue(),
                                      ((Number)that.value).doubleValue());
            }
            // Compares their string representation instead.
            final String name1 = this.value.toString().trim();
            final String name2 = that.value.toString().trim();
            return name1.compareTo(name2);
        }
    }

    /**
     * Compares the specified object with this geometry for equality.
     * This methods checks and all coordinate points.
     */
    @Override
    public synchronized boolean equals(final Object object) {
        if (object==this) {
            // Slight optimization
            return true;
        }
        if (super.equals(object)) {
            final GeometryCollection that = (GeometryCollection) object;
            if (this.count==that.count && Utilities.equals(this.value, that.value)) {
                for (int i=count; --i>=0;) {
                    if (Utilities.equals(this.geometries[i], that.geometries[i])) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a hash value for this geometry.
     */
    @Override
    public synchronized int hashCode() {
        // Do not take the value in account, since it
        // is not a property protected against changes.
        int code = (int)serialVersionUID;
        for (int i=0; i<count; i++) {
            // Must be insensitive to order.
            code += geometries[i].hashCode();
        }
        return code;
    }

    /**
     * Invoked during deserialization.
     */
    protected void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        flattened = checkFlattenedShape(); // Reasonably fast to compute.
    }

    /**
     * Invoked during serialization.
     */
    protected synchronized void writeObject(final ObjectOutputStream out) throws IOException {
        trimToSize();
        out.defaultWriteObject();
    }



    /**
     * The collection of geometries meeting a condition.
     * The check for inclusion or intersection will be performed only when first needed.
     *
     * @version $Id: GeometryCollection.java 17672 2006-01-19 00:25:55Z desruisseaux $
     * @author Martin Desruisseaux
     */
    private static abstract class Filtered extends AbstractCollection {
        /**
         * The geometries to check. This array <strong>must</strong> be a copy of
         * {@link GeometryCollection#geometries}. It will be changed during iteration:
         * geometries that do not obey the condition will be set to <code>null</code>.
         */
        private Geometry[] geometries;

        /**
         * Index of the next geometry to check. All geometries
         * before this index are considered valid.
         */
        private int upper;

        /**
         * Construct a filtered collection.
         *
         * @param source The source.
         */
        public Filtered(final GeometryCollection source) {
            geometries = new Geometry[source.count];
            for (int i=source.count,j=0; --i>=0; j++) {
                geometries[j] = source.geometries[i];
            }
        }

        /**
         * Returns <code>true</code> if this collection should accept the given geometry.
         */
        protected abstract boolean accept(final Geometry geometry);

        /**
         * Returns the index of the next valid polygon starting at of after the specified
         * index. If there is no polygon left, it returns a number greater than or equal to
         * <code>geometries.length</code>. This method should be invoked with increasing
         * value of <code>from</code> only (values in random order are not supported).
         */
        private int next(int from) {
            while (from < geometries.length) {
                Geometry polygon = geometries[from];
                if (polygon != null) {
                    if (from >= upper) {
                        // This polygon has not been checked yet for validity.
                        upper = from+1;
                        if (!accept(polygon)) {
                            geometries[from] = null;
                            continue;
                        }
                        polygon.freeze();
                        geometries[from] = polygon;
                    }
                    break;
                }
            }
            return from;
        }
        
        /**
         * Returns the number of elements in this collection.
         */
        public int size() {
            int n = 0;
            for (int i=next(0); i<geometries.length; i=next(i+1)) {
                n++;
            }
            return n;
        }

        /**
         * Returns an iterator over the elements in this collection.
         */
        public Iterator iterator() {
            return new Iterator() {
                /** Index of the next valid polygon. */
                private int index = Filtered.this.next(0);

                /** Check if there are more geometries. */
                public boolean hasNext() {
                    return index < geometries.length;
                }

                /** Returns the next polygon. */
                public Object next() {
                    if (index < geometries.length) {
                        final Geometry next = geometries[index];
                        index = Filtered.this.next(index+1);
                        return next;
                    } else {
                        throw new NoSuchElementException();
                    }
                }

                /** Unsupported operation. */
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
}
