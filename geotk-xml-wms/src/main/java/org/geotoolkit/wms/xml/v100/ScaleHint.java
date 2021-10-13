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


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "ScaleHint")
public class ScaleHint {

    @XmlAttribute(name = "min", required = true)
    protected Double min;
    @XmlAttribute(name = "max", required = true)
    protected Double max;

    /**
     * Obtient la valeur de la propriété min.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public double getMin() {
        return min;
    }

    /**
     * Définit la valeur de la propriété min.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMin(Double value) {
        this.min = value;
    }

    /**
     * Obtient la valeur de la propriété max.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public double getMax() {
        return max;
    }

    /**
     * Définit la valeur de la propriété max.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMax(Double value) {
        this.max = value;
    }

}
