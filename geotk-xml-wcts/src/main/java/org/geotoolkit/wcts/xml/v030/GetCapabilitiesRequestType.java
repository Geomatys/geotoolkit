/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.wcts.xml.v030;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Request to a WCTS to perform the {@code GetCapabilities} operation ({@code wcts:GetCapabilities}).
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCapabilitiesRequestType")
@XmlRootElement(name = "GetCapabilities")
public class GetCapabilitiesRequestType {

    @XmlAttribute(required = true)
    private String service = "WCTS";
    @XmlAttribute
    private String updateSequence;

    public String getService() {
        return service;
    }

    public String getUpdateSequence() {
        return updateSequence;
    }

    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }
}
