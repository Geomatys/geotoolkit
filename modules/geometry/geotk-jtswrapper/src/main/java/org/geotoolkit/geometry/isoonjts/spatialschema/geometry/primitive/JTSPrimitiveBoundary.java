/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/primitive/PrimitiveBoundaryImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive;

import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.AbstractJTSBoundary;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.primitive.PrimitiveBoundary;


/**
 * The boundary of {@linkplain Primitive primitive} objects. This is the root for the various
 * return types of the {@link org.opengis.geometry.coordinate.#getBoundary getBoundary()} method for
 * subtypes of {@link Primitive}. Since points have no boundary, no special subclass is needed
 * for their boundary.
 *
 * @UML type GM_PrimitiveBoundary
 * @author ISO/DIS 19107
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 2.0
 * @module pending
 */
public class JTSPrimitiveBoundary extends AbstractJTSBoundary implements PrimitiveBoundary {
        
    /**
     * Creates a new {@code PrimitiveBoundaryImpl}.
     * 
     */
    public JTSPrimitiveBoundary() {
        this(null);
    }

    /**
     * Creates a new {@code PrimitiveBoundaryImpl}.
     * @param crs
     */
    public JTSPrimitiveBoundary(final CoordinateReferenceSystem crs) {
        super(crs);
    }
}
