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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for ExistenceOperatorsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExistenceOperatorsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="ExistenceOperator" type="{http://www.opengis.net/ogc}ExistenceOperatorType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExistenceOperatorsType", propOrder = {
    "existenceOperator"
})
public class ExistenceOperatorsType {

    @XmlElement(name = "ExistenceOperator", required = true)
    private List<ExistenceOperatorType> existenceOperator;

    /**
     * Gets the value of the existenceOperator property.
     * 
     */
    public List<ExistenceOperatorType> getExistenceOperator() {
        if (existenceOperator == null) {
            existenceOperator = new ArrayList<ExistenceOperatorType>();
        }
        return this.existenceOperator;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[ExistenceOperatorsType]").append("\n");
        if (existenceOperator != null) {
            sb.append("existenceOperator:\n");
            for (ExistenceOperatorType q: existenceOperator) {
                sb.append(q).append('\n');
            }
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
        if (object instanceof ExistenceOperatorsType) {
            final ExistenceOperatorsType that = (ExistenceOperatorsType) object;

            return Utilities.equals(this.existenceOperator, that.existenceOperator);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.existenceOperator != null ? this.existenceOperator.hashCode() : 0);
        return hash;
    }
}
