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
package org.geotoolkit.sos.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * XML encoded SOS operation request base, for all operations except Get Capabilities. 
 * In this XML encoding, no "request" parameter is included, 
 * since the element name specifies the specific operation. 
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestBaseType", namespace="http://www.opengis.net/sos/1.0")
@XmlSeeAlso({
    DescribeFeatureType.class,
    DescribeResultModel.class,
    InsertObservation.class,
    DescribeSensor.class,
    GetObservationById.class,
    GetFeatureOfInterestTime.class,
    GetResult.class,
    RegisterSensor.class,
    GetObservation.class,
    DescribeObservationType.class,
    GetFeatureOfInterest.class
})
public class RequestBaseType {

    /**
     * Service type identifier. 
     */
    @XmlAttribute(required = true)
    private String service;
    
    /**
     * Specification version for SOS version and operation.
     */
    @XmlAttribute(required = true)
    private String version;
    
     /**
     * An empty constructor used by jaxB
     */
    RequestBaseType(){}
    
    /**
     * Build a base request
     */
    public RequestBaseType(String version) {
        this.version = version;
        this.service = "SOS";
    }

    /**
     * Build a base request
     */
    public RequestBaseType(String version, String service) {
        this.version = version;
        this.service = service;
    }

    /**
     * Gets the value of the service property.
     */
    public String getService() {
        if (service == null) {
            return "SOS";
        } else {
            return service;
        }
    }

    /**
     * Return the value of the version property.
     */
    public String getVersion() {
        if (version == null) {
            return "1.0.0";
        } else {
            return version;
        }
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof RequestBaseType) {
            final RequestBaseType that = (RequestBaseType) object;
            return Utilities.equals(this.service, that.service) &&
                   Utilities.equals(this.version, that.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.service != null ? this.service.hashCode() : 0);
        hash = 17 * hash + (this.version != null ? this.version.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return "request base: service=" + service + " version=" + version;
    }

}
