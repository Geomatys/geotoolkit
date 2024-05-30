/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/geometry/PointArrayImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry;

import java.util.List;

import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.NotifyingArrayList;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.PointArray;



/**
 * A sequence of points. The {@code PointArray} interface outlines a means
 * of efficiently storing large numbers of homogeneous {@link DirectPosition}s;
 * i.e. all having the same {@linkplain CoordinateReferenceSystem coordinate reference
 * system}. Classes implementing the {@code PointArray} interface are not required
 * to store only one type of {@code DirectPosition} (the benefit of a homogenous
 * collection arises in sub-interfaces). A simple implementation of {@code PointArray}
 * will generally be no more efficient than a simple array of {@code DirectPosition}s.
 * <br><br>
 *
 * {@code PointArray} is similar to {@code {@link List}&lt;{@link DirectPosition}&gt;}
 * from the <A HREF="http://java.sun.com/j2se/1.5.0/docs/guide/collections/index.html">collection
 * framework</A>. Implementations are free to implement directly the {@link List} interface.
 *
 * @author ISO/DIS 19107
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 2.0
 * @module
 */
public class JTSPointArray extends NotifyingArrayList<DirectPosition> implements PointArray, JTSGeometry {
    private static final long serialVersionUID = -9202900942004287122L;

    //*************************************************************************
    //  Fields
    //*************************************************************************
    //private List pointList;

    private CoordinateReferenceSystem crs;

    // When we are a component part of a larger geometry, we need to be able
    // to let that geometry know that we have been modified so that it can
    // invalidate its cached JTS object.  So we have this member which points
    // to an object having that method.
    //private JTSGeometry parent;

    //*************************************************************************
    //  Constructor
    //*************************************************************************

    public JTSPointArray() {
        this(null);
    }

    public JTSPointArray(final CoordinateReferenceSystem crs) {
        this( null, crs );
    }
    public JTSPointArray(final JTSGeometry parent,final CoordinateReferenceSystem crs) {
        super( parent );
        this.crs = crs;
    }

    //*************************************************************************
    //  implement the PointArray interface
    //*************************************************************************

    /**
     * Returns the dimensionality of the coordinates in this array.
     * This may be less than or equal to the dimensionality of the
     * {@linkplain #getCoordinateReferenceSystem() coordinate reference system}
     * for these coordinates.
     *
     * @return the dimensionality of this array.
     *
     * @see DirectPosition#getDimension
     */
    @Override
    public int getDimension() {
        return crs.getCoordinateSystem().getDimension();
    }

    /**
     * Returns the Coordinate Reference System of this array.
     *
     * @return the Coordinate Reference System for this array.
     *
     * @see DirectPosition#getCoordinateReferenceSystem
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    /**
     * Returns the point at the given index. This is equivalent to
     * {@code getColumns().get(column).getDirect()}.
     *
     * @param  column The location in the array, from 0 inclusive
     *                to the array's {@linkplain #size} exclusive.
     * @return The point at the given location in this array.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     *
     * @see List#get
     * @see #getDirectPosition(int, org.opengis.geometry.DirectPosition)
     *
     * @todo Should we specify that changes to the returned point will not be reflected
     *          to this array, or should we left the decision to the implementor?
     */
    @Override
    public DirectPosition get(final int column) throws IndexOutOfBoundsException {
        return new GeneralDirectPosition((DirectPosition)super.get(column));
    }

    /**
     * Gets the {@code DirectPosition} at the particular location in this
     * {@code PointArray}. If the {@code dest} argument is non-null,
     * that object will be populated with the value from the list.
     *
     * @param  column The location in the array, from 0 inclusive
     *                to the array's {@linkplain #size} exclusive.
     * @param  dest An optionnaly pre-allocated direct position.
     * @return The {@code dest} argument, or a new object if {@code dest} was null.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     *
     * @see #get(int)
     */
    @Override
    public DirectPosition getDirectPosition(final int column, DirectPosition dest) throws IndexOutOfBoundsException {
        DirectPosition position = (DirectPosition) get(column);
        if (dest == null) {
            if (position.getCoordinateReferenceSystem() != null) {
                dest = new GeneralDirectPosition(position.getCoordinateReferenceSystem());
            } else {
                dest = new GeneralDirectPosition(position.getDimension());
            }
        }
        for (int i = 0; i < position.getDimension(); i++) {
            dest.setCoordinate(i, position.getCoordinate(i));
        }
        return dest;
    }

    /**
     * Set the point at the given index.
     *
     * @param  column The location in the array, from 0 inclusive
     *         to the array's {@linkplain #size} exclusive.
     * @param  position The point to set at the given location in this array.
     *         The point coordinates will be copied, i.e. changes to the given
     *         {@code position} after the method call will not be reflected
     *         to this array.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     *
     * @see List#set
     */
    public void setDirectPosition(final int column, final DirectPosition position) throws IndexOutOfBoundsException {
        DirectPosition thisPosition = get(column);
        for (int i = 0; i < thisPosition.getDimension(); i++) {
            thisPosition.setCoordinate(i, position.getCoordinate(i));
        }
        invalidateCachedJTSPeer();
    }

    /**
     * Returns the elements of this {@code PointArray} as an array of
     * {@code DirectPosition}s.
     *
     * @return The elements as an array of direct positions.
     *
     * @see List#toArray
     *
     * @todo Should we specify that changes to the returned points will not be reflected
     *          into this array, or should we left the decision to the implementor?
     */
    @Override
    public Object[] toArray() {
        int n = size();
        DirectPosition [] result = new DirectPosition[n];
        for (int i=0; i<n; i++) {
            result[i] = new GeneralDirectPosition((DirectPosition)get(i));
        }
        return result;
    }

    @Override
    public org.locationtech.jts.geom.Geometry getJTSGeometry() {
        int n = super.size();
        org.locationtech.jts.geom.Coordinate [] coords =
            new org.locationtech.jts.geom.Coordinate[n];
        for (int i=0; i<n; i++) {
            coords[i] = JTSUtils.directPositionToCoordinate(
                (DirectPosition) super.get(i));
        }
        return JTSUtils.GEOMETRY_FACTORY.createMultiPoint(coords);
    }
}
