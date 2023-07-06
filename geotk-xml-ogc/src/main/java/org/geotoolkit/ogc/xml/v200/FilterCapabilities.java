/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.ogc.xml.v200;

import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


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
 *         &lt;element name="Conformance" type="{http://www.opengis.net/fes/2.0}ConformanceType"/>
 *         &lt;element name="Id_Capabilities" type="{http://www.opengis.net/fes/2.0}Id_CapabilitiesType" minOccurs="0"/>
 *         &lt;element name="Scalar_Capabilities" type="{http://www.opengis.net/fes/2.0}Scalar_CapabilitiesType" minOccurs="0"/>
 *         &lt;element name="Spatial_Capabilities" type="{http://www.opengis.net/fes/2.0}Spatial_CapabilitiesType" minOccurs="0"/>
 *         &lt;element name="Temporal_Capabilities" type="{http://www.opengis.net/fes/2.0}Temporal_CapabilitiesType" minOccurs="0"/>
 *         &lt;element name="Functions" type="{http://www.opengis.net/fes/2.0}AvailableFunctionsType" minOccurs="0"/>
 *         &lt;element name="Extended_Capabilities" type="{http://www.opengis.net/fes/2.0}Extended_CapabilitiesType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "conformance",
    "idCapabilities",
    "scalarCapabilities",
    "spatialCapabilities",
    "temporalCapabilities",
    "functions",
    "extendedCapabilities"
})
@XmlRootElement(name = "Filter_Capabilities")
public class FilterCapabilities extends org.geotoolkit.filter.capability.FilterCapabilities {

    @XmlElement(name = "Conformance", required = true)
    private ConformanceType conformance;
    @XmlElement(name = "Id_Capabilities")
    private IdCapabilitiesType idCapabilities;
    @XmlElement(name = "Scalar_Capabilities")
    private ScalarCapabilitiesType scalarCapabilities;
    @XmlElement(name = "Spatial_Capabilities")
    private SpatialCapabilitiesType spatialCapabilities;
    @XmlElement(name = "Temporal_Capabilities")
    private TemporalCapabilitiesType temporalCapabilities;
    @XmlElement(name = "Functions")
    private AvailableFunctionsType functions;
    @XmlElement(name = "Extended_Capabilities")
    private ExtendedCapabilitiesType extendedCapabilities;

    /**
     * An empty constructor used by JAXB
     */
    public FilterCapabilities() {
    }

    /**
     * Build a new filter capabilities (light version)
     */
    public FilterCapabilities(final ScalarCapabilitiesType scalar, final SpatialCapabilitiesType spatial, final IdCapabilitiesType id, final ConformanceType conformance) {
        this.spatialCapabilities = spatial;
        this.idCapabilities      = id;
        this.scalarCapabilities  = scalar;
        this.conformance = conformance;
    }

    public FilterCapabilities(final ScalarCapabilitiesType scalar, final SpatialCapabilitiesType spatial, final TemporalCapabilitiesType temporal,
            final IdCapabilitiesType id, final ConformanceType conformance) {
        this.spatialCapabilities  = spatial;
        this.temporalCapabilities = temporal;
        this.scalarCapabilities   = scalar;
        this.conformance = conformance;
        this.idCapabilities = id;
    }

    /**
     * Gets the value of the conformance property.
     */
    public ConformanceType getConformance() {
        return conformance;
    }

    /**
     * Sets the value of the conformance property.
     */
    public void setConformance(ConformanceType value) {
        this.conformance = value;
    }

    /**
     * Gets the value of the idCapabilities property.
     */
    @Override
    public IdCapabilitiesType getIdCapabilities() {
        return idCapabilities;
    }

    /**
     * Sets the value of the idCapabilities property.
     */
    public void setIdCapabilities(final IdCapabilitiesType value) {
        this.idCapabilities = value;
    }

    /**
     * Gets the value of the scalarCapabilities property.
     */
    @Override
    public ScalarCapabilitiesType getScalarCapabilities() {
        return scalarCapabilities;
    }

    /**
     * Sets the value of the scalarCapabilities property.
     */
    public void setScalarCapabilities(final ScalarCapabilitiesType value) {
        this.scalarCapabilities = value;
    }

    /**
     * Gets the value of the spatialCapabilities property.
     */
    @Override
    public SpatialCapabilitiesType getSpatialCapabilities() {
        return spatialCapabilities;
    }

    /**
     * Sets the value of the spatialCapabilities property.
     */
    public void setSpatialCapabilities(final SpatialCapabilitiesType value) {
        this.spatialCapabilities = value;
    }

    /**
     * Gets the value of the temporalCapabilities property.
     */
    @Override
    public TemporalCapabilitiesType getTemporalCapabilities() {
        return temporalCapabilities;
    }

    /**
     * Sets the value of the temporalCapabilities property.
     */
    public void setTemporalCapabilities(TemporalCapabilitiesType value) {
        this.temporalCapabilities = value;
    }

    /**
     * Gets the value of the functions property.
     */
    public AvailableFunctionsType getFunctions() {
        return functions;
    }

    /**
     * Sets the value of the functions property.
     */
    public void setFunctions(AvailableFunctionsType value) {
        this.functions = value;
    }

    /**
     * Gets the value of the extendedCapabilities property.
     */
    public ExtendedCapabilitiesType getExtendedCapabilities() {
        return extendedCapabilities;
    }

    /**
     * Sets the value of the extendedCapabilities property.
     */
    public void setExtendedCapabilities(ExtendedCapabilitiesType value) {
        this.extendedCapabilities = value;
    }

    @Override
    public String getVersion() {
        return "2.0.0";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[FilterCapabilities]").append("\n");
        if (conformance != null) {
            sb.append("conformance: ").append(conformance).append('\n');
        }
        if (extendedCapabilities != null) {
            sb.append("extendedCapabilities: ").append(extendedCapabilities).append('\n');
        }
        if (functions != null) {
            sb.append("functions: ").append(functions).append('\n');
        }
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
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
       if (object instanceof FilterCapabilities) {
           final FilterCapabilities that = (FilterCapabilities) object;
            return Objects.equals(this.conformance,          that.conformance)            &&
                   Objects.equals(this.extendedCapabilities, that.extendedCapabilities)   &&
                   Objects.equals(this.functions,            that.functions)              &&
                   Objects.equals(this.idCapabilities,       that.idCapabilities)         &&
                   Objects.equals(this.scalarCapabilities,   that.scalarCapabilities)     &&
                   Objects.equals(this.spatialCapabilities,  that.spatialCapabilities)    &&
                   Objects.equals(this.temporalCapabilities, that.temporalCapabilities);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.spatialCapabilities != null ? this.spatialCapabilities.hashCode() : 0);
        hash = 59 * hash + (this.temporalCapabilities != null ? this.temporalCapabilities.hashCode() : 0);
        hash = 59 * hash + (this.extendedCapabilities != null ? this.extendedCapabilities.hashCode() : 0);
        hash = 59 * hash + (this.functions != null ? this.functions.hashCode() : 0);
        hash = 59 * hash + (this.conformance != null ? this.conformance.hashCode() : 0);
        hash = 59 * hash + (this.scalarCapabilities != null ? this.scalarCapabilities.hashCode() : 0);
        hash = 59 * hash + (this.idCapabilities != null ? this.idCapabilities.hashCode() : 0);
        return hash;
    }
}
