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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InsertionCapabilitiesType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="InsertionCapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="procedureDescriptionFormat" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
 *         &lt;element name="featureOfInterestType" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
 *         &lt;element name="observationType" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
 *         &lt;element name="supportedEncoding" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertionCapabilitiesType", propOrder = {
    "procedureDescriptionFormat",
    "featureOfInterestType",
    "observationType",
    "supportedEncoding"
})
public class InsertionCapabilitiesType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private List<String> procedureDescriptionFormat;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private List<String> featureOfInterestType;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private List<String> observationType;
    @XmlSchemaType(name = "anyURI")
    private List<String> supportedEncoding;

    public InsertionCapabilitiesType() {

    }

    public InsertionCapabilitiesType(final List<String> procedureDescriptionFormat, List<String> featureOfInterestType,
            final List<String> observationType, final List<String> supportedEncoding) {
        this.featureOfInterestType      = featureOfInterestType;
        this.observationType            = observationType;
        this.procedureDescriptionFormat = procedureDescriptionFormat;
        this.supportedEncoding          = supportedEncoding;
    }

    /**
     * Gets the value of the procedureDescriptionFormat property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     */
    public List<String> getProcedureDescriptionFormat() {
        if (procedureDescriptionFormat == null) {
            procedureDescriptionFormat = new ArrayList<String>();
        }
        return this.procedureDescriptionFormat;
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
     * Gets the value of the supportedEncoding property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     */
    public List<String> getSupportedEncoding() {
        if (supportedEncoding == null) {
            supportedEncoding = new ArrayList<String>();
        }
        return this.supportedEncoding;
    }

}
