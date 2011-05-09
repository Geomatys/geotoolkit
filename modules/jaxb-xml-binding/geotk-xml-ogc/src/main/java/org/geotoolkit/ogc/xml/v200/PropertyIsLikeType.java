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


/**
 * <p>Java class for PropertyIsLikeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PropertyIsLikeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/fes/2.0}ComparisonOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}expression" maxOccurs="2" minOccurs="2"/>
 *       &lt;/sequence>
 *       &lt;attribute name="wildCard" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="singleChar" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="escapeChar" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyIsLikeType", propOrder = {
    "expression"
})
public class PropertyIsLikeType
    extends ComparisonOpsType
{

    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private List<JAXBElement<?>> expression;
    @XmlAttribute(required = true)
    private String wildCard;
    @XmlAttribute(required = true)
    private String singleChar;
    @XmlAttribute(required = true)
    private String escapeChar;

    /**
     * Gets the value of the expression property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the expression property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExpression().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link LiteralType }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link FunctionType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getExpression() {
        if (expression == null) {
            expression = new ArrayList<JAXBElement<?>>();
        }
        return this.expression;
    }

    /**
     * Gets the value of the wildCard property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWildCard() {
        return wildCard;
    }

    /**
     * Sets the value of the wildCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWildCard(String value) {
        this.wildCard = value;
    }

    /**
     * Gets the value of the singleChar property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSingleChar() {
        return singleChar;
    }

    /**
     * Sets the value of the singleChar property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSingleChar(String value) {
        this.singleChar = value;
    }

    /**
     * Gets the value of the escapeChar property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEscapeChar() {
        return escapeChar;
    }

    /**
     * Sets the value of the escapeChar property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEscapeChar(String value) {
        this.escapeChar = value;
    }

}
