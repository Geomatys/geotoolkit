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
package org.geotoolkit.ebrim.xml.v300;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.Duration;


/**
 * Mapping of the same named interface in ebRIM.
 *
 * <p>Java class for RegistryType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RegistryType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;attribute name="operator" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="specificationVersion" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="replicationSyncLatency" type="{http://www.w3.org/2001/XMLSchema}duration" default="P1D" />
 *       &lt;attribute name="catalogingLatency" type="{http://www.w3.org/2001/XMLSchema}duration" default="P1D" />
 *       &lt;attribute name="conformanceProfile" default="registryLite">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NCName">
 *             &lt;enumeration value="registryFull"/>
 *             &lt;enumeration value="registryLite"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistryType")
@XmlRootElement(name = "Registry")
public class RegistryType extends RegistryObjectType {

    @XmlAttribute(required = true)
    private String operator;
    @XmlAttribute(required = true)
    private String specificationVersion;
    @XmlAttribute
    private Duration replicationSyncLatency;
    @XmlAttribute
    private Duration catalogingLatency;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String conformanceProfile;

    /**
     * Gets the value of the operator property.
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Sets the value of the operator property.
     */
    public void setOperator(final String value) {
        this.operator = value;
    }

    /**
     * Gets the value of the specificationVersion property.
     */
    public String getSpecificationVersion() {
        return specificationVersion;
    }

    /**
     * Sets the value of the specificationVersion property.
     */
    public void setSpecificationVersion(final String value) {
        this.specificationVersion = value;
    }

    /**
     * Gets the value of the replicationSyncLatency property.
     */
    public Duration getReplicationSyncLatency() {
        return replicationSyncLatency;
    }

    /**
     * Sets the value of the replicationSyncLatency property.
     */
    public void setReplicationSyncLatency(final Duration value) {
        this.replicationSyncLatency = value;
    }

    /**
     * Gets the value of the catalogingLatency property.
     */
    public Duration getCatalogingLatency() {
        return catalogingLatency;
    }

    /**
     * Sets the value of the catalogingLatency property.
     */
    public void setCatalogingLatency(final Duration value) {
        this.catalogingLatency = value;
    }

    /**
     * Gets the value of the conformanceProfile property.
     */
    public String getConformanceProfile() {
        if (conformanceProfile == null) {
            return "registryLite";
        } else {
            return conformanceProfile;
        }
    }

    /**
     * Sets the value of the conformanceProfile property.
     */
    public void setConformanceProfile(final String value) {
        this.conformanceProfile = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (catalogingLatency != null) {
            sb.append("catalogingLatency:").append(catalogingLatency).append('\n');
        }
        if (conformanceProfile != null) {
            sb.append("conformanceProfile:").append(conformanceProfile).append('\n');
        }
        if (operator != null) {
            sb.append("operator:").append(operator).append('\n');
        }
        if (replicationSyncLatency != null) {
            sb.append("replicationSyncLatency:").append(replicationSyncLatency).append('\n');
        }
        if (specificationVersion != null) {
            sb.append("specificationVersion:").append(specificationVersion).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof RegistryType && super.equals(obj)) {
            final RegistryType that = (RegistryType) obj;
            return Objects.equals(this.catalogingLatency,      that.catalogingLatency) &&
                   Objects.equals(this.conformanceProfile,     that.conformanceProfile) &&
                   Objects.equals(this.operator,               that.operator) &&
                   Objects.equals(this.replicationSyncLatency, that.replicationSyncLatency) &&
                   Objects.equals(this.specificationVersion,   that.specificationVersion);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.operator != null ? this.operator.hashCode() : 0);
        hash = 79 * hash + (this.specificationVersion != null ? this.specificationVersion.hashCode() : 0);
        hash = 79 * hash + (this.replicationSyncLatency != null ? this.replicationSyncLatency.hashCode() : 0);
        hash = 79 * hash + (this.catalogingLatency != null ? this.catalogingLatency.hashCode() : 0);
        hash = 79 * hash + (this.conformanceProfile != null ? this.conformanceProfile.hashCode() : 0);
        return hash;
    }

}
