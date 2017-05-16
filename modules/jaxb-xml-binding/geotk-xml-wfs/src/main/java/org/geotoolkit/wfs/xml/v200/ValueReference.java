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
package org.geotoolkit.wfs.xml.v200;

import javax.xml.bind.annotation.*;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="action" type="{http://www.opengis.net/wfs/2.0}UpdateActionType" default="replace" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
public class ValueReference {

    @XmlValue
    private String value;
    @XmlAttribute
    private UpdateActionType action;

    public ValueReference() {

    }

    public ValueReference(final String value, final UpdateActionType action) {
        this.action = action;
        this.value = value;
    }
    /**
     * Gets the value of the value property.
     *
     * @return possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is
     *     {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the action property.
     *
     * @return possible object is
     *     {@link UpdateActionType }
     *
     */
    public UpdateActionType getAction() {
        if (action == null) {
            return UpdateActionType.REPLACE;
        } else {
            return action;
        }
    }

    /**
     * Sets the value of the action property.
     *
     * @param value allowed object is
     *     {@link UpdateActionType }
     *
     */
    public void setAction(UpdateActionType value) {
        this.action = value;
    }
}
