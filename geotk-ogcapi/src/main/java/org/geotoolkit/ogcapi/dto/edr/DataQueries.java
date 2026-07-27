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
public final class DataQueries extends DataTransferObject {

    public static final String JSON_PROPERTY_POSITION = "position";
    private DataQueryLink position;

    public static final String JSON_PROPERTY_RADIUS = "radius";
    private DataQueryLink radius;

    public static final String JSON_PROPERTY_AREA = "area";
    private DataQueryLink area;

    public static final String JSON_PROPERTY_CUBE = "cube";
    private DataQueryLink cube;

    public static final String JSON_PROPERTY_TRAJECTORY = "trajectory";
    private DataQueryLink trajectory;

    public static final String JSON_PROPERTY_CORRIDOR = "corridor";
    private DataQueryLink corridor;

    public static final String JSON_PROPERTY_LOCATIONS = "locations";
    private DataQueryLink locations;

    public static final String JSON_PROPERTY_ITEMS = "items";
    private DataQueryLink items;

    public static final String JSON_PROPERTY_INSTANCES = "instances";
    private DataQueryLink instances;

    public DataQueries() {
    }

    public DataQueries position(DataQueryLink position) {
        this.position = position;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_POSITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DataQueryLink getPosition() {
        return position;
    }

    @JsonProperty(JSON_PROPERTY_POSITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setPosition(DataQueryLink position) {
        this.position = position;
    }

    public DataQueries radius(DataQueryLink radius) {
        this.radius = radius;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_RADIUS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DataQueryLink getRadius() {
        return radius;
    }

    @JsonProperty(JSON_PROPERTY_RADIUS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRadius(DataQueryLink radius) {
        this.radius = radius;
    }

    public DataQueries area(DataQueryLink area) {
        this.area = area;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_AREA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DataQueryLink getArea() {
        return area;
    }

    @JsonProperty(JSON_PROPERTY_AREA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setArea(DataQueryLink area) {
        this.area = area;
    }

    public DataQueries cube(DataQueryLink cube) {
        this.cube = cube;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_CUBE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DataQueryLink getCube() {
        return cube;
    }

    @JsonProperty(JSON_PROPERTY_CUBE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCube(DataQueryLink cube) {
        this.cube = cube;
    }

    public DataQueries trajectory(DataQueryLink trajectory) {
        this.trajectory = trajectory;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_TRAJECTORY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DataQueryLink getTrajectory() {
        return trajectory;
    }

    @JsonProperty(JSON_PROPERTY_TRAJECTORY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTrajectory(DataQueryLink trajectory) {
        this.trajectory = trajectory;
    }

    public DataQueries corridor(DataQueryLink corridor) {
        this.corridor = corridor;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_CORRIDOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DataQueryLink getCorridor() {
        return corridor;
    }

    @JsonProperty(JSON_PROPERTY_CORRIDOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCorridor(DataQueryLink corridor) {
        this.corridor = corridor;
    }

    public DataQueries locations(DataQueryLink locations) {
        this.locations = locations;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_LOCATIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DataQueryLink getLocations() {
        return locations;
    }

    @JsonProperty(JSON_PROPERTY_LOCATIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLocations(DataQueryLink locations) {
        this.locations = locations;
    }

    public DataQueries items(DataQueryLink items) {
        this.items = items;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_ITEMS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DataQueryLink getItems() {
        return items;
    }

    @JsonProperty(JSON_PROPERTY_ITEMS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setItems(DataQueryLink items) {
        this.items = items;
    }

    public DataQueries instances(DataQueryLink instances) {
        this.instances = instances;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_INSTANCES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DataQueryLink getInstances() {
        return instances;
    }

    @JsonProperty(JSON_PROPERTY_INSTANCES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setInstances(DataQueryLink instances) {
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
