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
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.wms.xml.AbstractBoundingBox;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "BoundingBox")
public class BoundingBox implements AbstractBoundingBox {

    @XmlAttribute(name = "SRS", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String srs;
    @XmlAttribute(name = "minx", required = true)
    protected double minx;
    @XmlAttribute(name = "miny", required = true)
    protected double miny;
    @XmlAttribute(name = "maxx", required = true)
    protected double maxx;
    @XmlAttribute(name = "maxy", required = true)
    protected double maxy;

    /**
     * Obtient la valeur de la propriété srs.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSRS() {
        return srs;
    }

    /**
     * Définit la valeur de la propriété srs.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSRS(String value) {
        this.srs = value;
    }

    /**
     * Obtient la valeur de la propriété minx.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public double getMinx() {
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
    public void setMinx(double value) {
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
    public double getMiny() {
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
    public void setMiny(double value) {
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
    public double getMaxx() {
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
    public void setMaxx(double value) {
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
    public double getMaxy() {
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
    public void setMaxy(double value) {
        this.maxy = value;
    }

    @Override
    public String getCRSCode() {
        return srs;
    }

    @Override
    public Double getResx() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Double getResy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
