/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/complex/ComplexBoundaryImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.complex;

// OpenGIS direct dependencies

import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.AbstractJTSBoundary;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.complex.ComplexBoundary;


/**
 * The boundary of {@linkplain Complex complex} objects. The
 * {@link org.opengis.geometry.coordinate.#getBoundary getBoundary()} method for {@link Complex}
 * objects shall return a {@code ComplexBoundary}, which is a collection of primitives
 * and a {@linkplain Complex complex} of dimension 1 less than the original object.
 *
 * @author ISO/DIS 19107
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 2.0
 * @module
 */
public class JTSComplexBoundary extends AbstractJTSBoundary implements ComplexBoundary {
}
