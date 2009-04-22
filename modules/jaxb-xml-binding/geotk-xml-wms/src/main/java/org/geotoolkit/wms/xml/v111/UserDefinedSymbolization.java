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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "UserDefinedSymbolization")
public class UserDefinedSymbolization {

    @XmlAttribute(name = "SupportSLD")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String supportSLD;
    @XmlAttribute(name = "UserLayer")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String userLayer;
    @XmlAttribute(name = "UserStyle")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String userStyle;
    @XmlAttribute(name = "RemoteWFS")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String remoteWFS;

    /**
     * Gets the value of the supportSLD property.
     * 
     *     
     */
    public String getSupportSLD() {
        if (supportSLD == null) {
            return "0";
        } else {
            return supportSLD;
        }
    }

    /**
     * Gets the value of the userLayer property.
     */
    public String getUserLayer() {
        if (userLayer == null) {
            return "0";
        } else {
            return userLayer;
        }
    }

    /**
     * Gets the value of the userStyle property.
     */
    public String getUserStyle() {
        if (userStyle == null) {
            return "0";
        } else {
            return userStyle;
        }
    }

    /**
     * Gets the value of the remoteWFS property.
     */
    public String getRemoteWFS() {
        if (remoteWFS == null) {
            return "0";
        } else {
            return remoteWFS;
        }
    }

}
