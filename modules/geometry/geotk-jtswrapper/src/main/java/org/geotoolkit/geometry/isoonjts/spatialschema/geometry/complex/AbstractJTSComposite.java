/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/complex/CompositeImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.complex;

import java.util.Collection;

import org.opengis.geometry.complex.Complex;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.complex.Composite;
import org.opengis.geometry.primitive.Primitive;

/**
 * A geometric complex with an underlying core geometry that is isomorphic to a primitive. Thus,
 * a composite curve is a collection of curves whose geometry interface could be satisfied by a
 * single curve (albeit a much more complex one). Composites are intended for use as attribute
 * values in datasets in which the underlying geometry has been decomposed, usually to expose its
 * topological nature.
 *
 * @author ISO/DIS 19107
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 2.0
 * @module pending
 */
public abstract class AbstractJTSComposite extends JTSComplex implements Composite {

    public AbstractJTSComposite() {
        this(null);
    }

    public AbstractJTSComposite(CoordinateReferenceSystem crs) {
        super(crs);
    }

    /**
     * Returns a homogeneous collection of {@linkplain Primitive primitives} whose union would be
     * the core geometry of the composite. The complex would include all primitives in the generator
     * and all primitives on the boundary of these primitives, and so forth until
     * {@linkplain org.opengis.geometry.primitive.Point points} are included. Thus the
     * {@code generators} on {@code Composite} is a subset of the
     * {@linkplain Complex#getElements() elements} on {@linkplain Complex complex}.
     *
     * @return The list of primitives in this composite.
     */
    @Override
    public Collection<? extends Primitive> getGenerators() {
        return null;
    }
}
