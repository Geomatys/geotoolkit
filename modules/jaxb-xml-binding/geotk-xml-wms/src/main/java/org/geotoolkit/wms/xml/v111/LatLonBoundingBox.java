/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wms.xml.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractGeographicBoundingBox;
import org.opengis.metadata.extent.GeographicBoundingBox;

/**
 * Geographic bounding box for 1.1.1 version of WMS
 * @author legal
 * @module pending
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

    public LatLonBoundingBox(final GeographicBoundingBox geoBox) {
        this.minx = geoBox.getWestBoundLongitude();
        this.miny = geoBox.getSouthBoundLatitude();
        this.maxx = geoBox.getEastBoundLongitude();
        this.maxy = geoBox.getNorthBoundLatitude();
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
