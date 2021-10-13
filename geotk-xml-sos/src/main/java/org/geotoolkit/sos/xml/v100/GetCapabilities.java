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
package org.geotoolkit.sos.xml.v100;

import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.AcceptFormatsType;
import org.geotoolkit.ows.xml.v110.AcceptVersionsType;
import org.geotoolkit.ows.xml.v110.GetCapabilitiesType;
import org.geotoolkit.ows.xml.v110.SectionsType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}GetCapabilitiesType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="service" use="required" type="{http://www.opengis.net/ows/1.1}ServiceType" fixed="SOS" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCapabilities")
@XmlRootElement(name = "GetCapabilities")
public class GetCapabilities extends GetCapabilitiesType implements org.geotoolkit.sos.xml.GetCapabilities {

    private static List<String> ACCEPTED_SECTIONS = Arrays.asList("All",
                                                                  "ServiceIdentification",
                                                                  "ServiceProvider",
                                                                  "OperationsMetadata",
                                                                  "Filter_Capabilities",
                                                                  "Contents");
    /**
     * minimal getCapabilities request.
     */
    public  GetCapabilities() {
        super("SOS");
    }

    /**
     * Build a new getCapabilities request with the specified service
     */
    public  GetCapabilities(final AcceptVersionsType acceptVersions, final SectionsType sections,
            final AcceptFormatsType acceptFormats, final String updateSequence, final String service) {
        super(acceptVersions, sections, acceptFormats, updateSequence, service);
    }

     /**
     * Build a new getCapabilities (simplified version).
     */
    public  GetCapabilities(final String acceptVersions, final String acceptFormats) {
        super(acceptVersions, acceptFormats, "SOS");
    }


    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof GetCapabilities) {
            return super.equals(object);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean isValidSections() {
        if (sections != null) {
            for (String section : sections.getSection()) {
                if (!ACCEPTED_SECTIONS.contains(section)) {
                    return false;
                }
            }
        }
        return true;
    }
}
