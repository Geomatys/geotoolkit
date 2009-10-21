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
package org.geotoolkit.csw.xml.v202;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.DomainValues;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for DomainValuesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DomainValuesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="PropertyName" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *           &lt;element name="ParameterName" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;/choice>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="ListOfValues" type="{http://www.opengis.net/cat/csw/2.0.2}ListOfValuesType"/>
 *           &lt;element name="ConceptualScheme" type="{http://www.opengis.net/cat/csw/2.0.2}ConceptualSchemeType"/>
 *           &lt;element name="RangeOfValues" type="{http://www.opengis.net/cat/csw/2.0.2}RangeOfValuesType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="uom" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DomainValuesType", propOrder = {
    "propertyName",
    "parameterName",
    "listOfValues",
    "conceptualScheme",
    "rangeOfValues"
})
public class DomainValuesType implements DomainValues {

    @XmlElement(name = "PropertyName")
    @XmlSchemaType(name = "anyURI")
    private String propertyName;
    @XmlElement(name = "ParameterName")
    @XmlSchemaType(name = "anyURI")
    private String parameterName;
    @XmlElement(name = "ListOfValues")
    private ListOfValuesType listOfValues;
    @XmlElement(name = "ConceptualScheme")
    private ConceptualSchemeType conceptualScheme;
    @XmlElement(name = "RangeOfValues")
    private RangeOfValuesType rangeOfValues;
    @XmlAttribute(required = true)
    private QName type;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String uom;

    /**
     * An empty constructor used by JAXB
     */
    public DomainValuesType() {
        
    }
    
    /**
     * Build a new Domain values type with the specified list Of Values.
     * One of parameterName or propertyName must be null.
     * 
     */
    public DomainValuesType(String parameterName, String propertyName, ListOfValuesType listOfValues, QName type) {
        
        if (propertyName != null && parameterName != null) {
            throw new IllegalArgumentException("One of propertyName or parameterName must be null");
        } else if (propertyName == null && parameterName == null) {
            throw new IllegalArgumentException("One of propertyName or parameterName must be filled");
        }
        this.propertyName  = propertyName;
        this.parameterName = parameterName;
        this.listOfValues  = listOfValues; 
        this.type          = type; 
    }

    /**
     * Build a new Domain values type with the specified list Of Values.
     * One of parameterName or propertyName must be null.
     *
     */
    public DomainValuesType(String parameterName, String propertyName, List<String> listOfValues, QName type) {

        if (propertyName != null && parameterName != null) {
            throw new IllegalArgumentException("One of propertyName or parameterName must be null");
        } else if (propertyName == null && parameterName == null) {
            throw new IllegalArgumentException("One of propertyName or parameterName must be filled");
        }
        this.propertyName  = propertyName;
        this.parameterName = parameterName;
        this.listOfValues  = new ListOfValuesType(listOfValues);
        this.type          = type;
    }
    
    /**
     * Gets the value of the propertyName property.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Gets the value of the parameterName property.
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * Gets the value of the listOfValues property.
     */
    public ListOfValuesType getListOfValues() {
        return listOfValues;
    }

    /**
     * Gets the value of the conceptualScheme property.
     */
    public ConceptualSchemeType getConceptualScheme() {
        return conceptualScheme;
    }

    /**
     * Gets the value of the rangeOfValues property.
     */
    public RangeOfValuesType getRangeOfValues() {
        return rangeOfValues;
    }

    /**
     * Gets the value of the type property.
     */
    public QName getType() {
        return type;
    }

    /**
     * Gets the value of the uom property.
     */
    public String getUom() {
        return uom;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[DomainValuesType]").append('\n');
        if (conceptualScheme != null) {
            sb.append("conceptualScheme:").append(conceptualScheme).append('\n');
        }
        if (listOfValues != null) {
            sb.append("listOfValues:").append(listOfValues).append('\n');
        }
        if (parameterName != null) {
            sb.append("parameterName:").append(parameterName).append('\n');
        }
        if (propertyName != null) {
            sb.append("propertyName:").append(propertyName).append('\n');
        }
        if (rangeOfValues != null) {
            sb.append("rangeOfValues:").append(rangeOfValues).append('\n');
        }
        if (type != null) {
            sb.append("type:").append(type).append('\n');
        }
        if (uom != null) {
            sb.append("uom:").append(uom).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DomainValuesType) {
            final DomainValuesType that = (DomainValuesType) object;

            return  Utilities.equals(this.conceptualScheme, that.conceptualScheme) &&
                    Utilities.equals(this.listOfValues,     that.listOfValues) &&
                    Utilities.equals(this.parameterName,    that.parameterName) &&
                    Utilities.equals(this.propertyName,     that.propertyName) &&
                    Utilities.equals(this.rangeOfValues,    that.rangeOfValues) &&
                    Utilities.equals(this.type,             that.type) &&
                    Utilities.equals(this.uom,              that.uom);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.propertyName != null ? this.propertyName.hashCode() : 0);
        hash = 97 * hash + (this.parameterName != null ? this.parameterName.hashCode() : 0);
        hash = 97 * hash + (this.listOfValues != null ? this.listOfValues.hashCode() : 0);
        hash = 97 * hash + (this.conceptualScheme != null ? this.conceptualScheme.hashCode() : 0);
        hash = 97 * hash + (this.rangeOfValues != null ? this.rangeOfValues.hashCode() : 0);
        hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 97 * hash + (this.uom != null ? this.uom.hashCode() : 0);
        return hash;
    }
    
}
