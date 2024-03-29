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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3c.dom.Element;


/**
 * <p>Java class for TopicNamespaceType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TopicNamespaceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsn/t-1}ExtensibleDocumented">
 *       &lt;sequence>
 *         &lt;element name="Topic" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://docs.oasis-open.org/wsn/t-1}TopicType">
 *                 &lt;attribute name="parent" type="{http://docs.oasis-open.org/wsn/t-1}ConcreteTopicExpression" />
 *                 &lt;anyAttribute processContents='lax' namespace='##other'/>
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="targetNamespace" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
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
@XmlType(name = "TopicNamespaceType", propOrder = {
    "topic",
    "any"
})
public class TopicNamespaceType extends ExtensibleDocumented {

    @XmlElement(name = "Topic")
    private List<TopicNamespaceType.Topic> topic;
    @XmlAnyElement(lax = true)
    private List<Object> any;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    private String name;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String targetNamespace;
    @XmlAttribute(name = "final")
    private Boolean _final;

    /**
     * Gets the value of the topic property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link TopicNamespaceType.Topic }
     *
     *
     */
    public List<TopicNamespaceType.Topic> getTopic() {
        if (topic == null) {
            topic = new ArrayList<TopicNamespaceType.Topic>();
        }
        return this.topic;
    }

    /**
     * Gets the value of the any property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
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
     * Gets the value of the targetNamespace property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * Sets the value of the targetNamespace property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTargetNamespace(String value) {
        this.targetNamespace = value;
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


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://docs.oasis-open.org/wsn/t-1}TopicType">
     *       &lt;attribute name="parent" type="{http://docs.oasis-open.org/wsn/t-1}ConcreteTopicExpression" />
     *       &lt;anyAttribute processContents='lax' namespace='##other'/>
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Topic extends TopicType {

        @XmlAttribute
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        private String parent;

        /**
         * Gets the value of the parent property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getParent() {
            return parent;
        }

        /**
         * Sets the value of the parent property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setParent(String value) {
            this.parent = value;
        }

    }

}
