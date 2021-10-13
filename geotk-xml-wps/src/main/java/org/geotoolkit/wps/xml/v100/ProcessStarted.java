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
package org.geotoolkit.wps.xml.v100;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * Indicates that this process has been has been accepted by the server, and processing has begun.
 *
 * A human-readable text string whose contents are left open to definition by each WPS server, but is expected to include any messages the server may wish to let the clients know. Such information could include how much longer the process may take to execute, or any warning conditions that may have been encountered to date. The client may display this text to a human user.
 *
 * <p>Java class for ProcessStarted complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ProcessStarted">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="percentCompleted">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *             &lt;minInclusive value="0"/>
 *             &lt;maxInclusive value="99"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessStartedType", propOrder = {
    "value"
})
public class ProcessStarted {

    @XmlValue
    protected String value;
    @XmlAttribute
    protected Integer percentCompleted;

    public ProcessStarted() {

    }

    public ProcessStarted(String value, Integer percentCompleted) {
        this.percentCompleted = percentCompleted;
        this.value = value;
    }

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Gets the value of the percentCompleted property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getPercentCompleted() {
        return percentCompleted;
    }

    /**
     * Sets the value of the percentCompleted property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setPercentCompleted(final Integer value) {
        this.percentCompleted = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (percentCompleted != null) {
            sb.append("percentCompleted:").append(percentCompleted).append('\n');
        }
        if (value != null) {
            sb.append("value:").append(value).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify that this entry is identical to the specified object.
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ProcessStarted) {
            final ProcessStarted that = (ProcessStarted) object;
            return Objects.equals(this.percentCompleted, that.percentCompleted) &&
                   Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.value);
        hash = 47 * hash + Objects.hashCode(this.percentCompleted);
        return hash;
    }
}
