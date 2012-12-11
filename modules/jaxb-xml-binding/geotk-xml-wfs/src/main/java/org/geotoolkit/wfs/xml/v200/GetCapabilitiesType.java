/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.wfs.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.AcceptFormatsType;
import org.geotoolkit.ows.xml.v110.AcceptVersionsType;
import org.geotoolkit.ows.xml.v110.SectionsType;
import org.geotoolkit.wfs.xml.GetCapabilities;


/**
 * <p>Java class for GetCapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCapabilitiesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}GetCapabilitiesType">
 *       &lt;attribute name="service" use="required" type="{http://www.opengis.net/ows/1.1}ServiceType" fixed="WFS" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCapabilitiesType")
@XmlRootElement(name = "GetCapabilities", namespace= "http://www.opengis.net/wfs/2.0")
public class GetCapabilitiesType extends org.geotoolkit.ows.xml.v110.GetCapabilitiesType implements GetCapabilities {

    /**
     * minimal getCapabilities request.
     */
    public GetCapabilitiesType() {
        super("WFS");
    }
    
    /**
     * Build a new getCapabilities request with the specified service
     */
    public GetCapabilitiesType(final AcceptVersionsType acceptVersions, final SectionsType sections,
            final AcceptFormatsType acceptFormats, final String updateSequence, final String service) {
        super(acceptVersions, sections, acceptFormats, updateSequence, service);
    }

     /**
     * Build a new getCapabilities (simplified version).
     */
    public GetCapabilitiesType(final String acceptVersions, final String acceptFormats) {
        super(acceptVersions, acceptFormats, "WFS");
    }

}
