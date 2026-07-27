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
 * A Procedure resource, encoded as a GeoJSON Feature.
 *
 * <p>Describes a method, algorithm or workflow used to produce or process observations, e.g. a
 * sensor datasheet or an observing/sampling/actuating procedure. Procedures are non-geospatial:
 * {@link #getGeometry()} is always {@code null}.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 */
public class Procedure extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_GEOMETRY = "geometry";
    private GeoJSONGeometry geometry;

    public static final String JSON_PROPERTY_BBOX = "bbox";
    private List<Double> bbox;

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    private ProcedureProperties properties;

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

    public Procedure id(String id) {
        setId(id);
        return this;
    }

    /**
     * Always {@code null}: procedures are non-geospatial. (field kept present for consistency
     * with the other GeoJSON feature resources)
     */
    @JsonProperty(JSON_PROPERTY_GEOMETRY)
    public GeoJSONGeometry getGeometry() {
        return geometry;
    }

    @JsonProperty(JSON_PROPERTY_GEOMETRY)
    public void setGeometry(GeoJSONGeometry geometry) {
        this.geometry = geometry;
    }

    public Procedure geometry(GeoJSONGeometry geometry) {
        setGeometry(geometry);
        return this;
    }

    /**
     * Bounding box, always absent since procedures are non-geospatial. (optional)
     */
    @JsonProperty(JSON_PROPERTY_BBOX)
    public List<Double> getBbox() {
        return bbox;
    }

    @JsonProperty(JSON_PROPERTY_BBOX)
    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    public Procedure bbox(List<Double> bbox) {
        setBbox(bbox);
        return this;
    }

    /**
     * Procedure-specific properties. (required)
     */
    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    public ProcedureProperties getProperties() {
        return properties;
    }

    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    public void setProperties(ProcedureProperties properties) {
        this.properties = properties;
    }

    public Procedure properties(ProcedureProperties properties) {
        setProperties(properties);
        return this;
    }

    /**
     * Links related to this procedure. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LINKS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Procedure links(List<Link> links) {
        setLinks(links);
        return this;
    }

}
