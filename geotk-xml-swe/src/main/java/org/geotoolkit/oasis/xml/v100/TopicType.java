/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.oasis.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Java class for TopicType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TopicType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsn/t-1}ExtensibleDocumented">
 *       &lt;sequence>
 *         &lt;element name="MessagePattern" type="{http://docs.oasis-open.org/wsn/t-1}QueryExpressionType" minOccurs="0"/>
 *         &lt;element name="Topic" type="{http://docs.oasis-open.org/wsn/t-1}TopicType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="messageTypes">
 *         &lt;simpleType>
 *           &lt;list itemType="{http://www.w3.org/2001/XMLSchema}QName" />
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="final" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TopicType", propOrder = {
    "messagePattern",
    "topic",
    "any"
})
@XmlSeeAlso({
    TopicNamespaceType.Topic.class
})
public class TopicType extends ExtensibleDocumented {

    @XmlElement(name = "MessagePattern")
    private QueryExpressionType messagePattern;
    @XmlElement(name = "Topic")
    private List<TopicType> topic;
    @XmlAnyElement(lax = true)
    private List<Object> any;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    private String name;
    @XmlAttribute
    private List<QName> messageTypes;
    @XmlAttribute(name = "final")
    private Boolean _final;

    /**
     * Gets the value of the messagePattern property.
     *
     * @return
     *     possible object is
     *     {@link QueryExpressionType }
     *
     */
    public QueryExpressionType getMessagePattern() {
        return messagePattern;
    }

    /**
     * Sets the value of the messagePattern property.
     *
     * @param value
     *     allowed object is
     *     {@link QueryExpressionType }
     *
     */
    public void setMessagePattern(QueryExpressionType value) {
        this.messagePattern = value;
    }

    /**
     * Gets the value of the topic property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link TopicType }
     *
     *
     */
    public List<TopicType> getTopic() {
        if (topic == null) {
            topic = new ArrayList<TopicType>();
        }
        return this.topic;
    }

    /**
     * Gets the value of the any property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the messageTypes property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link QName }
     *
     */
    public List<QName> getMessageTypes() {
        if (messageTypes == null) {
            messageTypes = new ArrayList<QName>();
        }
        return this.messageTypes;
    }

    /**
     * Gets the value of the final property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isFinal() {
        if (_final == null) {
            return false;
        } else {
            return _final;
        }
    }

    /**
     * Sets the value of the final property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setFinal(Boolean value) {
        this._final = value;
    }

}
