/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019
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

package org.geotoolkit.ops.xml.v110;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour QueryType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="QueryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="role" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="request"/>
 *             &lt;enumeration value="example"/>
 *             &lt;enumeration value="related"/>
 *             &lt;enumeration value="correction"/>
 *             &lt;enumeration value="subset"/>
 *             &lt;enumeration value="superset"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="title">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="256"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="totalResults">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="searchTerms" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="count">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="startIndex">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="startPage">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="language" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" default="*" />
 *       &lt;attribute name="inputEncoding" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" default="UTF-8" />
 *       &lt;attribute name="outputEncoding" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryType")
@XmlSeeAlso({
    InspireQueryType.class
})
public class QueryType {

    @XmlAttribute(name = "role", required = true)
    protected String role;
    @XmlAttribute(name = "title")
    protected String title;
    @XmlAttribute(name = "totalResults")
    protected BigInteger totalResults;
    @XmlAttribute(name = "searchTerms")
    @XmlSchemaType(name = "anySimpleType")
    protected String searchTerms;
    @XmlAttribute(name = "count")
    protected BigInteger count;
    @XmlAttribute(name = "startIndex")
    protected BigInteger startIndex;
    @XmlAttribute(name = "startPage")
    protected BigInteger startPage;
    @XmlAttribute(name = "language")
    @XmlSchemaType(name = "anySimpleType")
    protected String language;
    @XmlAttribute(name = "inputEncoding")
    @XmlSchemaType(name = "anySimpleType")
    protected String inputEncoding;
    @XmlAttribute(name = "outputEncoding")
    @XmlSchemaType(name = "anySimpleType")
    protected String outputEncoding;

    public QueryType() {

    }

    public QueryType(QueryType that) {
        if (that != null) {
            this.count = that.count;
            this.inputEncoding = that.inputEncoding;
            this.language = that.language;
            this.outputEncoding = that.outputEncoding;
            this.role = that.role;
            this.searchTerms = that.searchTerms;
            this.startIndex = that.startIndex;
            this.startPage = that.startPage;
            this.title = that.title;
            this.totalResults = that.totalResults;
        }
    }

    /**
     * Obtient la valeur de la propriété role.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRole() {
        return role;
    }

    /**
     * Définit la valeur de la propriété role.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Obtient la valeur de la propriété title.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * Définit la valeur de la propriété title.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Obtient la valeur de la propriété totalResults.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getTotalResults() {
        return totalResults;
    }

    /**
     * Définit la valeur de la propriété totalResults.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setTotalResults(BigInteger value) {
        this.totalResults = value;
    }

    /**
     * Obtient la valeur de la propriété searchTerms.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSearchTerms() {
        return searchTerms;
    }

    /**
     * Définit la valeur de la propriété searchTerms.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSearchTerms(String value) {
        this.searchTerms = value;
    }

    /**
     * Obtient la valeur de la propriété count.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getCount() {
        return count;
    }

    /**
     * Définit la valeur de la propriété count.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setCount(BigInteger value) {
        this.count = value;
    }

    /**
     * Obtient la valeur de la propriété startIndex.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getStartIndex() {
        return startIndex;
    }

    /**
     * Définit la valeur de la propriété startIndex.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setStartIndex(BigInteger value) {
        this.startIndex = value;
    }

    /**
     * Obtient la valeur de la propriété startPage.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getStartPage() {
        return startPage;
    }

    /**
     * Définit la valeur de la propriété startPage.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setStartPage(BigInteger value) {
        this.startPage = value;
    }

    /**
     * Obtient la valeur de la propriété language.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLanguage() {
        if (language == null) {
            return "*";
        } else {
            return language;
        }
    }

    /**
     * Définit la valeur de la propriété language.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Obtient la valeur de la propriété inputEncoding.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInputEncoding() {
        if (inputEncoding == null) {
            return "UTF-8";
        } else {
            return inputEncoding;
        }
    }

    /**
     * Définit la valeur de la propriété inputEncoding.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInputEncoding(String value) {
        this.inputEncoding = value;
    }

    /**
     * Obtient la valeur de la propriété outputEncoding.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOutputEncoding() {
        return outputEncoding;
    }

    /**
     * Définit la valeur de la propriété outputEncoding.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOutputEncoding(String value) {
        this.outputEncoding = value;
    }

}
