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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.AbstractCapabilities;
import org.geotoolkit.csw.xml.CSWResponse;
import org.geotoolkit.ogc.xml.v200.FilterCapabilities;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.ows.xml.v200.CapabilitiesBaseType;
import org.geotoolkit.ows.xml.v200.OperationsMetadata;
import org.geotoolkit.ows.xml.v200.ServiceIdentification;
import org.geotoolkit.ows.xml.v200.ServiceProvider;

/**
 *
 *             This type extends ows:CapabilitiesBaseType defined in OGC 06-121r9
 *             to include information about supported OGC filter components. A
 *             profile may extend this type to describe additional capabilities.
 *
 *
 * <p>Classe Java pour CapabilitiesType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/2.0}CapabilitiesBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}Filter_Capabilities" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapabilitiesType", propOrder = {
    "filterCapabilities"
})
@XmlRootElement(name = "Capabilities")
public class CapabilitiesType extends CapabilitiesBaseType implements AbstractCapabilities, CSWResponse {

    @XmlElement(name = "Filter_Capabilities", namespace = "http://www.opengis.net/fes/2.0")
    protected FilterCapabilities filterCapabilities;

    /**
     * An empty constructor used by JAXB
     */
    public CapabilitiesType(){
    }

     /**
     * Build a new Capabilities document
     */
    public CapabilitiesType(final String version, final String updateSequence){
        super(version, updateSequence);
    }

    /**
     * Build a new Capabilities document
     */
    public CapabilitiesType(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence, final FilterCapabilities filterCapabilities){
        super(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence, null);
            this.filterCapabilities = filterCapabilities;
    }

    /**
     * Obtient la valeur de la propriété filterCapabilities.
     */
    @Override
    public FilterCapabilities getFilterCapabilities() {
        return filterCapabilities;
    }

    /**
     * Définit la valeur de la propriété filterCapabilities.
     */
    public void setFilterCapabilities(FilterCapabilities value) {
        this.filterCapabilities = value;
    }

    @Override
    public CapabilitiesType applySections(final Sections sections) {
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
        return new CapabilitiesType(si, sp, om, "3.0.0", getUpdateSequence(), fc);
    }
}
