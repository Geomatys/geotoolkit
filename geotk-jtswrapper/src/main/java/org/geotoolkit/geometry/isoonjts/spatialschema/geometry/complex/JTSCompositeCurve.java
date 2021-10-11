/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/complex/CompositeCurveImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.complex;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.complex.CompositeCurve;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.OrientablePrimitive;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveBoundary;

/**
 * A {@linkplain Complex complex} with all the geometric properties of a curve. Thus, this
 * composite can be considered as a type of {@linkplain OrientableCurve orientable curve}.
 * Essentially, a composite curve is a list of {@linkplain OrientableCurve orientable curves}
 * agreeing in orientation in a manner such that each curve (except the first) begins where
 * the previous one ends.
 * <strong>However, this implementation does NOT currently require that the start
 * point for element i+1 must be identical to the end point of element i.</strong>
 * This may change for later versions when arcs are supported.
 *
 * @author ISO/DIS 19107
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 2.0
 *
 * @todo This interface extends (indirectly) both {@link org.opengis.geometry.primitive.Primitive} and
 *          {@link org.opengis.geometry.complex.Complex}. Concequently, there is a clash in the semantics
 *          of some set theoretic operation. Specifically, {@code Primitive.contains(...)}
 *          (returns FALSE for end points) is different from {@code Complex.contains(...)}
 *          (returns TRUE for end points).
 * @module
 */
@XmlType(name="CompositeCurveType", namespace="http://www.opengis.net/gml")
public class JTSCompositeCurve extends AbstractJTSComposite implements CompositeCurve {

    // A parent curve, if any.
    private CompositeCurve parent;

    public JTSCompositeCurve() {
        this(null, null);
    }

    public JTSCompositeCurve(final CompositeCurve parent) {
        this(parent, parent.getCoordinateReferenceSystem());
    }

    public JTSCompositeCurve(final CompositeCurve parent, final CoordinateReferenceSystem crs) {
        super(crs);
        this.parent = parent;
    }

    //*************************************************************************
    //  implement the CompositeCurve interface
    //*************************************************************************

    @Override
    public CurveBoundary getBoundary() {
        return (CurveBoundary) super.getBoundary();
    }
    /**
     * Returns the list of orientable curves in this composite.
     *
     * To get a full representation of the elements in the {@linkplain Complex complex},
     * the {@linkplain org.opengis.geometry.primitive.Point points} on the boundary of the
     * generator set of {@linkplain org.opengis.geometry.primitive.Curve curve} would be
     * added to the curves in the generator list.
     *
     * @return The list of orientable curves in this composite.
     */
    @Override
    public final List<OrientableCurve> getGenerators() {
        return getElementList();
    }

    //*************************************************************************
    //  implement the OrientableCurve interface
    //*************************************************************************

    /**
     * Returns the owner of this orientable curve, or {@code null} if none.
     *
     * @return The owner of this orientable curve, or {@code null} if none.
     *
     * @todo I'm not sure to interpret correctly the ISO specification.
     *          Sound like ISO returns an array (or a sequence) here.
     */
    @Override
    public final CompositeCurve getComposite() {
        return parent;
    }

    //*************************************************************************
    //  implement the OrientablePrimitive interface
    //*************************************************************************

    /**
     * This implementation always returns +1, indicating that the curve is to
     * be traversed in its "natural" order.
     */
    @Override
    public int getOrientation() {
        return +1;
    }

    /**
     * This returns the "positive orientation" primitive.  In this
     * implementation, we only support positively oriented primitives, so this
     * method always returns "null".  (In the future, it might return the
     * positively oriented object for which this object is the negative proxy.)
     */
    @Override
    public Curve getPrimitive() {
        return null;
    }

    /**
     * This implementation doesn't support traversing this association in
     * this direction, so this method always returns null.
     */
    @Override
    public final Set<Primitive> getContainingPrimitives() {
        return null;
    }

    @Override
    public final Set<Complex> getComplexes() {
        return null;
    }

    @Override
    public final Set getContainedPrimitives() {
        return setViewOfElements;
    }

    /**
     * This implementation currently only supports positively oriented
     * primitives, so this method will always return null, indicating that this
     * object itself is the only instance in existence and that there are no
     * proxies for the positively and negatively oriented versions of this
     * primitive.
     */
    @Override
    public OrientablePrimitive[] getProxy() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (parent != null)
            sb.append("parent:").append(parent).append('\n');
        return sb.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this)
            return true;
        if (object instanceof JTSCompositeCurve && super.equals(object)) {
            JTSCompositeCurve that = (JTSCompositeCurve) object;
            return Objects.equals(this.parent, that.parent);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 59 * hash + (this.parent != null ? this.parent.hashCode() : 0);
        return hash;
    }
}
