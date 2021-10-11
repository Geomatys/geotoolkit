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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.Query;
import org.geotoolkit.csw.xml.QueryConstraint;
import org.geotoolkit.ogc.xml.v110.SortByType;


/**
 * Specifies a query to execute against instances of one or more object types.
 * A set of ElementName elements may be included to specify an adhoc view of the csw:Record instances in the result set.
 * Otherwise, use ElementSetName to specify a predefined view.
 * The Constraint element contains a query filter expressed in a supported query language.
 * A sorting criterion that specifies a property to sort by may be included.
 *
 *  typeNames - a list of object types to query.
 *
 * <p>Java class for QueryType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="QueryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/2.0.2}AbstractQueryType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/cat/csw/2.0.2}ElementSetName"/>
 *           &lt;element name="ElementName" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/cat/csw/2.0.2}Constraint" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}SortBy" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="typeNames" use="required" type="{http://www.opengis.net/cat/csw/2.0.2}TypeNameListType" />
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
    "constraint",
    "sortBy"
})
@XmlRootElement(name = "Query")
public class QueryType extends AbstractQueryType implements Query {

    @XmlElement(name = "ElementSetName", defaultValue = "summary")
    private ElementSetNameType elementSetName;
    @XmlElement(name = "ElementName")
    private List<QName> elementName;
    @XmlElement(name = "Constraint")
    private QueryConstraintType constraint;
    @XmlElement(name = "SortBy", namespace = "http://www.opengis.net/ogc")
    private SortByType sortBy;
    @XmlAttribute(required = true)
    private List<QName> typeNames;

    /**
     * Empty constructor used by JAXB
     */
    public QueryType() {

    }

    /**
     * Build a new Query
     *
     * @param typeNames A list of QName describing the different types of Record.
     * @param elementSetName The element set required for this query.
     * @param sortBy A sort by Object.
     * @param constraint A constraint object containing the different filters to constraint the query.
     */
    public QueryType(final List<QName> typeNames, final ElementSetNameType elementSetName, final SortByType sortBy,
            final QueryConstraintType constraint) {

        this.typeNames      = typeNames;
        this.elementSetName = elementSetName;
        this.sortBy         = sortBy;
        this.constraint     = constraint;
    }

    /**
     * Build a new Query
     *
     * @param typeNames A list of QName describing the different types of Record.
     * @param elementName The list of QName describing the element set required for this query.
     * @param sortBy A sort by Object.
     * @param constraint A constraint object containing the different filters to constraint the query.
     */
    public QueryType(final List<QName> typeNames, final List<QName> elementName, final SortByType sortBy,
            final QueryConstraintType constraint) {

        this.typeNames      = typeNames;
        this.elementName    = elementName;
        this.sortBy         = sortBy;
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
            if (other.sortBy != null) {
                this.sortBy = new SortByType(other.sortBy);
            }
            if (other.typeNames != null) {
                this.typeNames = new ArrayList<>(other.typeNames);
            }
        }
    }

    /**
     * Gets the value of the elementSetName property.
     */
    @Override
    public ElementSetNameType getElementSetName() {
        return elementSetName;
    }

    /**
     * Sets the value of the elementSetName property.
     */
    public void setElementSetName(final ElementSetNameType elementSetName) {
        this.elementSetName = elementSetName;
    }

    /**
     * Gets the value of the elementName property.
     * (unmodifiable)
     */
    @Override
    public List<QName> getElementName() {
        if (elementName == null) {
            elementName = new ArrayList<>();
        }
        return Collections.unmodifiableList(elementName);
    }

    /**
     * Sets the value of the elementName property.
     */
    public void setElementName(final QName elementName) {
        if (this.elementName == null) {
            this.elementName = new ArrayList<>();
        }
        this.elementName.add(elementName);
    }

    /**
     * Sets the value of the elementName property.
     */
    public void setElementName(final List<QName> elementName) {
        this.elementName = elementName;
    }

    /**
     * Gets the value of the constraint property.
     */
    @Override
    public QueryConstraintType getConstraint() {
        return constraint;
    }

    @Override
    public void setConstraint(final QueryConstraint value) {
        if (value instanceof QueryConstraint) {
            this.constraint = (QueryConstraintType) value;
        } else {
            throw new IllegalArgumentException("unexpected version of the query constraint object (not v202)");
        }
    }

    /**
     * Gets the value of the sortBy property.
     */
    @Override
    public SortByType getSortBy() {
        return sortBy;
    }

    /**
     * Sets the value of the sortBy property.
     */
    public void setSortBy(final SortByType sortBy) {
        this.sortBy = sortBy;
    }

    /**
     * Gets the value of the typeNames property.
     * (unmodifiable)
     */
    @Override
    public List<QName> getTypeNames() {
        if (typeNames == null) {
            typeNames = new ArrayList<>();
        }
        return Collections.unmodifiableList(typeNames);
    }

    @Override
    public void setTypeNames(final List<QName> typeNames) {
        this.typeNames = typeNames;
    }

    /**
     * Sets the value of the typeNames property.
     */
    public void setTypeNames(final QName typeName) {
        if (typeNames == null) {
            typeNames = new ArrayList<>();
        }
        typeNames.add(typeName);
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
                   Objects.equals(this.sortBy,  that.sortBy)   &&
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
        hash = 23 * hash + (this.sortBy != null ? this.sortBy.hashCode() : 0);
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
        if (sortBy != null) {
            s.append("sortBy: ").append(sortBy).append('\n');
        }
        if (typeNames != null) {
            s.append("typeNames: ").append(typeNames).append('\n');
        }
        return s.toString();
    }


}
