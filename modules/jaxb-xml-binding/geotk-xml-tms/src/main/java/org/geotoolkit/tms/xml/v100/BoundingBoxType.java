/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.tms.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BoundingBoxType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BoundingBoxType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="minx" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="miny" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="maxx" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="maxy" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BoundingBoxType")
public class BoundingBoxType {

    @XmlAttribute(name = "minx")
    protected Double minx;
    @XmlAttribute(name = "miny")
    protected Double miny;
    @XmlAttribute(name = "maxx")
    protected Double maxx;
    @XmlAttribute(name = "maxy")
    protected Double maxy;

    public BoundingBoxType() {
    }

    public BoundingBoxType(Double minx, Double miny, Double maxx, Double maxy) {
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
    }

    /**
     * Gets the value of the minx property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getMinx() {
        return minx;
    }

    /**
     * Sets the value of the minx property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setMinx(Double value) {
        this.minx = value;
    }

    /**
     * Gets the value of the miny property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getMiny() {
        return miny;
    }

    /**
     * Sets the value of the miny property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setMiny(Double value) {
        this.miny = value;
    }

    /**
     * Gets the value of the maxx property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getMaxx() {
        return maxx;
    }

    /**
     * Sets the value of the maxx property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setMaxx(Double value) {
        this.maxx = value;
    }

    /**
     * Gets the value of the maxy property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getMaxy() {
        return maxy;
    }

    /**
     * Sets the value of the maxy property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setMaxy(Double value) {
        this.maxy = value;
    }

}
