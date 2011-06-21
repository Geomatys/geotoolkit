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
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wms.xml.AbstractBoundingBox;


/**
 * <p>Java class for anonymous complex type.
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "BoundingBox")
public class BoundingBox extends AbstractBoundingBox{

   
    @XmlAttribute(name = "SRS")
    private String srs;
    @XmlAttribute(required = true)
    private double minx;
    @XmlAttribute(required = true)
    private double miny;
    @XmlAttribute(required = true)
    private double maxx;
    @XmlAttribute(required = true)
    private double maxy;
    @XmlAttribute
    private Double resx;
    @XmlAttribute
    private Double resy;

    /**
     * An empty constructor used by JAXB.
     */
    BoundingBox() {
    }

    /**
     * Build a new bounding box version 1.1.1
     *
     * @param crs 
     * @param minx 
     * @param miny 
     * @param maxx
     * @param maxy
     * @param resx
     * @param resy
     * @param version
     */
    public BoundingBox(final String crs, final double minx, final double miny,
            final double maxx, final double maxy, final Double resx, final Double resy, final String version) {
        this.maxx = maxx;
        this.maxy = maxy;
        this.minx = minx;
        this.miny = miny;
        this.srs  = crs;
        this.resx = resx;
        this.resy = resy;
    }
    
    /**
     * Gets the value of the srs property.
     */
    public String getSRS() {
        return srs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMinx() {
        return minx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMiny() {
        return miny;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaxx() {
        return maxx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaxy() {
        return maxy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getResx() {
        return resx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getResy() {
        return resy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCRSCode() {
        return getSRS();
    }

    /**
     * Verifie si cette entree est identique a l'objet specifie.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BoundingBox) {
            final BoundingBox that = (BoundingBox) object;

            
            return Utilities.equals(this.maxx, that.maxx) &&
                   Utilities.equals(this.maxy,     that.maxy)     &&
                   Utilities.equals(this.minx,   that.minx)   &&
                   Utilities.equals(this.miny,   that.miny)   &&
                   Utilities.equals(this.resx,   that.resx)   &&
                   Utilities.equals(this.resy,   that.resy)   &&
                   Utilities.equals(this.srs,   that.srs);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.srs != null ? this.srs.hashCode() : 0);
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.minx) ^ (Double.doubleToLongBits(this.minx) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.miny) ^ (Double.doubleToLongBits(this.miny) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.maxx) ^ (Double.doubleToLongBits(this.maxx) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.maxy) ^ (Double.doubleToLongBits(this.maxy) >>> 32));
        hash = 97 * hash + (this.resx != null ? this.resx.hashCode() : 0);
        hash = 97 * hash + (this.resy != null ? this.resy.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder("[BoundingBox]\n");
        s.append("minx=").append(minx).append(" maxx=").append(maxx).append(" miny=").append(miny).append(" maxy=").append(maxy);
        
        if (srs != null) {
            s.append("srs:").append(srs).append('\n');
        }
        if (resx != null) {
            s.append("resx:").append(resx).append('\n');
        }
        if (resy != null) {
           s.append("resy:").append(resy).append('\n');
        }
        return s.toString();
    }
}
