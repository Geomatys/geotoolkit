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
package org.geotoolkit.wfs.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v100.AcceptFormatsType;
import org.geotoolkit.ows.xml.v100.AcceptVersionsType;
import org.geotoolkit.ows.xml.v100.SectionsType;
import org.geotoolkit.wfs.xml.GetCapabilities;


/**
 * Request to a WFS to perform the GetCapabilities operation.
 * This operation allows a client to retrieve a Capabilities XML document providing metadata for the specific WFS server.
 *
 * The GetCapapbilities element is used to request that a Web Feature Service generate an XML document describing the organization
 * providing the service, the WFS operations that the service supports, a list of feature types that the service can operate on and
 * list of filtering capabilities that the service support. Such an XML document is called a capabilities document.
 *
 *
 * <p>Java class for GetCapabilitiesType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GetCapabilitiesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows}GetCapabilitiesType">
 *       &lt;attribute name="service" type="{http://www.opengis.net/ows}ServiceType" default="WFS" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCapabilitiesType")
@XmlRootElement(name = "GetCapabilities")
public class GetCapabilitiesType extends org.geotoolkit.ows.xml.v100.GetCapabilitiesType implements GetCapabilities {

    /**
     * An empty constructor used by JAXB
     */
    public GetCapabilitiesType() {
    }

    /**
     * Build a minimal new getCapabilities request with the specified service.
     *
     * @param service MUST be WFS.
     */
    public GetCapabilitiesType(final String service) {
        super(service);
    }

    /**
     * Build a new getCapabilities request with the specified service
     *
     * @param acceptVersions The different versions accepted by the client.
     * @param sections The different sections of the capabilities document requested.
     *                 one or more of "ServiceIdentification", "ServiceProvider", "OperationsMetadata", "Filter_Capabilities", "All".
     * @param acceptFormats The different fomat (MIME type) accepted by the client.
     * @param updateSequence not used yet.
     * @param service MUST be WFS.
     */
    public GetCapabilitiesType(final AcceptVersionsType acceptVersions, final SectionsType sections,
            final AcceptFormatsType acceptFormats, final String updateSequence, final String service) {
        super(acceptVersions, sections, acceptFormats, updateSequence, service);
    }
}
