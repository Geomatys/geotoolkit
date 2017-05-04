/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.AbstractGeometryType;
import org.geotoolkit.wps.xml.ComplexDataTypeDescription;
import org.w3c.dom.Element;


/**
 * <p>Java class for ComplexDataType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ComplexDataType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}DataDescriptionType">
 *       &lt;sequence>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComplexDataType", propOrder = {
    "any"
})
@XmlRootElement(name = "ComplexData")
public class ComplexDataType extends DataDescriptionType implements org.geotoolkit.wps.xml.ComplexDataType, ComplexDataTypeDescription {

    @XmlMixed
    @XmlElementRefs({
        @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml/3.2", type = AbstractGeometryType.class),
        @XmlElementRef(name = "math", namespace = "http://www.w3.org/1998/Math/MathML", type = org.geotoolkit.mathml.xml.Math.class),
        @XmlElementRef(name = "GeoJSON", namespace = "http://geotoolkit.org", type = org.geotoolkit.wps.xml.v100.ext.GeoJSONType.class)
    })
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    public ComplexDataType() {

    }

    public ComplexDataType(List<Format> format) {
        super(format);
    }

    /**
     * Gets the value of the any property.
     *
     * @return Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     *
     */
    @Override
    public List<Object> getContent() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (any != null) {
            sb.append("any:\n");
            for (Object out : any) {
                sb.append(out).append('\n');
            }
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
        if (object instanceof ComplexDataType && super.equals(object)) {
            final ComplexDataType that = (ComplexDataType) object;
            return Objects.equals(this.any, that.any);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.any);
        return hash;
    }
}
