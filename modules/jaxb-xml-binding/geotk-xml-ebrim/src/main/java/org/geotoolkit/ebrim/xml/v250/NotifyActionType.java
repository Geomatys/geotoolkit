/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.ebrim.xml.v250;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 * Abstract Base type for all types of Notify Actions
 * 			
 * 
 * <p>Java class for NotifyActionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NotifyActionType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ActionType">
 *       &lt;attribute name="notificationOption" default="ObjectRefs">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NCName">
 *             &lt;enumeration value="ObjectRefs"/>
 *             &lt;enumeration value="Objects"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="endPoint" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotifyActionType")
public abstract class NotifyActionType extends ActionType {

    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String notificationOption;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String endPoint;

    /**
     * Gets the value of the notificationOption property.
     */
    public String getNotificationOption() {
        if (notificationOption == null) {
            return "ObjectRefs";
        } else {
            return notificationOption;
        }
    }

    /**
     * Sets the value of the notificationOption property.
     */
    public void setNotificationOption(String value) {
        this.notificationOption = value;
    }

    /**
     * Gets the value of the endPoint property.
     */
    public String getEndPoint() {
        return endPoint;
    }

    /**
     * Sets the value of the endPoint property.
     */
    public void setEndPoint(String value) {
        this.endPoint = value;
    }

}
