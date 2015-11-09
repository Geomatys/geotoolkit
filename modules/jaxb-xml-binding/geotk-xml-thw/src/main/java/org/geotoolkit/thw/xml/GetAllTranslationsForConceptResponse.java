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

import org.geotoolkit.thw.model.LanguageString;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAllTranslationsForConceptResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAllTranslationsForConceptResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://ws.geotk.org/}languageString" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAllTranslationsForConceptResponse", propOrder = {
    "response"
})
@XmlRootElement(name = "GetAllTranslationsForConceptResponse", namespace = "http://ws.geotk.org/")
public class GetAllTranslationsForConceptResponse {

    @XmlElement(name = "return")
    private List<LanguageString> response;

    public GetAllTranslationsForConceptResponse() {

    }

    public GetAllTranslationsForConceptResponse(List<LanguageString> response) {
        this.response = response;
    }

    /**
     * Gets the value of the return property.
     * 
     */
    public List<LanguageString> getReturn() {
        if (response == null) {
            response = new ArrayList<>();
        }
        return this.response;
    }

}
