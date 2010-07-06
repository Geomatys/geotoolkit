/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/primitive/PointImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive;

import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.AbstractJTSGeometry;
import org.geotoolkit.internal.jaxb.DirectPositionAdapter;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.geotoolkit.util.Utilities;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.UnmodifiableGeometryException;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Bearing;
import org.opengis.geometry.primitive.OrientablePrimitive;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.PrimitiveBoundary;
import org.opengis.geometry.complex.Composite;

/**
 * Basic data type for a geometric object consisting of one and only one point.
 * In most cases, the state of a {@code Point} is fully determined by its
 * position attribute. The only exception to this is if the {@code Point}
 * has been subclassed to provide additional non-geometric information such as
 * symbology.
 * 
 * @author SYS Technologies
 * @author crossley
 * @author cdillard
 * @version $Revision $
 * @module pending
 */
@XmlType(name="PointType", namespace="http://www.opengis.net/gml")
public class JTSPoint extends AbstractJTSGeometry implements Point {

    private DirectPosition position;

    /**
     * Creates a new {@code PointImpl}.
     */
    public JTSPoint() {
    	this( null, DefaultGeographicCRS.WGS84 );
    }

    /**
     * Creates a new {@code PointImpl}.
     * @param position
     */
    public JTSPoint(final DirectPosition position) {
        this(position, position.getCoordinateReferenceSystem());
    }

    /**
     * Creates a new {@code PointImpl}.
     * @param position
     * @param crs
     */
    public JTSPoint(final DirectPosition position, final CoordinateReferenceSystem crs) {
        super(crs);
        this.position = (position == null) ? new GeneralDirectPosition(crs) : position;
    }

    //*************************************************************************
    //  Methods
    //*************************************************************************

    /**
     * Returns a copy of this point's position.  We must return a copy (and not
     * a reference to our internal object), otherwise the caller could modify
     * the values of the object and we would not know.
     */
    @Override
    public DirectPosition getDirectPosition() {
        return new GeneralDirectPosition(position);
    }

    /**
     * Makes a copy of the given point and keeps that copy around.  If the given
     * point is not in the same coordinate reference system as this primitive,
     * then we attempt to convert it.
     */
    @Override
    @XmlElement(name="pos", namespace="http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(DirectPositionAdapter.class)
    public void setDirectPosition(final DirectPosition position) throws UnmodifiableGeometryException {
        if (isMutable()) {
            CoordinateReferenceSystem myCRS    = getCoordinateReferenceSystem();
            CoordinateReferenceSystem pointCRS = position.getCoordinateReferenceSystem();
            if (pointCRS == null && position instanceof GeneralDirectPosition) {
                ((GeneralDirectPosition) position).setCoordinateReferenceSystem(myCRS);
                pointCRS = myCRS;
            }
            DirectPosition copy                = new GeneralDirectPosition(position);
            if ((myCRS != null) && (pointCRS != null) && (!myCRS.equals(pointCRS))) {
                // Do the conversion.
                try {
                    CoordinateOperationFactory cof = FactoryFinder.getCoordinateOperationFactory(null);
                    CoordinateOperation coordOp = cof.createOperation(pointCRS, myCRS);
                    MathTransform mt = coordOp.getMathTransform();
                    mt.transform(position, copy);
                }
                catch (OperationNotFoundException e) {
                    throw new RuntimeException("Unable to find an operation", e);
                }
                catch (FactoryException e) {
                    throw new RuntimeException("Factory exception", e);
                }
                catch (TransformException e) {
                    throw new RuntimeException("Error transforming", e);
                }
            }
            // Copy the position into our member.
            this.position = copy;
            // Let our cache know that something has changed so we can recompute.
            invalidateCachedJTSPeer();
        }
        else {
            throw new UnmodifiableGeometryException();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Deprecated
    @Override
    public void setPosition(final DirectPosition position) throws UnmodifiableGeometryException {
        setDirectPosition(position);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public PrimitiveBoundary getBoundary() {
        return (PrimitiveBoundary) super.getBoundary();
    }

    /**
     * Not supported in this implementation.
     */
    @Override
    public Bearing getBearing(final Position toPoint) {
        throw new UnsupportedOperationException("Bearing calculation is not supported");
    }

    /**
     * Computes the JTS equivalent of this geometry.
     */
    @Override
    protected com.vividsolutions.jts.geom.Geometry computeJTSPeer() {
        return JTSUtils.directPositionToPoint(position);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set getContainedPrimitives() {
        return Collections.EMPTY_SET;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set getContainingPrimitives() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set getComplexes() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Composite getComposite() {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public OrientablePrimitive[] getProxy() {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result      = 1;
        result          = PRIME * result + ((position == null) ? 0 : position.hashCode());
        return result;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof JTSPoint && super.equals(obj)) {
            final JTSPoint that = (JTSPoint) obj;
            return Utilities.equals(this.position, that.position);
        }
        return false;
    }

    @Override
    public String toString() {
        String s = super.toString();
        s = s + "position:" + position + '\n';
        return s;
    }
}

