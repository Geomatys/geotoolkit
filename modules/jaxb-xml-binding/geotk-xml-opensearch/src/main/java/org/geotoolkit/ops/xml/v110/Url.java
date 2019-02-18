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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour url complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="url">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="template" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="rel">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="results"/>
 *             &lt;enumeration value="suggestions"/>
 *             &lt;enumeration value="self"/>
 *             &lt;enumeration value="collection"/>
 *             &lt;enumeration value="describedby"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="indexOffset" default="1">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="pageOffset" default="1">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "url")
public class Url {

    @XmlAttribute(name = "template", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String template;
    @XmlAttribute(name = "type", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String type;
    @XmlAttribute(name = "rel")
    protected String rel;
    @XmlAttribute(name = "indexOffset")
    protected BigInteger indexOffset;
    @XmlAttribute(name = "pageOffset")
    protected BigInteger pageOffset;

    public Url() {

    }

    public Url(String type, String template) {
        this.type = type;
        this.template = template;
    }
    /**
     * Obtient la valeur de la propriété template.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Définit la valeur de la propriété template.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTemplate(String value) {
        this.template = value;
    }

    /**
     * Obtient la valeur de la propriété type.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propriété rel.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRel() {
        return rel;
    }

    /**
     * Définit la valeur de la propriété rel.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRel(String value) {
        this.rel = value;
    }

    /**
     * Obtient la valeur de la propriété indexOffset.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getIndexOffset() {
        if (indexOffset == null) {
            return new BigInteger("1");
        } else {
            return indexOffset;
        }
    }

    /**
     * Définit la valeur de la propriété indexOffset.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setIndexOffset(BigInteger value) {
        this.indexOffset = value;
    }

    /**
     * Obtient la valeur de la propriété pageOffset.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getPageOffset() {
        if (pageOffset == null) {
            return new BigInteger("1");
        } else {
            return pageOffset;
        }
    }

    /**
     * Définit la valeur de la propriété pageOffset.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setPageOffset(BigInteger value) {
        this.pageOffset = value;
    }

}
