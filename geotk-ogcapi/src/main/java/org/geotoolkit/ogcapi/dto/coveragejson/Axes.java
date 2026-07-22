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
package org.geotoolkit.ogcapi.dto.coveragejson;

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
 * The fixed set of named axes of a {@link Domain}: {@code x}, {@code y}, {@code z}, {@code t}
 * and {@code composite}, per the CoverageJSON {@code axes} object.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    Axes.JSON_PROPERTY_X,
    Axes.JSON_PROPERTY_Y,
    Axes.JSON_PROPERTY_Z,
    Axes.JSON_PROPERTY_T,
    Axes.JSON_PROPERTY_COMPOSITE
})
@XmlRootElement(name = "Axes")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Axes")
public final class Axes extends DataTransferObject {

    public static final String JSON_PROPERTY_X = "x";
    @XmlElement(name = "x")
    @jakarta.annotation.Nullable
    private Axis x;

    public static final String JSON_PROPERTY_Y = "y";
    @XmlElement(name = "y")
    @jakarta.annotation.Nullable
    private Axis y;

    public static final String JSON_PROPERTY_Z = "z";
    @XmlElement(name = "z")
    @jakarta.annotation.Nullable
    private Axis z;

    public static final String JSON_PROPERTY_T = "t";
    @XmlElement(name = "t")
    @jakarta.annotation.Nullable
    private Axis t;

    public static final String JSON_PROPERTY_COMPOSITE = "composite";
    @XmlElement(name = "composite")
    @jakarta.annotation.Nullable
    private Axis composite;

    public Axes() {
    }

    public Axes x(@jakarta.annotation.Nullable Axis x) {
        this.x = x;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_X)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "x")
    public Axis getX() {
        return x;
    }

    @JsonProperty(JSON_PROPERTY_X)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "x")
    public void setX(@jakarta.annotation.Nullable Axis x) {
        this.x = x;
    }

    public Axes y(@jakarta.annotation.Nullable Axis y) {
        this.y = y;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_Y)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "y")
    public Axis getY() {
        return y;
    }

    @JsonProperty(JSON_PROPERTY_Y)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "y")
    public void setY(@jakarta.annotation.Nullable Axis y) {
        this.y = y;
    }

    public Axes z(@jakarta.annotation.Nullable Axis z) {
        this.z = z;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_Z)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "z")
    public Axis getZ() {
        return z;
    }

    @JsonProperty(JSON_PROPERTY_Z)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "z")
    public void setZ(@jakarta.annotation.Nullable Axis z) {
        this.z = z;
    }

    public Axes t(@jakarta.annotation.Nullable Axis t) {
        this.t = t;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_T)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "t")
    public Axis getT() {
        return t;
    }

    @JsonProperty(JSON_PROPERTY_T)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "t")
    public void setT(@jakarta.annotation.Nullable Axis t) {
        this.t = t;
    }

    public Axes composite(@jakarta.annotation.Nullable Axis composite) {
        this.composite = composite;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_COMPOSITE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "composite")
    public Axis getComposite() {
        return composite;
    }

    @JsonProperty(JSON_PROPERTY_COMPOSITE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "composite")
    public void setComposite(@jakarta.annotation.Nullable Axis composite) {
        this.composite = composite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Axes other = (Axes) o;
        return Objects.equals(this.x, other.x)
                && Objects.equals(this.y, other.y)
                && Objects.equals(this.z, other.z)
                && Objects.equals(this.t, other.t)
                && Objects.equals(this.composite, other.composite);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, t, composite);
    }

}
