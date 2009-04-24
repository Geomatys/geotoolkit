/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.constellation.sos.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.CapabilitiesBaseType;
import org.geotoolkit.ows.xml.v110.OperationsMetadata;
import org.geotoolkit.ows.xml.v110.ServiceIdentification;
import org.geotoolkit.ows.xml.v110.ServiceProvider;
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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "filterCapabilities",
    "contents"
})
@XmlRootElement(name="Capabilities")
public class Capabilities extends CapabilitiesBaseType {

    @XmlElement(name = "Filter_Capabilities")
    private FilterCapabilities filterCapabilities;
    @XmlElement(name = "Contents")
    private Contents contents;

    /**
     * An empty constructor used by JAXB
     */
    public Capabilities() {}
    
     /**
     * An empty constructor used by JAXB
     */
    public Capabilities(ServiceIdentification serviceIdentification, ServiceProvider serviceProvider,
            OperationsMetadata operationsMetadata, String version, String updateSequence, FilterCapabilities filterCapabilities,
            Contents contents) {
            super(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence);
            this.contents           = contents;
            this.filterCapabilities = filterCapabilities;
                    
    }

    /**
     * Return the value of the filterCapabilities property.
     * 
     */
    public FilterCapabilities getFilterCapabilities() {
        return filterCapabilities;
    }

    /**
     * Return the value of the contents property.
     */
    public Contents getContents() {
        return contents;
    }

    /**
     * Sets the value of the contents property.
     * 
     */
    public void setContents(Contents value) {
        this.contents = value;
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
