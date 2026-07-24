/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.dto.connectedsystems.geojson;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONGeometry;

/**
 * A SamplingFeature resource, encoded as a GeoJSON Feature.
 *
 * <p>Describes a feature used to sample another (real world) feature of interest, per the
 * OGC/ISO Observations and Measurements (O&amp;M) model.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 */
public class SamplingFeature extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_GEOMETRY = "geometry";
    private GeoJSONGeometry geometry;

    public static final String JSON_PROPERTY_BBOX = "bbox";
    private List<Double> bbox;

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    private SamplingFeatureProperties properties;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links;

    /**
     * Local identifier of the resource, assigned by the server. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    public void setId(String id) {
        this.id = id;
    }

    public SamplingFeature id(String id) {
        setId(id);
        return this;
    }

    /**
     * Geographic location of the sampling feature. (optional)
     */
    @JsonProperty(JSON_PROPERTY_GEOMETRY)
    public GeoJSONGeometry getGeometry() {
        return geometry;
    }

    @JsonProperty(JSON_PROPERTY_GEOMETRY)
    public void setGeometry(GeoJSONGeometry geometry) {
        this.geometry = geometry;
    }

    public SamplingFeature geometry(GeoJSONGeometry geometry) {
        setGeometry(geometry);
        return this;
    }

    /**
     * Bounding box of the sampling feature, as {@code [west, south, east, north]} or with elevation. (optional)
     */
    @JsonProperty(JSON_PROPERTY_BBOX)
    public List<Double> getBbox() {
        return bbox;
    }

    @JsonProperty(JSON_PROPERTY_BBOX)
    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    public SamplingFeature bbox(List<Double> bbox) {
        setBbox(bbox);
        return this;
    }

    /**
     * SamplingFeature-specific properties. (required)
     */
    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    public SamplingFeatureProperties getProperties() {
        return properties;
    }

    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    public void setProperties(SamplingFeatureProperties properties) {
        this.properties = properties;
    }

    public SamplingFeature properties(SamplingFeatureProperties properties) {
        setProperties(properties);
        return this;
    }

    /**
     * Links related to this sampling feature. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LINKS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public SamplingFeature links(List<Link> links) {
        setLinks(links);
        return this;
    }

}
