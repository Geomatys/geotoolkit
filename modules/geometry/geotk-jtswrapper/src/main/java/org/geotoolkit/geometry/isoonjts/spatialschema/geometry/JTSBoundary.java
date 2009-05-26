/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/BoundaryImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry;

// OpenGIS direct dependencies

import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.complex.JTSComplex;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.Boundary;


/**
 * The abstract root data type for all the data types used to represent the boundary of geometric
 * objects. Any subclass of {@link Geometry} will use a subclass of {@code Boundary} to
 * represent its boundary through the operation {@link Geometry#getBoundary}. By the nature of
 * geometry, boundary objects are cycles.
 *
 * @UML type GM_Boundary
 * @author ISO/DIS 19107
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 2.0
 */
public class JTSBoundary extends JTSComplex implements Boundary {
    
    //*************************************************************************
    //  
    //*************************************************************************
    
    /**
     * Creates a new {@code BoundaryImpl}.
     * 
     */
    public JTSBoundary() {
        this(null);
    }

    /**
     * Creates a new {@code BoundaryImpl}.
     * @param crs
     */
    public JTSBoundary(final CoordinateReferenceSystem crs) {
        super(crs);
    }
}
