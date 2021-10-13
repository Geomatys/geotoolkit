/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import org.geotoolkit.sml.xml.AbstractValues;
import org.apache.sis.util.ComparisonMode;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}anyType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Values extends SensorObject implements AbstractValues {

    /*@XmlAnyElement
    private List<Element> any; we need direct String value. TODO see how to handle both any xml and String value
     */

    @XmlValue
    private String any;

    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public Values() {

    }

    public Values(final AbstractValues v) {
        if (v != null) {
            this.any             = v.getAny();
            this.otherAttributes = v.getOtherAttributes();
        }
    }
    /**
     * Gets the value of the any property.
     *
     */
    public String getAny() {
        return this.any;
    }

    public void setAny(final String any) {
        this.any = any;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof Values) {
            final Values that = (Values) object;
            return Objects.equals(this.any,          that.any)   &&
                   Objects.equals(this.otherAttributes,    that.otherAttributes) ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + (this.any != null ? this.any.hashCode() : 0);
        hash = 11 * hash + (this.otherAttributes != null? this.otherAttributes.hashCode() : 0);
        return hash;
    }

    /**
     * Retourne une representation de l'objet (debug).
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[Values]\n");
        if (any != null) {
            sb.append("any=").append(any).append('\n');

        }
        if (otherAttributes != null) {
            sb.append("otherAttributes:\n");
            for (Entry<QName,String> entry : otherAttributes.entrySet()) {
                sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append('\n');
            }
        }
        return sb.toString();
    }
}

