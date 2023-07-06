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
public class FilterCapabilities extends org.geotoolkit.filter.capability.FilterCapabilities {

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
    public FilterCapabilities(final ScalarCapabilitiesType scalar, final SpatialCapabilitiesType spatial, final IdCapabilitiesType id) {
        this.spatialCapabilities = spatial;
        this.idCapabilities      = id;
        this.scalarCapabilities  = scalar;
    }

    /**
     * Build a new filter capabilities (light version)
     */
    public FilterCapabilities(final ScalarCapabilitiesType scalar, final SpatialCapabilitiesType spatial, final IdCapabilitiesType id, final TemporalCapabilitiesType temporal) {
        this.spatialCapabilities  = spatial;
        this.idCapabilities       = id;
        this.scalarCapabilities   = scalar;
    }

    /**
     * Gets the value of the spatialCapabilities property.
     */
    @Override
    public SpatialCapabilitiesType getSpatialCapabilities() {
        return spatialCapabilities;
    }

    public void setSpatialCapabilities(final SpatialCapabilitiesType spatialCapabilities) {
        this.spatialCapabilities = spatialCapabilities;
    }

    /**
     * Gets the value of the temporalCapabilities property.
    */
    @Override
    public TemporalCapabilitiesType getTemporalCapabilities() {
        return temporalCapabilities;
    }

    public void setTemporalCapabilities(final TemporalCapabilitiesType temporalCapabilities) {
        this.temporalCapabilities = temporalCapabilities;
    }

    /**
     * Gets the value of the existenceCapabilities property.
     */
    public ExistenceCapabilitiesType getExistenceCapabilities() {
        return existenceCapabilities;
    }

    public void setExistenceCapabilities(final ExistenceCapabilitiesType existenceCapabilities) {
        this.existenceCapabilities = existenceCapabilities;
    }

    /**
     * Gets the value of the classificationCapabilities property.
     */
    public ClassificationCapabilitiesType getClassificationCapabilities() {
        return classificationCapabilities;
    }

    public void setClassificationCapabilities(final ClassificationCapabilitiesType classificationCapabilities) {
        this.classificationCapabilities = classificationCapabilities;
    }

    /**
     * Gets the value of the scalarCapabilities property.
     */
    @Override
    public ScalarCapabilitiesType getScalarCapabilities() {
        return scalarCapabilities;
    }

    public void setScalarCapabilities(final ScalarCapabilitiesType scalarCapabilities) {
        this.scalarCapabilities = scalarCapabilities;
    }

    /**
     * Gets the value of the idCapabilities property.
     */
    @Override
    public IdCapabilitiesType getIdCapabilities() {
        return idCapabilities;
    }

    public void setIdCapabilities(final IdCapabilitiesType idCapabilities) {
        this.idCapabilities = idCapabilities;
    }

    @Override
    public String getVersion() {
        return "1.1.0";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[FilterCapabilities]").append("\n");
        if (classificationCapabilities != null) {
            sb.append("classificationCapabilities: ").append(classificationCapabilities).append('\n');
        }
        if (existenceCapabilities != null) {
            sb.append("existenceCapabilities: ").append(existenceCapabilities).append('\n');
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


            return Objects.equals(this.classificationCapabilities, that.classificationCapabilities) &&
                   Objects.equals(this.existenceCapabilities,      that.existenceCapabilities)      &&
                   Objects.equals(this.idCapabilities,             that.idCapabilities)             &&
                   Objects.equals(this.scalarCapabilities,         that.scalarCapabilities)         &&
                   Objects.equals(this.spatialCapabilities,        that.spatialCapabilities)        &&
                   Objects.equals(this.temporalCapabilities,        that.temporalCapabilities);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.spatialCapabilities != null ? this.spatialCapabilities.hashCode() : 0);
        hash = 59 * hash + (this.temporalCapabilities != null ? this.temporalCapabilities.hashCode() : 0);
        hash = 59 * hash + (this.existenceCapabilities != null ? this.existenceCapabilities.hashCode() : 0);
        hash = 59 * hash + (this.classificationCapabilities != null ? this.classificationCapabilities.hashCode() : 0);
        hash = 59 * hash + (this.scalarCapabilities != null ? this.scalarCapabilities.hashCode() : 0);
        hash = 59 * hash + (this.idCapabilities != null ? this.idCapabilities.hashCode() : 0);
        return hash;
    }
}
