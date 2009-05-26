/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/primitive/CurveBoundaryImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.primitive.CurveBoundary;
import org.opengis.geometry.primitive.Point;

/**
 * This is Chris's implementation of a CurveBoundary.  I started it and
 * realized about halfway through that I won't necessarily need it.  So the
 * last few methods are still unimplemented (and just delegate to the
 * superclass, which currently does nothing).
 */
public class JTSCurveBoundary extends JTSPrimitiveBoundary implements CurveBoundary {
        
    private Point startPoint;
    
    private Point endPoint;
    
    private Set pointSet;
    
    public JTSCurveBoundary(
            final CoordinateReferenceSystem crs, 
            final Point startPoint, 
            final Point endPoint) {
        
        super(crs);
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        HashSet tempSet = new HashSet();
        if (startPoint != null) {
            tempSet.add(startPoint);
        }
        if (endPoint != null) { 
            tempSet.add(endPoint);
        }
        this.pointSet = Collections.unmodifiableSet(tempSet);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Point getStartPoint() {
        return startPoint;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Point getEndPoint() {
        return endPoint;
    }
}
