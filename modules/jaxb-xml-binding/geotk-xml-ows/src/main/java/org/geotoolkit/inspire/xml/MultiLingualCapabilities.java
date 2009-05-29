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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v100.AbstractExtendedCapabilitiesType;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="MultiLingualCapabilities")
public class MultiLingualCapabilities extends AbstractExtendedCapabilitiesType {

    @XmlElement(name = "MultiLingualCapabilities")
    private InspireCapabilitiesType multiLingualCapabilities;

    /**
     * @return the multiLingualCapabilities
     */
    public InspireCapabilitiesType getMultiLingualCapabilities() {
        return multiLingualCapabilities;
    }

    /**
     * @param multiLingualCapabilities the multiLingualCapabilities to set
     */
    public void setMultiLingualCapabilities(InspireCapabilitiesType multiLingualCapabilities) {
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
            return Utilities.equals(this.multiLingualCapabilities, that.multiLingualCapabilities);
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
