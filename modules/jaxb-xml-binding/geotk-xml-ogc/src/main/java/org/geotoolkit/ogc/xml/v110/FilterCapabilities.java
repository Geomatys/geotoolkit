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
package org.geotoolkit.ogc.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.capability.IdCapabilities;
import org.opengis.filter.capability.ScalarCapabilities;
import org.opengis.filter.capability.SpatialCapabilities;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Spatial_Capabilities" type="{http://www.opengis.net/ogc}Spatial_CapabilitiesType"/>
 *         &lt;element name="Temporal_Capabilities" type="{http://www.opengis.net/ogc}Temporal_CapabilitiesType"/>
 *         &lt;element name="Existence_Capabilities" type="{http://www.opengis.net/ogc}Existence_CapabilitiesType"/>
 *         &lt;element name="Classification_Capabilities" type="{http://www.opengis.net/ogc}Classification_CapabilitiesType"/>
 *         &lt;element name="Scalar_Capabilities" type="{http://www.opengis.net/ogc}Scalar_CapabilitiesType"/>
 *         &lt;element name="Id_Capabilities" type="{http://www.opengis.net/ogc}Id_CapabilitiesType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "spatialCapabilities",
    "temporalCapabilities",
    "existenceCapabilities",
    "classificationCapabilities",
    "scalarCapabilities",
    "idCapabilities"
})
@XmlRootElement(name = "Filter_Capabilities")
public class FilterCapabilities implements org.opengis.filter.capability.FilterCapabilities {

    @XmlElement(name = "Spatial_Capabilities", required = true)
    private SpatialCapabilitiesType spatialCapabilities;
    @XmlElement(name = "Temporal_Capabilities", required = true)
    private TemporalCapabilitiesType temporalCapabilities;
    @XmlElement(name = "Existence_Capabilities", required = true)
    private ExistenceCapabilitiesType existenceCapabilities;
    @XmlElement(name = "Classification_Capabilities", required = true)
    private ClassificationCapabilitiesType classificationCapabilities;
    @XmlElement(name = "Scalar_Capabilities", required = true)
    private ScalarCapabilitiesType scalarCapabilities;
    @XmlElement(name = "Id_Capabilities", required = true)
    private IdCapabilitiesType idCapabilities;

    /**
     * An empty constructor used by JAXB
     */
    public FilterCapabilities() {
        
    }
    
    /**
     * Build a new filter capabilities (light version)
     */
    public FilterCapabilities(ScalarCapabilities scalar, SpatialCapabilities spatial, IdCapabilities id) {
        this.spatialCapabilities = (SpatialCapabilitiesType) spatial;
        this.idCapabilities      = (IdCapabilitiesType)      id;
        this.scalarCapabilities  = (ScalarCapabilitiesType)  scalar;
        
    }
    
    
    /**
     * Gets the value of the spatialCapabilities property.
     */
    public SpatialCapabilitiesType getSpatialCapabilities() {
        return spatialCapabilities;
    }

    /**
     * Gets the value of the temporalCapabilities property.
    */
    public TemporalCapabilitiesType getTemporalCapabilities() {
        return temporalCapabilities;
    }

    /**
     * Gets the value of the existenceCapabilities property.
     */
    public ExistenceCapabilitiesType getExistenceCapabilities() {
        return existenceCapabilities;
    }

    /**
     * Gets the value of the classificationCapabilities property.
     */
    public ClassificationCapabilitiesType getClassificationCapabilities() {
        return classificationCapabilities;
    }


    /**
     * Gets the value of the scalarCapabilities property.
     */
    public ScalarCapabilitiesType getScalarCapabilities() {
        return scalarCapabilities;
    }

    /**
     * Gets the value of the idCapabilities property.
     */
    public IdCapabilitiesType getIdCapabilities() {
        return idCapabilities;
    }

    public String getVersion() {
        return "1.1.0";
    }
}
