/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.ogc.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.ogc.xml.SortBy;
import org.opengis.filter.Filter;


/**
 * <p>Java class for AbstractAdhocQueryExpressionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractAdhocQueryExpressionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/fes/2.0}AbstractQueryExpressionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}AbstractProjectionClause" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}AbstractSelectionClause" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}AbstractSortingClause" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="typeNames" use="required" type="{http://www.opengis.net/fes/2.0}TypeNamesListType" />
 *       &lt;attribute name="aliases" type="{http://www.opengis.net/fes/2.0}AliasesType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractAdhocQueryExpressionType", propOrder = {
    "abstractProjectionClause",
    "abstractSelectionClause",
    "abstractSortingClause"
})
/*@XmlSeeAlso({
    QueryType.class
})*/
public abstract class AbstractAdhocQueryExpressionType extends AbstractQueryExpressionType {

    @XmlElementRef(name = "AbstractProjectionClause", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private List<JAXBElement<?>> abstractProjectionClause;
    @XmlElementRef(name = "AbstractSelectionClause", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private JAXBElement<?> abstractSelectionClause;
    @XmlElementRef(name = "AbstractSortingClause", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private JAXBElement<?> abstractSortingClause;
    @XmlAttribute(required = true)
    private List<QName> typeNames;
    @XmlAttribute
    private List<String> aliases;

    public AbstractAdhocQueryExpressionType() {

    }

    public AbstractAdhocQueryExpressionType(final FilterType filter, final List<QName> typeName) {
        this.typeNames = typeName;
        if (filter != null) {
            final ObjectFactory factory = new ObjectFactory();
            this.abstractSelectionClause = factory.createFilter(filter);
        }
    }
    /**
     * Gets the value of the abstractProjectionClause property.
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link PropertyName }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getAbstractProjectionClause() {
        if (abstractProjectionClause == null) {
            abstractProjectionClause = new ArrayList<JAXBElement<?>>();
        }
        return this.abstractProjectionClause;
    }
    
    public List<Object> getPropertyNames() {
        final List<Object> propertyNames = new ArrayList<Object>();
        if (abstractProjectionClause != null) {
            for (JAXBElement<?> jb : abstractProjectionClause) {
                propertyNames.add(jb.getValue());
            }
        }
        return propertyNames;
    }
    
    /**
     * Gets the value of the abstractSelectionClause property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link FilterType }{@code >}
     *     
     */
    public JAXBElement<?> getAbstractSelectionClause() {
        return abstractSelectionClause;
    }
    
    public Filter getFilter() {
        if (abstractSelectionClause != null && abstractSelectionClause.getValue() instanceof Filter) {
            return (Filter) abstractSelectionClause.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the abstractSelectionClause property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link FilterType }{@code >}
     *     
     */
    public void setAbstractSelectionClause(JAXBElement<?> value) {
        this.abstractSelectionClause = ((JAXBElement<?> ) value);
    }

    /**
     * Gets the value of the abstractSortingClause property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SortByType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public JAXBElement<?> getAbstractSortingClause() {
        return abstractSortingClause;
    }

    public SortBy getSortBy() {
        if (abstractSortingClause != null && abstractSortingClause.getValue() instanceof SortBy) {
            return (SortBy) abstractSortingClause.getValue();
        }
        return null;
    }

    
    /**
     * Sets the value of the abstractSortingClause property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SortByType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public void setAbstractSortingClause(JAXBElement<?> value) {
        this.abstractSortingClause = ((JAXBElement<?> ) value);
    }

    /**
     * Gets the value of the typeNames property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link QName }
     * 
     * 
     */
    public List<QName> getTypeNames() {
        if (typeNames == null) {
            typeNames = new ArrayList<QName>();
        }
        return this.typeNames;
    }

    /**
     * Gets the value of the aliases property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAliases() {
        if (aliases == null) {
            aliases = new ArrayList<String>();
        }
        return this.aliases;
    }

}
