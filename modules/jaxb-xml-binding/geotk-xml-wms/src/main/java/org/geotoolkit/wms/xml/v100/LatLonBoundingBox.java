/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.wms.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractGeographicBoundingBox;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "LatLonBoundingBox")
public class LatLonBoundingBox implements AbstractGeographicBoundingBox {

    @XmlAttribute(name = "minx", required = true)
    protected Double minx;
    @XmlAttribute(name = "miny", required = true)
    protected Double miny;
    @XmlAttribute(name = "maxx", required = true)
    protected Double maxx;
    @XmlAttribute(name = "maxy", required = true)
    protected Double maxy;

    /**
     * Obtient la valeur de la propriété minx.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Double getMinx() {
        return minx;
    }

    /**
     * Définit la valeur de la propriété minx.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMinx(Double value) {
        this.minx = value;
    }

    /**
     * Obtient la valeur de la propriété miny.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Double getMiny() {
        return miny;
    }

    /**
     * Définit la valeur de la propriété miny.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMiny(Double value) {
        this.miny = value;
    }

    /**
     * Obtient la valeur de la propriété maxx.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Double getMaxx() {
        return maxx;
    }

    /**
     * Définit la valeur de la propriété maxx.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMaxx(Double value) {
        this.maxx = value;
    }

    /**
     * Obtient la valeur de la propriété maxy.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Double getMaxy() {
        return maxy;
    }

    /**
     * Définit la valeur de la propriété maxy.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMaxy(Double value) {
        this.maxy = value;
    }

    @Override
    public double getWestBoundLongitude() {
        return minx == null? Double.NaN : minx;
    }

    @Override
    public double getEastBoundLongitude() {
        return maxx == null? Double.NaN : maxx;
    }

    @Override
    public double getSouthBoundLatitude() {
        return miny == null? Double.NaN : miny;
    }

    @Override
    public double getNorthBoundLatitude() {
        return maxy == null? Double.NaN : maxy;
    }

    @Override
    public Boolean getInclusion() {
        return true;
    }

}
