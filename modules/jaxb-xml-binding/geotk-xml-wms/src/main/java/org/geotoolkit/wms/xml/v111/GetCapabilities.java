/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
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
import org.geotoolkit.wms.xml.AbstractOperation;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "format",
    "dcpType"
})
@XmlRootElement(name = "GetCapabilities")
public class GetCapabilities extends AbstractOperation {

    @XmlElement(name = "Format", required = true)
    private List<Format> format;
    @XmlElement(name = "DCPType", required = true)
    private List<DCPType> dcpType;

    /**
     * Gets the value of the format property.
     * 
     */
    public List<Format> getFormat() {
        if (format == null) {
            format = new ArrayList<Format>();
        }
        return this.format;
    }

    /**
     * Gets the value of the dcpType property.
     * 
     */
    public List<DCPType> getDCPType() {
        if (dcpType == null) {
            dcpType = new ArrayList<DCPType>();
        }
        return this.dcpType;
    }

}
