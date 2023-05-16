/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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

package org.geotoolkit.csw.xml.v300;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.Query;
import org.geotoolkit.csw.xml.QueryConstraint;
import org.geotoolkit.ogc.xml.v200.SortByType;


/**
 *
 *             Specifies a query to execute against instances of one or
 *             more object types. A set of ElementName elements may be included
 *             to specify an adhoc view of the csw30:Record instances in the
 *             result set. Otherwise, use ElementSetName to specify a predefined
 *             view.  The Constraint element contains a query filter expressed
 *             in a supported query language. A sorting criterion that specifies
 *             a property to sort by may be included.
 *
 *             typeNames - a list of object types to query.
 *
 *
 * <p>Classe Java pour QueryType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="QueryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/3.0}AbstractQueryType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/cat/csw/3.0}ElementSetName"/>
 *           &lt;element name="ElementName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/cat/csw/3.0}Constraint" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}SortBy" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="typeNames" use="required" type="{http://www.opengis.net/cat/csw/3.0}TypeNameListType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryType", propOrder = {
    "elementSetName",
    "elementName",
    "constraint",
    "sortBy"
})
public class QueryType extends AbstractQueryType implements Query {

    @XmlElement(name = "ElementSetName")
    protected ElementSetNameType elementSetName;
    @XmlElement(name = "ElementName")
    protected List<String> elementName;
    @XmlElement(name = "Constraint")
    protected QueryConstraintType constraint;
    @XmlElement(name = "SortBy", namespace = "http://www.opengis.net/fes/2.0")
    protected SortByType sortBy;
    @XmlAttribute(name = "typeNames", required = true)
    protected List<QName> typeNames;

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
        this.sortBy         = sortBy;
        this.constraint     = constraint;
        if (elementName != null) {
            this.elementName = new ArrayList<>();
            for (QName q : elementName) {
                if (q.getNamespaceURI() != null) {
                    this.elementName.add(q.getNamespaceURI() + ':' + q.getLocalPart());
                } else {
                    this.elementName.add(q.getLocalPart());
                }
            }
        }
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
     * Obtient la valeur de la propriété elementSetName.
     *
     * @return
     *     possible object is
     *     {@link ElementSetNameType }
     *
     */
    @Override
    public ElementSetNameType getElementSetName() {
        return elementSetName;
    }

    /**
     * Définit la valeur de la propriété elementSetName.
     *
     * @param value
     *     allowed object is
     *     {@link ElementSetNameType }
     *
     */
    public void setElementSetName(ElementSetNameType value) {
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
        List<QName> results = new ArrayList<>();
        for (String e : elementName) {
            int pos = e.lastIndexOf(':');
            if (pos != -1) {
                results.add(new QName(e.substring(0, pos), e.substring(pos + 1)));
            } else {
                results.add(new QName(e));
            }
        }
        return results;
    }

    /**
     * Obtient la valeur de la propriété constraint.
     *
     * @return
     *     possible object is
     *     {@link QueryConstraintType }
     *
     */
    @Override
    public QueryConstraintType getConstraint() {
        return constraint;
    }


    @Override
    public void setConstraint(final QueryConstraint value) {
        if (value instanceof QueryConstraintType) {
            this.constraint = (QueryConstraintType) value;
        } else {
            throw new IllegalArgumentException("unexpected version of the query constraint object (not v300)");
        }
    }

    /**
     * Obtient la valeur de la propriété sortBy.
     *
     * @return
     *     possible object is
     *     {@link SortByType }
     *
     */
    @Override
    public SortByType getSortBy() {
        return sortBy;
    }

    /**
     * Définit la valeur de la propriété sortBy.
     *
     * @param value
     *     allowed object is
     *     {@link SortByType }
     *
     */
    public void setSortBy(SortByType value) {
        this.sortBy = value;
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
    public void setTypeNames(List<QName> typeNames) {
        this.typeNames = typeNames;
    }

}
