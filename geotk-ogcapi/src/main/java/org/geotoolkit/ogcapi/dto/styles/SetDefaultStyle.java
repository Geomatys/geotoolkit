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
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Body of a {@code PATCH .../styles} request updating the default style, encoded as
 * {@code application/merge-patch+json} (RFC 7396). A {@code null} value clears the default style; a
 * style identifier sets it. Unlike {@link Styles#getDefault()}, {@link #getDefault()} is always
 * serialized, even when {@code null}, since RFC 7396 merge-patch semantics require an explicit
 * {@code "default": null} to clear the member (an omitted property would mean "leave unchanged").
 *
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    SetDefaultStyle.JSON_PROPERTY_DEFAULT
})
public final class SetDefaultStyle extends DataTransferObject {

    public static final String JSON_PROPERTY_DEFAULT = "default";
    private String _default;

    public SetDefaultStyle() {
    }

    public SetDefaultStyle _default(String _default) {
        this._default = _default;
        return this;
    }

    /**
     * The {@code id} of the style to set as the default, or {@code null} to clear the default style.
     *
     * @return default
     */
    @JsonProperty(JSON_PROPERTY_DEFAULT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getDefault() {
        return _default;
    }

    @JsonProperty(JSON_PROPERTY_DEFAULT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setDefault(String _default) {
        this._default = _default;
    }

    /**
     * Return true if this defaultStyleUpdate object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SetDefaultStyle other = (SetDefaultStyle) o;
        return Objects.equals(this._default, other._default);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_default);
    }

}
