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
 * <p>Java class for ClassificationOperatorsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClassificationOperatorsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="ClassificationOperator" type="{http://www.opengis.net/ogc}ClassificationOperatorType"/>
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
@XmlType(name = "ClassificationOperatorsType", propOrder = {
    "classificationOperator"
})
public class ClassificationOperatorsType {

    @XmlElement(name = "ClassificationOperator", required = true)
    private List<ClassificationOperatorType> classificationOperator;

    /**
     * Gets the value of the classificationOperator property.
     * 
     */
    public List<ClassificationOperatorType> getClassificationOperator() {
        if (classificationOperator == null) {
            classificationOperator = new ArrayList<ClassificationOperatorType>();
        }
        return this.classificationOperator;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[ClassificationOperatorsType]").append("\n");
        if (classificationOperator != null) {
            sb.append("classificationOperator:\n");
            for (ClassificationOperatorType q: classificationOperator) {
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
        if (object instanceof ClassificationOperatorsType) {
            final ClassificationOperatorsType that = (ClassificationOperatorsType) object;

            return Utilities.equals(this.classificationOperator, that.classificationOperator);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.classificationOperator != null ? this.classificationOperator.hashCode() : 0);
        return hash;
    }
}
