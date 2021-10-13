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
package org.geotoolkit.ogc.xml.v110;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
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
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{http://www.opengis.net/ogc}EID"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}FID"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Id_CapabilitiesType", propOrder = {
    "eid",
    "fid"
})
public class IdCapabilitiesType extends DefaultIdCapabilities {

    @XmlElement(name = "EID")
    private EID eid;

    @XmlElement(name = "FID")
    private FID fid;

    /**
     * Empty constructor used By JAXB
     */
     public IdCapabilitiesType() {
         super(true, true);
     }

    /**
     * Build a new ID capabilities
     */
     public IdCapabilitiesType(final boolean eid, final boolean fid) {
        super(eid, fid);
        if (eid)
            this.eid = new EID();
        if (fid)
            this.fid = new FID();
     }

    /**
     * Gets the value of the eid property.
     */
    public EID getEID() {
        return eid;
    }

    /**
     * Gets the value of the eid property.
     */
    public FID getFID() {
        return fid;
    }

    public boolean hasEID() {
        return eid != null;
    }

    public boolean hasFID() {
        return fid != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[IdCapabilitiesType]").append("\n");
        if (eid != null) {
            sb.append("eid: ").append(eid).append('\n');
        }
        if (fid != null) {
            sb.append("fid: ").append(fid).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
       if (object instanceof IdCapabilitiesType) {
           final IdCapabilitiesType that = (IdCapabilitiesType) object;

            return Objects.equals(this.eid, that.eid) &&
                   Objects.equals(this.fid, that.fid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.eid != null ? this.eid.hashCode() : 0);
        hash = 97 * hash + (this.fid != null ? this.fid.hashCode() : 0);
        return hash;
    }
}
