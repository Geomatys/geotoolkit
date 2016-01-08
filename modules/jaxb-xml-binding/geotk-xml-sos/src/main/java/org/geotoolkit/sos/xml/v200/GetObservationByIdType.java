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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.sos.xml.GetObservationById;
import org.geotoolkit.swes.xml.v200.ExtensibleRequestType;


/**
 * <p>Java class for GetObservationByIdType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetObservationByIdType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleRequestType">
 *       &lt;sequence>
 *         &lt;element name="observation" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetObservationByIdType", propOrder = {
    "observation"
})
@XmlRootElement(name = "GetObservationById")
public class GetObservationByIdType extends ExtensibleRequestType implements GetObservationById {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private List<String> observation;

    public GetObservationByIdType() {
        
    }
    
    public GetObservationByIdType(final String version, final List<String> observation) {
        super(version, "SOS");
        this.observation = observation;
    }
    
    public GetObservationByIdType(final String version, final String service, final List<String> observation) {
        super(version, service);
        this.observation = observation;
    }
    
    public GetObservationByIdType(final String version, final String service, final List<String> observation, final List<Object> extension) {
        super(version, service, extension);
        this.observation = observation;
    }
    
    /**
     * Gets the value of the observation property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    @Override
    public List<String> getObservation() {
        if (observation == null) {
            observation = new ArrayList<>();
        }
        return this.observation;
    }

    @Override
    public QName getResultModel() {
        return new QName("http://www.opengis.net/om/1.0", "Observation", "om");
    }

    @Override
    public String getResponseFormat() {
        for (Object ext : getExtension()) {
            if (ext instanceof String) {
                String outputFormat = (String) ext;
                if (outputFormat.startsWith("responseFormat=")) {
                    return outputFormat.substring(15);
                }
            }
        }
        return null;
    }

}
