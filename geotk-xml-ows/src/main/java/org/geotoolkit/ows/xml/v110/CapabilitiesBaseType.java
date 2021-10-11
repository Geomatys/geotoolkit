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
package org.geotoolkit.ows.xml.v110;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractCapabilitiesBase;


/**
 * XML encoded GetCapabilities operation response. This document provides clients with service metadata about a specific service instance, usually including metadata about the tightly-coupled data served. If the server does not implement the updateSequence parameter, the server shall always return the complete Capabilities document, without the updateSequence parameter. When the server implements the updateSequence parameter and the GetCapabilities operation request included the updateSequence parameter with the current value, the server shall return this element with only the "version" and "updateSequence" attributes. Otherwise, all optional elements shall be included or not depending on the actual value of the Contents parameter in the GetCapabilities operation request. This base type shall be extended by each specific OWS to include the additional contents needed.
 *
 * <p>Java class for CapabilitiesBaseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CapabilitiesBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}ServiceIdentification" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}ServiceProvider" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}OperationsMetadata" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.opengis.net/ows/1.1}VersionType" />
 *       &lt;attribute name="updateSequence" type="{http://www.opengis.net/ows/1.1}UpdateSequenceType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapabilitiesBaseType", propOrder = {
    "serviceIdentification",
    "serviceProvider",
    "operationsMetadata"
})
public abstract class CapabilitiesBaseType implements AbstractCapabilitiesBase {

    @XmlElement(name = "ServiceIdentification")
    private ServiceIdentification serviceIdentification;
    @XmlElement(name = "ServiceProvider")
    private ServiceProvider serviceProvider;
    @XmlElement(name = "OperationsMetadata")
    private OperationsMetadata operationsMetadata;
    @XmlAttribute(required = true)
    private String version;
    @XmlAttribute
    private String updateSequence;

    /**
     * Empty constructor used by JAXB.
     */
    protected CapabilitiesBaseType() {
    }

    /**
     * Build the base of a Capabilities document.
     */
    public CapabilitiesBaseType(final String version, final String updateSequence) {
        this(null, null, null, version, updateSequence);
    }

    /**
     * Build the base of a Capabilities document.
     */
    public CapabilitiesBaseType(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence) {
        this.operationsMetadata    = operationsMetadata;
        this.serviceIdentification = serviceIdentification;
        this.serviceProvider       = serviceProvider;
        this.updateSequence        = updateSequence;
        this.version               = version;
    }

    /**
     * Gets the value of the serviceIdentification property.
     *
     */
    @Override
    public ServiceIdentification getServiceIdentification() {
        return serviceIdentification;
    }

    public void setServiceIdentification(final ServiceIdentification SI) {
        serviceIdentification = SI;
    }


    /**
     * Gets the value of the serviceProvider property.
     *
     */
    @Override
    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(final ServiceProvider SP) {
        serviceProvider = SP;
    }

    /**
     * Gets the value of the operationsMetadata property.
     */
    @Override
    public OperationsMetadata getOperationsMetadata() {
        return operationsMetadata;
    }

    public void setOperationsMetadata(final OperationsMetadata operationsMetadata) {
        this.operationsMetadata = operationsMetadata;
    }

    @Override
    public void updateURL(String url) {
        if (this.operationsMetadata != null) {
            this.operationsMetadata.updateURL(url);
        }
    }

    /**
     * Gets the value of the version property.
     *
     */
    @Override
    public String getVersion() {
        return version;
    }

    /**
     * Gets the value of the updateSequence property.
     *
     */
    @Override
    public String getUpdateSequence() {
        return updateSequence;
    }

    public void setUpdateSequence(final String updateSequence) {
        this.updateSequence = updateSequence;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof CapabilitiesBaseType) {
            final CapabilitiesBaseType that = (CapabilitiesBaseType) object;

            return Objects.equals(this.operationsMetadata,    that.operationsMetadata)    &&
                   Objects.equals(this.serviceIdentification, that.serviceIdentification) &&
                   Objects.equals(this.serviceProvider,       that.serviceProvider)       &&
                   Objects.equals(this.updateSequence,        that.updateSequence)        &&
                   Objects.equals(this.version,               that.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.serviceIdentification != null ? this.serviceIdentification.hashCode() : 0);
        hash = 83 * hash + (this.serviceProvider != null ? this.serviceProvider.hashCode() : 0);
        hash = 83 * hash + (this.operationsMetadata != null ? this.operationsMetadata.hashCode() : 0);
        hash = 83 * hash + (this.updateSequence != null ? this.updateSequence.hashCode() : 0);
        hash = 83 * hash + (this.version != null ? this.version.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (operationsMetadata != null) {
            s.append(operationsMetadata.toString()).append('\n');
        } else {
            s.append("OperationMetadata is null").append('\n');
        }
        if (serviceIdentification != null) {
            s.append(serviceIdentification.toString()).append('\n');
        } else {
            s.append("serviceIdentification is null").append('\n');
        }
        if (serviceProvider != null) {
            s.append(serviceProvider.toString()).append('\n');
        } else {
            s.append("serviceProvider is null").append('\n');
        }
        if (updateSequence != null) {
            s.append(updateSequence).append('\n');
        } else {
            s.append("updateSequence is null").append('\n');
        }
        if (version != null) {
            s.append(version.toString()).append('\n');
        } else {
            s.append("version is null").append('\n');
        }
        return s.toString();
    }
}
