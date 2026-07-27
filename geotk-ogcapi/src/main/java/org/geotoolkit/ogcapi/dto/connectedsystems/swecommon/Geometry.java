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
package org.geotoolkit.ogcapi.dto.connectedsystems.swecommon;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONGeometry;

/**
 * A geometry SWE Common data component, wrapping a GeoJSON geometry.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class Geometry extends AbstractDataComponent {

    public static final String JSON_PROPERTY_CONSTRAINT = "constraint";
    private GeometryConstraint constraint;

    public static final String JSON_PROPERTY_NIL_VALUES = "nilValues";
    private List<NilValue> nilValues;

    public static final String JSON_PROPERTY_SRS = "srs";
    private String srs;

    public static final String JSON_PROPERTY_VALUE = "value";
    private GeoJSONGeometry value;

    public Geometry() {
        setType("Geometry");
    }

    @JsonProperty(JSON_PROPERTY_CONSTRAINT)
    public GeometryConstraint getConstraint() {
        return constraint;
    }

    @JsonProperty(JSON_PROPERTY_CONSTRAINT)
    public void setConstraint(GeometryConstraint constraint) {
        this.constraint = constraint;
    }

    @JsonProperty(JSON_PROPERTY_NIL_VALUES)
    public List<NilValue> getNilValues() {
        return nilValues;
    }

    @JsonProperty(JSON_PROPERTY_NIL_VALUES)
    public void setNilValues(List<NilValue> nilValues) {
        this.nilValues = nilValues;
    }

    /**
     * URI of the spatial reference system the geometry is expressed in. (required)
     */
    @JsonProperty(JSON_PROPERTY_SRS)
    public String getSrs() {
        return srs;
    }

    @JsonProperty(JSON_PROPERTY_SRS)
    public void setSrs(String srs) {
        this.srs = srs;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public GeoJSONGeometry getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public void setValue(GeoJSONGeometry value) {
        this.value = value;
    }

    /**
     * Constrains which GeoJSON geometry types are allowed.
     */
    public static class GeometryConstraint extends DataTransferObject {

        public static final String JSON_PROPERTY_GEOM_TYPES = "geomTypes";
        private List<String> geomTypes;

        @JsonProperty(JSON_PROPERTY_GEOM_TYPES)
        public List<String> getGeomTypes() {
            return geomTypes;
        }

        @JsonProperty(JSON_PROPERTY_GEOM_TYPES)
        public void setGeomTypes(List<String> geomTypes) {
            this.geomTypes = geomTypes;
        }
    }

}
