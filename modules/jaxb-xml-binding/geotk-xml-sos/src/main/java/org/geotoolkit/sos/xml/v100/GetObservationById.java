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
package org.geotoolkit.sos.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sos/1.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element name="ObservationId" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="responseFormat" type="{http://www.opengis.net/ows/1.1}MimeType" minOccurs="0"/>
 *         &lt;element name="resultModel" type="{http://www.w3.org/2001/XMLSchema}QName" minOccurs="0"/>
 *         &lt;element name="responseMode" type="{http://www.opengis.net/sos/1.0}responseModeType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="srsName" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetObservationById", propOrder = {
    "observationId",
    "responseFormat",
    "resultModel",
    "responseMode"
})
@XmlRootElement(name = "GetObservationById")
public class GetObservationById extends RequestBaseType {

    @XmlElement(name = "ObservationId", required = true)
    @XmlSchemaType(name = "anyURI")
    private String observationId;
    private String responseFormat;
    private QName resultModel;
    private ResponseModeType responseMode;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String srsName;

    /**
     * Empty constructor used by JAXB
     */
    GetObservationById() {}

    /**
     * Build a new full GetObservationById request
     */
    public GetObservationById(String version, String observationId, String responseFormat,
                              QName resultModel, ResponseModeType responseMode, String srsName)
    {
        super(version);
        this.observationId = observationId;
        this.responseFormat = responseFormat;
        this.resultModel = resultModel;
        this.responseMode = responseMode;
        this.srsName = srsName;
    }

    /**
     * Gets the value of the observationId property.
     */
    public String getObservationId() {
        return observationId;
    }

    /**
     * Gets the value of the responseFormat property.
     */
    public String getResponseFormat() {
        return responseFormat;
    }

    /**
     * Gets the value of the resultModel property.
     */
    public QName getResultModel() {
        return resultModel;
    }

    /**
     * Gets the value of the responseMode property.
     */
    public ResponseModeType getResponseMode() {
        return responseMode;
    }

    /**
     * Gets the value of the srsName property.
     */
    public String getSrsName() {
        return srsName;
    }

    /**
     * Verify if this entry is identical to�the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof GetObservationById && super.equals(object)) {
            final GetObservationById that = (GetObservationById) object;
            return Utilities.equals(this.observationId,         that.observationId) &&
                   Utilities.equals(this.responseFormat,    that.responseFormat)    &&
                   Utilities.equals(this.responseMode,      that.responseMode)      &&
                   Utilities.equals(this.resultModel,       that.resultModel)       &&
                   Utilities.equals(this.srsName,           that.srsName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.observationId != null ? this.observationId.hashCode() : 0);
        hash = 71 * hash + (this.responseFormat != null ? this.responseFormat.hashCode() : 0);
        hash = 71 * hash + (this.resultModel != null ? this.resultModel.hashCode() : 0);
        hash = 71 * hash + (this.responseMode != null ? this.responseMode.hashCode() : 0);
        hash = 71 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        return hash;
    }
}
