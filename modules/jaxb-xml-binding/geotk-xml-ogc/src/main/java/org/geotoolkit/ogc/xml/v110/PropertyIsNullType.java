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

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.Expression;
import org.opengis.filter.NullOperator;


/**
 * <p>Java class for PropertyIsNullType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PropertyIsNullType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}ComparisonOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}PropertyName"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyIsNullType", propOrder = {
    "propertyName"
})
public class PropertyIsNullType extends ComparisonOpsType implements NullOperator {

    @XmlElement(name = "PropertyName", required = true)
    private PropertyNameType propertyName;

    /**
     * An empty constructor used by JAXB
     */
     public PropertyIsNullType() {
     }

     /**
     * Build a new Property is null operator.
     */
     public PropertyIsNullType(final PropertyNameType prop) {
         this.propertyName = prop;
     }

     public PropertyIsNullType(final PropertyIsNullType that) {
        if (that != null && that.propertyName != null) {
            this.propertyName = new PropertyNameType(that.propertyName);
        }
    }

    /**
     * Gets the value of the propertyName property.
     */
    public PropertyNameType getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the value of the propertyName property.
     */
    public void setPropertyName(final PropertyNameType propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (propertyName != null) {
            s.append("PropertyName= ").append(propertyName.toString()).append('\n');
        } else {
            s.append(" PropertyName is null");
        }
        return s.toString();
    }

    @Override
    public List getExpressions() {
        return Collections.singletonList(getExpression());
    }

    /**
     * implements PropertyIsNull Types interface
     */
    public Expression getExpression() {
        return propertyName;
    }

    @Override
    public boolean test(final Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ComparisonOpsType getClone() {
        return new PropertyIsNullType(this);
    }
}
