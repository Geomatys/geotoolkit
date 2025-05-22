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
package org.geotoolkit.ogcapi.model.dggs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;
import org.geotoolkit.ogcapi.model.common.Link;

/**
 * DatasetGetDGGRSZones200Response
 */
@JsonPropertyOrder({
    DggrsZonesResponse.JSON_PROPERTY_ZONES,
    DggrsZonesResponse.JSON_PROPERTY_RETURNED_AREA_METERS_SQUARE,
    DggrsZonesResponse.JSON_PROPERTY_RETURNED_VOLUME_METERS_CUBE,
    DggrsZonesResponse.JSON_PROPERTY_RETURNED_VOLUME_METERS_SQUARE_SECONDS,
    DggrsZonesResponse.JSON_PROPERTY_RETURNED_HYPER_VOLUME_METERS_CUBE_SECONDS,
    DggrsZonesResponse.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "DatasetGetDGGRSZones200Response")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DatasetGetDGGRSZones200Response")
public class DggrsZonesResponse extends DataTransferObject {

    public static final String JSON_PROPERTY_ZONES = "zones";
    @XmlElement(name = "zones")
    @jakarta.annotation.Nonnull
    private List<String> zones = new ArrayList<>();

    public static final String JSON_PROPERTY_RETURNED_AREA_METERS_SQUARE = "returnedAreaMetersSquare";
    @XmlElement(name = "returnedAreaMetersSquare")
    @jakarta.annotation.Nullable
    private Double returnedAreaMetersSquare;

    public static final String JSON_PROPERTY_RETURNED_VOLUME_METERS_CUBE = "returnedVolumeMetersCube";
    @XmlElement(name = "returnedVolumeMetersCube")
    @jakarta.annotation.Nullable
    private Double returnedVolumeMetersCube;

    public static final String JSON_PROPERTY_RETURNED_VOLUME_METERS_SQUARE_SECONDS = "returnedVolumeMetersSquareSeconds";
    @XmlElement(name = "returnedVolumeMetersSquareSeconds")
    @jakarta.annotation.Nullable
    private Double returnedVolumeMetersSquareSeconds;

    public static final String JSON_PROPERTY_RETURNED_HYPER_VOLUME_METERS_CUBE_SECONDS = "returnedHyperVolumeMetersCubeSeconds";
    @XmlElement(name = "returnedHyperVolumeMetersCubeSeconds")
    @jakarta.annotation.Nullable
    private Double returnedHyperVolumeMetersCubeSeconds;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nullable
    private List<Link> links = new ArrayList<>();

    public DggrsZonesResponse() {
    }

    public DggrsZonesResponse zones(@jakarta.annotation.Nonnull List<String> zones) {
        this.zones = zones;
        return this;
    }

    public DggrsZonesResponse addZonesItem(String zonesItem) {
        if (this.zones == null) {
            this.zones = new ArrayList<>();
        }
        this.zones.add(zonesItem);
        return this;
    }

    /**
     * Get zones
     *
     * @return zones
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ZONES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "zones")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getZones() {
        return zones;
    }

    @JsonProperty(JSON_PROPERTY_ZONES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "zones")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setZones(@jakarta.annotation.Nonnull List<String> zones) {
        this.zones = zones;
    }

    public DggrsZonesResponse returnedAreaMetersSquare(@jakarta.annotation.Nullable Double returnedAreaMetersSquare) {
        this.returnedAreaMetersSquare = returnedAreaMetersSquare;
        return this;
    }

    /**
     * Get returnedAreaMetersSquare
     *
     * @return returnedAreaMetersSquare
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RETURNED_AREA_METERS_SQUARE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "returnedAreaMetersSquare")
    public Double getReturnedAreaMetersSquare() {
        return returnedAreaMetersSquare;
    }

    @JsonProperty(JSON_PROPERTY_RETURNED_AREA_METERS_SQUARE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "returnedAreaMetersSquare")
    public void setReturnedAreaMetersSquare(@jakarta.annotation.Nullable Double returnedAreaMetersSquare) {
        this.returnedAreaMetersSquare = returnedAreaMetersSquare;
    }

    public DggrsZonesResponse returnedVolumeMetersCube(@jakarta.annotation.Nullable Double returnedVolumeMetersCube) {
        this.returnedVolumeMetersCube = returnedVolumeMetersCube;
        return this;
    }

    /**
     * Get returnedVolumeMetersCube
     *
     * @return returnedVolumeMetersCube
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RETURNED_VOLUME_METERS_CUBE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "returnedVolumeMetersCube")
    public Double getReturnedVolumeMetersCube() {
        return returnedVolumeMetersCube;
    }

    @JsonProperty(JSON_PROPERTY_RETURNED_VOLUME_METERS_CUBE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "returnedVolumeMetersCube")
    public void setReturnedVolumeMetersCube(@jakarta.annotation.Nullable Double returnedVolumeMetersCube) {
        this.returnedVolumeMetersCube = returnedVolumeMetersCube;
    }

    public DggrsZonesResponse returnedVolumeMetersSquareSeconds(@jakarta.annotation.Nullable Double returnedVolumeMetersSquareSeconds) {
        this.returnedVolumeMetersSquareSeconds = returnedVolumeMetersSquareSeconds;
        return this;
    }

    /**
     * Get returnedVolumeMetersSquareSeconds
     *
     * @return returnedVolumeMetersSquareSeconds
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RETURNED_VOLUME_METERS_SQUARE_SECONDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "returnedVolumeMetersSquareSeconds")
    public Double getReturnedVolumeMetersSquareSeconds() {
        return returnedVolumeMetersSquareSeconds;
    }

    @JsonProperty(JSON_PROPERTY_RETURNED_VOLUME_METERS_SQUARE_SECONDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "returnedVolumeMetersSquareSeconds")
    public void setReturnedVolumeMetersSquareSeconds(@jakarta.annotation.Nullable Double returnedVolumeMetersSquareSeconds) {
        this.returnedVolumeMetersSquareSeconds = returnedVolumeMetersSquareSeconds;
    }

    public DggrsZonesResponse returnedHyperVolumeMetersCubeSeconds(@jakarta.annotation.Nullable Double returnedHyperVolumeMetersCubeSeconds) {
        this.returnedHyperVolumeMetersCubeSeconds = returnedHyperVolumeMetersCubeSeconds;
        return this;
    }

    /**
     * Get returnedHyperVolumeMetersCubeSeconds
     *
     * @return returnedHyperVolumeMetersCubeSeconds
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RETURNED_HYPER_VOLUME_METERS_CUBE_SECONDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "returnedHyperVolumeMetersCubeSeconds")
    public Double getReturnedHyperVolumeMetersCubeSeconds() {
        return returnedHyperVolumeMetersCubeSeconds;
    }

    @JsonProperty(JSON_PROPERTY_RETURNED_HYPER_VOLUME_METERS_CUBE_SECONDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "returnedHyperVolumeMetersCubeSeconds")
    public void setReturnedHyperVolumeMetersCubeSeconds(@jakarta.annotation.Nullable Double returnedHyperVolumeMetersCubeSeconds) {
        this.returnedHyperVolumeMetersCubeSeconds = returnedHyperVolumeMetersCubeSeconds;
    }

    public DggrsZonesResponse links(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
        return this;
    }

    public DggrsZonesResponse addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Get links
     *
     * @return links
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
    }

    /**
     * Return true if this _dataset_getDGGRSZones_200_response object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DggrsZonesResponse datasetGetDGGRSZones200Response = (DggrsZonesResponse) o;
        return Objects.equals(this.zones, datasetGetDGGRSZones200Response.zones)
                && Objects.equals(this.returnedAreaMetersSquare, datasetGetDGGRSZones200Response.returnedAreaMetersSquare)
                && Objects.equals(this.returnedVolumeMetersCube, datasetGetDGGRSZones200Response.returnedVolumeMetersCube)
                && Objects.equals(this.returnedVolumeMetersSquareSeconds, datasetGetDGGRSZones200Response.returnedVolumeMetersSquareSeconds)
                && Objects.equals(this.returnedHyperVolumeMetersCubeSeconds, datasetGetDGGRSZones200Response.returnedHyperVolumeMetersCubeSeconds)
                && Objects.equals(this.links, datasetGetDGGRSZones200Response.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zones, returnedAreaMetersSquare, returnedVolumeMetersCube, returnedVolumeMetersSquareSeconds, returnedHyperVolumeMetersCubeSeconds, links);
    }

}
