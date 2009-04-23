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
package org.geotoolkit.csw.xml.v200;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.GetRecordByIdResponse;


/**
 * Returns a representation of the matching catalogue object.
 * If there is no matching record, the response message element is empty.
 *          
 * 
 * <p>Java class for GetRecordByIdResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetRecordByIdResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/cat/csw}AbstractRecord" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetRecordByIdResponseType", propOrder = {
    "abstractRecord",
    "any"
})
public class GetRecordByIdResponseType implements GetRecordByIdResponse {

    @XmlElementRef(name = "AbstractRecord", namespace = "http://www.opengis.net/cat/csw", type = JAXBElement.class)
    private JAXBElement<? extends AbstractRecordType> abstractRecord;
    @XmlAnyElement(lax = true)
    private List<? extends Object> any;

    /**
     * Gets the value of the abstractRecord property.
     * 
     */
    public JAXBElement<? extends AbstractRecordType> getAbstractRecord() {
        return abstractRecord;
    }

    /**
     * Sets the value of the abstractRecord property.
     * 
     */
    public void setAbstractRecord(JAXBElement<? extends AbstractRecordType> value) {
        this.abstractRecord = ((JAXBElement<? extends AbstractRecordType> ) value);
    }

    /**
     * Gets the value of the any property.
     * (unmodifiable)
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return Collections.unmodifiableList(any);
    }
}
