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

/**
 * A GeoJSON FeatureCollection of {@link DeployedSystem} resources.
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 */
public class DeployedSystemCollection extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type = "FeatureCollection";

    public static final String JSON_PROPERTY_FEATURES = "features";
    private List<DeployedSystem> features;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links;

    /**
     * Discriminator, always {@code "FeatureCollection"}. (required, default {@code "FeatureCollection"})
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    public void setType(String type) {
        this.type = type;
    }

    public DeployedSystemCollection type(String type) {
        setType(type);
        return this;
    }

    /**
     * The deployed systems contained in this collection page. (required)
     */
    @JsonProperty(JSON_PROPERTY_FEATURES)
    public List<DeployedSystem> getFeatures() {
        return features;
    }

    @JsonProperty(JSON_PROPERTY_FEATURES)
    public void setFeatures(List<DeployedSystem> features) {
        this.features = features;
    }

    public DeployedSystemCollection features(List<DeployedSystem> features) {
        setFeatures(features);
        return this;
    }

    /**
     * Links related to this collection page (e.g. {@code self}, {@code next}). (optional)
     */
    @JsonProperty(JSON_PROPERTY_LINKS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public DeployedSystemCollection links(List<Link> links) {
        setLinks(links);
        return this;
    }

}
