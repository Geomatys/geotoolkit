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


package org.geotoolkit.wfs.xml.v200;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v200.AbstractQueryExpressionType;


/**
 * <p>Java class for GetFeatureType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetFeatureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wfs/2.0}BaseRequestType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}AbstractQueryExpression" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/wfs/2.0}StandardPresentationParameters"/>
 *       &lt;attGroup ref="{http://www.opengis.net/wfs/2.0}StandardResolveParameters"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetFeatureType", propOrder = {
    "abstractQueryExpression"
})
@XmlSeeAlso({
    GetFeatureWithLockType.class
})
public class GetFeatureType extends BaseRequestType {
 

    @XmlElementRef(name = "AbstractQueryExpression", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractQueryExpressionType>> abstractQueryExpression;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private BigInteger startIndex;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private BigInteger count;
    @XmlAttribute
    private ResultTypeType resultType;
    @XmlAttribute
    private String outputFormat;
    @XmlAttribute
    private ResolveValueType resolve;
    @XmlAttribute
    private String resolveDepth;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private BigInteger resolveTimeout;

    /**
     * Gets the value of the abstractQueryExpression property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractQueryExpression property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractQueryExpression().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link StoredQueryType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractQueryExpressionType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractAdhocQueryExpressionType }{@code >}
     * {@link JAXBElement }{@code <}{@link QueryType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends AbstractQueryExpressionType>> getAbstractQueryExpression() {
        if (abstractQueryExpression == null) {
            abstractQueryExpression = new ArrayList<JAXBElement<? extends AbstractQueryExpressionType>>();
        }
        return this.abstractQueryExpression;
    }

    /**
     * Gets the value of the startIndex property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStartIndex() {
        if (startIndex == null) {
            return new BigInteger("0");
        } else {
            return startIndex;
        }
    }

    /**
     * Sets the value of the startIndex property.
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
     * Gets the value of the count property.
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
     * Sets the value of the count property.
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
     * Gets the value of the resultType property.
     * 
     * @return
     *     possible object is
     *     {@link ResultTypeType }
     *     
     */
    public ResultTypeType getResultType() {
        if (resultType == null) {
            return ResultTypeType.RESULTS;
        } else {
            return resultType;
        }
    }

    /**
     * Sets the value of the resultType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultTypeType }
     *     
     */
    public void setResultType(ResultTypeType value) {
        this.resultType = value;
    }

    /**
     * Gets the value of the outputFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutputFormat() {
        if (outputFormat == null) {
            return "application/gml+xml; version=3.2";
        } else {
            return outputFormat;
        }
    }

    /**
     * Sets the value of the outputFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutputFormat(String value) {
        this.outputFormat = value;
    }

    /**
     * Gets the value of the resolve property.
     * 
     * @return
     *     possible object is
     *     {@link ResolveValueType }
     *     
     */
    public ResolveValueType getResolve() {
        if (resolve == null) {
            return ResolveValueType.NONE;
        } else {
            return resolve;
        }
    }

    /**
     * Sets the value of the resolve property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResolveValueType }
     *     
     */
    public void setResolve(ResolveValueType value) {
        this.resolve = value;
    }

    /**
     * Gets the value of the resolveDepth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResolveDepth() {
        if (resolveDepth == null) {
            return "*";
        } else {
            return resolveDepth;
        }
    }

    /**
     * Sets the value of the resolveDepth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResolveDepth(String value) {
        this.resolveDepth = value;
    }

    /**
     * Gets the value of the resolveTimeout property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getResolveTimeout() {
        if (resolveTimeout == null) {
            return new BigInteger("300");
        } else {
            return resolveTimeout;
        }
    }

    /**
     * Sets the value of the resolveTimeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setResolveTimeout(BigInteger value) {
        this.resolveTimeout = value;
    }

}
