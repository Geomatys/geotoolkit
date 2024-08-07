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
package org.geotoolkit.csw.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.Query;
import org.geotoolkit.csw.xml.QueryConstraint;
import org.geotoolkit.ogc.xml.SortBy;


/**
 * Specifies a query to execute against instances of one or more object types.
 * The ElementName elements specify the object types to be included in the result set.
 * The QueryConstraint element contains a query filter expressed in a supported query language.
 * A sorting criterion that specifies a property to sort on may be included.
 *
 *  typeNames - a list of object types implicated in the query specification.
 *
 *
 * <p>Java class for QueryType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="QueryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw}AbstractQueryType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/cat/csw}ElementSetName"/>
 *           &lt;element name="ElementName" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/cat/csw}Constraint" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="typeNames" use="required" type="{http://www.opengis.net/cat/csw}TypeNameListType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryType", propOrder = {
    "elementSetName",
    "elementName",
    "constraint"
})
@XmlRootElement(name = "Query")
public class QueryType extends AbstractQueryType implements Query{

    @XmlElement(name = "ElementSetName", defaultValue = "summary")
    private ElementSetNameType elementSetName;
    @XmlElement(name = "ElementName")
    @XmlSchemaType(name = "anyURI")
    private List<QName> elementName;
    @XmlElement(name = "Constraint")
    private QueryConstraintType constraint;
    @XmlAttribute(required = true)
    private List<QName> typeNames;

    /**
     * Empty constructor used by JAXB
     */
    QueryType() {

    }

    /**
     * Build a new Query
     */
    public QueryType(final List<QName> typeNames, final ElementSetNameType elementSetName, final QueryConstraintType constraint) {
        this.typeNames      = typeNames;
        this.elementSetName = elementSetName;
        this.constraint     = constraint;
    }

    /**
     *
     * @param typeNames
     * @param elementName
     * @param constraint
     */
    public QueryType(final List<QName> typeNames, final List<QName> elementName, final QueryConstraintType constraint) {
        this.typeNames      = typeNames;
        this.elementName    = elementName;
        this.constraint     = constraint;
    }

    public QueryType(final QueryType other) {
        if (other != null) {
            if (other.constraint != null) {
                this.constraint = new QueryConstraintType(other.constraint);
            }
            if (other.elementName != null) {
                this.elementName = new ArrayList<>(other.elementName);
            }
            if (other.elementSetName != null) {
                this.elementSetName = new ElementSetNameType(other.elementSetName);
            }
            if (other.typeNames != null) {
                this.typeNames = new ArrayList<>(other.typeNames);
            }
        }
    }

    /**
     * Gets the value of the elementSetName property.
     *
     */
    @Override
    public ElementSetNameType getElementSetName() {
        return elementSetName;
    }

    /**
     * Sets the value of the elementSetName property.
     *
     */
    public void setElementSetName(final ElementSetNameType value) {
        this.elementSetName = value;
    }

    /**
     * Gets the value of the elementName property.
     *
     */
    @Override
    public List<QName> getElementName() {
        if (elementName == null) {
            elementName = new ArrayList<>();
        }
        return this.elementName;
    }

    /**
     * Gets the value of the constraint property.
     *
     */
    @Override
    public QueryConstraintType getConstraint() {
        return constraint;
    }

    /**
     * Sets the value of the constraint property.
     */
    @Override
    public void setConstraint(final QueryConstraint value) {
        if (value instanceof QueryConstraint) {
            this.constraint = (QueryConstraintType) value;
        } else {
            throw new IllegalArgumentException("unexpected version of the query constraint object (not v200)");
        }
    }
    /**
     * Gets the value of the typeNames property.
     */
    @Override
    public List<QName> getTypeNames() {
        if (typeNames == null) {
            typeNames = new ArrayList<>();
        }
        return this.typeNames;
    }

    @Override
    public void setTypeNames(final List<QName> typeNames) {
        this.typeNames = typeNames;
    }

    /**
     * Always null in that version of csw.
     *
     * @return null.
     */
    @Override
    public SortBy getSortBy() {
        return null;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof QueryType) {
            final QueryType that = (QueryType) object;
            return Objects.equals(this.constraint,  that.constraint)   &&
                   Objects.equals(this.elementName,  that.elementName)   &&
                   Objects.equals(this.elementSetName,  that.elementSetName)   &&
                   Objects.equals(this.typeNames,  that.typeNames);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.elementSetName != null ? this.elementSetName.hashCode() : 0);
        hash = 23 * hash + (this.elementName != null ? this.elementName.hashCode() : 0);
        hash = 23 * hash + (this.constraint != null ? this.constraint.hashCode() : 0);
        hash = 23 * hash + (this.typeNames != null ? this.typeNames.hashCode() : 0);
        return hash;
    }

     @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[QueryType]").append('\n');

        if (elementName != null) {
            s.append("elementName: ").append(elementName).append('\n');
        }
        if (elementSetName != null) {
            s.append("elementSetName: ").append(elementSetName).append('\n');
        }
        if (constraint != null) {
            s.append("constraint: ").append(constraint).append('\n');
        }
        if (typeNames != null) {
            s.append("typeNames: ").append(typeNames).append('\n');
        }
        return s.toString();
    }

}
