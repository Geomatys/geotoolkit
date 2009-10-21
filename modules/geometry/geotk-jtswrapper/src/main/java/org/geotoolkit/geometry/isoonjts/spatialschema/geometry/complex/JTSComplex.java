/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/complex/ComplexImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.complex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.AbstractJTSGeometry;

import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.primitive.Primitive;


/**
 * A collection of geometrically disjoint, simple {@linkplain Primitive primitives}. If a
 * {@linkplain Primitive primitive} (other than a {@linkplain org.opengis.geometry.primitive.Point point}
 * is in a particular {@code Complex}, then there exists a set of primitives of lower dimension
 * in the same complex that form the boundary of this primitive.
 * <br><br>
 * A geometric complex can be thought of as a set in two distinct ways. First, it is a finite set
 * of objects (via delegation to its elements member) and, second, it is an infinite set of point
 * values as a subtype of geometric object. The dual use of delegation and subtyping is to
 * disambiguate the two types of set interface. To determine if a {@linkplain Primitive primitive}
 * <var>P</var> is an element of a {@code Complex} <var>C</var>,
 * call: {@code C.element().contains(P)}.
 * <br><br>
 * The "{@linkplain #getElements elements}" attribute allows {@code Complex} to inherit the
 * behavior of {@link Set Set&lt;Primitive&gt;} without confusing the same sort of behavior
 * inherited from {@link org.opengis.geometry.coordinate.TransfiniteSet TransfiniteSet&lt;DirectPosition&gt;}
 * inherited through {@link Geometry}. Complexes shall be used in application schemas where
 * the sharing of geometry is important, such as in the use of computational topology. In a
 * complex, primitives may be aggregated many-to-many into composites for use as attributes
 * of features.
 *
 * @UML type GM_Complex
 * @author ISO/DIS 19107
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 2.0
 *
 * @revisit Some associations are commented out for now.
 * @module pending
 */
public class JTSComplex<T extends Geometry> extends AbstractJTSGeometry implements Complex {

    private List<T> elements;

    protected Set setViewOfElements;

    private Set subComplexes;

    public JTSComplex(CoordinateReferenceSystem crs) {
        super(crs);
        // Override a couple of methods to make sure that they invalidate our
        // cached JTS representation.
        elements = new ArrayList<T>() {
            @Override
            public boolean add(T o) {
                invalidateCachedJTSPeer();
                return super.add(o);
            }
            @Override
            public boolean remove(Object o) {
                invalidateCachedJTSPeer();
                return super.remove(o);
            }
        };
        setViewOfElements = listAsSet(elements);
        subComplexes = new HashSet();
    }

    protected List<T> getElementList() {
        return elements;
    }

    //*************************************************************************
    //  implement the Complex interface
    //*************************************************************************

    /**
     * This implementation does not support knowing about "larger" objects that
     * contain this one.  Therefore it cannot be known if a complex is maximal.
     * So this method always returns false.
     */
    @Override
    public final boolean isMaximal() {
        return false;
    }

    /**
     * This implementation does not support knowing about "larger" objects that
     * contain this one.  Therefore this method always returns null.
     */
    @Override
    public final Complex[] getSuperComplexes() {
        return null;
    }

    /**
     * Returns an array that lists the subcomplexes currently added to this
     * object.
     */
    @Override
    public final Complex[] getSubComplexes() {
        Complex [] result = new Complex[subComplexes.size()];
        subComplexes.toArray(result);
        return result;
    }

    /**
     * Returns a modifiable set that allows the user to add to the list of
     * subcomplexes.
     */
    public final Set getSubComplexSet() {
        return subComplexes;
    }

    /**
     * Returns a modifiable reference to our set of elements.  This class makes
     * no attempt to keep the set of subComplexes up to date when this set is
     * modified, so modify with caution.
     */
    @Override
    public final Collection<Primitive> getElements() {
        return setViewOfElements;
    }

    /**
     * Creates the JTS peer.
     */
    @Override
    protected final com.vividsolutions.jts.geom.Geometry computeJTSPeer() {
        ArrayList subParts = new ArrayList();
        Iterator elemIt = elements.iterator();
        while (elemIt.hasNext()) {
            JTSGeometry prim = (JTSGeometry) elemIt.next();
            subParts.add(prim.getJTSGeometry());
        }
        // JTS's geometry factory interface has a convenient method that'll
        // combine geometries by putting them into the most specific collection
        // class it can.
        return JTSUtils.GEOMETRY_FACTORY.buildGeometry(subParts);
    }
    
}
