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
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.TimePeriod;

/**
 * The {@code properties} sub-object of a {@link System} GeoJSON feature.
 *
 * <p>{@code featureType} is restricted to one of: {@code http://www.w3.org/ns/sosa/Sensor},
 * {@code http://www.w3.org/ns/sosa/Actuator}, {@code http://www.w3.org/ns/sosa/Platform},
 * {@code http://www.w3.org/ns/sosa/Sampler}, {@code http://www.w3.org/ns/sosa/System}, or the
 * equivalent CURIE forms {@code sosa:Sensor}, {@code sosa:Actuator}, {@code sosa:Platform},
 * {@code sosa:Sampler}, {@code sosa:System}.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 */
public class SystemProperties extends FeatureProperties {

    public static final String JSON_PROPERTY_ASSET_TYPE = "assetType";
    private String assetType;

    public static final String JSON_PROPERTY_VALID_TIME = "validTime";
    private TimePeriod validTime;

    public static final String JSON_PROPERTY_SYSTEM_KIND_LINK = "systemKind@link";
    private Link systemKindLink;

    /**
     * Type of asset represented by the system. (optional) One of: {@code Equipment}, {@code
     * Human}, {@code LivingThing}, {@code Simulation}, {@code Process}, {@code Group}, {@code
     * Other}.
     */
    @JsonProperty(JSON_PROPERTY_ASSET_TYPE)
    public String getAssetType() {
        return assetType;
    }

    @JsonProperty(JSON_PROPERTY_ASSET_TYPE)
    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public SystemProperties assetType(String assetType) {
        setAssetType(assetType);
        return this;
    }

    /**
     * Time period during which the system exists/is valid. (optional)
     */
    @JsonProperty(JSON_PROPERTY_VALID_TIME)
    public TimePeriod getValidTime() {
        return validTime;
    }

    @JsonProperty(JSON_PROPERTY_VALID_TIME)
    public void setValidTime(TimePeriod validTime) {
        this.validTime = validTime;
    }

    public SystemProperties validTime(TimePeriod validTime) {
        setValidTime(validTime);
        return this;
    }

    /**
     * Link to the kind/type description (e.g. a SensorML datasheet) of this system. (optional)
     */
    @JsonProperty(JSON_PROPERTY_SYSTEM_KIND_LINK)
    public Link getSystemKindLink() {
        return systemKindLink;
    }

    @JsonProperty(JSON_PROPERTY_SYSTEM_KIND_LINK)
    public void setSystemKindLink(Link systemKindLink) {
        this.systemKindLink = systemKindLink;
    }

    public SystemProperties systemKindLink(Link systemKindLink) {
        setSystemKindLink(systemKindLink);
        return this;
    }

}
