/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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

package org.geotoolkit.csw.xml.v300;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.GetCapabilities;
import org.geotoolkit.ows.xml.v200.AcceptFormatsType;
import org.geotoolkit.ows.xml.v200.AcceptVersionsType;
import org.geotoolkit.ows.xml.v200.SectionsType;


/**
 *
 *             Request for a description of service capabilities. See
 *             OGC 06-121r9 for more information.
 *
 *
 * <p>Classe Java pour GetCapabilitiesType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="GetCapabilitiesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/2.0}GetCapabilitiesType">
 *       &lt;attribute name="service" type="{http://www.opengis.net/ows/2.0}ServiceType" default="CSW" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCapabilitiesType")
@XmlRootElement(name="GetCapabilities")
public class GetCapabilitiesType extends org.geotoolkit.ows.xml.v200.GetCapabilitiesType implements GetCapabilities {

    /**
     * An empty constructor used by JAXB
     */
    GetCapabilitiesType() {
    }

    /**
     * Build a minimal new getCapabilities request with the specified service.
     *
     * @param service MUST be CSW.
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
     * @param service MUST be CSW.
     */
    public GetCapabilitiesType(final AcceptVersionsType acceptVersions, final SectionsType sections,
            final AcceptFormatsType acceptFormats, final String updateSequence, final String service) {
        super(acceptVersions, sections, acceptFormats, updateSequence, service, null);
    }
}
