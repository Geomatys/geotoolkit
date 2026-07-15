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
package org.geotoolkit.ogcapi.dto.styles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * The list of styles currently available for a base resource.
 *
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    Styles.JSON_PROPERTY_DEFAULT,
    Styles.JSON_PROPERTY_STYLES
})
@XmlRootElement(name = "Styles")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Styles")
public final class Styles extends DataTransferObject {

    public static final String JSON_PROPERTY_DEFAULT = "default";
    @XmlElement(name = "default")
    @jakarta.annotation.Nullable
    private String _default;

    public static final String JSON_PROPERTY_STYLES = "styles";
    @XmlElement(name = "styles")
    @jakarta.annotation.Nonnull
    private List<Style> styles = new ArrayList<>();

    public Styles() {
    }

    public Styles _default(@jakarta.annotation.Nullable String _default) {
        this._default = _default;
        return this;
    }

    /**
     * The {@code id} of the default style among {@link #getStyles()}, if one is set.
     *
     * @return default
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DEFAULT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "default")
    public String getDefault() {
        return _default;
    }

    @JsonProperty(JSON_PROPERTY_DEFAULT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "default")
    public void setDefault(@jakarta.annotation.Nullable String _default) {
        this._default = _default;
    }

    public Styles styles(@jakarta.annotation.Nonnull List<Style> styles) {
        this.styles = styles;
        return this;
    }

    public Styles addStylesItem(Style stylesItem) {
        if (this.styles == null) {
            this.styles = new ArrayList<>();
        }
        this.styles.add(stylesItem);
        return this;
    }

    /**
     * One item for each style currently on the server.
     *
     * @return styles
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_STYLES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "styles")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Style> getStyles() {
        return styles;
    }

    @JsonProperty(JSON_PROPERTY_STYLES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "styles")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setStyles(@jakarta.annotation.Nonnull List<Style> styles) {
        this.styles = styles;
    }

    /**
     * Return true if this styles object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Styles other = (Styles) o;
        return Objects.equals(this._default, other._default)
                && Objects.equals(this.styles, other.styles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_default, styles);
    }

}
