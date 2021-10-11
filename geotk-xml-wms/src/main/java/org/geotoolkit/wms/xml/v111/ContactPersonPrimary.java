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
package org.geotoolkit.wms.xml.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractContactPersonPrimary;


/**
 * <p>Java class for anonymous complex type.
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "contactPerson",
    "contactOrganization"
})
@XmlRootElement(name = "ContactPersonPrimary")
public class ContactPersonPrimary implements AbstractContactPersonPrimary {

    @XmlElement(name = "ContactPerson", required = true)
    private String contactPerson;
    @XmlElement(name = "ContactOrganization", required = true)
    private String contactOrganization;

    /**
     * An empty constructor used by JAXB.
     */
     ContactPersonPrimary() {
     }

    /**
     * Build a new Contact person primary object.
     */
    public ContactPersonPrimary(final String contactPerson, final String contactOrganization) {
        this.contactOrganization = contactOrganization;
        this.contactPerson       = contactPerson;
    }

    /**
     * Gets the value of the contactPerson property.
     *
     */
    public String getContactPerson() {
        return contactPerson;
    }

    /**
     * Gets the value of the contactOrganization property.
     */
    public String getContactOrganization() {
        return contactOrganization;
    }
}
