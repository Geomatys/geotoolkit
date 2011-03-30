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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import org.geotoolkit.util.Utilities;


/**
 * 
 * Mapping of the same named interface in ebRIM.
 * 			
 * 
 * <p>Java class for RegistryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RegistryType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryEntryType">
 *       &lt;attribute name="operator" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="specificationVersion" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="replicationSyncLatency" type="{http://www.w3.org/2001/XMLSchema}duration" default="P1D" />
 *       &lt;attribute name="catalogingLatency" type="{http://www.w3.org/2001/XMLSchema}duration" default="P1D" />
 *       &lt;attribute name="sqlQuerySupported" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="eventNotificationSupported" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="objectReplicationSupported" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="objectRelocationSupported" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistryType")
@XmlRootElement(name = "Registry")
public class RegistryType extends RegistryEntryType {

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String operator;
    @XmlAttribute(required = true)
    private String specificationVersion;
    @XmlAttribute
    private Duration replicationSyncLatency;
    @XmlAttribute
    private Duration catalogingLatency;
    @XmlAttribute
    private Boolean sqlQuerySupported;
    @XmlAttribute
    private Boolean eventNotificationSupported;
    @XmlAttribute
    private Boolean objectReplicationSupported;
    @XmlAttribute
    private Boolean objectRelocationSupported;

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
     * Gets the value of the sqlQuerySupported property.
     */
    public boolean isSqlQuerySupported() {
        if (sqlQuerySupported == null) {
            return false;
        } else {
            return sqlQuerySupported;
        }
    }

    /**
     * Sets the value of the sqlQuerySupported property.
     */
    public void setSqlQuerySupported(final Boolean value) {
        this.sqlQuerySupported = value;
    }

    /**
     * Gets the value of the eventNotificationSupported property.
    */
    public boolean isEventNotificationSupported() {
        if (eventNotificationSupported == null) {
            return false;
        } else {
            return eventNotificationSupported;
        }
    }

    /**
     * Sets the value of the eventNotificationSupported property.
     */
    public void setEventNotificationSupported(final Boolean value) {
        this.eventNotificationSupported = value;
    }

    /**
     * Gets the value of the objectReplicationSupported property.
      */
    public boolean isObjectReplicationSupported() {
        if (objectReplicationSupported == null) {
            return false;
        } else {
            return objectReplicationSupported;
        }
    }

    /**
     * Sets the value of the objectReplicationSupported property.
     */
    public void setObjectReplicationSupported(final Boolean value) {
        this.objectReplicationSupported = value;
    }

    /**
     * Gets the value of the objectRelocationSupported property.
     */
    public boolean isObjectRelocationSupported() {
        if (objectRelocationSupported == null) {
            return false;
        } else {
            return objectRelocationSupported;
        }
    }

    /**
     * Sets the value of the objectRelocationSupported property.
     */
    public void setObjectRelocationSupported(final Boolean value) {
        this.objectRelocationSupported = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (eventNotificationSupported != null) {
            sb.append("eventNotificationSupported:").append(eventNotificationSupported).append('\n');
        }
        if (objectRelocationSupported != null) {
            sb.append("objectRelocationSupported:").append(objectRelocationSupported).append('\n');
        }
        if (objectReplicationSupported != null) {
            sb.append("objectReplicationSupported:").append(objectReplicationSupported).append('\n');
        }
        if (sqlQuerySupported != null) {
            sb.append("sqlQuerySupported:").append(sqlQuerySupported).append('\n');
        }
        if (catalogingLatency != null) {
            sb.append("catalogingLatency:").append(catalogingLatency).append('\n');
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
            return Utilities.equals(this.catalogingLatency,          that.catalogingLatency) &&
                   Utilities.equals(this.eventNotificationSupported, that.eventNotificationSupported) &&
                   Utilities.equals(this.objectRelocationSupported,  that.objectRelocationSupported) &&
                   Utilities.equals(this.objectReplicationSupported, that.objectReplicationSupported) &&
                   Utilities.equals(this.operator,                   that.operator) &&
                   Utilities.equals(this.replicationSyncLatency,     that.replicationSyncLatency) &&
                   Utilities.equals(this.specificationVersion,       that.specificationVersion) &&
                   Utilities.equals(this.sqlQuerySupported,          that.sqlQuerySupported);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + super.hashCode();
        hash = 73 * hash + (this.operator != null ? this.operator.hashCode() : 0);
        hash = 73 * hash + (this.specificationVersion != null ? this.specificationVersion.hashCode() : 0);
        hash = 73 * hash + (this.replicationSyncLatency != null ? this.replicationSyncLatency.hashCode() : 0);
        hash = 73 * hash + (this.catalogingLatency != null ? this.catalogingLatency.hashCode() : 0);
        hash = 73 * hash + (this.sqlQuerySupported != null ? this.sqlQuerySupported.hashCode() : 0);
        hash = 73 * hash + (this.eventNotificationSupported != null ? this.eventNotificationSupported.hashCode() : 0);
        hash = 73 * hash + (this.objectReplicationSupported != null ? this.objectReplicationSupported.hashCode() : 0);
        hash = 73 * hash + (this.objectRelocationSupported != null ? this.objectRelocationSupported.hashCode() : 0);
        return hash;
    }
}
