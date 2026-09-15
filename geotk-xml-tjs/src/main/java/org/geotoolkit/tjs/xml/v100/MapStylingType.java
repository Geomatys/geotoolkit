/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.tjs.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Styling that shall be applied if the AccessMechanisms of the requested output includes WMS.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MapStylingType", propOrder = {"stylingIdentifier", "stylingURL"})
public class MapStylingType {

    @XmlElement(name = "StylingIdentifier", required = true)
    private String stylingIdentifier;

    @XmlElement(name = "StylingURL", required = true)
    private String stylingURL;

    public String getStylingIdentifier() {
        return stylingIdentifier;
    }

    public void setStylingIdentifier(String stylingIdentifier) {
        this.stylingIdentifier = stylingIdentifier;
    }

    public String getStylingURL() {
        return stylingURL;
    }

    public void setStylingURL(String stylingURL) {
        this.stylingURL = stylingURL;
    }
}
