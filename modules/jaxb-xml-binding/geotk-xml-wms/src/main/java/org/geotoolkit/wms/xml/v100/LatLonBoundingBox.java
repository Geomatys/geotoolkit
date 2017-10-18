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


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "LatLonBoundingBox")
public class LatLonBoundingBox {

    @XmlAttribute(name = "minx", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String minx;
    @XmlAttribute(name = "miny", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String miny;
    @XmlAttribute(name = "maxx", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String maxx;
    @XmlAttribute(name = "maxy", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String maxy;

    /**
     * Obtient la valeur de la propriété minx.
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
     * Définit la valeur de la propriété minx.
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
     * Obtient la valeur de la propriété miny.
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
     * Définit la valeur de la propriété miny.
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
     * Obtient la valeur de la propriété maxx.
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
     * Définit la valeur de la propriété maxx.
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
     * Obtient la valeur de la propriété maxy.
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
     * Définit la valeur de la propriété maxy.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMaxy(String value) {
        this.maxy = value;
    }

}
