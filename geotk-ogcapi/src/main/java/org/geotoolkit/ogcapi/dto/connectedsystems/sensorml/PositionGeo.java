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
package org.geotoolkit.ogcapi.dto.connectedsystems.sensorml;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Geodetic (lat/lon/height) position, one of the two possible shapes of {@link Pose#getPosition()}
 * (used when {@link Pose#getType()} is {@code "GeoPose"}).
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class PositionGeo extends DataTransferObject {

    public static final String JSON_PROPERTY_LAT = "lat";
    private Double lat;

    public static final String JSON_PROPERTY_LON = "lon";
    private Double lon;

    public static final String JSON_PROPERTY_H = "h";
    private Double h;

    /**
     * Latitude in degrees. (required)
     */
    @JsonProperty(JSON_PROPERTY_LAT)
    public Double getLat() {
        return lat;
    }

    @JsonProperty(JSON_PROPERTY_LAT)
    public void setLat(Double lat) {
        this.lat = lat;
    }

    public PositionGeo lat(Double lat) {
        setLat(lat);
        return this;
    }

    /**
     * Longitude in degrees. (required)
     */
    @JsonProperty(JSON_PROPERTY_LON)
    public Double getLon() {
        return lon;
    }

    @JsonProperty(JSON_PROPERTY_LON)
    public void setLon(Double lon) {
        this.lon = lon;
    }

    public PositionGeo lon(Double lon) {
        setLon(lon);
        return this;
    }

    /**
     * Height/altitude. (required)
     */
    @JsonProperty(JSON_PROPERTY_H)
    public Double getH() {
        return h;
    }

    @JsonProperty(JSON_PROPERTY_H)
    public void setH(Double h) {
        this.h = h;
    }

    public PositionGeo h(Double h) {
        setH(h);
        return this;
    }

}
