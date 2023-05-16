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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.QueryConstraint;
import org.geotoolkit.ogc.xml.v200.FilterType;


/**
 *
 *             A search constraint that adheres to one of the following syntaxes:
 *             Filter   - OGC filter expression
 *             CqlText  - OGC CQL predicate
 *
 *
 * <p>Classe Java pour QueryConstraintType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="QueryConstraintType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}Filter"/>
 *         &lt;element name="CqlText" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/choice>
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryConstraintType", propOrder = {
    "filter",
    "cqlText"
})
public class QueryConstraintType implements QueryConstraint {

    @XmlElement(name = "Filter", namespace = "http://www.opengis.net/fes/2.0")
    protected FilterType filter;
    @XmlElement(name = "CqlText")
    protected String cqlText;
    @XmlAttribute(name = "version", required = true)
    protected String version;

    /**
     * Empty constructor used by JAXB
     */
    public QueryConstraintType(){

    }

    /**
     * Build a new Query constraint with a filter.
     */
    public QueryConstraintType(final FilterType filter, final String version){
        this.filter  = filter;
        this.version = version;
    }

    /**
     * Build a new Query constraint with a CQL text.
     */
    public QueryConstraintType(final String cqlText, final String version){
        this.cqlText = cqlText;
        this.version = version;
    }

    public QueryConstraintType(final QueryConstraintType other){
        if (other != null) {
            this.cqlText = other.cqlText;
            this.version = other.version;
            if (other.filter != null) {
                this.filter = new FilterType(other.filter);
            }
        }
    }

    /**
     * Obtient la valeur de la propriété filter.
     *
     * @return
     *     possible object is
     *     {@link FilterType }
     *
     */
    @Override
    public FilterType getFilter() {
        return filter;
    }

    /**
     * Définit la valeur de la propriété filter.
     *
     * @param value
     *     allowed object is
     *     {@link FilterType }
     *
     */
    public void setFilter(FilterType value) {
        this.filter = value;
    }

    /**
     * Obtient la valeur de la propriété cqlText.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCqlText() {
        return cqlText;
    }

    /**
     * Définit la valeur de la propriété cqlText.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCqlText(String value) {
        this.cqlText = value;
    }

    /**
     * Obtient la valeur de la propriété version.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersion() {
        return version;
    }

    /**
     * Définit la valeur de la propriété version.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

}
