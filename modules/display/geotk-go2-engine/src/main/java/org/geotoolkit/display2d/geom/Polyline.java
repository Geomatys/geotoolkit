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
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Writer;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.geotoolkit.display2d.array.ArrayData;
import org.geotoolkit.display2d.array.PointArray;
import org.geotoolkit.math.Statistics;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotools.resources.Arguments;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.math.XMath;
import org.geotoolkit.display.shape.ShapeUtilities;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Utilities;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.TransformException;


/**
 * A succession of lines linked by their extremities. A polyline is closed if it is part of a
 * {@link Polygon} (either the external ring of one of the holes). Each <code>Polyline</code>
 * object can have its own {@link CoordinateSystem} object, usually specified at construction
 * time.
 *
 * A set of <code>Polyline</code>s can be built from an array of (<var>x</var>,<var>y</var>)
 * coordinates or from a geometric {@linkplain Shape shape} using one of
 * {@link GeometryCollection#add(Shape) GeometryCollection.add(...)} methods. <strong>Points given
 * to those methods should not contain map border.</strong> Border points (orange points in the
 * figure below) are treated specially and must be specified using
 * {@link #appendBorder appendBorder(...)} or {@link #prependBorder prependBorder(...)} methods.
 *
 * <p align="center"><img src="doc-files/borders.png"></p>
 *
 * @source $URL: http://svn.geotools.org/branches/legacy/migrate/src/org/geotools/renderer/geom/Polyline.java $
 * @version $Id: Polyline.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 *
 * @see Polygon
 * @see GeometryCollection
 */
public class Polyline extends Geometry {
    /**
     * Version number for compatibility with geometries serialized with previous versions
     * of this class.
     */
    private static final long serialVersionUID = 4201362804977681771L;

    /**
     * Small number for comparisons (mostly in assertions).
     * Should be in the range of precision of <code>float</code> type.
     */
    private static final double EPS = 1E-6;

    /**
     * Projection to use for calculations that require a Cartesian coordinate system.
     * We prefer "Stereographic" rather than "Mercator", because it can work at poles.
     * (Note that in Geotools implementation, the difference between "Oblique_Stereographic"
     * and "Polar_Stereographic" is just a matter of default value; the correct implementation
     * will be automatically selected according the latitude of origin).
     */
    private static final String CARTESIAN_PROJECTION = "Oblique_Stereographic";

    /**
     * Last coordinate transformation used for computing {@link #coordinateTransform}.
     * Used in order to avoid the costly call to {@link CoordinateSystemFactory} methods
     * when the same transform is requested many consecutive time, which is a very common
     * situation.
     */
    private static CoordinateOperation lastCoordinateTransform =
                    getIdentityTransform(DEFAULT_COORDINATE_SYSTEM);

    /**
     * Un des maillons de la cha�ne de polylignes, ou
     * <code>null</code> s'il n'y a aucune donn�e de
     * m�moris�e.
     */
    private LineString data;

    /**
     * Transformation from coordinate system in use for <code>data</code> to the coordinate
     * system of this <code>Polyline</code>. {@link CoordinateTransformation#getSourceCS}
     * absolutely must be the <code>data</code> coordinate system  (usually fixed once for
     * ever at construction time), whilst {@link CoordinateTransformation#getTargetCS} is
     * the <code>Polyline</code>'s coordinate system, which can be changed at any time.
     * When this polyline uses the same coordinate system as <code>data</code> (which is
     * normally the case), this field will contain an identity transformation.
     * This field can be null if <code>data</code>'s coordinate system is unknown.
     */
    private CoordinateOperation coordinateTransform;

    /**
     * Rectangle completely encompassing all <code>data</code>'s points, or <code>null</code>
     * if it is not yet computed. This rectangle is very useful for quickly spotting features
     * which don't need to be redrawn (for example, when zoomed in on). <strong>The rectangle
     * {@link Rectangle2D} referenced by this field must never be modified</strong>, as it could
     * be shared by several {@link Polyline} objects.
     */
    private transient UnmodifiableRectangle dataBounds;

    /**
     * Rectangle completely encompassing the projected coordinates of this polyline.
     * This field is used as a cache for the {@link #getBounds2D()} method to make it
     * quicker.
     *
     * <strong>The {@link Rectangle2D} rectangle referenced by this field should never be
     * modified</strong>, as it could be shared by several {@link Polyline} objects.
     */
    private transient UnmodifiableRectangle bounds;

    /**
     * <code>true</code> if {@link #getPathIterator} will return a flattened iterator.
     * In this case, there is no need to wrap it into a {@link FlatteningPathIterator}.
     * This field doesn't need to be serialized because it is reasonably fast to compute.
     */
    private transient boolean flattened;

    /**
     * <code>true</code> if this <code>Polyline</code> is a closed ring.
     */
    private boolean isClosed;

    /**
     * <code>true</code> if this polyline has been frozen (see <code>freeze()</code>).
     * Invoking a mutator method like {@link #setResolution} on a frozen geometry
     * will thrown a {@link UnmodifiableGeometryException}.
     */
    private boolean frozen;

    /**
     * The resolution to apply at rendering time.
     * The value 0 means that all data should be used.
     */
    private transient float renderingResolution;

    /**
     * Soft reference to a <code>float[]</code> array. This array is used to
     * keep in memory the points that have already been projected or transformed.
     */
    private transient PolylineCache cache;




    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////                                                                       ////////////
    ////////////    C O N S T R U C T O R S   A N D   F A C T O R Y   M E T H O D S    ////////////
    ////////////                                                                       ////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs a <code>Polyline</code> which is initially empty.
     */
    private Polyline(final CoordinateOperation coordinateTransform) {
        this.coordinateTransform = coordinateTransform;
        if (coordinateTransform != null) {
            CoordinateReferenceSystem crs;
            if ((crs=coordinateTransform.getSourceCRS()).getCoordinateSystem().getDimension() != 2 ||
                (crs=coordinateTransform.getTargetCRS()).getCoordinateSystem().getDimension() != 2)
            {
                throw new IllegalArgumentException(Errors.format(
                           Errors.Keys.CANT_REDUCE_TO_TWO_DIMENSIONS_$1, crs));
            }
        }
        refreshFlattenedShape();
    }

    /**
     * Construct an empty <code>Polyline</code>.
     * Points can be added after the construction with the {@link #append} method.
     *
     * @param coordinateSystem The coordinate system to use for all points in this
     *        <code>Polyline</code>, or <code>null</code> if unknown.
     */
    public Polyline(final CoordinateReferenceSystem coordinateSystem) {
        this(getIdentityTransform(getCoordinateSystem2D(coordinateSystem)));
    }

    /**
     * Construct a new <code>Polyline</code> with the same data as the specified one.
     * The new <code>Polyline</code> will have a copy semantic. However, implementation
     * shares as much internal data as possible in order to reduce memory footprint.
     */
    public Polyline(final Polyline polyline) {
        super(polyline);
        data                = LineString.clone(polyline.data);
        coordinateTransform = polyline.coordinateTransform;
        dataBounds          = polyline.dataBounds;
        bounds              = polyline.bounds;
        flattened           = polyline.flattened;
        isClosed            = polyline.isClosed;
    }

    /**
     * Construct a <code>Polyline</code> with the specified point array.
     * The new <code>Polyline</code> will be empty if the point array was empty.
     *
     * @param data The data to copy in the new <code>Polyline</code>.
     * @param coordinateSystem The data's coordinate system, or <code>null</code> if unknown.
     */
    Polyline(final PointArray data, final CoordinateReferenceSystem coordinateSystem) {
        this(coordinateSystem);
        this.data = new LineString(data);
    }

    /**
     * Construct a closed <code>Polyline</code> with the specified rectangle.
     * The new <code>Polyline</code> will be empty if the rectangle was empty
     * or contains at least one <code>NaN</code> value.
     *
     * @param rectangle Rectangle to copy in the new <code>Polyline</code>.
     * @param coordinateSystem The rectangle's coordinate system,
     *        or <code>null</code> if unknown.
     */
    Polyline(final Rectangle2D rectangle, final CoordinateReferenceSystem coordinateSystem) {
        this(coordinateSystem);
        if (!rectangle.isEmpty()) {
            final float xmin = (float)rectangle.getMinX();
            final float ymin = (float)rectangle.getMinY();
            final float xmax = (float)rectangle.getMaxX();
            final float ymax = (float)rectangle.getMaxY();
            final LineString[] strings = LineString.getInstances(new float[] {
                xmin,ymin,
                xmax,ymin,
                xmax,ymax,
                xmin,ymax
            });
            if (strings.length == 1) {
                // length may be 0 or 2 if some points contain NaN
                data = strings[0];
                isClosed = true;
            }
        }
    }

    /**
     * Constructs <code>Polyline</code>s from specified (<var>x</var>,<var>y</var>) coordinates.
     * <code>NaN</code> values at the beginning and end of <code>data</code> will be ignored.
     * Those that appear in the middle will separate the feature in a number of
     * <code>Polyline</code>s.
     *
     * @param  data Array of (<var>x</var>,<var>y</var>) coordinates points (may contain NaNs).
     *         These data will be copied, in such a way that any future modifications of
     *         <code>data</code> will have no impact on the <code>Polyline</code>s created.
     * @param  lower Index of the first <var>x</var> ordinate to add to the polyline.
     * @param  upper Index after the last <var>y</var> ordinate to add to the polyline.
     * @param  coordinateSystem <code>data</code> point coordinate system.
     *         This argument can be null if the coordinate system is unknown.
     * @return List of <code>Polyline</code> objects. May have 0 length, but will never be null.
     */
    static Polyline[] getInstances(final float[] data, final int lower, final int upper,
                                   final CoordinateReferenceSystem coordinateSystem)
    {
        final LineString[] strings = LineString.getInstances(data, lower, upper);
        final Polyline[] polylines = new Polyline[strings.length];
        final CoordinateOperation ct = getIdentityTransform(coordinateSystem);
        for (int i=0; i<polylines.length; i++) {
            final Polyline polyline = new Polyline(ct);
            polyline.data = strings[i];
            polyline.refreshFlattenedShape();
            polylines[i] = polyline;
        }
        return polylines;
    }

    /**
     * Constructs polylines from the specified geometric shape. If <code>shape</code>
     * is already from the <code>Polyline</code> class, it will be returned in an array of
     * length 1. In all other cases, this method can return an array of 0 length, but never
     * returns <code>null</code>.
     *
     * @param  shape Geometric shape to copy in one or more polylines.
     * @param  coordinateSystem <code>shape</code> point coordinate system.
     *         This argument may be null if the coordinate system is unknown.
     * @return List of <code>Polyline</code> objects. Can have 0 length, but will never be null.
     */
    static Polyline[] getInstances(final Shape shape, CoordinateReferenceSystem coordinateSystem) {
        coordinateSystem = getCoordinateSystem2D(coordinateSystem);
        if (shape instanceof Polyline) {
            return new Polyline[] {(Polyline) shape};
        }
        final CoordinateOperation ct = getIdentityTransform(coordinateSystem);
        final List              polylines = new ArrayList();
        final float[]              buffer = new float[6];
        float[]                     array = new float[64];
        final PathIterator            pit = shape.getPathIterator(null,
                                            ShapeUtilities.getFlatness(shape));
        while (!pit.isDone()) {
            if (pit.currentSegment(array) != PathIterator.SEG_MOVETO) {
                throw new IllegalPathStateException();
            }
            /*
             * Once in this block, the array <code>array</code> already contains
             * the first point at index 0 (for x) and 1 (for y). Now the other points
             * are added so that they correspond to the <code>LINETO</code> instructions.
             */
            int index = 2;
            boolean isClosed = false;
      loop: for (pit.next(); !pit.isDone(); pit.next()) {
                switch (pit.currentSegment(buffer)) {
                    case PathIterator.SEG_LINETO: {
                        if (index >= array.length) {
                            array = XArrays.resize(array, 2*index);
                        }
                        System.arraycopy(buffer, 0, array, index, 2);
                        index += 2;
                        break;
                    }
                    case PathIterator.SEG_MOVETO: {
                        break loop;
                    }
                    case PathIterator.SEG_CLOSE: {
                        isClosed = true;
                        pit.next();
                        break loop;
                    }
                    default: {
                        throw new IllegalPathStateException();
                    }
                }
            }
            /*
             * Construit les polylignes qui correspondent �
             * la forme g�om�trique qui vient d'�tre balay�e.
             */
            final LineString[] strings = LineString.getInstances(array, 0, index);
            for (int i=0; i<strings.length; i++) {
                final Polyline polyline = new Polyline(ct);
                polyline.data = strings[i];
                polyline.refreshFlattenedShape();
                if (isClosed) {
                    polyline.close();
                }
                polylines.add(polyline);
            }
        }
        return (Polyline[]) polylines.toArray(new Polyline[polylines.size()]);
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////                                                                       ////////////
    ////////////          C O O R D I N A T E   S Y S T E M S   S E T T I N G          ////////////
    ////////////                                                                       ////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Same as {@link CTSUtilities#getCoordinateSystem2D}, but wraps the {@link TransformException}
     * into an {@link IllegalArgumentException}. Used for constructors only. Other methods still
     * use the method throwing a transform exception.
     */
    private static CoordinateReferenceSystem getCoordinateSystem2D(final CoordinateReferenceSystem cs)
            throws IllegalArgumentException
    {
        try {
            return CRSUtilities.getCRS2D(cs);
        } catch (TransformException exception) {
            throw new IllegalArgumentException(exception.getLocalizedMessage());
        }
    }

    /**
     * Returns the native coordinate system of {@link #data}'s points, or <code>null</code>
     * if unknown.
     */
    private CoordinateReferenceSystem getInternalCRS() {
        // copy 'coordinateTransform' reference in order to avoid synchronization
        final CoordinateOperation coordinateTransform = this.coordinateTransform;
        return (coordinateTransform!=null) ? coordinateTransform.getSourceCRS() : null;
    }

    /**
     * Returns the polyline's coordinate system, or <code>null</code> if unknown.
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        // copy 'coordinateTransform' reference in order to avoid synchronization
        final CoordinateOperation coordinateTransform = this.coordinateTransform;
        return (coordinateTransform!=null) ? coordinateTransform.getTargetCRS() : null;
    }

    /**
     * Returns the transform from coordinate system used by {@link #data} to the specified
     * coordinate system. If at least one of the coordinate systems is unknown, this method
     * returns <code>null</code>.
     *
     * @throws CannotCreateTransformException If the transform cannot be created.
     */
    final CoordinateOperation getTransformationFromInternalCRS(final CoordinateReferenceSystem crs){
        // copy 'coordinateTransform' reference in order to avoid synchronization
        CoordinateOperation ct = coordinateTransform;
        if (crs!=null && ct!=null) {
            if (crs.equals(ct.getTargetCRS())) {
                return ct;
            }
            final CoordinateReferenceSystem internalCS = ct.getSourceCRS();
            ct = lastCoordinateTransform;
            if (crs.equals(ct.getTargetCRS())) {
                if (equivalents(ct.getSourceCRS(), internalCS)) {
                    return ct;
                }
            }
            
            try {
                ct = getCoordinateTransformation(internalCS, crs);
            } catch (OperationNotFoundException ex) {
                Logger.getLogger(Polyline.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FactoryException ex) {
                Logger.getLogger(Polyline.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            lastCoordinateTransform = ct;
            return ct;
        }
        return null;
    }

    /**
     * Returns a math transform for the specified transformations.
     * If no transformation is available, or if it is the identity
     * transform, then this method returns <code>null</code>. This
     * method accepts null argument.
     */
    static MathTransform2D getMathTransform2D(final CoordinateOperation transformation) {
        if (transformation != null) {
            final MathTransform transform = transformation.getMathTransform();
            if (!transform.isIdentity()) {
                return (MathTransform2D) transform;
            }
        }
        return null;
    }

    /**
     * Sets the polyline's coordinate system. Calling this method is equivalent
     * to reprojecting all polyline's points from the old coordinate system to the
     * new one.
     *
     * @param  coordinateSystem The new coordinate system. A <code>null</code> value resets the
     *         coordinate system given at construction time.
     * @throws TransformException If a transformation failed. In case of failure,
     *         the state of this object will stay unchanged (as if this method has
     *         never been invoked).
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    @Override
    public synchronized void setCoordinateReferenceSystem(CoordinateReferenceSystem coordinateSystem)
            throws TransformException, UnmodifiableGeometryException
    {
        if (frozen) {
            throw new UnmodifiableGeometryException((Locale)null);
        }
        // Do not use 'Polyline.getCoordinateSystem2D', since
        // we want a 'TransformException' in case of failure.
        coordinateSystem = CRSUtilities.getCRS2D(coordinateSystem);
        if (coordinateSystem == null) {
            coordinateSystem = getInternalCRS();
            // May still null. Its ok.
        }
        if (Utilities.equals(coordinateSystem, getCoordinateReferenceSystem())) {
            return;
        }
        CoordinateOperation transformCandidate =
                getTransformationFromInternalCRS(coordinateSystem);
        if (transformCandidate == null) {
            transformCandidate = getIdentityTransform(coordinateSystem);
        }
        /*
         * Compute bounds now. The getBounds2D(...) method scans every point.
         * Consequently, if an exception must be thrown, it will be thrown now.
         */
        bounds = new UnmodifiableRectangle(LineString.getBounds2D(data,
                                (transformCandidate==null) ? null :
                                (MathTransform2D)transformCandidate.getMathTransform()));
        /*
         * Store the new coordinate transform
         * only after projection has succeeded.
         */
        this.coordinateTransform = transformCandidate;
        this.cache = null;
        refreshFlattenedShape();
        assert Utilities.equals(coordinateSystem, getCoordinateReferenceSystem());
    }

    /**
     * Indicates whether the specified transform is the identity transform.
     * A null transform (<code>null</code>) is considered to be an identity transform.
     */
    private static boolean isIdentity(final CoordinateOperation coordinateTransform) {
        return coordinateTransform==null || coordinateTransform.getMathTransform().isIdentity();
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////                                                                       ////////////
    ////////////        M O D I F I E R S :   append / prepend   M E T H O D S         ////////////
    ////////////                                                                       ////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Adds points to the start of this polyline. These points will be considered to 
     * form part of the map border, and not considered as points representing
     * a geographic structure.
     *
     * @param  border Coordinates to add as (x,y) number pairs.
     * @param  lower Index of the first <var>x</var> ordinate to add to the border.
     * @param  upper Index after the last <var>y</var> ordinate to add to the border.
     * @throws TransformException if <code>border</code> contains points that are invalid
     *         for this polyline's native coordinate system.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     * @throws IllegalStateException if this polyline has already been closed.
     */
    public void prependBorder(final float[] border, final int lower, final int upper)
            throws TransformException, IllegalStateException
    {
        prependBorder(border, lower, upper, getCoordinateReferenceSystem());
    }

    /**
     * Adds points to the end of this polyline. These points will be considered to
     * form part of the map border, and not considered as points representing 
     * a geographic structure.
     *
     * @param  border Coordinates to add as (x,y) number pairs.
     * @param  lower Index of the first <var>x</var> ordinate to add to the border.
     * @param  upper Index after the last <var>y</var> ordinate to add to the border.
     * @throws TransformException if <code>border</code> contains points that are invalid
     *         for this polyline's native coordinate system.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     * @throws IllegalStateException if this polyline has already been closed.
     */
    public void appendBorder(final float[] border, final int lower, final int upper)
            throws TransformException, IllegalStateException
    {
        appendBorder(border, lower, upper, getCoordinateReferenceSystem());
    }

    /**
     * Prepends a border expressed in an arbitrary coordinate system.
     * If <code>cs</code> is null, then the internal CS is assumed.
     */
    final void prependBorder(final float[] border, final int lower, final int upper,
                             final CoordinateReferenceSystem cs)
            throws TransformException, IllegalStateException
    {
        addBorder(border, lower, upper, cs, false);
    }

    /**
     * Appends a border expressed in an arbitrary coordinate system.
     * If <code>cs</code> is null, then the internal CS is assumed.
     */
    final void appendBorder(final float[] border, final int lower, final int upper,
                             final CoordinateReferenceSystem cs)
            throws TransformException, IllegalStateException
    {
        addBorder(border, lower, upper, cs, true);
    }

    /**
     * Implementation of <code>appendBorder(...)</code> and <code>prependBorder(...)</code>.
     *
     * @param cs The border coordinate system, or <code>null</code> for the internal CS.
     * @param append <code>true</code> to carry out the operation <code>appendBorder</code>, or
     *               <code>false</code> to carry out the operation <code>prependBorder</code>.
     */
    private synchronized void addBorder(float[] border, int lower, int upper,
                                        final CoordinateReferenceSystem cs, final boolean append)
            throws TransformException, IllegalStateException
    {
        if (frozen) {
            throw new UnmodifiableGeometryException((Locale)null);
        }
        if (isClosed) {
            throw new IllegalStateException(Errors.format(Errors.Keys.POLYGON_CLOSED));
        }
        MathTransform2D transform = getMathTransform2D(getTransformationFromInternalCRS(cs));
        if (transform != null) {
            final float[] oldBorder = border;
            border = new float[upper-lower];
            transform.inverse().transform(oldBorder, lower, border, 0, border.length);
            lower = 0;
            upper = border.length;
        }
        if (append) {
            data = LineString.appendBorder(data, border, lower, upper);
        } else {
            data = LineString.prependBorder(data, border, lower, upper);
        }
        refreshFlattenedShape();
        dataBounds = null;
        bounds     = null;
        cache      = null;
        // No change to resolution, since it doesn't take border into account.
    }

    /**
     * Adds the specified coordinate points to the end of this polyline.
     *
     * @param  points Array of (<var>x</var>,<var>y</var>) coordinates points.
     *         These data will be copied, in such a way that any future modifications of
     *         <code>data</code> will have no impact on the <code>Polyline</code>s created.
     * @param  lower Index of the first <var>x</var> ordinate to add to the polyline.
     * @param  upper Index after the last <var>y</var> ordinate to add to the polyline.
     * @throws TransformException if <code>points</code> contains points that are invalid
     *         for this polyline's native coordinate system.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     * @throws IllegalStateException if this polyline has already been closed.
     */
    public synchronized void append(final float[] points, final int lower, final int upper)
            throws TransformException, IllegalStateException
    {
        if (points != null) {
            final Polyline[] polylines = getInstances(points, lower, upper, getCoordinateReferenceSystem());
            for (int i=0; i<polylines.length; i++) {
                append(polylines[i]);
            }
        }
    }

    /**
     * Adds to the end of this polyline the data of the specified polyline.
     * This method does nothing if <code>toAppend</code> is null.
     *
     * @param  toAppend <code>Polyline</code> to add to the end of <code>this</code>.
     *         The polyline <code>toAppend</code> will not be modified.
     * @throws IllegalStateException if this polyline has already been closed.
     * @throws TransformException if <code>toAppend</code> contains points that are invalid
     *         for this polyline's native coordinate system.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     * @throws IllegalArgumentException if the polyline <code>toAppend</code> has already been closed.
     */
    public synchronized void append(final Polyline toAppend)
            throws TransformException, IllegalStateException
    {
        if (frozen) {
            throw new UnmodifiableGeometryException((Locale)null);
        }
        if (isClosed || toAppend.isClosed) {
            throw new IllegalStateException(Errors.format(Errors.Keys.POLYGON_CLOSED));
        }
        if (toAppend == null) {
            return;
        }
        if (!equivalents(getInternalCRS(), toAppend.getInternalCRS())) {
            throw new TransformException("Transformation not yet implemented"); // TODO.
        }
        data = LineString.append(data, LineString.clone(toAppend.data));
        if (dataBounds != null) {
            if (toAppend.dataBounds != null) {
                // Instead of recomputing all the bounds, just add the two rectangles.
                // Try to keep the existing references if possible.
                final XRectangle2D rect = new XRectangle2D(dataBounds);
                rect.add(toAppend.dataBounds);
                if (!rect.equals(dataBounds)) {
                    if (rect.equals(toAppend.dataBounds)) {
                        dataBounds = toAppend.dataBounds;
                    } else {
                        dataBounds = new UnmodifiableRectangle(rect);
                    }
                }
                assert equalsEps(dataBounds, getDataBounds()) : dataBounds;
            } else {
                dataBounds = null;
            }
        }
        bounds    = null;
        cache     = null;
        refreshFlattenedShape();
    }

    /**
     * Reverse point order in this polyline.
     */
    public synchronized void reverse() {
        data  = LineString.reverse(data);
        cache = null;
        refreshFlattenedShape();
    }
    
    /**
     * Returns a polyline with the point of this polyline from <code>lower</code>
     * inclusive to <code>upper</code> exclusive. The returned polyline may not be
     * closed. If no data are available in the specified range, this method returns
     * <code>null</code>.
     */
    public synchronized Polyline subpoly(final int lower, final int upper) {
        final LineString sub = LineString.subpoly(data, lower, upper);
        if (sub == null) {
            return null;
        }
        if (LineString.equals(sub, data)) {
            freeze();
            return this;
        }
        final Polyline subPoly = new Polyline(coordinateTransform);
        subPoly.data = sub;
        subPoly.refreshFlattenedShape();
        assert subPoly.getPointCount() == (upper-lower);
        return subPoly;
    }

    /**
     * Returns a polyline with the point of this polyline from <code>lower</code>
     * inclusive to the end. The returned polyline may not be closed. If no data
     * are available in the specified range, this method returns <code>null</code>.
     */
    final synchronized Polyline subpoly(final int lower) {
        return subpoly(lower, getPointCount());
    }

    /**
     * Close this polyline. After closing it, no more points can be added to this polyline.
     */
    public synchronized void close() {
        data     = LineString.freeze(data, true, null);
        isClosed = true;
        cache    = null;
        refreshFlattenedShape();
    }

    /**
     * Returns whether this polyline is closed or not. A closed
     * polyline is usually a {@link Polygon} instance.
     */
    public boolean isClosed() {
        return isClosed;
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////                                                                       ////////////
    ////////////           A C C E S S O R S :   'getPoints'   M E T H O D S           ////////////
    ////////////                                                                       ////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Test if this polyline is empty. An empty polyline contains no points.
     *
     * @see #getPointCount
     */
    @Override
    public boolean isEmpty() {
        if (!frozen) {
            // synchronize only if the shape is mutable.
            synchronized (this) {
                return LineString.isEmpty(data);
            }
        }
        return LineString.isEmpty(data);
    }

    /**
     * Add to the specified collection all non-empty {@link Polyline} objects making this
     * geometry. This method is used by {@link GeometryCollection#getPathIterator} and
     * {@link PolygonAssembler} only.
     */
    @Override
    void getPolylines(final Collection polylines) {
        freeze(); // Because this method is usually invoked from a container.
        if (!isEmpty()) {
            polylines.add(this);
        }
    }

    /**
     * Returns an estimation of memory usage in bytes. This method is for information
     * purposes only. The memory really used by two polylines may be lower than the sum
     * of their  <code>getMemoryUsage()</code>  return values, since polylgons try to
     * share their data when possible. Furthermore, this method does not take into account
     * the extra bytes generated by Java Virtual Machine for each objects.
     *
     * @return An <em>estimation</em> of memory usage in bytes.
     */
    @Override
    synchronized long getMemoryUsage() {
        return LineString.getMemoryUsage(data) + 50;
    }

    /**
     * Returns the number of points in the cache. This method is invoked only for statistics
     * purpose after a rendering. The number of points is the absolute value of the returned
     * value. A positive value means that the cache has been reused. A negative value means
     * that the cache has been flushed and recomputed.
     */
    public int getCachedPointCount() {
        final PolylineCache cache = this.cache;
        if (cache == null) {
            return 0;
        }
        return cache.getPointCount();
    }

    /**
     * Return the number of points in this polyline.
     *
     * @see #isEmpty
     * @see #getPoints
     * @see #getFirstPoint
     * @see #getFirstPoints
     * @see #getLastPoint
     * @see #getLastPoints
     * @see #toArray
     */
    @Override
    public int getPointCount() {
        if (!frozen) {
            // synchronize only if the shape is mutable.
            synchronized (this) {
                return LineString.getPointCount(data);
            }
        }
        return LineString.getPointCount(data);
    }

    /**
     * Returns all polyline's points. Point coordinates are stored in {@link Point2D}
     * objects using this polyline's coordinate system ({@link #getCoordinateSystem}).
     * This method returns an immutable collection: changes done to <code>Polyline</code>
     * after calling this method will not affect the collection. Despite the fact that
     * this method has a copy semantic, the collection will share many internal structures
     * in such a way that memory consumption should stay low.
     *
     * @return The polyline's points as a collection of {@link Point2D} objects.
     *
     * @see #getFirstPoint
     * @see #getFirstPoints
     * @see #getLastPoint
     * @see #getLastPoints
     */
    public synchronized Collection getPoints() {
        return new LineString.Collection(LineString.clone(data),
                                         getMathTransform2D(coordinateTransform));
    }

    /**
     * Returns an iterator for this polyline's internal points.
     * Points are projected in the specified coordinate system.
     *
     * @param  cs The destination coordinate system, or <code>null</code>
     *            for this polyline's native coordinate system.
     * @return An iterator for points in the specified coordinate system.
     * @throws CannotCreateTransformException if a transformation can't be constructed.
     */
    final LineString.Iterator iterator(final CoordinateReferenceSystem cs) {
        assert frozen || Thread.holdsLock(this) : frozen;
        return new LineString.Iterator(data, getMathTransform2D(getTransformationFromInternalCRS(cs)));
    }

    /**
     * Stores the value of the first point into the specified point object.
     *
     * @param  point Object in which to store the unprojected coordinate.
     * @return <code>point</code>, or a new {@link Point2D} if <code>point</code> was null.
     * @throws NoSuchElementException If this polyline contains no point.
     *
     * @see #getFirstPoints(Point2D[])
     * @see #getLastPoint(Point2D)
     */
    public synchronized Point2D getFirstPoint(Point2D point) throws NoSuchElementException {
        point = LineString.getFirstPoint(data, point);
        final MathTransform2D transform = getMathTransform2D(coordinateTransform);
        if (transform!=null) try {
            point = transform.transform(point, point);
        } catch (TransformException exception) {
            // Should not happen, since {@link #setCoordinateSystem}
            // has already successfully projected every points.
            unexpectedException("getFirstPoint", exception);
        }
        assert !Double.isNaN(point.getX()) && !Double.isNaN(point.getY());
        return point;
    }

    /**
     * Stores the value of the last point into the specified point object.
     *
     * @param  point Object in which to store the unprojected coordinate.
     * @return <code>point</code>, or a new {@link Point2D} if <code>point</code> was null.
     * @throws NoSuchElementException If this polyline contains no point.
     *
     * @see #getLastPoints(Point2D[])
     * @see #getFirstPoint(Point2D)
     */
    public synchronized Point2D getLastPoint(Point2D point) throws NoSuchElementException {
        point = LineString.getLastPoint(data, point);
        final MathTransform2D transform = getMathTransform2D(coordinateTransform);
        if (transform!=null) try {
            point = transform.transform(point, point);
        } catch (TransformException exception) {
            // Should not happen, since {@link #setCoordinateSystem}
            // has already successfully projected every point.
            unexpectedException("getLastPoint", exception);
        }
        assert !Double.isNaN(point.getX()) && !Double.isNaN(point.getY());
        return point;
    }

    /**
     * Stores the values of <code>points.length</code> first points into the specified array.
     *
     * @param points An array to fill with first polyline's points. <code>points[0]</code>
     *               will contains the first point, <code>points[1]</code> the second point,
     *               etc.
     *
     * @throws NoSuchElementException If this polyline doesn't contain enough points.
     */
    public synchronized void getFirstPoints(final Point2D[] points) throws NoSuchElementException {
        LineString.getFirstPoints(data, points);
        final MathTransform2D transform = getMathTransform2D(coordinateTransform);
        if (transform!=null) try {
            for (int i=0; i<points.length; i++) {
                points[i] = transform.transform(points[i], points[i]);
                assert !Double.isNaN(points[i].getX()) && !Double.isNaN(points[i].getY());
            }
        } catch (TransformException exception) {
            // Should not happen, since {@link #setCoordinateSystem}
            // has already successfully projected every point.
            unexpectedException("getFirstPoints", exception);
        }
        assert points.length==0 || Utilities.equals(getFirstPoint(null), points[0]);
    }

    /**
     * Stores the values of <code>points.length</code> last points into the specified array.
     *
     * @param points An array to fill with last polyline's points.
     *               <code>points[points.length-1]</code> will contains the last point,
     *               <code>points[points.length-2]</code> the point before the last one, etc.
     *
     * @throws NoSuchElementException If this polyline doesn't contain enough points.
     */
    public synchronized void getLastPoints(final Point2D[] points) throws NoSuchElementException {
        LineString.getLastPoints(data, points);
        final MathTransform2D transform = getMathTransform2D(coordinateTransform);
        if (transform!=null) try {
            for (int i=0; i<points.length; i++) {
                points[i] = transform.transform(points[i], points[i]);
                assert !Double.isNaN(points[i].getX()) && !Double.isNaN(points[i].getY());
            }
        } catch (TransformException exception) {
            // Should not happen, since {@link #setCoordinateSystem}
            // has already successfully projected every point.
            unexpectedException("getLastPoints", exception);
        }
        assert points.length==0 || Utilities.equals(getLastPoint(null), points[points.length-1]);
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
     * Return the bounding box of this polyline, including its possible
     * borders. This method uses a cache, such that after a first calling,
     * the following calls should be fairly quick.
     *
     * @return A bounding box of this polyline. Changes to the
     *         fields of this rectangle will not affect the cache.
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
     * Returns a rectangle encompassing all {@link #data}'s points. Because this method
     * returns the rectangle directly from the cache and not a copy, the returned rectangle
     * should never be modified (and is immutable anyway).
     *
     * @return A rectangle encompassing all {@link #data}'s points.
     *         This rectangle may be empty, but will never be null.
     */
    final Rectangle2D getDataBounds() {
        // assert Thread.holdsLock(this);
        // Can't make this assertion, because this method is invoked
        // by {@link #getCachedBounds}. See later for details.

        if (dataBounds == null) {
            dataBounds = new UnmodifiableRectangle(getBounds(data, null));
            if (isIdentity(coordinateTransform)) {
                bounds = dataBounds; // Avoid computing the same rectangle twice
            }
        }
        assert equalsEps(getBounds(data, null), dataBounds) : dataBounds;
        return dataBounds;
    }

    /**
     * Return the bounding box of this polyline.
     */
    private Rectangle2D getCachedBounds() {
        assert frozen || Thread.holdsLock(this) : frozen;
        if (bounds == null) {
            bounds = new UnmodifiableRectangle(getBounds(data, coordinateTransform));
            if (isIdentity(coordinateTransform)) {
                dataBounds = bounds; // Avoid computing the same rectangle twice
            }
        }
        assert equalsEps(getBounds(data, coordinateTransform), bounds) : bounds;
        return bounds;
    }

    /**
     * Returns a rectangle encompassing all the points projected in the specified coordinate system.
     * This method will try to return one of the rectangles from the internal cache when appropriate.
     * Because this method can return the rectangle directly from the cache and not a copy, the
     * returned rectangle should never be modified.
     *
     * @param  The coordinate system according to which the points should be projected.
     * @return A rectangle encompassing all the points of this polyline.
     *         This rectangle may be empty, but will never be null.
     * @throws TransformException if a cartographic projection fails.
     */
    private Rectangle2D getCachedBounds(final CoordinateReferenceSystem coordinateSystem)
            throws TransformException
    {
        // assert Thread.holdsLock(this);
        // Can't make this assertion, because {@link #intersects(Polyline,boolean)} invokes
        // this method without synchronization on this polyline. In doesn't hurt as long as
        // {@link #intersectsPolyline} and {@link #intersectsEdge} are private methods.

        if (equivalents(getInternalCRS(),       coordinateSystem)) return getDataBounds();
        if (equivalents(getCoordinateReferenceSystem(), coordinateSystem)) return getCachedBounds();
        Rectangle2D bounds = LineString.getBounds2D(data, getMathTransform2D(coordinateTransform));
        if (bounds == null) {
            bounds = new Rectangle2D.Float();
        }
        return bounds;
    }

    /**
     * Returns a rectangle encompassing all <code>data</code>'s points.  This method should
     * only be called in a context where it is known that the cartographic projection 
     * should never fail.
     *
     * @param  data One of the links in the chain of <code>PointArray</code>s (may be null).
     * @param  coordinateTransform Transform to apply on <code>data</code>'s points.
     * @return A rectangle encompassing all <code>data</code>'s points.
     *         This rectangle may be empty, but will never be null.
     */
    private static Rectangle2D getBounds(final LineString data,
                                         final CoordinateOperation coordinateTransform)
    {
        Rectangle2D bounds;
        try {
            bounds = LineString.getBounds2D(data, getMathTransform2D(coordinateTransform));
            if (bounds == null) {
                assert LineString.getPointCount(data) == 0;
                bounds = new Rectangle2D.Float();
            }
        } catch (TransformException exception) {
            // Should not happen, since {@link #setCoordinateSystem}
            // has already successfully projected every point.
            unexpectedException("getBounds2D", exception);
            bounds = null; // Make the compiler happy, but should never reach this point.
        }
        return bounds;
    }

    /**
     * Check if two rectangles are almost equal (except for an epsilon value).  If one or
     * both arguments are <code>null</code>, then this method does nothing. This method occurs
     * when one rectangle comes from the cache and hasn't been computed yet.  This method is
     * used for assertions only.
     */
    private static boolean equalsEps(final Rectangle2D expected, final Rectangle2D actual) {
        if (expected==null || actual==null) {
            return true;
        }
        final double eps = EPS * Math.hypot(expected.getCenterX(), expected.getCenterY());
        return Math.abs(expected.getMinX() - actual.getMinX()) <= eps &&
               Math.abs(expected.getMinY() - actual.getMinY()) <= eps &&
               Math.abs(expected.getMaxX() - actual.getMaxX()) <= eps &&
               Math.abs(expected.getMaxY() - actual.getMaxY()) <= eps;
    }

    /**
     * Indicates whether the specified (<var>x</var>,<var>y</var>) coordinate is inside this
     * polyline. The polyline should have been closed before the call to this method (see
     * {@link #close}).
     *
     * @param  x <var>x</var> coordinate of the point to test.
     * @param  y <var>y</var> coordinate of the point to test.
     * @param  transformation Transform to use for converting {@link #data}'s points,
     *         or <code>null</code> if no transform is required. If a non-null transform
     *         is specified, it should have been obtained by a call to the method 
     *         <code>getTransformationFromInternalCS(targetCS)</code>. All the polyline's points
     *         will then be projected according to the <code>targetCS</code> coordinate system. Where
     *         possible, it is more efficient to calculate only the inverse projection of point
     *         (<var>x</var>,<var>y</var>) and to specify <code>null</code> for this argument.
     * @return <code>true</code> if the point is inside this polyline.
     *
     * @author Andr� Gosselin (original C version)
     * @author Martin Desruisseaux (Java adaptation)
     */
    private boolean contains(final float x, final float y,
                             final CoordinateOperation transformation)
    {
        assert isClosed;
        /*
         * Imagine a straight line starting at point (<var>x</var>,<var>y</var>)
         * and going to infinity to the right of this point (i.e., towards the <var>x</var>
         * positive axis). We count the number of times the polyline intercepts this line.
         * If the number is odd, the point is inside the polyline. The variable <code>nInt</code>
         * will do this counting.
         */
        int   nInt                   = 0;
        int   intSuspended           = 0;
        int   nPointsToRecheck       = 0;
        final Point2D.Float nextPt   = new Point2D.Float();
        final LineString.Iterator it = new LineString.Iterator(data, getMathTransform2D(transformation));
        float x1                     = Float.NaN;
        float y1                     = Float.NaN;
        /*
         * Extracts a first point.  There will be a problem in the following algorithm if the
         * first point is on the same horizontal line as the point to check.
         * To solve the problem, we look for the first point that isn't on the same horizontal
         * line.
         */
        while (true) {
            final float x0=x1;
            nPointsToRecheck++;
            if (it.next(nextPt) == null) {
                return false;
            }
            x1 = nextPt.x;
            y1 = nextPt.y;
            if (y1 != y) break;
            /*
             * Checks whether the point falls exactly on the
             * segment (x0,y0)-(x1-y1). If it does,
             * it's not worth going any further.
             */
            if (x0 < x1) {
                if (x>=x0 && x<=x1) return true;
            } else {
                if (x>=x1 && x<=x0) return true;
            }
        }
        /*
         * Sweeps through all the points of the polyline. When the last point is extracted
         * the variable <code>count</code> is adjusted so that only the points that need
         * passing through again are 're-swept'.
         */
        for (int count=-1; count!=0; count--) {
            /*
             * Obtains the following point.  If we have reached the end of the polyline,
             * we reclose the polyline if this has not already been done.
             * If the polyline had already been reclosed, that is the end of the loop.
             */
            final float x0 = x1;
            final float y0 = y1;
            if (it.next(nextPt) == null) {
                count = nPointsToRecheck+1;
                nPointsToRecheck = 0;
                it.rewind();
                continue;
            }
            x1 = nextPt.x;
            y1 = nextPt.y;
            /*
             * We now have a right-hand segment going from the coordinates
             * (<var>x0</var>,<var>y0</var>) to (<var>x1</var>,<var>y1</var>).
             * If we realise that the right-hand segment is completely above or completely
             * below the point (<var>x</var>,<var>y</var>), we know that there is no right-hand
             * intersection and we continue the loop.
             */
            if (y0 < y1) {
                if (y<y0 || y>y1) continue;
            } else {
                if (y<y1 || y>y0) continue;
            }
            /*
             * We now know that our segment passes either to the right or the left of our point.
             * We now calculate the coordinate <var>xi</var> where the intersection takes place
             * (with the horizontal right passing through our point).
             */
            final float dy = y1-y0;
            final float xi = x0 + (x1-x0)*(y-y0)/dy;
            if (!Float.isInfinite(xi) && !Float.isNaN(xi)) {
                /*
                 * If the intersection is completely to the left of the point, there is
                 * evidently no intersection to the right and we continue the loop. Otherwise,
                 * if the intersection occurs precisely at the coordinate <var>x</var> (which
                 * is unlikely...), this means our point is exactly on the border of the
                 * polyline and the treatment ends.
                 */
                if (x >  xi) continue;
                if (x == xi) return true;
            } else {
                /*
                 * There is a special treatment if the segment is horizontal. The value
                 * <var>xi</var> isn't valid (we can visualize that as if intersections were 
                 * found all over the right-hand side rather than on a single point). Instead
                 * of performing checks with <var>xi</var>, we will do them with the minimum and
                 * maximum <var>x</var>s of the segment.
                 */
                if (x0 < x1) {
                    if (x >  x1) continue;
                    if (x >= x0) return true;
                } else {
                    if (x >  x0) continue;
                    if (x >= x1) return true;
                }
            }
            /*
             * We now know that there is an intersection on the right.  In principal, it
             * would be sufficient to increment 'nInt'.  However, we should pay particular
             * attention to the case where <var>y</var> is at exactly the same height as one of the
             * extremities of the segment.  Is there an intersection or not?  That depends on
             * whether the following segments continue in the same direction or not.  We adjust
             * a flag, so that the decision to increment 'nInt' or not is taken later in the loop
             * when the other segments have been examined.
             */
            if (x0==x1 && y0==y1) {
                continue;
            }
            if (y==y0 || y==y1) {
                final int sgn=XMath.sgn(dy);
                if (sgn != 0) {
                    if (intSuspended!=0) {
                        if (intSuspended==sgn) nInt++;
                        intSuspended=0;
                    } else {
                        intSuspended=sgn;
                    }
                }
            }
            else nInt++;
        }
        /*
         * If the number of intersections to the right of the point is odd,
         * the point is inside the polyline. Otherwise, it is outside.
         */
        return (nInt & 1)!=0;
    }

    /**
     * Indicates whether the specified (<var>x</var>,<var>y</var>) coordinate is inside
     * this polyline.  The point's coordinates must be expressed according to the polyline's
     * coordinate system, that is {@link #getCoordinateSystem()}. The polyline must also
     * have been closed before the call to this method (see {@link #close}), if it wasn't
     * this method will always return <code>false</code>.
     */
    @Override
    public synchronized boolean contains(double x, double y) {
        if (!isClosed) {
            return false;
        }
        /*
         * IMPLEMENTATION NOTE: The polyline's native point array ({@link #data}) and the
         * (x,y) point may use different coordinate systems. For efficiency reasons, the
         * (x,y) point is projected to the "native" polyline's coordinate system instead
         * of projecting all polyline's points. As a result, points very close to the polyline's
         * edge may appear inside (when viewed on screen) while this method returns <code>false</code>,
         * and vice-versa. This is because some projections transform straight lines
         * into curves, but the Polyline class ignores curves and always uses straight
         * lines between any two points.
         */
        if (coordinateTransform!=null) try {
            final MathTransform transform = coordinateTransform.getMathTransform();
            if (!transform.isIdentity()) {
                Point2D point = new Point2D.Double(x,y);
                point = ((MathTransform2D) transform.inverse()).transform(point, point);
                x = point.getX();
                y = point.getY();
            }
        } catch (TransformException exception) {
            // If the projection fails, the point is probably outside the polyline
            // (since all the polyline's points are projectable).
            return false;
        }
        /*
         * First we check whether the rectangle 'dataBounds' contains
         * the point, before calling the costly method 'contains'.
         */
        return getDataBounds().contains(x,y) && contains((float)x, (float)y, null);
    }

    /**
     * Checks whether a point <code>pt</code> is inside this polyline. The point's coordinates
     * must be expressed according to the polyline's coordinate system, that is
     * {@link #getCoordinateSystem()}. The polyline must also have been closed before the call
     * to this method (see {@link #close}), if it wasn't this method will always return
     * <code>false</code>.
     */
    @Override
    public boolean contains(final Point2D pt) {
        return contains(pt.getX(), pt.getY());
    }

    /**
     * Test if the interior of this polyline entirely contains the given rectangle.
     * The rectangle's coordinates must be expressed in this polyline's coordinate
     * system (as returned by {@link #getCoordinateSystem}).
     */
    @Override
    public synchronized boolean contains(final Rectangle2D rect) {
        return containsPolyline(new Polyline(rect, getCoordinateReferenceSystem()));
    }

    /**
     * Test if the interior of this polyline entirely contains the given shape.
     */
    @Override
    public synchronized boolean contains(final Shape shape) {
        if (shape instanceof Polyline) {
            return containsPolyline((Polyline) shape);
        }
        final Polyline[] polylines = getInstances(shape, getCoordinateReferenceSystem());
        for (int i=0; i<polylines.length; i++) {
            if (!containsPolyline(polylines[i])) {
                return false;
            }
        }
        return polylines.length != 0;
    }

    /**
     * Test if the interior of this polyline entirely contains the given polyline.
     */
    boolean containsPolyline(final Polyline shape) {
        /*
         * This method returns <code>true</code> if this polyline contains at least
         * one point of <code>shape</code> and there is no intersection 
         * between <code>shape</code> and <code>this</code>.
         */
        if (isClosed) try {
            final CoordinateReferenceSystem coordinateSystem = getInternalCRS();
            if (getDataBounds().contains(shape.getCachedBounds(coordinateSystem))) {
                final Point2D.Float  firstPt = new Point2D.Float();
                final  Line2D.Float  segment = new  Line2D.Float();
                final LineString.Iterator it = new LineString.Iterator(shape.data,
                                          shape.getMathTransform2D(
                                          shape.getTransformationFromInternalCRS(coordinateSystem)));
                // Note: call to contains(...) must NOT call the method overriden in Polygon.
                if (it.next(firstPt)!=null && contains(firstPt.x, firstPt.y, null)) {
                    segment.x2 = firstPt.x;
                    segment.y2 = firstPt.y;
                    do if (!it.next(segment)) {
                        if (!shape.isClosed || isSingular(segment)) {
                            return true;
                        }
                        segment.x2 = firstPt.x;
                        segment.y2 = firstPt.y;
                    } while (!intersects(segment));
                }
            }
        } catch (TransformException exception) {
            // Conservatively returns 'false' if some points from 'shape' can't be projected into
            // {@link #data}'s coordinate system.  This behavior is compliant with the Shape
            // specification. Futhermore, those points are probably outside this polyline since
            // all polyline's points are projectable.
        }
        return false;
    }

    /**
     * Indicates whether or not the points (x1,y1) and (x2,y2)
     * from the specified line are identical.
     */
    private static boolean isSingular(final Line2D.Float segment) {
        return Float.floatToIntBits(segment.x1)==Float.floatToIntBits(segment.x2) &&
               Float.floatToIntBits(segment.y1)==Float.floatToIntBits(segment.y2);
    }

    /**
     * Determines whether the line <code>line</code> intercepts one of this polyline's lines.
     * The polyline will automatically be reclosed if necessary;
     * it is therefore not necessary for the last point to repeat the first.
     *
     * @param  line Line we want to check to see if it intercepts this polyline.
     *         This line absolutely must be expressed according to the native coordinate system
     *         of {@link #array}, i.e. {@link #getInternalCS}.
     * @return <code>true</code> if the line <code>line</code> intercepts this polyline.
     */
    private boolean intersects(final Line2D line) {
        final Point2D.Float  firstPt = new Point2D.Float();
        final  Line2D.Float  segment = new  Line2D.Float();
        final LineString.Iterator it = new LineString.Iterator(data, null); // Ok even if 'data' is null.
        if (it.next(firstPt) != null) {
            segment.x2 = firstPt.x;
            segment.y2 = firstPt.y;
            do if (!it.next(segment)) {
                if (!isClosed || isSingular(segment)) {
                    return false;
                }
                segment.x2 = firstPt.x;
                segment.y2 = firstPt.y;
            } while (!segment.intersectsLine(line));
            return true;
        }
        return false;
    }

    /**
     * Tests if the interior of the polyline intersects the interior of a specified rectangle.
     * The rectangle's coordinates must be expressed in this polyline's coordinate
     * system (as returned by {@link #getCoordinateSystem}).
     */
    @Override
    public synchronized boolean intersects(final Rectangle2D rect) {
        return intersectsPolyline(new Polyline(rect, getCoordinateReferenceSystem()));
    }

    /**
     * Tests if the interior of the polyline intersects the interior of a specified shape.
     * The shape's coordinates must be expressed in this polyline's coordinate
     * system (as returned by {@link #getCoordinateSystem}).
     */
    @Override
    public synchronized boolean intersects(final Shape shape) {
        if (shape instanceof Polyline) {
            return intersectsPolyline((Polyline) shape);
        }
        final Polyline[] polylines = getInstances(shape, getCoordinateReferenceSystem());
        for (int i=0; i<polylines.length; i++) {
            if (intersectsPolyline(polylines[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test if this polyline intercepts a specified polyline.
     *
     * If this polyline is <em>closed</em> (if it is an island or a lake),
     * this method will return <code>true</code> if at least one point of
     * <code>s</code> lies inside this polyline. If this polyline is not
     * closed, then this method will return the same thing as
     * {@link #intersectsEdge}.
     */
    boolean intersectsPolyline(final Polyline shape) {
        return intersects(shape, !isClosed);
    }

    /**
     * Test if the edge of this polyline intercepts the edge of a
     * specified polyline.
     *
     * This should never happen with an error-free bathymery map. However,
     * it could happen if the two polylines don't represent the same feature.
     * For example, this method may be used to test if an isoline of 15 degrees
     * celsius intercepts an isobath of 30 meters.
     *
     * @param s polylines to test.
     * @return <code>true</code> If an intersection is found.
     */
    final boolean intersectsEdge(final Polyline shape) {
        return intersects(shape, true);
    }

    /**
     * Impl�mentation of the <code>intersects[Polyline|Edge](Polyline)</code> methods.
     *
     * @param  shape polylines to check.
     * @param  checkEdgeOnly <code>true</code> to only check edges, without bothering with
     *         the inside of this polyline.
     */
    private boolean intersects(final Polyline shape, final boolean checkEdgeOnly) {
        assert frozen || Thread.holdsLock(this) : frozen;
        try {
            final CoordinateReferenceSystem coordinateSystem = getInternalCRS();
            if (getDataBounds().intersects(shape.getCachedBounds(coordinateSystem))) {
                final Point2D.Float  firstPt = new Point2D.Float();
                final  Line2D.Float  segment = new  Line2D.Float();
                final LineString.Iterator it = new LineString.Iterator(shape.data,
                                          shape.getMathTransform2D(
                                          shape.getTransformationFromInternalCRS(coordinateSystem)));
                if (it.next(firstPt) != null) {
                    // Note: call to contains(...) must NOT call the method overriden in Polygon.
                    if (checkEdgeOnly || !contains(firstPt.x, firstPt.y, null)) {
                        segment.x2 = firstPt.x;
                        segment.y2 = firstPt.y;
                        do if (!it.next(segment)) {
                            if (!isClosed || isSingular(segment)) {
                                return false;
                            }
                            segment.x2 = firstPt.x;
                            segment.y2 = firstPt.y;
                        } while (!intersects(segment));
                    }
                    return true;
                }
            }
            return false;
        } catch (TransformException exception) {
            // Conservatively return 'true' if some points from 'shape' can't be projected into
            // {@link #data}'s coordinate system.  This behavior is compliant with the Shape
            // specification.
            return true;
        }
    }

    /**
     * Returns a polyline approximately equal to this polyline clipped to the specified bounds.
     * The clip is only approximative in that the resulting polyline may extend outside the clip
     * area. However, it is guaranteed that the resulting polyline contains at least all the
     * interior of the clip area.
     *
     * If this method can't perform the clip, or if it believes that it isn't worth doing a clip,
     * it returns <code>this</code>. If this polyline doesn't intersect the clip area, then this
     * method returns <code>null</code>. Otherwise, a new polyline is created and returned. The new
     * polyline will try to share as much internal data as possible with <code>this</code> in order
     * to keep memory footprint low.
     *
     * @param  clipper The clip area.
     * @return <code>null</code> if this polyline doesn't intersect the clip, <code>this</code>
     *         if no clip has been performed, or a new clipped polyline otherwise.
     *
     * @task TODO: Change the returns type to Polyline when we will be allowed to use the J2SE 1.5
     *             compiler. Then remove the cast in Polygon.clip(Clipper).
     */
    @Override
    public synchronized Geometry clip(final Clipper clipper) {
        final Polyline clipped = clipper.clip(this);
        if (clipped != null) {
            if (LineString.equals(data, clipped.data)) {
                freeze();
                return this;
            }
            assert clipped.getUserObject() == getUserObject() : clipped;
        }
        return clipped;
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////                                                                       ////////////
    ////////////    C O M P R E S S I O N   /   R E S O L U T I O N   S E T T I N G    ////////////
    ////////////                                                                       ////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Compress this polyline. The <code>level</code> argument specify the algorithm,
     * which may be desctructive (i.e. data may loose precision). Compressing polyline
     * may help to reduce memory usage, providing that there is no reference to the
     * (<var>x</var>,<var>y</var>) coordinate points outside this polyline (otherwise
     * the garbage collector will not reclaim the old data).
     *
     * @param  level The compression level (or algorithm) to use. See the {@link CompressionLevel}
     *         javadoc for an explanation of available algorithms.
     * @return A <em>estimation</em> of the compression rate. For example a value of 0.2
     *         means that the new polyline use <em>approximatively</em> 20% less memory.
     *         Warning: this value may be inacurate, for example if the old polyline was
     *         used to shares its data with an other polyline, compressing one polyline
     *         may actually increase memory usage since the two polylines will no longer
     *         share their data.
     * @throws TransformException If an error has come up during a cartographic projection.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    @Override
    public synchronized float compress(final CompressionLevel level)
            throws FactoryException,TransformException, UnmodifiableGeometryException
    {
        if (frozen) {
            throw new UnmodifiableGeometryException((Locale)null);
        }
        final long memoryUsage = getMemoryUsage();
        if (CompressionLevel.RELATIVE_AS_BYTES.equals(level)) {
            final Statistics stats = LineString.getResolution(data, coordinateTransform);
            if (stats != null) {
                final double        mean = stats.mean();
                final double standardDev = stats.standardDeviation(false);
                final double  resolution = mean + 0.5*standardDev;
                if (resolution > 0) {
                    /*
                     * Do not resample if at least 84% of coordinate points will use more
                     * than half of the available range ([-64..+64] for byte values),
                     * assuming a gaussian distribution.
                     */
                    if ((mean-standardDev)/stats.maximum() < 0.5) {
                        setResolution(resolution);
                    }
                }
            }
        }
        data = LineString.freeze(data, false, level); // Apply the compression algorithm
        return (float) (memoryUsage - getMemoryUsage()) / (float) memoryUsage;
    }

    /**
     * Returns the polyline's resolution.  The mean resolution is the mean distance between
     * every pair of consecutive points in this polyline  (ignoring "extra" points used for
     * drawing a border, if there is one). This method tries to express the resolution in
     * linear units (usually meters) no matter whether the coordinate system is actually a
     * {@link ProjectedCoordinateSystem} or a {@link GeographicCoordinateSystem}.
     * More specifically:
     * <ul>
     *   <li>If the coordinate system is a {@linkplain GeographicCoordinateSystem geographic}
     *       one, then the resolution is expressed in units of the underlying
     *       {@linkplain Ellipsoid#getAxisUnit ellipsoid's axis length}.</li>
     *   <li>Otherwise (especially if the coordinate system is a {@linkplain
     *       ProjectedCoordinateSystem projected} one), the resolution is expressed in
     *       {@linkplain ProjectedCoordinateSystem#getUnits units of the coordinate system}.</li>
     * </ul>
     */
    @Override
    public synchronized Statistics getResolution() {
        try {
            return LineString.getResolution(data, coordinateTransform);
        } catch (TransformException exception) {
            // Should not happen, since {@link #setCoordinateSystem}
            // has already successfully projected every points.
            unexpectedException("getResolution", exception);
            return null;
        }
    }

    /**
     * Sets the polyline's resolution. This method interpolates new points in such a way
     * that every point is spaced by exactly <code>resolution</code> units (usually meters)
     * from the previous one.
     *
     * @param  resolution Desired resolution, in the same units as {@link #getResolution}.
     * @throws TransformException If some coordinate transformations were needed and failed.
     *         There is no guarantee on polyline's state in case of failure.
     * @throws UnmodifiableGeometryException if modifying this geometry would corrupt a container.
     *         To avoid this exception, {@linkplain #clone clone} this geometry before to modify it.
     */
    @Override
    public synchronized void setResolution(final double resolution)
            throws FactoryException,TransformException, UnmodifiableGeometryException
    {
        if (frozen) {
            throw new UnmodifiableGeometryException((Locale)null);
        }
        CoordinateReferenceSystem targetCRS = getCoordinateReferenceSystem();
        if (CRSUtilities.getHeadGeoEllipsoid(targetCRS) != null) {
            /*
             * The 'LineString.setResolution(...)' algorithm requires a cartesian coordinate system.
             * If this polyline's coordinate system is not cartesian, check whether the underlying data
             * used a cartesian CS  (this polyline may be a "view" of the data under another CS).
             * If the underlying data are not cartesian either, create a temporary sterographic
             * projection for computation purposes.
             */
            targetCRS = getInternalCRS();
            if (targetCRS instanceof GeographicCRS) {
                final GeographicCRS geoCRS = (GeographicCRS) targetCRS;
                final Ellipsoid ellipsoid = geoCRS.getDatum().getEllipsoid();
                final String         name = "Temporary cartesian";
                final Rectangle2D  bounds = getCachedBounds();
                final MathTransformFactory mtFactory = AuthorityFactoryFinder.getMathTransformFactory(null);
                final ParameterValueGroup params = mtFactory.getDefaultParameters(CARTESIAN_PROJECTION);
                params.parameter("central_meridian")  .setValue(bounds.getCenterX());
                params.parameter("latitude_of_origin").setValue(bounds.getCenterY());
                final DefiningConversion conversion = new DefiningConversion(name, params);
                final CRSFactory crsFactory = AuthorityFactoryFinder.getCRSFactory(null);
                targetCRS = crsFactory.createProjectedCRS(Collections.singletonMap("name", name),
                        geoCRS, conversion, DefaultCartesianCS.PROJECTED);
            }
        }
        LineString.setResolution(data, getTransformationFromInternalCRS(targetCRS), resolution);
        clearCache(); // Clear everything in the cache.
    }

    /**
     * Returns the rendering resolution. This is the spatial resolution used by
     * {@link PathIterator} only; it has no effect on the underyling data.
     *
     * @return The rendering resolution in units of this polyline's {@linkplain #getCoordinateSystem
     *         coordinate system} (linear or angular units), or 0 if the finest available
     *         resolution should be used.
     */
    @Override
    public float getRenderingResolution() {
        return renderingResolution;
    }

    /**
     * Hints this polyline that the specified resolution is sufficient for rendering.
     * Value 0 ask for the best available resolution. If a value greater than 0 is provided,
     * then the {@link PathIterator} will skip as many points as it can while preserving a
     * distance equals or smaller than <code>resolution</code> between two consecutive points.
     *
     * @param resolution The resolution to use at rendering time, in units of this polyline's
     *        {@linkplain #getCoordinateSystem coordinate system} (linear or angular units).
     */
    @Override
    public void setRenderingResolution(final float resolution) {
        if (!Float.isNaN(resolution) && resolution!=renderingResolution) {
            cache = null;
            renderingResolution = resolution;
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
     * Returns a path iterator for this polyline.
     */
    @Override
    public synchronized PathIterator getPathIterator(final AffineTransform transform) {
        return new PolygonPathIterator(this, null, transform);
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
     * Reset the {@link #isFlattenedShape() flattened} flag.
     */
    final void refreshFlattenedShape() {
        flattened = checkFlattenedShape();
    }

    /**
     * Returns <code>true</code> if {@link #getPathIterator} returns a flattened iterator.
     * In this case, there is no need to wrap it into a {@link FlatteningPathIterator}.
     */
    boolean checkFlattenedShape() {
        return coordinateTransform == null ||
               coordinateTransform.getMathTransform().isIdentity() ||
               !LineString.hasBorder(data);
    }

    /**
     * Returns the cache for rendering data. This cache
     * is used by the {@link PolygonPathIterator} only.
     */
    final PolylineCache getCache() {
        assert Thread.holdsLock(this);
        if (cache == null) {
            cache = new PolylineCache();
        }
        return cache;
    }

    /**
     * Returns a copy of all coordinates of this polyline. Coordinates are usually
     * (<var>x</var>,<var>y</var>) or (<var>longitude</var>,<var>latitude</var>)
     * pairs, depending on the {@linkplain #getCoordinateSystem coordinate system
     * in use}.
     *
     * @param  The destination array. The coordinates will be filled in {@link ArrayData#array}
     *         from index {@link ArrayData#length}. The array will be expanded if needed, and
     *         {@link ArrayData#length} will be updated with index after the <code>array</code>'s
     *         element filled with the last <var>y</var> ordinates.
     * @param  resolution The minimum distance desired between points, in linear or angular units.
     */
    final void toArray(final ArrayData dest, float resolution) {
        assert frozen || Thread.holdsLock(this) : frozen;
        try {
            /*
             * Transform the resolution from this polyline's CS to the underlying data CS.
             */
            if (coordinateTransform != null) {
                final MathTransform tr = coordinateTransform.getMathTransform();
                if (!tr.isIdentity()) {
                    final Rectangle2D bounds = getCachedBounds();
                    final double  centerX = bounds.getCenterX();
                    final double  centerY = bounds.getCenterY();
                    final double[] coords = new double[] {
                        bounds.getMinX(), centerY,
                        bounds.getMaxX(), centerY,
                        centerX,          bounds.getMinY(),
                        centerX,          bounds.getMaxY()
                    };
                    tr.inverse().transform(coords, 0, coords, 0, coords.length/2);
                    double scaleX = Math.hypot(coords[2]-coords[0], coords[3]-coords[1]);
                    double scaleY = Math.hypot(coords[6]-coords[4], coords[7]-coords[5]);
                    scaleX /= bounds.getWidth();
                    scaleY /= bounds.getHeight();
                    resolution *= 0.5*(scaleX + scaleY);
                }
            }
            /*
             * Gets the array and transforms it, if needed.
             */
            LineString.toArray(data, dest, resolution, getMathTransform2D(coordinateTransform));
        } catch (TransformException exception) {
            // Should not happen, since {@link #setCoordinateSystem}
            // has already successfully projected every point.
            unexpectedException("toArray", exception);
        }
    }

    /**
     * Returns a copy of all coordinates of this polyline. Coordinates are usually
     * (<var>x</var>,<var>y</var>) or (<var>longitude</var>,<var>latitude</var>)
     * pairs, depending on the {@linkplain #getCoordinateSystem coordinate system
     * in use}. This method never returns <code>null</code>, but may return an array
     * of length 0 if no data are available.
     *
     * @param  resolution The minimum distance desired between points, in the same units
     *         as for the {@link #getResolution} method  (i.e. linear units as much as
     *         possible - usually meters - even for geographic coordinate system).
     *         If <code>resolution</code> is greater than 0, then points that are closer
     *         than <code>resolution</code> from previous points will be skipped. This method
     *         is not required to perform precise distance computations.
     * @return The coordinates expressed in this
     *         {@linkplain #getCoordinateSystem polyline's coordinate system}.
     */
    public synchronized float[] toArray(float resolution) {
        /*
         * If the polyline's coordinate system is geographic, then we must translate
         * the resolution (which is in linear units, usually meters) to angular units.
         * The formula used below is only an approximation (probably not the best one).
         * It estimates the average of latitudinal and longitudinal angles corresponding
         * to the distance 'resolution' in the middle of the polyline's bounds. The average
         * is weighted according to the width/height ratio of the polyline's bounds.
         */
        final CoordinateReferenceSystem cs = getCoordinateReferenceSystem();
        final Ellipsoid ellipsoid = CRSUtilities.getHeadGeoEllipsoid(cs);
        if (ellipsoid != null) {
            final Unit          unit = cs.getCoordinateSystem().getAxis(1).getUnit();
            final Rectangle2D bounds = getCachedBounds();
            double             width = bounds.getWidth();
            double            height = bounds.getHeight();
            double          latitude = bounds.getCenterY();
            latitude = SI.RADIAN.getConverterTo(unit).convert(latitude);
            final double sin = Math.sin(latitude);
            final double cos = Math.cos(latitude);
            final double normalize = width+height;
            width  /= normalize;
            height /= normalize;
            resolution *= (height + width/cos) * Math.hypot(sin/ellipsoid.getSemiMajorAxis(),
                                                            cos/ellipsoid.getSemiMinorAxis());
            // Assume that longitude has the same unit as latitude.
            resolution = (float) unit.getConverterTo(SI.RADIAN).convert(resolution);
        }
        final ArrayData array = new ArrayData(64);
        toArray(array, resolution);
        return XArrays.resize(array.array(), array.length());
    }

    /**
     * Write all point coordinates to the specified stream.
     * This method is useful for debugging purposes.
     *
     * @param  out The destination stream, or <code>null</code> for the standard output.
     * @param  locale Desired locale, or <code>null</code> for a default one.
     * @throws IOException If an error occured while writing to the destination stream.
     */
    public synchronized void print(final Writer out, final Locale locale) throws IOException {
        print(new String[]{getName(locale)}, new Collection[]{getPoints()}, out, locale);
    }

    /**
     * Write all point coordinates of many polylines side by side.
     * This method is useful for checking the result of a coordinate
     * transformation; one could write the original and transformed
     * polylines side by side. Note that this method may require unicode
     * support for proper output.
     *
     * @param  polylines The set of polylines. Polygons may have different lengths.
     * @param  out The destination stream, or <code>null</code> for the standard output.
     * @param  locale Desired locale, or <code>null</code> for a default one.
     * @throws IOException If an error occured while writing to the destination stream.
     */
    public static void print(final Polyline[] polylines, final Writer out, final Locale locale)
            throws IOException
    {
        final String[]     titles = new String[polylines.length];
        final Collection[] arrays = new Collection[polylines.length];
        for (int i=0; i<polylines.length; i++) {
            final Polyline polyline = polylines[i];
            titles[i] = polyline.getName(locale);
            arrays[i] = polyline.getPoints();
        }
        print(titles, arrays, out, locale);
    }

    /**
     * Write all points from arbitrary collections side by side.
     * Note that this method may require unicode support for proper output.
     *
     * @param  titles The column's titles. Should have the same length as <code>points</code>.
     * @param  points Array of points collections. Collections may have different sizes.
     * @param  out The destination stream, or <code>null</code> for the standard output.
     * @param  locale Desired locale, or <code>null</code> for a default one.
     * @throws IOException If an error occured while writing to the destination stream.
     */
    public static void print(final String[] titles, final Collection[] points, Writer out, Locale locale)
            throws IOException
    {
        if (locale == null) locale = Locale.getDefault();
        if (out    == null)    out = Arguments.getWriter(System.out);

        final int            width = 8; // Columns width.
        final int        precision = 3; // Significant digits.
        final String     separator = "  \u2502  "; // Vertical bar.
        final String lineSeparator = System.getProperty("line.separator", "\n");
        final NumberFormat  format = NumberFormat.getNumberInstance(locale);
        final FieldPosition  dummy = new FieldPosition(0);
        final StringBuffer  buffer = new StringBuffer();
        format.setMinimumFractionDigits(precision);
        format.setMaximumFractionDigits(precision);
        format.setGroupingUsed(false);

        final Iterator[] iterators = new Iterator[points.length];
        for (int i=0; i<points.length; i++) {
            if (i != 0) {
                out.write(separator);
            }
            int length=0;
            if (titles[i] != null) {
                length=titles[i].length();
                final int spaces = Math.max(width-length/2, 0);
                out.write(Utilities.spaces(spaces));
                out.write(titles[i]);
                length += spaces;
            }
            out.write(Utilities.spaces(1+2*width-length));
            iterators[i]=points[i].iterator();
        }
        out.write(lineSeparator);
        boolean hasNext; do {
            hasNext=false;
            buffer.setLength(0);
            for (int i=0; i<iterators.length; i++) {
                if (i!=0) buffer.append(separator);
                final Iterator   it = iterators[i];
                final boolean hasPt = it.hasNext();
                final Point2D point = (hasPt) ? (Point2D) it.next() : null;
                boolean xy=true; do {
                    final int start = buffer.length();
                    if (point != null) {
                        format.format(xy ? point.getX() : point.getY(), buffer, dummy);
                    }
                    buffer.insert(start, Utilities.spaces(width-(buffer.length()-start)));
                    if (xy) {
                        buffer.append('\u00A0'); // No-break space
                    }
                } while (!(xy = !xy));
                hasNext |= hasPt;
            }
            if (!hasNext) {
                break;
            }
            buffer.append(lineSeparator);
            out.write(buffer.toString());
        } while (hasNext);
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////                                                                       ////////////
    ////////////           C L O N E   /   E Q U A L S   /   H A S H C O D E           ////////////
    ////////////                                                                       ////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Freeze this polyline. Once this method is invoked, no more points can
     * be added to the polyline and the coordinate system can't be changed.
     */
    @Override
    final synchronized void freeze() {
        frozen = true;
    }

    /**
     * Returns <code>true</code> if we are not allowed to change this polyline.
     */
    @Override
    final boolean isFrozen() {
        return frozen;
    }

    /**
     * Return a clone of this polyline. The clone has a deep copy semantic,
     * i.e. any change to the current polyline (including adding new points)
     * will not affect the clone,  and vice-versa   (any change to the clone
     * will not affect the current polyline). However, the two polylines will
     * share many internal structures in such a way that memory consumption
     * for polyline's clones should be kept low.
     */
    @Override
    public synchronized Object clone() {
        final Polyline polyline = (Polyline) super.clone();
        polyline.data = LineString.clone(data); // Take an immutable view of 'data'.
        polyline.frozen = false;
        return polyline;
    }

    /**
     * Compare the specified object with this polyline for equality.
     */
    @Override
    public synchronized boolean equals(final Object object) {
        if (object == this) {
            // Slight optimization
            return true;
        }
        if (super.equals(object)) {
            final Polyline that = (Polyline) object;
            return                  this.isClosed        ==   that.isClosed             &&
                   Utilities.equals(this.coordinateTransform, that.coordinateTransform) &&
                  LineString.equals(this.data,                that.data);
        }
        return false;
    }

    /**
     * Returns a hash value for this polyline.
     */
    @Override
    public int hashCode() {
        if (!frozen) {
            // synchronize only if the shape is mutable.
            synchronized (this) {
                return LineString.hashCode(data);
            }
        }
        return LineString.hashCode(data);
    }

    /**
     * Clears all information that was kept in an internal cache.
     * This method can be called when we know that this polyline will no longer be used
     * before a long time. It does not cause the loss of any information but will make
     * the next use of this polyline slower (the time during which the internal caches
     * are reconstructed, after which the polyline will resume its normal speed).
     */
    @Override
    synchronized void clearCache() {
        cache      = null;
        bounds     = null;
        dataBounds = null;
        refreshFlattenedShape();
        super.clearCache();
    }

    /**
     * Invoked during deserialization.
     */
    protected void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        refreshFlattenedShape(); // Reasonably fast to compute.
    }

    /**
     * Method called when an unexpected error has occurred.
     *
     * @param  method Name of the method in which the exception has occurred.
     * @param  exception The exception which has occurred.
     * @throws IllegalPathStateException systematically rethrown.
     */
    static void unexpectedException(final String method, final TransformException exception) {
        final IllegalPathStateException e = new IllegalPathStateException(
                                                exception.getLocalizedMessage());
        e.initCause(exception);
        throw e;
    }
}
