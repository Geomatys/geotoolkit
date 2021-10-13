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
package org.geotoolkit.ebrim.xml.v250;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wrs.xml.v090.ApplicationModuleType;


/**
 *
 * RegistryPackage is the mapping of the same named interface in ebRIM.
 * It extends RegistryEntry.
 *
 * A RegistryPackage is a named collection of objects.
 *
 *
 * <p>Java class for RegistryPackageType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RegistryPackageType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryEntryType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryObjectList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistryPackageType", propOrder = {
    "registryObjectList"
})
@XmlSeeAlso({
    ApplicationModuleType.class
})
@XmlRootElement( name = "RegistryPackage")
public class RegistryPackageType extends RegistryEntryType {

    @XmlElement(name = "RegistryObjectList")
    private RegistryObjectListType registryObjectList;

    /**
     * Gets the value of the registryObjectList property.
     */
    public RegistryObjectListType getRegistryObjectList() {
        return registryObjectList;
    }

    /**
     * Sets the value of the registryObjectList property.
     */
    public void setRegistryObjectList(final RegistryObjectListType value) {
        this.registryObjectList = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (registryObjectList != null) {
            sb.append("registryObjectList:").append(registryObjectList).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof RegistryPackageType && super.equals(obj)) {
            final RegistryPackageType that = (RegistryPackageType) obj;
            return Objects.equals(this.registryObjectList,         that.registryObjectList);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + super.hashCode();
        hash = 59 * hash + (this.registryObjectList != null ? this.registryObjectList.hashCode() : 0);
        return hash;
    }

}
