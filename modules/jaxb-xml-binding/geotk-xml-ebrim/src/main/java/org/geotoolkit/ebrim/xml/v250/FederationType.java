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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import org.geotoolkit.util.Utilities;


/**
 * 
 * Mapping of the same named interface in ebRIM.
 * 			
 * 
 * <p>Java class for FederationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FederationType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryEntryType">
 *       &lt;sequence>
 *         &lt;element name="Members" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ObjectRefListType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="replicationSyncLatency" use="required" type="{http://www.w3.org/2001/XMLSchema}duration" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FederationType", propOrder = {
    "members"
})
@XmlRootElement(name = "Federation")
public class FederationType extends RegistryEntryType {

    @XmlElement(name = "Members", namespace = "", required = true)
    private ObjectRefListType members;
    @XmlAttribute(required = true)
    private Duration replicationSyncLatency;

    /**
     * Gets the value of the members property.
     */
    public ObjectRefListType getMembers() {
        return members;
    }

    /**
     * Sets the value of the members property.
     */
    public void setMembers(final ObjectRefListType value) {
        this.members = value;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (members != null) {
            sb.append("members:").append(members).append('\n');
        }
        if (replicationSyncLatency != null) {
            sb.append("replicationSyncLatency:").append(replicationSyncLatency).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof FederationType && super.equals(obj)) {
            final FederationType that = (FederationType) obj;
            return Utilities.equals(this.members,                that.members) &&
                   Utilities.equals(this.replicationSyncLatency, that.replicationSyncLatency);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + super.hashCode();
        hash = 97 * hash + (this.members != null ? this.members.hashCode() : 0);
        hash = 97 * hash + (this.replicationSyncLatency != null ? this.replicationSyncLatency.hashCode() : 0);
        return hash;
    }
}
