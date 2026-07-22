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
package org.geotoolkit.ogcapi.dto.edr;

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
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Advertises which EDR query types ({@code data_queries} in the {@code collection}
 * schema) a collection supports. A server only needs to implement one of these.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    DataQueries.JSON_PROPERTY_POSITION,
    DataQueries.JSON_PROPERTY_RADIUS,
    DataQueries.JSON_PROPERTY_AREA,
    DataQueries.JSON_PROPERTY_CUBE,
    DataQueries.JSON_PROPERTY_TRAJECTORY,
    DataQueries.JSON_PROPERTY_CORRIDOR,
    DataQueries.JSON_PROPERTY_LOCATIONS,
    DataQueries.JSON_PROPERTY_ITEMS,
    DataQueries.JSON_PROPERTY_INSTANCES
})
@XmlRootElement(name = "DataQueries")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DataQueries")
public final class DataQueries extends DataTransferObject {

    public static final String JSON_PROPERTY_POSITION = "position";
    @XmlElement(name = "position")
    @jakarta.annotation.Nullable
    private DataQueryLink position;

    public static final String JSON_PROPERTY_RADIUS = "radius";
    @XmlElement(name = "radius")
    @jakarta.annotation.Nullable
    private DataQueryLink radius;

    public static final String JSON_PROPERTY_AREA = "area";
    @XmlElement(name = "area")
    @jakarta.annotation.Nullable
    private DataQueryLink area;

    public static final String JSON_PROPERTY_CUBE = "cube";
    @XmlElement(name = "cube")
    @jakarta.annotation.Nullable
    private DataQueryLink cube;

    public static final String JSON_PROPERTY_TRAJECTORY = "trajectory";
    @XmlElement(name = "trajectory")
    @jakarta.annotation.Nullable
    private DataQueryLink trajectory;

    public static final String JSON_PROPERTY_CORRIDOR = "corridor";
    @XmlElement(name = "corridor")
    @jakarta.annotation.Nullable
    private DataQueryLink corridor;

    public static final String JSON_PROPERTY_LOCATIONS = "locations";
    @XmlElement(name = "locations")
    @jakarta.annotation.Nullable
    private DataQueryLink locations;

    public static final String JSON_PROPERTY_ITEMS = "items";
    @XmlElement(name = "items")
    @jakarta.annotation.Nullable
    private DataQueryLink items;

    public static final String JSON_PROPERTY_INSTANCES = "instances";
    @XmlElement(name = "instances")
    @jakarta.annotation.Nullable
    private DataQueryLink instances;

    public DataQueries() {
    }

    public DataQueries position(@jakarta.annotation.Nullable DataQueryLink position) {
        this.position = position;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_POSITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "position")
    public DataQueryLink getPosition() {
        return position;
    }

    @JsonProperty(JSON_PROPERTY_POSITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "position")
    public void setPosition(@jakarta.annotation.Nullable DataQueryLink position) {
        this.position = position;
    }

    public DataQueries radius(@jakarta.annotation.Nullable DataQueryLink radius) {
        this.radius = radius;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RADIUS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "radius")
    public DataQueryLink getRadius() {
        return radius;
    }

    @JsonProperty(JSON_PROPERTY_RADIUS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "radius")
    public void setRadius(@jakarta.annotation.Nullable DataQueryLink radius) {
        this.radius = radius;
    }

    public DataQueries area(@jakarta.annotation.Nullable DataQueryLink area) {
        this.area = area;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_AREA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "area")
    public DataQueryLink getArea() {
        return area;
    }

    @JsonProperty(JSON_PROPERTY_AREA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "area")
    public void setArea(@jakarta.annotation.Nullable DataQueryLink area) {
        this.area = area;
    }

    public DataQueries cube(@jakarta.annotation.Nullable DataQueryLink cube) {
        this.cube = cube;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CUBE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "cube")
    public DataQueryLink getCube() {
        return cube;
    }

    @JsonProperty(JSON_PROPERTY_CUBE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "cube")
    public void setCube(@jakarta.annotation.Nullable DataQueryLink cube) {
        this.cube = cube;
    }

    public DataQueries trajectory(@jakarta.annotation.Nullable DataQueryLink trajectory) {
        this.trajectory = trajectory;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TRAJECTORY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "trajectory")
    public DataQueryLink getTrajectory() {
        return trajectory;
    }

    @JsonProperty(JSON_PROPERTY_TRAJECTORY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "trajectory")
    public void setTrajectory(@jakarta.annotation.Nullable DataQueryLink trajectory) {
        this.trajectory = trajectory;
    }

    public DataQueries corridor(@jakarta.annotation.Nullable DataQueryLink corridor) {
        this.corridor = corridor;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CORRIDOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "corridor")
    public DataQueryLink getCorridor() {
        return corridor;
    }

    @JsonProperty(JSON_PROPERTY_CORRIDOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "corridor")
    public void setCorridor(@jakarta.annotation.Nullable DataQueryLink corridor) {
        this.corridor = corridor;
    }

    public DataQueries locations(@jakarta.annotation.Nullable DataQueryLink locations) {
        this.locations = locations;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LOCATIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "locations")
    public DataQueryLink getLocations() {
        return locations;
    }

    @JsonProperty(JSON_PROPERTY_LOCATIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "locations")
    public void setLocations(@jakarta.annotation.Nullable DataQueryLink locations) {
        this.locations = locations;
    }

    public DataQueries items(@jakarta.annotation.Nullable DataQueryLink items) {
        this.items = items;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ITEMS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "items")
    public DataQueryLink getItems() {
        return items;
    }

    @JsonProperty(JSON_PROPERTY_ITEMS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "items")
    public void setItems(@jakarta.annotation.Nullable DataQueryLink items) {
        this.items = items;
    }

    public DataQueries instances(@jakarta.annotation.Nullable DataQueryLink instances) {
        this.instances = instances;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_INSTANCES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "instances")
    public DataQueryLink getInstances() {
        return instances;
    }

    @JsonProperty(JSON_PROPERTY_INSTANCES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "instances")
    public void setInstances(@jakarta.annotation.Nullable DataQueryLink instances) {
        this.instances = instances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataQueries other = (DataQueries) o;
        return Objects.equals(this.position, other.position)
                && Objects.equals(this.radius, other.radius)
                && Objects.equals(this.area, other.area)
                && Objects.equals(this.cube, other.cube)
                && Objects.equals(this.trajectory, other.trajectory)
                && Objects.equals(this.corridor, other.corridor)
                && Objects.equals(this.locations, other.locations)
                && Objects.equals(this.items, other.items)
                && Objects.equals(this.instances, other.instances);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, radius, area, cube, trajectory, corridor, locations, items, instances);
    }

}
