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
package org.geotoolkit.ogc.xml.v110;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Existence_CapabilitiesType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Existence_CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ExistenceOperators" type="{http://www.opengis.net/ogc}ExistenceOperatorsType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Existence_CapabilitiesType", propOrder = {
    "existenceOperators"
})
public class ExistenceCapabilitiesType {

    @XmlElement(name = "ExistenceOperators")
    private ExistenceOperatorsType existenceOperators;

    /**
     * Gets the value of the existenceOperators property.
     */
    public ExistenceOperatorsType getExistenceOperators() {
        return existenceOperators;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ExistenceCapabilitiesType]").append("\n");
        if (existenceOperators != null) {
            sb.append("existenceOperators: ").append(existenceOperators).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

       if (object instanceof ExistenceCapabilitiesType) {
           final ExistenceCapabilitiesType that = (ExistenceCapabilitiesType) object;

            return Objects.equals(this.existenceOperators, that.existenceOperators);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.existenceOperators != null ? this.existenceOperators.hashCode() : 0);
        return hash;
    }
}
