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
    private EdrQueryObject position;

    public static final String JSON_PROPERTY_RADIUS = "radius";
    private EdrQueryObject radius;

    public static final String JSON_PROPERTY_AREA = "area";
    private EdrQueryObject area;

    public static final String JSON_PROPERTY_CUBE = "cube";
    private EdrQueryObject cube;

    public static final String JSON_PROPERTY_TRAJECTORY = "trajectory";
    private EdrQueryObject trajectory;

    public static final String JSON_PROPERTY_CORRIDOR = "corridor";
    private EdrQueryObject corridor;

    public static final String JSON_PROPERTY_LOCATIONS = "locations";
    private EdrQueryObject locations;

    public static final String JSON_PROPERTY_ITEMS = "items";
    private EdrQueryObject items;

    public static final String JSON_PROPERTY_INSTANCES = "instances";
    private EdrQueryObject instances;

    public DataQueries() {
    }

    public DataQueries position(EdrQueryObject position) {
        this.position = position;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_POSITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public EdrQueryObject getPosition() {
        return position;
    }

    @JsonProperty(JSON_PROPERTY_POSITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setPosition(EdrQueryObject position) {
        this.position = position;
    }

    public DataQueries radius(EdrQueryObject radius) {
        this.radius = radius;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_RADIUS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public EdrQueryObject getRadius() {
        return radius;
    }

    @JsonProperty(JSON_PROPERTY_RADIUS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRadius(EdrQueryObject radius) {
        this.radius = radius;
    }

    public DataQueries area(EdrQueryObject area) {
        this.area = area;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_AREA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public EdrQueryObject getArea() {
        return area;
    }

    @JsonProperty(JSON_PROPERTY_AREA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setArea(EdrQueryObject area) {
        this.area = area;
    }

    public DataQueries cube(EdrQueryObject cube) {
        this.cube = cube;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_CUBE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public EdrQueryObject getCube() {
        return cube;
    }

    @JsonProperty(JSON_PROPERTY_CUBE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCube(EdrQueryObject cube) {
        this.cube = cube;
    }

    public DataQueries trajectory(EdrQueryObject trajectory) {
        this.trajectory = trajectory;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_TRAJECTORY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public EdrQueryObject getTrajectory() {
        return trajectory;
    }

    @JsonProperty(JSON_PROPERTY_TRAJECTORY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTrajectory(EdrQueryObject trajectory) {
        this.trajectory = trajectory;
    }

    public DataQueries corridor(EdrQueryObject corridor) {
        this.corridor = corridor;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_CORRIDOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public EdrQueryObject getCorridor() {
        return corridor;
    }

    @JsonProperty(JSON_PROPERTY_CORRIDOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCorridor(EdrQueryObject corridor) {
        this.corridor = corridor;
    }

    public DataQueries locations(EdrQueryObject locations) {
        this.locations = locations;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_LOCATIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public EdrQueryObject getLocations() {
        return locations;
    }

    @JsonProperty(JSON_PROPERTY_LOCATIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLocations(EdrQueryObject locations) {
        this.locations = locations;
    }

    public DataQueries items(EdrQueryObject items) {
        this.items = items;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_ITEMS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public EdrQueryObject getItems() {
        return items;
    }

    @JsonProperty(JSON_PROPERTY_ITEMS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setItems(EdrQueryObject items) {
        this.items = items;
    }

    public DataQueries instances(EdrQueryObject instances) {
        this.instances = instances;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_INSTANCES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public EdrQueryObject getInstances() {
        return instances;
    }

    @JsonProperty(JSON_PROPERTY_INSTANCES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setInstances(EdrQueryObject instances) {
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
