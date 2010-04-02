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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
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
 *         &lt;element name="FeatureOfInterestId" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetFeatureOfInterestTime", propOrder = {
    "featureOfInterestId"
})
@XmlRootElement(name = "GetFeatureOfInterestTime")
public class GetFeatureOfInterestTime extends RequestBaseType {

    @XmlElement(name = "FeatureOfInterestId", required = true)
    @XmlSchemaType(name = "anyURI")
    private String featureOfInterestId;

    /**
     * An empty constructor used by jaxB
     */
    GetFeatureOfInterestTime() {}

    public GetFeatureOfInterestTime(String version, String featureOfInterestId) {
        super(version);
        this.featureOfInterestId = featureOfInterestId;
    }

    /**
     * Gets the value of the featureOfInterestId property.
     */
    public String getFeatureOfInterestId() {
        return featureOfInterestId;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof GetFeatureOfInterestTime && super.equals(object)) {
            final GetFeatureOfInterestTime that = (GetFeatureOfInterestTime) object;
            return Utilities.equals(this.featureOfInterestId, that.featureOfInterestId);
        } 
        return false;
    }

    @Override
    public int hashCode() {
        return this.featureOfInterestId.hashCode();
    }

}
