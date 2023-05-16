/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.ogc.xml.v200;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.filter.capability.DefaultIdCapabilities;


/**
 * <p>Java class for Id_CapabilitiesType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Id_CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ResourceIdentifier" type="{http://www.opengis.net/fes/2.0}ResourceIdentifierType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Id_CapabilitiesType", propOrder = {
    "resourceIdentifier"
})
public class IdCapabilitiesType extends DefaultIdCapabilities {

    @XmlElement(name = "ResourceIdentifier", required = true)
    private List<ResourceIdentifierType> resourceIdentifier;

    public IdCapabilitiesType() {
        super(false, false);
    }

    public IdCapabilitiesType(ResourceIdentifierType resourceIdentifier) {
        super(false, false);
        if (resourceIdentifier != null) {
            this.resourceIdentifier = new ArrayList<>();
            this.resourceIdentifier.add(resourceIdentifier);
        }
    }

    public IdCapabilitiesType(final List<ResourceIdentifierType> resourceIdentifier) {
        super(false, false);
        this.resourceIdentifier = resourceIdentifier;
    }
    /**
     * Gets the value of the resourceIdentifier property.
     */
    public List<ResourceIdentifierType> getResourceIdentifier() {
        if (resourceIdentifier == null) {
            resourceIdentifier = new ArrayList<>();
        }
        return this.resourceIdentifier;
    }
}
