/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.thw.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.skos.xml.Concept;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetNumeredConceptResponse", propOrder = {
    "response"
})
@XmlRootElement(name = "GetNumeredConceptResponse", namespace = "http://ws.geotk.org/")
public class GetNumeredConceptResponse {

    @XmlElement(name = "return")
    private Concept response;

    public GetNumeredConceptResponse() {

    }

    public GetNumeredConceptResponse(Concept response) {
        this.response = response;
    }

    /**
     * Gets the value of the return property.
     *
     * @return
     *     possible object is
     *     {@link Concept }
     *
     */
    public Concept getReturn() {
        return response;
    }

    /**
     * Sets the value of the return property.
     *
     * @param value
     *     allowed object is
     *     {@link Concept }
     *
     */
    public void setReturn(Concept value) {
        this.response = value;
    }
}
