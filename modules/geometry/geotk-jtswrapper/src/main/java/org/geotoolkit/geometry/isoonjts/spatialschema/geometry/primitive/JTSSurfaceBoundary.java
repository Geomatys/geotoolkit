/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/primitive/SurfaceBoundaryImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.internal.jaxb.RingAdapter;
import org.geotoolkit.util.Utilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.SurfaceBoundary;

/**
 * LiteGO1 implementation of the SurfaceBoundary interface.
 * @module pending
 */
public class JTSSurfaceBoundary extends JTSPrimitiveBoundary implements SurfaceBoundary {
    private static final long serialVersionUID = 8658623156496260842L;

    @XmlElement(namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(RingAdapter.class)
    private Ring exterior;

    @XmlElement(namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(RingAdapter.class)
    private List<Ring> interior;

    public JTSSurfaceBoundary() {
        
    }
    
    public JTSSurfaceBoundary(CoordinateReferenceSystem crs, Ring exterior, List<Ring> interior) {
        super(crs);
        this.exterior = exterior;
        this.interior = interior;
    }
    public JTSSurfaceBoundary(CoordinateReferenceSystem crs, Ring exterior, Ring [] interior) {
        super(crs);
        this.exterior = exterior;
        this.interior = new ArrayList( Arrays.asList( interior) );
    }

    /**
     * Returns the exterior ring, or {@code null} if none.
     */
    @Override
    public Ring getExterior() {
        return exterior;
    }

    /**
     * Returns the interior rings.
     */
    @Override
    public List<Ring> getInteriors() {
        return interior;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this)
            return true;

        if (object instanceof JTSSurfaceBoundary && super.equals(object)) {
            JTSSurfaceBoundary that = (JTSSurfaceBoundary) object;
            return Utilities.equals(this.exterior, that.exterior) &&
                   Utilities.equals(this.interior, that.interior);
        }
        return false;
    }
}
