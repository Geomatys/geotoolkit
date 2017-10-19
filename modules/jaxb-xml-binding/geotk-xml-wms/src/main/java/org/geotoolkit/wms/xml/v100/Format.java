/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.wms.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "formats"
})
@XmlRootElement(name = "Format")
public class Format {

    @XmlElements({
        @XmlElement(name = "GIF", required = true, type = String.class, defaultValue = "GIF"),
        @XmlElement(name = "JPEG", required = true, type = String.class, defaultValue = "JPEG"),
        @XmlElement(name = "PNG", required = true, type = String.class, defaultValue = "PNG"),
        @XmlElement(name = "WebCGM", required = true, type = String.class, defaultValue = "WebCGM"),
        @XmlElement(name = "SVG", required = true, type = String.class, defaultValue = "SVG"),
        @XmlElement(name = "GML.1", required = true, type = String.class, defaultValue = "GML.1"),
        @XmlElement(name = "GML.2", required = true, type = String.class, defaultValue = "GML.2"),
        @XmlElement(name = "GML.3", required = true, type = String.class, defaultValue = "GML.3"),
        @XmlElement(name = "WBMP", required = true, type = String.class, defaultValue = "WBMP"),
        @XmlElement(name = "WMS_XML", required = true, type = String.class, defaultValue = "WMS_XML"),
        @XmlElement(name = "MIME", required = true, type = String.class, defaultValue = "MIME"),
        @XmlElement(name = "INIMAGE", required = true, type = String.class, defaultValue = "INIMAGE"),
        @XmlElement(name = "TIFF", required = true, type = String.class, defaultValue = "TIFF"),
        @XmlElement(name = "GeoTIFF", required = true, type = String.class, defaultValue = "GeoTIFF"),
        @XmlElement(name = "PPM", required = true, type = String.class, defaultValue = "PPM"),
        @XmlElement(name = "BLANK", required = true, type = String.class, defaultValue = "BLANK")
    })
    protected List<String> formats;

    /**
     * Gets the value of the formats property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the formats property.
     */
    public List<String> formats() {
        if (formats == null) {
            formats = new ArrayList<>();
        }
        return this.formats;
    }

}
