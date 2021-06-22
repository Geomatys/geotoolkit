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
 * <p>Java class for Classification_CapabilitiesType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Classification_CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ClassificationOperators" type="{http://www.opengis.net/ogc}ClassificationOperatorsType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Classification_CapabilitiesType", propOrder = {
    "classificationOperators"
})
public class ClassificationCapabilitiesType {

    @XmlElement(name = "ClassificationOperators")
    private ClassificationOperatorsType classificationOperators;

    /**
     * Gets the value of the classificationOperators property.
     */
    public ClassificationOperatorsType getClassificationOperators() {
        return classificationOperators;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ClassificationCapabilitiesType]").append("\n");
        if (classificationOperators != null) {
            sb.append("classificationOperators: ").append(classificationOperators).append('\n');
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

       if (object instanceof ClassificationCapabilitiesType) {
           final ClassificationCapabilitiesType that = (ClassificationCapabilitiesType) object;

            return Objects.equals(this.classificationOperators, that.classificationOperators);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.classificationOperators != null ? this.classificationOperators.hashCode() : 0);
        return hash;
    }
}
