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

import org.geotoolkit.thw.model.XmlThesaurus;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAvailableThesauriResponse complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getAvailableThesauriResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://ws.geotk.org/}xmlThesaurus" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAvailableThesauriResponse", propOrder = {
    "response"
})
@XmlRootElement(name = "GetAvailableThesauriResponse", namespace = "http://ws.geotk.org/")
public class GetAvailableThesauriResponse {

    @XmlElement(name = "return")
    private List<XmlThesaurus> response;

    public GetAvailableThesauriResponse() {

    }

    public GetAvailableThesauriResponse(List<XmlThesaurus> response) {
        this.response = response;
    }

    /**
     * Gets the value of the return property.
     *
     */
    public List<XmlThesaurus> getReturn() {
        if (response == null) {
            response = new ArrayList<>();
        }
        return this.response;
    }

}
