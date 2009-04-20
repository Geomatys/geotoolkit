package org.geotoolkit.internal.jaxb.backend.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.internal.jaxb.backend.AbstractGeographicBoundingBox;

/**
 * Geographic bounding box for 1.1.1 version of WMS
 * @author legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "LatLonBoundingBox")
public class LatLonBoundingBox extends AbstractGeographicBoundingBox {

    @XmlAttribute
    private double minx;
    @XmlAttribute
    private double miny;
    @XmlAttribute
    private double maxx;
    @XmlAttribute
    private double maxy;
    
    /**
     * An empty constructor used by JAXB.
     */
    LatLonBoundingBox() {
    }

    /**
     * Build a new bounding box.
     *
     */
    public LatLonBoundingBox(final double minx, final double miny, 
            final double maxx, final double maxy) {
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
        
    }
    /**
     * Gets the value of the maxy property.
     * 
     */
    public double getWestBoundLongitude() {
        return minx;
    }

    /**
     * Gets the value of the minx property.
     * 
     */
    public double getEastBoundLongitude() {
        return maxx;
    }

    /**
     * Gets the value of the maxx property.
     * 
     */
    public double getSouthBoundLatitude() {
        return miny;
    }

    /**
     * Gets the value of the miny property.
     * 
     */
    public double getNorthBoundLatitude() {
        return maxy;
    }
    
    @Override
    public String toString() {
        return "Env[" + minx + " : " + maxx + ", " + miny + " : " + maxy + "]";
    }
    
}
