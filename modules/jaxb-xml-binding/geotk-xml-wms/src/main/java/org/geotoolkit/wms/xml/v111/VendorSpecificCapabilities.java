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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wmsc.xml.v111.TileSet;


/**
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "VendorSpecificCapabilities")
public class VendorSpecificCapabilities {
    @XmlElement(name = "TileSet")
    private List<TileSet> tileSet;

    /**
     * Gets the value of the tileSet property.
     */
    public List<TileSet> getTileSet() {
        if (tileSet == null) {
            tileSet = new ArrayList<TileSet>();
        }
        return this.tileSet;
    }

    /**
     * Verifie si cette entree est identique a l'objet specifie.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof VendorSpecificCapabilities) {
            final VendorSpecificCapabilities that = (VendorSpecificCapabilities) object;

            return Utilities.equals(this.tileSet, that.tileSet);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.tileSet != null ? this.tileSet.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder("[VendorSpecificCapabilities]\n");
        if (tileSet != null) {
            s.append("tileSet:").append('\n');
            for (TileSet ts : tileSet) {
                s.append(ts).append("\n");
            }
        }
        return s.toString();
    }

}
