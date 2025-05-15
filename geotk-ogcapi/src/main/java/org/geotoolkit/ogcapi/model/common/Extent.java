/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogcapi.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * This extent schema includes optional additional dimensions, but will still
 * validate for objects not conforming to UAD.
 */
@JsonPropertyOrder({
    Extent.JSON_PROPERTY_SPATIAL,
    Extent.JSON_PROPERTY_TEMPORAL
})
@XmlRootElement(name = "Extent")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Extent")
public final class Extent extends DataTransferObject {

    public static final String JSON_PROPERTY_SPATIAL = "spatial";
    @XmlElement(name = "spatial")
    @jakarta.annotation.Nullable
    private SpatialExtent spatial;

    public static final String JSON_PROPERTY_TEMPORAL = "temporal";
    @XmlElement(name = "temporal")
    @jakarta.annotation.Nullable
    private TemporalExtent temporal;

    public Extent() {
    }

    public Extent spatial(@jakarta.annotation.Nullable SpatialExtent spatial) {
        this.spatial = spatial;
        return this;
    }

    /**
     * Get spatial
     *
     * @return spatial
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SPATIAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "spatial")
    public SpatialExtent getSpatial() {
        return spatial;
    }

    @JsonProperty(JSON_PROPERTY_SPATIAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "spatial")
    public void setSpatial(@jakarta.annotation.Nullable SpatialExtent spatial) {
        this.spatial = spatial;
    }

    public Extent temporal(@jakarta.annotation.Nullable TemporalExtent temporal) {
        this.temporal = temporal;
        return this;
    }

    /**
     * Get temporal
     *
     * @return temporal
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TEMPORAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "temporal")
    public TemporalExtent getTemporal() {
        return temporal;
    }

    @JsonProperty(JSON_PROPERTY_TEMPORAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "temporal")
    public void setTemporal(@jakarta.annotation.Nullable TemporalExtent temporal) {
        this.temporal = temporal;
    }

    /**
     * Return true if this extent object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Extent extent = (Extent) o;
        return Objects.equals(this.spatial, extent.spatial)
                && Objects.equals(this.temporal, extent.temporal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spatial, temporal);
    }

}
