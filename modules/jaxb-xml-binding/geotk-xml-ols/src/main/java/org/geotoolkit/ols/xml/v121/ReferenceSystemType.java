/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.ols.xml.v121;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReferenceSystemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferenceSystemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}_NamedReferenceSystem" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenceSystemType", propOrder = {
    "namedReferenceSystem"
})
public class ReferenceSystemType {

    @XmlElementRef(name = "_NamedReferenceSystem", namespace = "http://www.opengis.net/xls", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractNamedReferenceSystem>> namedReferenceSystem;

    /**
     * Gets the value of the namedReferenceSystem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the namedReferenceSystem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNamedReferenceSystem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AbstractNamedReferenceSystem }{@code >}
     * {@link JAXBElement }{@code <}{@link SICType }{@code >}
     * {@link JAXBElement }{@code <}{@link NAICSType }{@code >}
     * {@link JAXBElement }{@code <}{@link NACEType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends AbstractNamedReferenceSystem>> getNamedReferenceSystem() {
        if (namedReferenceSystem == null) {
            namedReferenceSystem = new ArrayList<JAXBElement<? extends AbstractNamedReferenceSystem>>();
        }
        return this.namedReferenceSystem;
    }

}
