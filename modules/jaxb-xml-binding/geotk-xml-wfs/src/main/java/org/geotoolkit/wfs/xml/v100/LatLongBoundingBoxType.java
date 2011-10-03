/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wfs.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for LatLongBoundingBoxType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LatLongBoundingBoxType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="minx" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="miny" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="maxx" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="maxy" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LatLongBoundingBoxType")
public class LatLongBoundingBoxType {

    @XmlAttribute(required = true)
    private String minx;
    @XmlAttribute(required = true)
    private String miny;
    @XmlAttribute(required = true)
    private String maxx;
    @XmlAttribute(required = true)
    private String maxy;

    public LatLongBoundingBoxType() {
        
    }
    
    /**
     * Build a 2 dimension boundingBox.
     * 
     */
    public LatLongBoundingBoxType(final double minx, final double miny, final double maxx, final double maxy){
        this.minx = Double.toString(minx);
        this.miny = Double.toString(miny);
        this.maxx = Double.toString(maxx);
        this.maxy = Double.toString(maxy);
    }

    
    /**
     * Gets the value of the minx property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinx() {
        return minx;
    }

    /**
     * Sets the value of the minx property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinx(String value) {
        this.minx = value;
    }

    /**
     * Gets the value of the miny property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiny() {
        return miny;
    }

    /**
     * Sets the value of the miny property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiny(String value) {
        this.miny = value;
    }

    /**
     * Gets the value of the maxx property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaxx() {
        return maxx;
    }

    /**
     * Sets the value of the maxx property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaxx(String value) {
        this.maxx = value;
    }

    /**
     * Gets the value of the maxy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaxy() {
        return maxy;
    }

    /**
     * Sets the value of the maxy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaxy(String value) {
        this.maxy = value;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof LatLongBoundingBoxType) {
            final LatLongBoundingBoxType that = (LatLongBoundingBoxType) object;
            return Utilities.equals(this.minx        , that.minx)         &&
                   Utilities.equals(this.maxx , that.maxx)  &&
                   Utilities.equals(this.miny, that.miny) &&
                   Utilities.equals(this.maxy, that.maxy);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (this.minx != null ? this.minx.hashCode() : 0);
        hash = 11 * hash + (this.miny != null ? this.miny.hashCode() : 0);
        hash = 11 * hash + (this.maxx != null ? this.maxx.hashCode() : 0);
        hash = 11 * hash + (this.maxy != null ? this.maxy.hashCode() : 0);
        return hash;
    }

   
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]:").append('\n');
        if (minx != null)
            s.append("minx:").append(minx).append('\n');
        if (miny != null)
            s.append("miny:").append(miny).append('\n');
        if (maxx != null)
            s.append("maxx:").append(maxx).append('\n');
        if (maxy != null)
            s.append("maxy:").append(maxy).append('\n');
        
        return s.toString();
    }
}
