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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.skos.xml.Concept;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getGeometricConceptResponse", propOrder = {
    "response"
})
@XmlRootElement(name = "GetGeometricConceptResponse", namespace = "http://ws.geotk.org/")
public class GetGeometricConceptResponse {

    @XmlElement(name = "return")
    private Concept response;

    public GetGeometricConceptResponse() {

    }

    public GetGeometricConceptResponse(Concept response) {
        this.response = response;
    }

    /**
     * Gets the value of the return property.
     */
    public Concept getReturn() {
        return this.response;
    }

}
