/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.model.geojson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.Objects;
import static org.geotoolkit.ogcapi.model.geojson.GeoJSONGeometry.PROPERTY_COORD_REF_SYS;
import static org.geotoolkit.ogcapi.model.geojson.GeoJSONObject.PROPERTY_TYPE;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "type")
@JsonSubTypes({
  @Type(value = GeoJSONPoint.class, name = "Point"),
  @Type(value = GeoJSONLineString.class, name = "LineString"),
  @Type(value = GeoJSONPolygon.class, name = "Polygon"),
  @Type(value = GeoJSONMultiPoint.class, name = "MultiPoint"),
  @Type(value = GeoJSONMultiLineString.class, name = "MultiLineString"),
  @Type(value = GeoJSONMultiPolygon.class, name = "MultiPolygon"),
  @Type(value = GeoJSONGeometryCollection.class, name = "GeometryCollection"),
  @Type(value = JSONFGPrism.class, name = "Prism"),
  @Type(value = JSONFGMultiPrism.class, name = "MultiPrism"),
  @Type(value = JSONFGPolyhedron.class, name = "Polyhedron"),
  @Type(value = JSONFGMultiPolyhedron.class, name = "MultiPolyhedron")
})
@JsonPropertyOrder({
    PROPERTY_COORD_REF_SYS
})
public abstract class GeoJSONGeometry extends GeoJSONObject {

    //added in JSON-FG
    public static final String PROPERTY_COORD_REF_SYS = "coordRefSys";

    @XmlElement(name = PROPERTY_COORD_REF_SYS)
    @jakarta.annotation.Nullable
    private JSONFGCoordRefSys coordRefSys;

    @jakarta.annotation.Nonnull
    @JsonProperty(PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = PROPERTY_TYPE)
    @Override
    public abstract String getType();

    /**
     * Get coordRefSys
     * @return coordRefSys
     */
    @jakarta.annotation.Nullable
    @JsonProperty(PROPERTY_COORD_REF_SYS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_COORD_REF_SYS)
    public JSONFGCoordRefSys getCoordRefSys() {
        return coordRefSys;
    }


    @JsonProperty(PROPERTY_COORD_REF_SYS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_COORD_REF_SYS)
    public void setCoordRefSys(@jakarta.annotation.Nullable JSONFGCoordRefSys coordRefSys) {
        this.coordRefSys = coordRefSys;
    }

    /**
     * Return true if this GeoJSON_Feature object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoJSONGeometry other = (GeoJSONGeometry) o;
        return super.equals(o)
                && Objects.equals(this.coordRefSys, other.coordRefSys);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(coordRefSys);
    }
}
