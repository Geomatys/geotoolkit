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
package org.geotoolkit.wcs.xml.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.ows.xml.v110.CapabilitiesBaseType;
import org.geotoolkit.ows.xml.v110.OperationsMetadata;
import org.geotoolkit.ows.xml.v110.SectionsType;
import org.geotoolkit.ows.xml.v110.ServiceIdentification;
import org.geotoolkit.ows.xml.v110.ServiceProvider;
import org.geotoolkit.wcs.xml.GetCapabilitiesResponse;
import org.geotoolkit.wcs.xml.WCSResponse;


/**
 * <p>Root Document for a response to a getCapabilities request (WCS version 1.1.1).
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}CapabilitiesBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs/1.1.1}Contents" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "contents"
})
@XmlRootElement(name = "Capabilities")
public class Capabilities extends CapabilitiesBaseType implements GetCapabilitiesResponse, WCSResponse {

    @XmlElement(name = "Contents")
    private Contents contents;

    /**
     * An empty constructor used by JAXB
     */
    Capabilities(){}

    /**
     * Build a new Capabilities document.
     */
    public Capabilities(final String version, final String updateSequence) {
        super(null, null, null, version, updateSequence);
    }

    /**
     * Build a new Capabilities document.
     */
    public Capabilities(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence, final Contents contents) {
        super(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence);
        this.contents = contents;
    }

    /**
     * Gets the value of the contents property.
     *
     */
    @Override
    public Contents getContents() {
        return contents;
    }

    public void setContents(final Contents contents) {
        this.contents = contents;
    }

    @Override
    public Capabilities applySections(Sections sections) {
        if (sections == null) {
            sections = new SectionsType("All");
        }
        ServiceIdentification si = null;
        ServiceProvider sp       = null;
        OperationsMetadata om    = null;
        Contents ct              = null;
        //we add the static sections if the are included in the requested sections
        if (sections.containsSection("ServiceProvider") || sections.containsSection("All")) {
            sp = getServiceProvider();
        }
        if (sections.containsSection("ServiceIdentification") || sections.containsSection("All")) {
            si = getServiceIdentification();
        }
        if (sections.containsSection("OperationsMetadata") || sections.containsSection("All")) {
            om = getOperationsMetadata();
        }
        // if the user does not request the contents section we can return the result.
        if (sections.containsSection("Contents") || sections.containsSection("All")) {
            ct = contents;
        }
        return new Capabilities(si, sp, om, "1.1.1", getUpdateSequence(), ct);
    }
}
