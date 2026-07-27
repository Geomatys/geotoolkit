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
package org.geotoolkit.ogcapi.dto.dggs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Link;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeature;

/**
 * Zone information for a particular zone of a DGGS, including useful information such as zone geometry, data access
 * links and statistics
 */
@JsonPropertyOrder({
    ZoneInfo.JSON_PROPERTY_ID,
    ZoneInfo.JSON_PROPERTY_LINKS,
    ZoneInfo.JSON_PROPERTY_SHAPE_TYPE,
    ZoneInfo.JSON_PROPERTY_LEVEL,
    ZoneInfo.JSON_PROPERTY_CRS,
    ZoneInfo.JSON_PROPERTY_CENTROID,
    ZoneInfo.JSON_PROPERTY_BBOX,
    ZoneInfo.JSON_PROPERTY_AREA_METERS_SQUARE,
    ZoneInfo.JSON_PROPERTY_VOLUME_METERS_CUBE,
    ZoneInfo.JSON_PROPERTY_TEMPORAL_DURATION_SECONDS,
    ZoneInfo.JSON_PROPERTY_GEOMETRY,
    ZoneInfo.JSON_PROPERTY_TEMPORAL_INTERVAL,
    ZoneInfo.JSON_PROPERTY_STATISTICS
})
public final class ZoneInfo extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_SHAPE_TYPE = "shapeType";
    private String shapeType;

    public static final String JSON_PROPERTY_LEVEL = "level";
    private Integer level;

    public static final String JSON_PROPERTY_CRS = "crs";
    private URI crs;

    public static final String JSON_PROPERTY_CENTROID = "centroid";
    private List<Double> centroid = new ArrayList<>();

    public static final String JSON_PROPERTY_BBOX = "bbox";
    private List<Double> bbox = new ArrayList<>();

    public static final String JSON_PROPERTY_AREA_METERS_SQUARE = "areaMetersSquare";
    private Double areaMetersSquare;

    public static final String JSON_PROPERTY_VOLUME_METERS_CUBE = "volumeMetersCube";
    private Double volumeMetersCube;

    public static final String JSON_PROPERTY_TEMPORAL_DURATION_SECONDS = "temporalDurationSeconds";
    private Double temporalDurationSeconds;

    public static final String JSON_PROPERTY_GEOMETRY = "geometry";
    private GeoJSONFeature geometry;

    public static final String JSON_PROPERTY_TEMPORAL_INTERVAL = "temporalInterval";
    private List<String> temporalInterval = new ArrayList<>();

    public static final String JSON_PROPERTY_STATISTICS = "statistics";
    private Map<String, ZoneInfoStatisticsValue> statistics = new HashMap<>();

    public ZoneInfo() {
    }

    public ZoneInfo id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Zone identifier based on the DGGRS.
     *
     * @return id
     */
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setId(String id) {
        this.id = id;
    }

    public ZoneInfo links(List<Link> links) {
        this.links = links;
        return this;
    }

    public ZoneInfo addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Links to related resources. A &#x60;[ogc-rel:dggrs-zone-data]&#x60; link to retrieve data for this zone and a
     * &#x60;[ogc-rel:dggrs]&#x60; back to the &#x60;.../dggs&#x60; resource must be included.
     *
     * @return links
     */
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public ZoneInfo shapeType(String shapeType) {
        this.shapeType = shapeType;
        return this;
    }

    /**
     * The type of shape for the zone geometry (e.g., hexagon or pentagon)
     *
     * @return shapeType
     */
    @JsonProperty(JSON_PROPERTY_SHAPE_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getShapeType() {
        return shapeType;
    }

    @JsonProperty(JSON_PROPERTY_SHAPE_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setShapeType(String shapeType) {
        this.shapeType = shapeType;
    }

    public ZoneInfo level(Integer level) {
        this.level = level;
        return this;
    }

    /**
     * The refinement level of this zone minimum: 0
     *
     * @return level
     */
    @JsonProperty(JSON_PROPERTY_LEVEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getLevel() {
        return level;
    }

    @JsonProperty(JSON_PROPERTY_LEVEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLevel(Integer level) {
        this.level = level;
    }

    public ZoneInfo crs(URI crs) {
        this.crs = crs;
        return this;
    }

    /**
     * The Coordinate Reference System in which the geometry, centroid and bbox properties are specified
     *
     * @return crs
     */
    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public URI getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCrs(URI crs) {
        this.crs = crs;
    }

    public ZoneInfo centroid(List<Double> centroid) {
        this.centroid = centroid;
        return this;
    }

    public ZoneInfo addCentroidItem(Double centroidItem) {
        if (this.centroid == null) {
            this.centroid = new ArrayList<>();
        }
        this.centroid.add(centroidItem);
        return this;
    }

    /**
     * The centroid of the zone, in the CRS specified in crs property
     *
     * @return centroid
     */
    @JsonProperty(JSON_PROPERTY_CENTROID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Double> getCentroid() {
        return centroid;
    }

    @JsonProperty(JSON_PROPERTY_CENTROID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCentroid(List<Double> centroid) {
        this.centroid = centroid;
    }

    public ZoneInfo bbox(List<Double> bbox) {
        this.bbox = bbox;
        return this;
    }

    public ZoneInfo addBboxItem(Double bboxItem) {
        if (this.bbox == null) {
            this.bbox = new ArrayList<>();
        }
        this.bbox.add(bboxItem);
        return this;
    }

    /**
     * The spatial envelope of the zone (bounding box), in the CRS specified in crs property
     *
     * @return bbox
     */
    @JsonProperty(JSON_PROPERTY_BBOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Double> getBbox() {
        return bbox;
    }

    @JsonProperty(JSON_PROPERTY_BBOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    public ZoneInfo areaMetersSquare(Double areaMetersSquare) {
        this.areaMetersSquare = areaMetersSquare;
        return this;
    }

    /**
     * Surface area of the zone in meters square.
     *
     * @return areaMetersSquare
     */
    @JsonProperty(JSON_PROPERTY_AREA_METERS_SQUARE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getAreaMetersSquare() {
        return areaMetersSquare;
    }

    @JsonProperty(JSON_PROPERTY_AREA_METERS_SQUARE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setAreaMetersSquare(Double areaMetersSquare) {
        this.areaMetersSquare = areaMetersSquare;
    }

    public ZoneInfo volumeMetersCube(Double volumeMetersCube) {
        this.volumeMetersCube = volumeMetersCube;
        return this;
    }

    /**
     * Volume of the zone in meters cube for a DGGS with three spatial dimension.
     *
     * @return volumeMetersCube
     */
    @JsonProperty(JSON_PROPERTY_VOLUME_METERS_CUBE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getVolumeMetersCube() {
        return volumeMetersCube;
    }

    @JsonProperty(JSON_PROPERTY_VOLUME_METERS_CUBE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setVolumeMetersCube(Double volumeMetersCube) {
        this.volumeMetersCube = volumeMetersCube;
    }

    public ZoneInfo temporalDurationSeconds(Double temporalDurationSeconds) {
        this.temporalDurationSeconds = temporalDurationSeconds;
        return this;
    }

    /**
     * Amount of time covered by the zone for a temporal DGGS.
     *
     * @return temporalDurationSeconds
     */
    @JsonProperty(JSON_PROPERTY_TEMPORAL_DURATION_SECONDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getTemporalDurationSeconds() {
        return temporalDurationSeconds;
    }

    @JsonProperty(JSON_PROPERTY_TEMPORAL_DURATION_SECONDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTemporalDurationSeconds(Double temporalDurationSeconds) {
        this.temporalDurationSeconds = temporalDurationSeconds;
    }

    public ZoneInfo geometry(GeoJSONFeature geometry) {
        this.geometry = geometry;
        return this;
    }

    /**
     * Get geometry
     *
     * @return geometry
     */
    @JsonProperty(JSON_PROPERTY_GEOMETRY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public GeoJSONFeature getGeometry() {
        return geometry;
    }

    @JsonProperty(JSON_PROPERTY_GEOMETRY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setGeometry(GeoJSONFeature geometry) {
        this.geometry = geometry;
    }

    public ZoneInfo temporalInterval(List<String> temporalInterval) {
        this.temporalInterval = temporalInterval;
        return this;
    }

    public ZoneInfo addTemporalIntervalItem(String temporalIntervalItem) {
        if (this.temporalInterval == null) {
            this.temporalInterval = new ArrayList<>();
        }
        this.temporalInterval.add(temporalIntervalItem);
        return this;
    }

    /**
     * Sart and end time of the zone.
     *
     * @return temporalInterval
     */
    @JsonProperty(JSON_PROPERTY_TEMPORAL_INTERVAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getTemporalInterval() {
        return temporalInterval;
    }

    @JsonProperty(JSON_PROPERTY_TEMPORAL_INTERVAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTemporalInterval(List<String> temporalInterval) {
        this.temporalInterval = temporalInterval;
    }

    public ZoneInfo statistics(Map<String, ZoneInfoStatisticsValue> statistics) {
        this.statistics = statistics;
        return this;
    }

    public ZoneInfo putStatisticsItem(String key, ZoneInfoStatisticsValue statisticsItem) {
        if (this.statistics == null) {
            this.statistics = new HashMap<>();
        }
        this.statistics.put(key, statisticsItem);
        return this;
    }

    /**
     * Statistics for individual fields of the data (e.g., fields of the range of a coverage, or relevant numeric
     * properties of a feature collection)
     *
     * @return statistics
     */
    @JsonProperty(JSON_PROPERTY_STATISTICS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, ZoneInfoStatisticsValue> getStatistics() {
        return statistics;
    }

    @JsonProperty(JSON_PROPERTY_STATISTICS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setStatistics(Map<String, ZoneInfoStatisticsValue> statistics) {
        this.statistics = statistics;
    }

    /**
     * Return true if this zone-info object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ZoneInfo zoneInfo = (ZoneInfo) o;
        return Objects.equals(this.id, zoneInfo.id)
                && Objects.equals(this.links, zoneInfo.links)
                && Objects.equals(this.shapeType, zoneInfo.shapeType)
                && Objects.equals(this.level, zoneInfo.level)
                && Objects.equals(this.crs, zoneInfo.crs)
                && Objects.equals(this.centroid, zoneInfo.centroid)
                && Objects.equals(this.bbox, zoneInfo.bbox)
                && Objects.equals(this.areaMetersSquare, zoneInfo.areaMetersSquare)
                && Objects.equals(this.volumeMetersCube, zoneInfo.volumeMetersCube)
                && Objects.equals(this.temporalDurationSeconds, zoneInfo.temporalDurationSeconds)
                && Objects.equals(this.geometry, zoneInfo.geometry)
                && Objects.equals(this.temporalInterval, zoneInfo.temporalInterval)
                && Objects.equals(this.statistics, zoneInfo.statistics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, links, shapeType, level, crs, centroid, bbox, areaMetersSquare, volumeMetersCube, temporalDurationSeconds, geometry, temporalInterval, statistics);
    }

}
