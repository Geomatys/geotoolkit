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
package org.geotoolkit.sml.xml.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractRelaxNG;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorML/1.0.1}ruleLanguageType">
 *       &lt;sequence minOccurs="0">
 *         &lt;any/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "any"
})
public class RelaxNG extends RuleLanguageType implements AbstractRelaxNG {

    @XmlAnyElement(lax = true)
    private Object any;

    public RelaxNG() {

    }

    public RelaxNG(final AbstractRelaxNG rn) {
        if (rn != null && rn.getAny() != null) {
            this.any = rn.getAny();
        }
    }

    /**
     * Gets the value of the any property.
     *
     * @return
     *     possible object is
     *     {@link Element }
     *     {@link Object }
     *
     */
    public Object getAny() {
        return any;
    }

    /**
     * Sets the value of the any property.
     *
     * @param value
     *     allowed object is
     *     {@link Element }
     *     {@link Object }
     *
     */
    public void setAny(final Object value) {
        this.any = value;
    }

}
