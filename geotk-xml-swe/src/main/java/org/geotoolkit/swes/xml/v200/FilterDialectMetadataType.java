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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FilterDialectMetadataType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FilterDialectMetadataType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}AbstractSWESType">
 *       &lt;sequence>
 *         &lt;element name="topicExpressionDialect" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="messageContentDialect" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="producerPropertiesDialect" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilterDialectMetadataType", propOrder = {
    "topicExpressionDialect",
    "messageContentDialect",
    "producerPropertiesDialect"
})
public class FilterDialectMetadataType extends AbstractSWESType {

    @XmlSchemaType(name = "anyURI")
    private List<String> topicExpressionDialect;
    @XmlSchemaType(name = "anyURI")
    private List<String> messageContentDialect;
    @XmlSchemaType(name = "anyURI")
    private List<String> producerPropertiesDialect;

    /**
     * Gets the value of the topicExpressionDialect property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getTopicExpressionDialect() {
        if (topicExpressionDialect == null) {
            topicExpressionDialect = new ArrayList<String>();
        }
        return this.topicExpressionDialect;
    }

    /**
     * Gets the value of the messageContentDialect property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getMessageContentDialect() {
        if (messageContentDialect == null) {
            messageContentDialect = new ArrayList<String>();
        }
        return this.messageContentDialect;
    }

    /**
     * Gets the value of the producerPropertiesDialect property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     */
    public List<String> getProducerPropertiesDialect() {
        if (producerPropertiesDialect == null) {
            producerPropertiesDialect = new ArrayList<String>();
        }
        return this.producerPropertiesDialect;
    }

}
