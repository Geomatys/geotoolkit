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
package org.geotoolkit.display2d.geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

import org.geotoolkit.math.Statistics;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Utilities;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;


/**
 * A polygon bounded by one exterior ring (the "shell") and zero or more interior rings
 * (the "holes"). Shell and holes are stored as {@link Polyline} objects.
 *
 * @source $URL: http://svn.geotools.org/branches/legacy/migrate/src/org/geotools/renderer/geom/Polygon.java $
 * @version $Id: Polygon.java 19515 2006-05-17 06:07:00Z desruisseaux $
 * @author Martin Desruisseaux
 */
public class Polygon extends Polyline {
    /**
     * Version number for compatibility with geometry created with previous versions.
     */
    private static final long serialVersionUID = 4862662818696526222L;

    /**
     * An empty array of polylines.
     */
    private static final Polyline[] EMPTY_ARRAY = new Polyline[0];

    /**
     * Nom de ce polygone.  Il s'agit en g�n�ral d'un nom g�ographique, par exemple
     * "�le d'Anticosti" ou "Lac Sup�rieur". Ce champs peut �tre nul si ce polygone
     * ne porte pas de nom.
     */
    private String name;

    /**
     * The holes, or {@code null} if none.
     */
    private Polyline[] holes;

    /**
     * Construct a polygon from the specified polyline.
     *
     * @param shell The exterior ring.
     */
    public Polygon(final Polyline shell) {
        super(shell);
        super.close();
        if (shell instanceof Polygon) {
            holes = ((Polygon)shell).holes;
        }
    }

    /**
     * Construct a polygon from the specified rectangle. The polygon will be empty
     * if the rectangle was empty or contains at least one <code>NaN</code> value.
     *
     * @param rectangle Rectangle to copy in the new <code>Polygon</code>.
     * @param coordinateSystem The rectangle's coordinate system, or {@code null} if unknown.
     */
    public Polygon(final Rectangle2D rectangle, final CoordinateReferenceSystem coordinateSystem) {
        super(rectangle, coordinateSystem);
    }

    /**
     * Returns the localized name for this polygon. The default implementation
     * returns the last name set by {@link #setName}.
     *
     * @param  locale The desired locale. If no name is available
     *         for this locale, a default locale will be used.
     * @return The polygon's name, localized if possible.
     */
    @Override
    public String getName(final Locale locale) {
        return name;
    }

    /**
     * Set a default name for this polygon. For example, a polygon may have the name of a
     * lake or an island. This name may be {@code null} if this polygon is unnamed.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Set the polygon's coordinate system. Calling this method is equivalents
     * to reproject all polygon's points from the old coordinate system to the
     * new one.
     *
     * @param  coordinateSystem The new coordinate system. A {@code null} value reset
     *         the default coordinate system (usually the one that best fits internal data).
     * @throws TransformException If a transformation failed. In case of failure,
     *         the state of this object will stay unchanged, as if this method has
     *         never been invoked.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    @Override
    public synchronized void setCoordinateReferenceSystem(CoordinateReferenceSystem coordinateSystem)
            throws TransformException, UnmodifiableGeometryException
    {
        final CoordinateReferenceSystem oldCS = getCoordinateReferenceSystem();
        super.setCoordinateReferenceSystem(coordinateSystem);
        if (holes != null) {
            coordinateSystem = getCoordinateReferenceSystem();
            final Polyline[] projected = getModifiableHoles();
            int i=0;
            try {
                while (i < projected.length) {
                    projected[i].setCoordinateReferenceSystem(coordinateSystem);
                    i++;
                }
            } catch (TransformException exception) {
                // Roll back the change.
                while (--i >= 0) {
                    if (projected[i] == holes[i]) try {
                        projected[i].setCoordinateReferenceSystem(oldCS);
                    } catch (TransformException unexpected) {
                        // Should not happen, since the old coordinate system is supposed to be ok.
                        LineString.unexpectedException(Classes.getShortClassName(projected[i]),
                                                       "setCoordinateSystem", unexpected);
                    }
                }
                super.setCoordinateReferenceSystem(oldCS);
                throw exception;
            }
            holes = projected;
        }
    }

    /**
     * Add a hole to this polygon.
     *
     * @param  hole The hole to add.
     * @throws TransformException if the hole uses an incompatible coordinate system.
     * @throws IllegalArgumentException if the hole is not inside the exterior ring.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     *
     * @task TODO: The check for hole inclusion should use 'contains(Shape)'. However, this is an
     *             expensive check in current version. We just check the bounding box for now. We
     *             should make a stricter check when Polyline.contains will be optimized.
     *             We should also make sure that the new hole doesn't intersect an existing hole.
     */
    public synchronized void addHole(Polyline hole)
            throws TransformException, UnmodifiableGeometryException
    {
        if (isFrozen()) {
            throw new UnmodifiableGeometryException((Locale)null);
        }
        // Use 'new Polyline(hole)' rather than 'hole.clone()' in order to
        // keep only the exterior ring if 'hole' is an instance of Polygon.
        hole = new Polyline(hole);
        hole.close();
        hole.setCoordinateReferenceSystem(getCoordinateReferenceSystem());
        if (hole.isEmpty()) {
            return;
        }
        if (!getBounds2D().contains(hole.getBounds2D())) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.HOLE_NOT_INSIDE_POLYGON));
        }
        if (holes == null) {
            holes = new Polyline[] {hole};
        } else {
            final int length = holes.length;
            holes = (Polyline[]) XArrays.resize(holes, length+1);
            holes[length] = hole;
        }
        clearCache();
    }

    /**
     * Reverse point order in this polygon.
     */
    @Override
    public synchronized void reverse() {
        super.reverse();
        if (holes != null) {
            for (int i=0; i<holes.length; i++) {
                holes[i].reverse();
            }
        }
    }

    /**
     * Returns a copy of {@link #holes} with modifiable geometries.
     * All frozen geometries will be cloned.
     */
    private Polyline[] getModifiableHoles() {
        assert Thread.holdsLock(this);
        if (holes == null) {
            return EMPTY_ARRAY;
        }
        final Polyline[] copy = (Polyline[])holes.clone();
        Map alreadyCloned = null;
        for (int i=0; i<copy.length; i++) {
            if (copy[i].isFrozen()) {
                if (alreadyCloned == null) {
                    alreadyCloned = new IdentityHashMap();
                }
                copy[i] = (Polyline) copy[i].clone(alreadyCloned);
            }
            assert !copy[i].isFrozen() : copy[i];
        }
        return copy;
    }

    /**
     * Add to the specified collection all {@link Polyline} objects making this
     * geometry. This method is used by {@link GeometryCollection#getPathIterator}
     * and {@link PolygonAssembler} only.
     */
    @Override
    synchronized void getPolylines(final Collection polylines) {
        super.getPolylines(polylines);
        if (holes != null) {
            for (int i=0; i<holes.length; i++) {
                holes[i].getPolylines(polylines);
            }
        }
    }

    /**
     * Returns an estimation of memory usage in bytes. This method is for information
     * purposes only. The memory really used by two polygons may be lower than the sum
     * of their  <code>getMemoryUsage()</code>  return values, since polylgons try to
     * share their data when possible.
     *
     * @return An <em>estimation</em> of memory usage in bytes.
     */
    @Override
    synchronized long getMemoryUsage() {
        long count = super.getMemoryUsage() + 8;
        if (holes != null) {
            count += 4*holes.length;
            for (int i=0; i<holes.length; i++) {
                count += holes[i].getMemoryUsage();
            }
        }
        return count;
    }

    /**
     * Return the number of points in this polygon. This number include the points in holes.
     * It may not be the same than the number of points returned by {@link #getPoints}, which
     * contains the points in the exterior ring only.
     */
    @Override
    public synchronized int getPointCount() {
        int count = super.getPointCount();
        if (holes != null) {
            for (int i=0; i<holes.length; i++) {
                count += holes[i].getPointCount();
            }
        }
        return count;
    }

    /**
     * Tests if the specified coordinates are inside the boundary of this polygon.
     *
     * @param  x the specified <var>x</var> coordinates in this polygon coordinate system.
     * @param  y the specified <var>y</var> coordinates in this polygon coordinate system.
     * @return <code>true</code> if the specified coordinates are inside the polygon
     *         boundary and outside any hole; <code>false</code> otherwise.
     */
    @Override
    public synchronized boolean contains(final double x, final double y) {
        if (super.contains(x,y)) {
            if (holes != null) {
                for (int i=0; i<holes.length; i++) {
                    if (holes[i].contains(x,y)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Test if the interior of this polygon entirely contains the given shape.
     * This method returns <code>false</code> if the given shape intersects a hole.
     */
    @Override
    public boolean contains(final Shape shape) {
        // Method overriden for documentation purpose only.
        // The real work is performed by 'containsPolyline'.
        return super.contains(shape);
    }

    /**
     * Test if the interior of this polygon entirely contains the given shape.
     * This method returns <code>false</code> if the given shape intersects a hole.
     */
    @Override
    boolean containsPolyline(final Polyline shape) {
        if (super.containsPolyline(shape)) {
            if (holes != null) {
                for (int i=0; i<holes.length; i++) {
                    if (shape.intersectsPolyline(holes[i])) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Tests if the interior of this polygon intersects the interior of a specified shape.
     * This method returns <code>false</code> if the given shape in entirely contained in a hole.
     */
    @Override
    public boolean intersects(final Shape shape) {
        // Method overriden for documentation purpose only.
        // The real work is performed by 'intersectsPolyline'.
        return super.intersects(shape);
    }

    /**
     * Tests if the interior of this polygon intersects the interior of a specified polyline.
     * This method returns <code>false</code> if the given shape in entirely contained in a hole.
     */
    @Override
    boolean intersectsPolyline(final Polyline shape) {
        if (super.intersectsPolyline(shape)) {
            if (holes != null) {
                for (int i=0; i<holes.length; i++) {
                    if (holes[i].containsPolyline(shape)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Compress this polygon. The <code>level</code> argument specify the algorithm,
     * which may be desctructive (i.e. data may loose precision).
     *
     * @param  level The compression level (or algorithm) to use. See the {@link CompressionLevel}
     *         javadoc for an explanation of available algorithms.
     * @return A <em>estimation</em> of the compression rate. For example a value of 0.2
     *         means that the new polyline use <em>approximatively</em> 20% less memory.
     * @throws TransformException If an error has come up during a cartographic projection.
     */
    @Override
    public synchronized float compress(final CompressionLevel level) throws TransformException, FactoryException {
        final long memoryUsage = getMemoryUsage();
        super.compress(level);
        if (holes != null) {
            holes = getModifiableHoles();
            int count = 0;
            for (int i=0; i<holes.length; i++) {
                final Polyline polyline = holes[i];
                polyline.compress(level);
                if (!polyline.isEmpty()) {
                    holes[count++] = polyline;
                }
            }
            holes = (count!=0) ? (Polyline[]) XArrays.resize(holes, count) : null;
        }
        return (float) (memoryUsage - getMemoryUsage()) / (float) memoryUsage;
    }

    /**
     * Returns the polygon's resolution.
     */
    @Override
    public synchronized Statistics getResolution() {
        final Statistics stats = super.getResolution();
        if (holes != null) {
            for (int i=0; i<holes.length; i++) {
                stats.add(holes[i].getResolution());
            }
        }
        return stats;
    }

    /**
     * Sets the polygon's resolution.
     *
     * @throws TransformException If some coordinate transformations were needed and failed.
     *         There is no guarantee on polygon's state in case of failure.
     */
    @Override
    public synchronized void setResolution(final double resolution) throws TransformException, FactoryException {
        super.setResolution(resolution);
        if (holes != null) {
            holes = getModifiableHoles();
            for (int i=0; i<holes.length; i++) {
                holes[i].setResolution(resolution);
            }
        }
    }

    /**
     * Hints this polygon that the specified resolution is sufficient for rendering.
     */
    @Override
    public void setRenderingResolution(final float resolution) {
        super.setRenderingResolution(resolution);
        final Polyline[] holes = this.holes; // Avoid the need for synchronisation.
        if (holes != null) {
            for (int i=0; i<holes.length; i++) {
                holes[i].setRenderingResolution(resolution);
            }
        }
    }

    /**
     * Returns a path iterator for this polyline.
     */
    @Override
    public synchronized PathIterator getPathIterator(final AffineTransform transform) {
        return new PolygonPathIterator(this, (holes!=null) ? new Iterator(holes) : null, transform);
    }

    /**
     * Returns <code>true</code> if {@link #getPathIterator} returns a flattened iterator.
     * In this case, there is no need to wrap it into a {@link FlatteningPathIterator}.
     */
    @Override
    synchronized boolean checkFlattenedShape() {
        if (super.checkFlattenedShape()) {
            if (holes != null) {
                for (int i=0; i<holes.length; i++) {
                    if (!holes[i].isFlattenedShape()) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns a polygon approximately equal to this polygon clipped to the specified bounds.
     * The clip is only approximative in that the resulting polygon may extend outside the clip
     * area. However, it is guaranteed that the resulting polygon contains at least all the
     * interior of the clip area.
     * <p>
     * If this method can't perform the clip, or if it believes that it isn't worth doing a clip,
     * it returns {@code this}. If this polygon doesn't intersect the clip area, then this
     * method returns {@code null}. Otherwise, a new polygon is created and returned. The new
     * polyline will try to share as much internal data as possible with {@code this} in order
     * to keep memory footprint low.
     *
     * @param  clipper The clip area.
     * @return {@code null} if this polygon doesn't intersect the clip, {@code this}
     *         if no clip has been performed, or a new clipped polygon otherwise.
     */
    @Override
    public synchronized Geometry clip(final Clipper clipper) {
        Polyline clipped = (Polyline) super.clip(clipper);
        if (clipped==this || clipped==null || holes==null) {
            return clipped;
        }
        final Polygon shell = new Polygon((Polyline) clipped);
        assert shell.getUserObject() == getUserObject() : shell;
        assert shell.holes           == holes           : shell;
        /*
         * Clips every holes in the polygons. The clipped holes are stored (for now) in a
         * local array. The local array will be given later to 'shell' only if at least one
         * hole was actually clipped.
         */
        Polyline[] clip = new Polyline[holes.length];
        int count = 0;
        for (int i=0; i<holes.length; i++) {
            clipped = (Polyline) holes[i].clip(clipper);
            if (clipped != null) {
                clip[count++] = clipped;
            }
        }
        if (count == 0) {
            shell.holes = null;
        } else {
            /*
             * At this point, the new clipped polygon ('shell') shares the same array of holes than
             * the original geometry. We will replace the array only if at least one hole is really
             * different (otherwise we will continue to share the same array in order to reduce the
             * memory usage). Note that the loop below works also when count < holes.length, because
             * in such case clip[i] == null during the first execution of the loop, and consequently
             * holes[i] != clip[i], which does the trick.
             */
            for (int i=holes.length; --i>=0;) {
                if (holes[i] != clip[i]) {
                    shell.holes = (Polyline[]) XArrays.resize(clip, count);
                    break;
                }
            }
            assert shell.holes.length == count;
        }
        shell.refreshFlattenedShape();
        return shell;
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
     * Clone this geometry, trying to avoid cloning twice the chlid geometries.
     */
    @Override
    synchronized Object clone(final Map alreadyCloned) {
        final Polygon copy = (Polygon) super.clone();
        if (copy.holes != null) {
            for (int i=copy.holes.length; --i>=0;) {
                copy.holes[i] = (Polyline) copy.holes[i].resolveClone(alreadyCloned);
            }
        }
        return copy;
    }

    /**
     * Compare the specified object with this polygon for equality.
     */
    @Override
    public synchronized boolean equals(final Object object) {
        if (object == this) {
            // Slight optimisation
            return true;
        }
        if (super.equals(object)) {
            final Polygon that = (Polygon) object;
            return Utilities.equals(this.name,  that.name ) &&
                      Arrays.equals(this.holes, that.holes);
        }
        return false;
    }
    
    /**
     * Returns a hash code for this polygon.
     */
    @Override
    public int hashCode() {
        // Do not take the name in account, since it
        // is not a property protected against changes.
        return super.hashCode() ^ (int)serialVersionUID;
    }

    /**
     * Clears all information that was kept in an internal cache.
     */
    @Override
    synchronized void clearCache() {
        if (holes != null) {
            for (int i=0; i<holes.length; i++) {
                holes[i].clearCache();
            }
        }
        super.clearCache(); // Should be last.
    }

    /**
     * Iterator through the polygon's holes.
     */
    private static final class Iterator implements java.util.Iterator {
        /** The holes. */
        private final Polyline[] holes;

        /** Index of the next hole to returns. */
        private int index = 0;

        /** Constructs a new iterator. */
        public Iterator(final Polyline[] holes) {
            this.holes = holes;
        }

        /** Returns <code>true</code> if the iteration has more elements. */
        @Override
        public boolean hasNext() {
            return index < holes.length;
        }
        
        /** Returns the next element in the iteration. */
        @Override
        public Object next() {
            return holes[index++];
        }

        /** Removes from the underlying collection the last element returned by the iterator. */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
