/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/complex/CompositeSurfaceImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.complex;

import java.util.Set;

import javax.xml.bind.annotation.XmlType;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.complex.CompositeSurface;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.OrientablePrimitive;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;


/**
 * A {@linkplain Complex complex} with all the geometric properties of a surface. Thus, this
 * composite can be considered as a type of {@linkplain OrientableSurface orientable surface}.
 * Essentially, a composite surface is a collection of oriented surfaces that join in pairs on
 * common boundary curves and which, when considered as a whole, form a single surface.
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
@XmlType(name="CompositeSurfaceType", namespace="http://www.opengis.net/gml")
public class JTSCompositeSurface extends AbstractJTSComposite implements CompositeSurface {//, OrientableSurface {


    /**
     * Returns the list of orientable surfaces in this composite.
     *
     * To get a full representation of the elements in the {@linkplain Complex complex}, the
     * {@linkplain org.opengis.geometry.primitive.Curve curves} and {@link org.opengis.geometry.primitive.Point
     * points} on the boundary of the generator set of {@linkplain org.opengis.geometry.primitive.Surface
     * surfaces} would be added to the curves in the generator list.
     *
     * @return The list of orientable surfaces in this composite.
     */
    @Override
    public Set<OrientableSurface> getGenerators() {
        return null;
    }

    //*************************************************************************
    //  implement the OrientableSurface
    //*************************************************************************

    /**
     * Returns the set of circular sequences of {@linkplain OrientableCurve orientable curve} that
     * limit the extent of this {@code OrientableSurface}. These curves shall be organized
     * into one circular sequence of curves for each boundary component of the
     * {@code OrientableSurface}. In cases where "exterior" boundary is not
     * well defined, all the rings of the {@linkplain SurfaceBoundary surface boundary}
     * shall be listed as "interior".
     *
     * <blockquote><font size=2>
     * <strong>NOTE:</strong> The concept of exterior boundary for a surface is really only
     * valid in a 2-dimensional plane. A bounded cylinder has two boundary components, neither
     * of which can logically be classified as its exterior. Thus, in 3 dimensions, there is no
     * valid definition of exterior that covers all cases.
     * </font></blockquote>
     *
     * @return The sets of positions on the boundary.
     */
    @Override
    public SurfaceBoundary getBoundary() {
        return (SurfaceBoundary) super.getBoundary();
    }

    /**
     * Returns the owner of this orientable surface, or {@code null} if none.
     *
     * @return The owner of this orientable surface, or {@code null} if none.
     *
     * @todo I'm not sure to interpret correctly the ISO specification.
     *          Sound like ISO returns an array (or a sequence) here.
     */
    @Override
    public CompositeSurface getComposite() {
        return null;
    }

    //*************************************************************************
    //  implement the OrientablePrimitive
    //*************************************************************************

    /**
     * Determines which of the two possible orientations this object represents.
     *
     * @return +1 for a positive orientation, or -1 for a negative orientation.
     *
     * @todo The UML specify a {@code Sign} return type.
     *          Should we create a {@code Sign} class?
     */
    @Override
    public int getOrientation() {
        return 0;
    }

    /**
     * Returns the primitive associated with this {@code OrientablePrimitive}.
     * Each {@linkplain Primitive primitive} of dimension 1 or 2 is associated to two
     * {@code OrientablePrimitive}s, one for each possible orientation.
     * For curves and surfaces, there are exactly two orientable primitives
     * for each geometric object.
     *
     * @return The primitive, never {@code null}.
     *
     * @see Primitive#getProxy
     */
    @Override
    public Surface getPrimitive() {
        return null;
    }

    //*************************************************************************
    //  implement the Primitive interface
    //*************************************************************************

    /**
     * Returns the boundary of a {@code Primitive} as a set of
     * {@code Primitive}s. This is a specialization of the operation at
     * {@link Geometry}, which does not restrict the class of the returned collection.
     * The organization of the boundary set of a {@code Primitive} depends on the
     * type of the primitive.
     *
     * @return The sets of positions on the boundary.
     */
/// public PrimitiveBoundary getBoundary();

    /**
     * Returns the {@code Primitive}s which are by definition coincident with this one.
     * This allows applications to override the
     * {@link org.opengis.geometry.coordinate.TransfiniteSet TransfiniteSet&lt;DirectPosition&gt;}
     * interpretation and its associated computational geometry, and declare one
     * {@code Primitive} to be "interior to" another.
     *
     * This set should normally be empty when the {@code Primitive}s are within a
     * {@linkplain Complex complex}, since in that case the boundary
     * information is sufficient for most cases.
     *
     * This association should not be used when the two {@code Primitive}s are not close
     * to one another. The intent is to allow applications to compensate for inherent and
     * unavoidable round off, truncation, and other mathematical problems indigenous to
     * computer calculations.
     *
     * @return The set of primitives contained into this primitive.
     *
     * @todo Using a {@link Set} returns type allows the user to add or remove element in
     *          this set at his convenience. Is it the right interpretation of this specification?
     *
     * @see #getContainingPrimitives
     */
    @Override
    public Set<Primitive> getContainedPrimitives() {
        return null;
    }

    /**
     * Returns the {@code Primitive}s which are by definition coincident with this one.
     *
     * @return The set of primitives which contains this primitive.
     *
     * @todo Using a {@link Set} returns type allows the user to add or remove element in
     *          this set at his convenience. Is it the right interpretation of this specification?
     *
     *          Should we stretch out some relation with contained primitive? For example
     *          should we update the specification with something like the following?
     *          "Invoking {@code B.getContainingPrimitive().add(A)} is equivalent to
     *           invoking {@code A.getContainedPrimitive().add(B)}".
     *
     * @see #getContainedPrimitives
     */
    @Override
    public Set<Primitive> getContainingPrimitives() {
        return null;
    }

    /**
     * Returns the set of complexes which contains this primitive. A {@code Primitive} may
     * be in several {@linkplain Complex complexes}. This association may not be navigable in this
     * direction (from primitive to complex), depending on the implementation.
     *
     * @return The set of complexex which contains this primitive.
     *
     * @todo Does it means that {@code Primitive} can't be immutable, since
     *          adding this primitive to a complex will change this set?
     */
    @Override
    public Set<Complex> getComplexes() {
        return null;
    }

    /**
     * Returns the orientable primitives associated with this primitive. Each {@code Primitive}
     * of dimension 1 or 2 is associated to two {@linkplain OrientablePrimitive orientable primitives},
     * one for each possible orientation. For curves and surfaces, there are exactly two orientable
     * primitives for each geometric object. For the positive orientation, the
     * {@linkplain OrientablePrimitive orientable primitive} shall be the corresponding
     * {@linkplain Curve curve} or {@linkplain Surface surface}.
     *
     * @return The orientable primitives as an array of length 2, or {@code null} if none.
     *
     * @see OrientablePrimitive#getPrimitive
     *
     * @todo Should we use the plural form for the method names?
     */
    @Override
    public OrientablePrimitive[] getProxy() {
        return null;
    }


}
