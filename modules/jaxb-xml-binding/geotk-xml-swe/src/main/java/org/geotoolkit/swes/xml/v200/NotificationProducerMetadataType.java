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

package org.geotoolkit.swes.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.oasis.xml.v100.TopicNamespaceType;
import org.geotoolkit.oasis.xml.v100.TopicSetType;
import org.geotoolkit.w3c.adressing.xml.v2005.EndpointReferenceType;


/**
 * <p>Java class for NotificationProducerMetadataType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="NotificationProducerMetadataType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}AbstractSWESType">
 *       &lt;sequence>
 *         &lt;element name="producerEndpoint">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.w3.org/2005/08/addressing}EndpointReference"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="supportedDialects">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/swes/2.0}FilterDialectMetadata"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="fixedTopicSet" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="servedTopics">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://docs.oasis-open.org/wsn/t-1}TopicSet"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="usedTopicNamespace" type="{http://docs.oasis-open.org/wsn/t-1}TopicNamespaceType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotificationProducerMetadataType", propOrder = {
    "producerEndpoint",
    "supportedDialects",
    "fixedTopicSet",
    "servedTopics",
    "usedTopicNamespace"
})
@XmlSeeAlso({
    NotificationBrokerMetadataType.class
})
public class NotificationProducerMetadataType
    extends AbstractSWESType
{

    @XmlElement(required = true)
    private NotificationProducerMetadataType.ProducerEndpoint producerEndpoint;
    @XmlElement(required = true)
    private NotificationProducerMetadataType.SupportedDialects supportedDialects;
    private boolean fixedTopicSet;
    @XmlElement(required = true)
    private NotificationProducerMetadataType.ServedTopics servedTopics;
    private List<TopicNamespaceType> usedTopicNamespace;

    /**
     * Gets the value of the producerEndpoint property.
     *
     * @return
     *     possible object is
     *     {@link NotificationProducerMetadataType.ProducerEndpoint }
     *
     */
    public NotificationProducerMetadataType.ProducerEndpoint getProducerEndpoint() {
        return producerEndpoint;
    }

    /**
     * Sets the value of the producerEndpoint property.
     *
     * @param value
     *     allowed object is
     *     {@link NotificationProducerMetadataType.ProducerEndpoint }
     *
     */
    public void setProducerEndpoint(NotificationProducerMetadataType.ProducerEndpoint value) {
        this.producerEndpoint = value;
    }

    /**
     * Gets the value of the supportedDialects property.
     *
     * @return
     *     possible object is
     *     {@link NotificationProducerMetadataType.SupportedDialects }
     *
     */
    public NotificationProducerMetadataType.SupportedDialects getSupportedDialects() {
        return supportedDialects;
    }

    /**
     * Sets the value of the supportedDialects property.
     *
     * @param value
     *     allowed object is
     *     {@link NotificationProducerMetadataType.SupportedDialects }
     *
     */
    public void setSupportedDialects(NotificationProducerMetadataType.SupportedDialects value) {
        this.supportedDialects = value;
    }

    /**
     * Gets the value of the fixedTopicSet property.
     *
     */
    public boolean isFixedTopicSet() {
        return fixedTopicSet;
    }

    /**
     * Sets the value of the fixedTopicSet property.
     *
     */
    public void setFixedTopicSet(boolean value) {
        this.fixedTopicSet = value;
    }

    /**
     * Gets the value of the servedTopics property.
     *
     * @return
     *     possible object is
     *     {@link NotificationProducerMetadataType.ServedTopics }
     *
     */
    public NotificationProducerMetadataType.ServedTopics getServedTopics() {
        return servedTopics;
    }

    /**
     * Sets the value of the servedTopics property.
     *
     * @param value
     *     allowed object is
     *     {@link NotificationProducerMetadataType.ServedTopics }
     *
     */
    public void setServedTopics(NotificationProducerMetadataType.ServedTopics value) {
        this.servedTopics = value;
    }

    /**
     * Gets the value of the usedTopicNamespace property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link TopicNamespaceType }
     *
     */
    public List<TopicNamespaceType> getUsedTopicNamespace() {
        if (usedTopicNamespace == null) {
            usedTopicNamespace = new ArrayList<TopicNamespaceType>();
        }
        return this.usedTopicNamespace;
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.w3.org/2005/08/addressing}EndpointReference"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "endpointReference"
    })
    public static class ProducerEndpoint {

        @XmlElement(name = "EndpointReference", namespace = "http://www.w3.org/2005/08/addressing", required = true)
        private EndpointReferenceType endpointReference;

        /**
         * Gets the value of the endpointReference property.
         *
         * @return
         *     possible object is
         *     {@link EndpointReferenceType }
         *
         */
        public EndpointReferenceType getEndpointReference() {
            return endpointReference;
        }

        /**
         * Sets the value of the endpointReference property.
         *
         * @param value
         *     allowed object is
         *     {@link EndpointReferenceType }
         *
         */
        public void setEndpointReference(EndpointReferenceType value) {
            this.endpointReference = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://docs.oasis-open.org/wsn/t-1}TopicSet"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "topicSet"
    })
    public static class ServedTopics {

        @XmlElement(name = "TopicSet", namespace = "http://docs.oasis-open.org/wsn/t-1", required = true)
        private TopicSetType topicSet;

        /**
         * Gets the value of the topicSet property.
         *
         * @return
         *     possible object is
         *     {@link TopicSetType }
         *
         */
        public TopicSetType getTopicSet() {
            return topicSet;
        }

        /**
         * Sets the value of the topicSet property.
         *
         * @param value
         *     allowed object is
         *     {@link TopicSetType }
         *
         */
        public void setTopicSet(TopicSetType value) {
            this.topicSet = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.opengis.net/swes/2.0}FilterDialectMetadata"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "filterDialectMetadata"
    })
    public static class SupportedDialects {

        @XmlElement(name = "FilterDialectMetadata", required = true)
        private FilterDialectMetadataType filterDialectMetadata;

        /**
         * Gets the value of the filterDialectMetadata property.
         *
         * @return
         *     possible object is
         *     {@link FilterDialectMetadataType }
         *
         */
        public FilterDialectMetadataType getFilterDialectMetadata() {
            return filterDialectMetadata;
        }

        /**
         * Sets the value of the filterDialectMetadata property.
         *
         * @param value
         *     allowed object is
         *     {@link FilterDialectMetadataType }
         *
         */
        public void setFilterDialectMetadata(FilterDialectMetadataType value) {
            this.filterDialectMetadata = value;
        }

    }

}
