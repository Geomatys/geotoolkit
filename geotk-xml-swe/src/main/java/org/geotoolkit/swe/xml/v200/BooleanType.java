/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swe.xml.v200;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractBoolean;


/**
 * <p>Java class for BooleanType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BooleanType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractSimpleComponentType">
 *       &lt;sequence>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BooleanType", propOrder = {
    "value"
})
public class BooleanType extends AbstractSimpleComponentType implements AbstractBoolean {

    private Boolean value;

    public BooleanType() {

    }

    public BooleanType(final Boolean value, final String definition) {
        this(null, value, definition, null);
    }

    public BooleanType(final String id, final Boolean value, final String definition, final List<QualityPropertyType> quality) {
        super(id, definition, null, quality);
        this.value = value;
    }

    /**
     * Build a new TimeType
     */
    public BooleanType(final AbstractBoolean bool) {
        super(bool);
        if (bool != null) {
            this.value  = bool.isValue();
            this.axisID = bool.getAxisID();
            this.referenceFrame = bool.getReferenceFrame();
        }

    }

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    @Override
    public Boolean isValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setValue(Boolean value) {
        this.value = value;
    }

}
