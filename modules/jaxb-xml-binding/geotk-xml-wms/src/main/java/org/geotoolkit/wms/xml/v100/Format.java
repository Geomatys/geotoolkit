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
    "gifOrJPEGOrPNGOrWebCGMOrSVGOrGML1OrGML2OrGML3OrWBMPOrWMSXMLOrMIMEOrINIMAGEOrTIFFOrGeoTIFFOrPPMOrBLANK"
})
@XmlRootElement(name = "Format")
public class Format {

    @XmlElements({
        @XmlElement(name = "GIF", required = true, type = GIF.class),
        @XmlElement(name = "JPEG", required = true, type = JPEG.class),
        @XmlElement(name = "PNG", required = true, type = PNG.class),
        @XmlElement(name = "WebCGM", required = true, type = WebCGM.class),
        @XmlElement(name = "SVG", required = true, type = SVG.class),
        @XmlElement(name = "GML.1", required = true, type = GML1 .class),
        @XmlElement(name = "GML.2", required = true, type = GML2 .class),
        @XmlElement(name = "GML.3", required = true, type = GML3 .class),
        @XmlElement(name = "WBMP", required = true, type = WBMP.class),
        @XmlElement(name = "WMS_XML", required = true, type = WMSXML.class),
        @XmlElement(name = "MIME", required = true, type = MIME.class),
        @XmlElement(name = "INIMAGE", required = true, type = INIMAGE.class),
        @XmlElement(name = "TIFF", required = true, type = TIFF.class),
        @XmlElement(name = "GeoTIFF", required = true, type = GeoTIFF.class),
        @XmlElement(name = "PPM", required = true, type = PPM.class),
        @XmlElement(name = "BLANK", required = true, type = BLANK.class)
    })
    protected List<Object> gifOrJPEGOrPNGOrWebCGMOrSVGOrGML1OrGML2OrGML3OrWBMPOrWMSXMLOrMIMEOrINIMAGEOrTIFFOrGeoTIFFOrPPMOrBLANK;

    /**
     * Gets the value of the gifOrJPEGOrPNGOrWebCGMOrSVGOrGML1OrGML2OrGML3OrWBMPOrWMSXMLOrMIMEOrINIMAGEOrTIFFOrGeoTIFFOrPPMOrBLANK property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gifOrJPEGOrPNGOrWebCGMOrSVGOrGML1OrGML2OrGML3OrWBMPOrWMSXMLOrMIMEOrINIMAGEOrTIFFOrGeoTIFFOrPPMOrBLANK property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGIFOrJPEGOrPNGOrWebCGMOrSVGOrGML1OrGML2OrGML3OrWBMPOrWMSXMLOrMIMEOrINIMAGEOrTIFFOrGeoTIFFOrPPMOrBLANK().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GIF }
     * {@link JPEG }
     * {@link PNG }
     * {@link WebCGM }
     * {@link SVG }
     * {@link GML1 }
     * {@link GML2 }
     * {@link GML3 }
     * {@link WBMP }
     * {@link WMSXML }
     * {@link MIME }
     * {@link INIMAGE }
     * {@link TIFF }
     * {@link GeoTIFF }
     * {@link PPM }
     * {@link BLANK }
     *
     *
     */
    public List<Object> getGIFOrJPEGOrPNGOrWebCGMOrSVGOrGML1OrGML2OrGML3OrWBMPOrWMSXMLOrMIMEOrINIMAGEOrTIFFOrGeoTIFFOrPPMOrBLANK() {
        if (gifOrJPEGOrPNGOrWebCGMOrSVGOrGML1OrGML2OrGML3OrWBMPOrWMSXMLOrMIMEOrINIMAGEOrTIFFOrGeoTIFFOrPPMOrBLANK == null) {
            gifOrJPEGOrPNGOrWebCGMOrSVGOrGML1OrGML2OrGML3OrWBMPOrWMSXMLOrMIMEOrINIMAGEOrTIFFOrGeoTIFFOrPPMOrBLANK = new ArrayList<Object>();
        }
        return this.gifOrJPEGOrPNGOrWebCGMOrSVGOrGML1OrGML2OrGML3OrWBMPOrWMSXMLOrMIMEOrINIMAGEOrTIFFOrGeoTIFFOrPPMOrBLANK;
    }

}
