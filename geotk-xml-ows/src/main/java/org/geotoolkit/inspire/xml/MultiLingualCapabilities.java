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
package org.geotoolkit.inspire.xml;

import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v100.AbstractExtendedCapabilitiesType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="MultiLingualCapabilitiesType")
@XmlRootElement(name="MultiLingualCapabilities")
public class MultiLingualCapabilities extends AbstractExtendedCapabilitiesType {

    @XmlElement(name = "MultiLingualCapabilities")
    private InspireCapabilitiesType multiLingualCapabilities;

    public MultiLingualCapabilities() {

    }

    public MultiLingualCapabilities(final MultiLingualCapabilities that) {
        if (that != null && that.multiLingualCapabilities != null) {
            this.multiLingualCapabilities = new InspireCapabilitiesType(that.multiLingualCapabilities);
        }
    }

    public MultiLingualCapabilities(final InspireCapabilitiesType multiLingualCapabilities) {
        this.multiLingualCapabilities = multiLingualCapabilities;
    }

    /**
     * @return the multiLingualCapabilities
     */
    public InspireCapabilitiesType getMultiLingualCapabilities() {
        return multiLingualCapabilities;
    }

    /**
     * @param multiLingualCapabilities the multiLingualCapabilities to set
     */
    public void setMultiLingualCapabilities(final InspireCapabilitiesType multiLingualCapabilities) {
        this.multiLingualCapabilities = multiLingualCapabilities;
    }

     /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof MultiLingualCapabilities) {
            final MultiLingualCapabilities that = (MultiLingualCapabilities) object;
            return Objects.equals(this.multiLingualCapabilities, that.multiLingualCapabilities);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.multiLingualCapabilities != null ? this.multiLingualCapabilities.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[MultiLingualCapabilities]\n");
        if ( multiLingualCapabilities != null) {
            sb.append("multilingual capabilities:").append(multiLingualCapabilities).append('\n');
        }
        return sb.toString();
    }
}
