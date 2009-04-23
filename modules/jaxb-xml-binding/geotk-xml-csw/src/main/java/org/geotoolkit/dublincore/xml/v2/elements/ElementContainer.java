/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.dublincore.xml.v2.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * This type definition is included as a convenience for schema authors 
 *       who need a container element for all of the DC elements.
 * 
 * <p>Java class for elementContainer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="elementContainer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;group ref="{http://purl.org/dc/elements/1.1/}DC-element-set"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "elementContainer", propOrder = {
    "dcElement"
})
public class ElementContainer {

    @XmlElementRef(name = "DC-element", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class)
    private List<JAXBElement<SimpleLiteral>> dcElement;

    /**
     * Gets the value of the dcElement property.
     * (unmodifiable)
     */
    public List<JAXBElement<SimpleLiteral>> getDCElement() {
        if (dcElement == null) {
            dcElement = new ArrayList<JAXBElement<SimpleLiteral>>();
        }
        return Collections.unmodifiableList(dcElement);
    }

}
