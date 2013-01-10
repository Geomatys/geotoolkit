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

package org.geotoolkit.sos.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swes.xml.v200.AbstractContentsType;


/**
 * <p>Java class for ContentsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContentsType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}AbstractContentsType">
 *       &lt;sequence>
 *         &lt;element name="responseFormat" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="observationType" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="featureOfInterestType" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentsType", propOrder = {
    "responseFormat",
    "observationType",
    "featureOfInterestType"
})
public class ContentsType extends AbstractContentsType {

    @XmlSchemaType(name = "anyURI")
    private List<String> responseFormat;
    @XmlSchemaType(name = "anyURI")
    private List<String> observationType;
    @XmlSchemaType(name = "anyURI")
    private List<String> featureOfInterestType;

    /**
     * Gets the value of the responseFormat property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    public List<String> getResponseFormat() {
        if (responseFormat == null) {
            responseFormat = new ArrayList<String>();
        }
        return this.responseFormat;
    }

    /**
     * Gets the value of the observationType property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    public List<String> getObservationType() {
        if (observationType == null) {
            observationType = new ArrayList<String>();
        }
        return this.observationType;
    }

    /**
     * Gets the value of the featureOfInterestType property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    public List<String> getFeatureOfInterestType() {
        if (featureOfInterestType == null) {
            featureOfInterestType = new ArrayList<String>();
        }
        return this.featureOfInterestType;
    }

}
