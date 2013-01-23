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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractCapabilitiesCore;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.ows.xml.v110.CapabilitiesBaseType;
import org.geotoolkit.ows.xml.v110.OperationsMetadata;
import org.geotoolkit.ows.xml.v110.ServiceIdentification;
import org.geotoolkit.ows.xml.v110.ServiceProvider;
import org.geotoolkit.swes.xml.SOSResponse;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}CapabilitiesBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/sos/1.0}Filter_Capabilities" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sos/1.0}Contents" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "filterCapabilities",
    "contents"
})
@XmlRootElement(name="Capabilities")
public class Capabilities extends CapabilitiesBaseType implements org.geotoolkit.sos.xml.Capabilities, SOSResponse {

    @XmlElement(name = "Filter_Capabilities")
    private FilterCapabilities filterCapabilities;
    @XmlElement(name = "Contents")
    private Contents contents;

    /**
     * An empty constructor used by JAXB
     */
    public Capabilities() {}
    
    public Capabilities(final String version, final String updateSequence) {
        super(version, updateSequence);
                    
    }
    
    public Capabilities(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence, final FilterCapabilities filterCapabilities,
            final Contents contents) {
        super(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence);
        this.contents           = contents;
        this.filterCapabilities = filterCapabilities;
                    
    }

    /**
     * Return the value of the filterCapabilities property.
     * 
     */
    @Override
    public FilterCapabilities getFilterCapabilities() {
        return filterCapabilities;
    }

    /**
     * Return the value of the contents property.
     */
    @Override
    public Contents getContents() {
        return contents;
    }

    /**
     * Sets the value of the contents property.
     * 
     */
    public void setContents(final Contents value) {
        this.contents = value;
    }
    
    @Override
    public AbstractCapabilitiesCore applySections(final Sections sections) {
        //we prepare the different parts response document
        ServiceIdentification si = null;
        ServiceProvider       sp = null;
        OperationsMetadata    om = null;
        FilterCapabilities    fc = null;
        Contents            cont = null;

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

        if (sections.containsSection("Contents") || sections.containsSection("All")) {
            cont = contents;
        }
        // we build and normalize the document
        return new Capabilities(si, sp, om, "1.0.0", getUpdateSequence(), fc, cont);
    }
    
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Capabilities && super.equals(object)) {
            final Capabilities that = (Capabilities) object;

            return Utilities.equals(this.contents,              that.contents)              &&
                   Utilities.equals(this.filterCapabilities,    that.filterCapabilities);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.filterCapabilities != null ? this.filterCapabilities.hashCode() : 0);
        hash = 89 * hash + (this.contents != null ? this.contents.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append(super.toString());
        if (this.contents != null) {
            s.append("contents: ").append(contents).append('\n');
        } else {
            s.append("contents is null").append('\n');
        }
        if (this.filterCapabilities != null) {
             s.append("filterCapabilities: ").append(filterCapabilities).append('\n');
        } else {
            s.append("filterCapabilities is null").append('\n');
        }
        return s.toString();
    }
}
