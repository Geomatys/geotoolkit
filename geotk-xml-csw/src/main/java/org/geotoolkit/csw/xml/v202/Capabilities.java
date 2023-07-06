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
package org.geotoolkit.csw.xml.v202;

import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.AbstractCapabilities;
import org.geotoolkit.csw.xml.CSWResponse;
import org.geotoolkit.ogc.xml.v110.FilterCapabilities;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.ows.xml.v100.CapabilitiesBaseType;
import org.geotoolkit.ows.xml.v100.OperationsMetadata;
import org.geotoolkit.ows.xml.v100.ServiceIdentification;
import org.geotoolkit.ows.xml.v100.ServiceProvider;


/**
 * This type extends ows:CapabilitiesBaseType defined in OGC-05-008
 * to include information about supported OGC filter components.
 * A profile may extend this type to describe additional capabilities.
 *
 * <p>Java class for CapabilitiesType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows}CapabilitiesBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}Filter_Capabilities"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "filterCapabilities"
})
@XmlRootElement(name="Capabilities")
public class Capabilities extends CapabilitiesBaseType implements AbstractCapabilities, CSWResponse {

    @XmlElement(name = "Filter_Capabilities", namespace = "http://www.opengis.net/ogc", required = true)
    private FilterCapabilities filterCapabilities;

    /**
     * An empty constructor used by JAXB
     */
    public Capabilities(){
    }

     /**
     * Build a new Capabilities document
     */
    public Capabilities(final String version, final String updateSequence){
        super(version, updateSequence);
    }

    /**
     * Build a new Capabilities document
     */
    public Capabilities(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence, final FilterCapabilities filterCapabilities){
        super(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence);
            this.filterCapabilities = filterCapabilities;
    }

    /**
     * Gets the value of the filterCapabilities property.
     */
    public FilterCapabilities getFilterCapabilities() {
        return filterCapabilities;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Capabilities && super.equals(object)) {
            final Capabilities that = (Capabilities) object;
            return Objects.equals(this.filterCapabilities, that.filterCapabilities);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 29 * hash + (this.filterCapabilities != null ? this.filterCapabilities.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if ( filterCapabilities != null) {
            sb.append("filter capabilities:").append(filterCapabilities).append('\n');
        }
        return sb.toString();
    }

    @Override
    public Capabilities applySections(final Sections sections) {
        ServiceIdentification si = null;
        ServiceProvider       sp = null;
        OperationsMetadata    om = null;
        FilterCapabilities    fc = null;

        //we enter the information for service identification.
        if (sections.containsSection("ServiceIdentification") || sections.containsSection("All")) {
            si = getServiceIdentification();
        }

        //we enter the information for service provider.
        if (sections.containsSection("ServiceProvider") || sections.containsSection("All")) {
            sp = getServiceProvider();
        }
        //we enter the operation Metadata
        if (sections.containsSection("OperationsMetadata") || sections.containsSection("All")) {
            om = getOperationsMetadata();
        }

        //we enter the information filter capablities.
        if (sections.containsSection("Filter_Capabilities") || sections.containsSection("All")) {
            fc = filterCapabilities;
        }
        return new Capabilities(si, sp, om, "2.0.2", getUpdateSequence(), fc);
    }
}
