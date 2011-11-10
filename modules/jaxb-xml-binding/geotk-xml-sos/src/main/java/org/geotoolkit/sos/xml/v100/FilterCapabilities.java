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
import org.geotoolkit.ogc.xml.v110.IdCapabilitiesType;
import org.geotoolkit.ogc.xml.v110.ScalarCapabilitiesType;
import org.geotoolkit.ogc.xml.v110.SpatialCapabilitiesType;
import org.geotoolkit.ogc.xml.v110.TemporalCapabilitiesType;
import org.geotoolkit.util.Utilities;


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
 *         &lt;element ref="{http://www.opengis.net/ogc}Spatial_Capabilities"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Temporal_Capabilities"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Scalar_Capabilities"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Id_Capabilities"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "spatialCapabilities",
    "temporalCapabilities",
    "scalarCapabilities",
    "idCapabilities"
})
@XmlRootElement(name = "Filter_Capabilities")
public class FilterCapabilities {

    @XmlElement(name = "Spatial_Capabilities", namespace = "http://www.opengis.net/ogc", required = true)
    private SpatialCapabilitiesType spatialCapabilities;
    @XmlElement(name = "Temporal_Capabilities", namespace = "http://www.opengis.net/ogc", required = true)
    private TemporalCapabilitiesType temporalCapabilities;
    @XmlElement(name = "Scalar_Capabilities", namespace = "http://www.opengis.net/ogc", required = true)
    private ScalarCapabilitiesType scalarCapabilities;
    @XmlElement(name = "Id_Capabilities", namespace = "http://www.opengis.net/ogc", required = true)
    private IdCapabilitiesType idCapabilities;

    /**
     * An empty constructor used by JAXB
     */
    public FilterCapabilities(){
        
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
    
    /**
     * @param spatialCapabilities the spatialCapabilities to set
     */
    public void setSpatialCapabilities(final SpatialCapabilitiesType spatialCapabilities) {
        this.spatialCapabilities = spatialCapabilities;
    }

    /**
     * @param temporalCapabilities the temporalCapabilities to set
     */
    public void setTemporalCapabilities(final TemporalCapabilitiesType temporalCapabilities) {
        this.temporalCapabilities = temporalCapabilities;
    }

    /**
     * @param scalarCapabilities the scalarCapabilities to set
     */
    public void setScalarCapabilities(final ScalarCapabilitiesType scalarCapabilities) {
        this.scalarCapabilities = scalarCapabilities;
    }

    /**
     * @param idCapabilities the idCapabilities to set
     */
    public void setIdCapabilities(final IdCapabilitiesType idCapabilities) {
        this.idCapabilities = idCapabilities;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[FilterCapabilities]").append("\n");
        if (idCapabilities != null) {
            sb.append("idCapabilities: ").append(idCapabilities).append('\n');
        }
        if (scalarCapabilities != null) {
            sb.append("scalarCapabilities: ").append(scalarCapabilities).append('\n');
        }
        if (spatialCapabilities != null) {
            sb.append("spatialCapabilities: ").append(spatialCapabilities).append('\n');
        }
        if (temporalCapabilities != null) {
            sb.append("temporalCapabilities: ").append(temporalCapabilities).append('\n');
        }
        return sb.toString();
    }
    
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof FilterCapabilities) {
            final FilterCapabilities that = (FilterCapabilities) object;
            return Utilities.equals(this.idCapabilities,       that.idCapabilities)      &&
                   Utilities.equals(this.scalarCapabilities,   that.scalarCapabilities)  &&
                   Utilities.equals(this.spatialCapabilities,  that.spatialCapabilities) &&
                   Utilities.equals(this.temporalCapabilities, that.temporalCapabilities);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.spatialCapabilities != null ? this.spatialCapabilities.hashCode() : 0);
        hash = 53 * hash + (this.temporalCapabilities != null ? this.temporalCapabilities.hashCode() : 0);
        hash = 53 * hash + (this.scalarCapabilities != null ? this.scalarCapabilities.hashCode() : 0);
        hash = 53 * hash + (this.idCapabilities != null ? this.idCapabilities.hashCode() : 0);
        return hash;
    }
}
