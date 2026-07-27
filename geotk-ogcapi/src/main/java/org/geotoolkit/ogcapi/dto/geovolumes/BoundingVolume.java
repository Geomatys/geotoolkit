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
package org.geotoolkit.ogcapi.dto.geovolumes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * A closed volume that completely contains the union of a set of geometric objects, as borrowed from
 * the 3D Tiles specification. Exactly one of {@code box}, {@code region} or {@code sphere} is expected
 * to be set.
 *
 * @see <a href="https://docs.ogc.org/DRAFTS/22-029.html#_15de6eb9_73f4_40c2_8b6d_60ec1b37030d">OGC API - 3D GeoVolumes, Annex C.2</a>
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    BoundingVolume.JSON_PROPERTY_BOX,
    BoundingVolume.JSON_PROPERTY_REGION,
    BoundingVolume.JSON_PROPERTY_SPHERE
})
public final class BoundingVolume extends DataTransferObject {

    public static final String JSON_PROPERTY_BOX = "box";
    private List<Double> box = new ArrayList<>();

    public static final String JSON_PROPERTY_REGION = "region";
    private List<Double> region = new ArrayList<>();

    public static final String JSON_PROPERTY_SPHERE = "sphere";
    private List<Double> sphere = new ArrayList<>();

    public BoundingVolume() {
    }

    public BoundingVolume box(List<Double> box) {
        this.box = box;
        return this;
    }

    public BoundingVolume addBoxItem(Double boxItem) {
        if (this.box == null) {
            this.box = new ArrayList<>();
        }
        this.box.add(boxItem);
        return this;
    }

    /**
     * An array of 12 numbers that define an oriented bounding box. The first three elements define the
     * x, y, and z values for the center of the box. The next three elements (indices 3, 4 and 5) define
     * the x-axis direction and half-length. The next three elements (indices 6, 7 and 8) define the
     * y-axis direction and half-length. The last three elements (indices 9, 10 and 11) define the z-axis
     * direction and half-length.
     *
     * @return box
     */
    @JsonProperty(JSON_PROPERTY_BOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Double> getBox() {
        return box;
    }

    @JsonProperty(JSON_PROPERTY_BOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setBox(List<Double> box) {
        this.box = box;
    }

    public BoundingVolume region(List<Double> region) {
        this.region = region;
        return this;
    }

    public BoundingVolume addRegionItem(Double regionItem) {
        if (this.region == null) {
            this.region = new ArrayList<>();
        }
        this.region.add(regionItem);
        return this;
    }

    /**
     * An array of six numbers that define a bounding geographic region in EPSG:4979 coordinates with
     * the order [west, south, east, north, minimum height, maximum height]. Longitudes and latitudes are
     * in radians, and heights are in meters above (or below) the WGS84 ellipsoid.
     *
     * @return region
     */
    @JsonProperty(JSON_PROPERTY_REGION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Double> getRegion() {
        return region;
    }

    @JsonProperty(JSON_PROPERTY_REGION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRegion(List<Double> region) {
        this.region = region;
    }

    public BoundingVolume sphere(List<Double> sphere) {
        this.sphere = sphere;
        return this;
    }

    public BoundingVolume addSphereItem(Double sphereItem) {
        if (this.sphere == null) {
            this.sphere = new ArrayList<>();
        }
        this.sphere.add(sphereItem);
        return this;
    }

    /**
     * An array of four numbers that define a bounding sphere. The first three elements define the x, y,
     * and z values for the center of the sphere. The last element (index 3) defines the radius in
     * meters.
     *
     * @return sphere
     */
    @JsonProperty(JSON_PROPERTY_SPHERE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Double> getSphere() {
        return sphere;
    }

    @JsonProperty(JSON_PROPERTY_SPHERE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setSphere(List<Double> sphere) {
        this.sphere = sphere;
    }

    /**
     * Return true if this boundingVolume object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoundingVolume other = (BoundingVolume) o;
        return Objects.equals(this.box, other.box)
                && Objects.equals(this.region, other.region)
                && Objects.equals(this.sphere, other.sphere);
    }

    @Override
    public int hashCode() {
        return Objects.hash(box, region, sphere);
    }

}
