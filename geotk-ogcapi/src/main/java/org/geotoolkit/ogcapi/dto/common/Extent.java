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
package org.geotoolkit.ogcapi.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.util.FactoryException;

/**
 * This extent schema includes optional additional dimensions, but will still
 * validate for objects not conforming to UAD.
 */
@JsonPropertyOrder({
    Extent.JSON_PROPERTY_SPATIAL,
    Extent.JSON_PROPERTY_TEMPORAL,
    Extent.JSON_PROPERTY_VERTICAL
})
public final class Extent extends DataTransferObject {

    public static final String JSON_PROPERTY_SPATIAL = "spatial";
    private SpatialExtent spatial;

    public static final String JSON_PROPERTY_TEMPORAL = "temporal";
    private TemporalExtent temporal;

    public static final String JSON_PROPERTY_VERTICAL = "vertical";
    private VerticalExtent vertical;

    public static final String JSON_PROPERTY_CRS = "crs";
    private String crs;

    public static final String JSON_PROPERTY_TRS = "trs";
    private String trs;

    public Extent() {
    }

    public Extent spatial(SpatialExtent spatial) {
        this.spatial = spatial;
        return this;
    }

    /**
     * Get spatial
     *
     * @return spatial
     */
    @JsonProperty(JSON_PROPERTY_SPATIAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public SpatialExtent getSpatial() {
        return spatial;
    }

    @JsonProperty(JSON_PROPERTY_SPATIAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setSpatial(SpatialExtent spatial) {
        this.spatial = spatial;
    }

    public Extent temporal(TemporalExtent temporal) {
        this.temporal = temporal;
        return this;
    }

    /**
     * Get temporal
     *
     * @return temporal
     */
    @JsonProperty(JSON_PROPERTY_TEMPORAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public TemporalExtent getTemporal() {
        return temporal;
    }

    @JsonProperty(JSON_PROPERTY_TEMPORAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTemporal(TemporalExtent temporal) {
        this.temporal = temporal;
    }

    public Extent vertical(VerticalExtent vertical) {
        this.vertical = vertical;
        return this;
    }

    /**
     * Get vertical
     *
     * @return vertical
     */
    @JsonProperty(JSON_PROPERTY_VERTICAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public VerticalExtent getVertical() {
        return vertical;
    }

    @JsonProperty(JSON_PROPERTY_VERTICAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setVertical(VerticalExtent vertical) {
        this.vertical = vertical;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCrs(String crs) {
        this.crs = crs;
        if(this.spatial != null) {
            this.spatial.setCrs(this.crs);
        } else {
            this.spatial = new SpatialExtent().crs(this.crs);
        }
    }

    @JsonProperty(JSON_PROPERTY_TRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTrs(String trs) {
        this.trs = trs;
        if(this.temporal != null) {
            this.temporal.setTrs(this.trs);
        } else {
            this.temporal = new TemporalExtent().trs(this.trs);
        }
    }

    private String buildCompoundUri(List<String> components) {
        StringBuilder uri = new StringBuilder("http://www.opengis.net/def/crs-compound?");
        for (int i = 0; i < components.size(); i++) {
            uri.append(i + 1).append("=").append(components.get(i));
            if (i < components.size() - 1) uri.append("&");
        }
        return uri.toString();
    }

    public void setFromCoordinateReferenceSystem(CoordinateReferenceSystem crs) {
        List<String> components = new ArrayList<>();

        if (crs instanceof CompoundCRS compoundCRS) {
            for (CoordinateReferenceSystem component : compoundCRS.getComponents()) {
                if (component instanceof org.opengis.referencing.crs.TemporalCRS) {
                    this.trs = opengisCRS(component);
                    if(this.temporal != null) {
                        this.temporal.setTrs(this.trs);
                    } else {
                        this.temporal = new TemporalExtent().trs(this.trs);
                    }
                } else {
                    components.add(opengisCRS(component));
                }
            }
            // Handle spatial + vertical combinations
            if (!components.isEmpty()) {
                this.crs = components.size() > 1
                        ? buildCompoundUri(components)
                        : components.get(0);
            }
        } else {
            this.crs = opengisCRS(crs);
        }
        if (this.crs != null) {
            if(this.spatial != null) {
                this.spatial.setCrs(this.crs);
            } else {
                this.spatial = new SpatialExtent().crs(this.crs);
            }
        }
    }

    //TODO : add others CRS and they links to opengis
    private String opengisCRS(CoordinateReferenceSystem crs) {
        Identifier name = crs.getName();

        // Spatial CRSs
        if (crs instanceof GeographicCRS || crs instanceof ProjectedCRS) {
            if ("WGS 84".equalsIgnoreCase(name.getCode())) {
                return "http://www.opengis.net/def/crs/OGC/1.3/CRS84";
            } else if ("ETRS89-extended / LAEA Europe".equalsIgnoreCase(name.getCode())) {
                return "https://www.opengis.net/def/crs/EPSG/0/3035";
            }
        }
        // Vertical CRSs
        else if (crs instanceof VerticalCRS) {
            if ("NAVD88".equalsIgnoreCase(name.getCode())) {
                return "https://www.opengis.net/def/crs/EPSG/0/5703";
            }
        }
        // Temporal CRSs
        else if (crs instanceof TemporalCRS) {
            if ("Java time".equalsIgnoreCase(name.getCode())) {
                return "https://www.opengis.net/def/crs/OGC/0/UnixTime";
            }
        }

        // Fallback: Use EPSG code if available
        return crs.getIdentifiers().isEmpty()
                ? name.getCode()
                : "https://www.opengis.net/def/crs/EPSG/0/" + crs.getIdentifiers().iterator().next().getCode();
    }

    public String getSrs() {
        List<String> parts = new ArrayList<>();
        if (crs != null) parts.add(crs);
        if (trs != null) parts.add(trs);

        return parts.isEmpty()
                ? null
                : (parts.size() > 1) ? buildCompoundUri(parts) : parts.get(0);
    }

    public static CoordinateReferenceSystem getCrsFromName(String crsName) {
        if (crsName != null) {
            try {
                return CRS.forCode(crsName);
            } catch (FactoryException e) {}
        }
        return CommonCRS.defaultGeographic();
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
                && Objects.equals(this.temporal, extent.temporal)
                && Objects.equals(this.vertical, extent.vertical);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spatial, temporal, vertical);
    }

}
