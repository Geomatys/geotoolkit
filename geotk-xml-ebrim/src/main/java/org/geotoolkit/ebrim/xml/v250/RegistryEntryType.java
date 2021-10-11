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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RegistryEntryType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RegistryEntryType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryObjectType">
 *       &lt;attribute name="expiration" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="majorVersion" type="{http://www.w3.org/2001/XMLSchema}integer" default="1" />
 *       &lt;attribute name="minorVersion" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *       &lt;attribute name="stability">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NCName">
 *             &lt;enumeration value="Dynamic"/>
 *             &lt;enumeration value="DynamicCompatible"/>
 *             &lt;enumeration value="Static"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="userVersion" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ShortName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistryEntryType")
@XmlSeeAlso({
    RegistryPackageType.class,
    ExtrinsicObjectType.class,
    ClassificationSchemeType.class,
    ServiceType.class,
    RegistryType.class,
    FederationType.class
})
@XmlRootElement(name = "RegistryEntry")
public class RegistryEntryType extends RegistryObjectType {

    @XmlAttribute
    private XMLGregorianCalendar expiration;
    @XmlAttribute
    private Integer majorVersion;
    @XmlAttribute
    private Integer minorVersion;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String stability;
    @XmlAttribute
    private String userVersion;

    /**
     * Gets the value of the expiration property.
     */
    public XMLGregorianCalendar getExpiration() {
        return expiration;
    }

    /**
     * Sets the value of the expiration property.
     */
    public void setExpiration(final XMLGregorianCalendar value) {
        this.expiration = value;
    }

    /**
     * Gets the value of the majorVersion property.
     */
    public Integer getMajorVersion() {
        if (majorVersion == null) {
            return new Integer("1");
        } else {
            return majorVersion;
        }
    }

    /**
     * Sets the value of the majorVersion property.
     */
    public void setMajorVersion(final Integer value) {
        this.majorVersion = value;
    }

    /**
     * Gets the value of the minorVersion property.
     */
    public Integer getMinorVersion() {
        if (minorVersion == null) {
            return new Integer("0");
        } else {
            return minorVersion;
        }
    }

    /**
     * Sets the value of the minorVersion property.
     */
    public void setMinorVersion(final Integer value) {
        this.minorVersion = value;
    }

    /**
     * Gets the value of the stability property.
     */
    public String getStability() {
        return stability;
    }

    /**
     * Sets the value of the stability property.
     */
    public void setStability(final String value) {
        this.stability = value;
    }

    /**
     * Gets the value of the userVersion property.
     */
    public String getUserVersion() {
        return userVersion;
    }

    /**
     * Sets the value of the userVersion property.
     */
    public void setUserVersion(final String value) {
        this.userVersion = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (expiration != null) {
            sb.append("expiration:").append(expiration).append('\n');
        }
        if (majorVersion != null) {
            sb.append("majorVersion:").append(majorVersion).append('\n');
        }
        if (minorVersion != null) {
            sb.append("minorVersion:").append(minorVersion).append('\n');
        }
        if (stability != null) {
            sb.append("stability:").append(stability).append('\n');
        }
        if (userVersion != null) {
            sb.append("userVersion:").append(userVersion).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof RegistryEntryType && super.equals(obj)) {
            final RegistryEntryType that = (RegistryEntryType) obj;
            return Objects.equals(this.expiration,         that.expiration) &&
                   Objects.equals(this.majorVersion,    that.majorVersion) &&
                   Objects.equals(this.minorVersion,          that.minorVersion) &&
                   Objects.equals(this.stability,  that.stability) &&
                   Objects.equals(this.userVersion, that.userVersion);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + super.hashCode();
        hash = 79 * hash + (this.expiration != null ? this.expiration.hashCode() : 0);
        hash = 79 * hash + (this.majorVersion != null ? this.majorVersion.hashCode() : 0);
        hash = 79 * hash + (this.minorVersion != null ? this.minorVersion.hashCode() : 0);
        hash = 79 * hash + (this.stability != null ? this.stability.hashCode() : 0);
        hash = 79 * hash + (this.userVersion != null ? this.userVersion.hashCode() : 0);
        return hash;
    }
}
