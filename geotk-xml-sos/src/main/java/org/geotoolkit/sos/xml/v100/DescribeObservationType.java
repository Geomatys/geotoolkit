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
package org.geotoolkit.sos.xml.v100;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sos/1.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element name="observedProperty" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeObservationType", propOrder = {
    "observedProperty"
})
@XmlRootElement(name = "DescribeObservationType")
public class DescribeObservationType extends RequestBaseType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String observedProperty;

    /**
     * An empty constructor used by jaxB
     */
     DescribeObservationType(){}

    public DescribeObservationType(final String observedProperty) {
        this.observedProperty = observedProperty;
    }

    public DescribeObservationType(final String version, final String observedProperty) {
        super(version);
        this.observedProperty = observedProperty;
    }

    /**
     * Gets the value of the observedProperty property.
     */
    public String getObservedProperty() {
        return observedProperty;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DescribeObservationType && super.equals(object)) {
            final DescribeObservationType that = (DescribeObservationType) object;
            return Objects.equals(this.observedProperty, that.observedProperty);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.observedProperty != null ? this.observedProperty.hashCode() : 0);
        return hash;
    }


}
