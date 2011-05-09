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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v200.AbstractQueryExpressionType;

/**
 * <p>Java class for GetPropertyValueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPropertyValueType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wfs/2.0}BaseRequestType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}AbstractQueryExpression"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/wfs/2.0}StandardResolveParameters"/>
 *       &lt;attGroup ref="{http://www.opengis.net/wfs/2.0}StandardPresentationParameters"/>
 *       &lt;attribute name="valueReference" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="resolvePath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetPropertyValueType", propOrder = {
    "abstractQueryExpression"
})
public class GetPropertyValueType extends BaseRequestType {

    @XmlElementRef(name = "AbstractQueryExpression", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractQueryExpressionType> abstractQueryExpression;
    @XmlAttribute(required = true)
    private String valueReference;
    @XmlAttribute
    private String resolvePath;
    @XmlAttribute
    private ResolveValueType resolve;
    @XmlAttribute
    private String resolveDepth;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private int resolveTimeout = 300;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private int startIndex = 0;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private int count;
    @XmlAttribute
    private ResultTypeType resultType;
    @XmlAttribute
    private String outputFormat;

    /**
     * Gets the value of the abstractQueryExpression property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link StoredQueryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractQueryExpressionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractAdhocQueryExpressionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link QueryType }{@code >}
     *     
     */
    public JAXBElement<? extends AbstractQueryExpressionType> getAbstractQueryExpression() {
        return abstractQueryExpression;
    }

    /**
     * Sets the value of the abstractQueryExpression property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link StoredQueryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractQueryExpressionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractAdhocQueryExpressionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link QueryType }{@code >}
     *     
     */
    public void setAbstractQueryExpression(JAXBElement<? extends AbstractQueryExpressionType> value) {
        this.abstractQueryExpression = ((JAXBElement<? extends AbstractQueryExpressionType> ) value);
    }

    /**
     * Gets the value of the valueReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValueReference() {
        return valueReference;
    }

    /**
     * Sets the value of the valueReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueReference(String value) {
        this.valueReference = value;
    }

    /**
     * Gets the value of the resolvePath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResolvePath() {
        return resolvePath;
    }

    /**
     * Sets the value of the resolvePath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResolvePath(String value) {
        this.resolvePath = value;
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
     *     {@link int }
     *     
     */
    public int getResolveTimeout() {
        return resolveTimeout;
    }

    /**
     * Sets the value of the resolveTimeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link int }
     *     
     */
    public void setResolveTimeout(int value) {
        this.resolveTimeout = value;
    }

    /**
     * Gets the value of the startIndex property.
     * 
     * @return
     *     possible object is
     *     {@link int }
     *     
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Sets the value of the startIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link int }
     *     
     */
    public void setStartIndex(int value) {
        this.startIndex = value;
    }

    /**
     * Gets the value of the count property.
     * 
     * @return
     *     possible object is
     *     {@link int }
     *     
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     * @param value
     *     allowed object is
     *     {@link int }
     *     
     */
    public void setCount(int value) {
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

}
