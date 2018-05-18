/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * FormatDescription
 */
public class FormatDescription extends Format {

    private Integer maximumMegabytes = null;

    @JsonProperty("default")
    private Boolean _default = false;

    public FormatDescription() {
        
    }
            
    public FormatDescription(org.geotoolkit.wps.xml.Format format) {
        super(format);
        if (format != null) {
            this._default = format.isDefault();
            this.maximumMegabytes = format.getMaximumMegabytes();
        }
    }

    public FormatDescription maximumMegabytes(Integer maximumMegabytes) {
        this.maximumMegabytes = maximumMegabytes;
        return this;
    }

    /**
     * Get maximumMegabytes
     *
     * @return maximumMegabytes
  *
     */
    public Integer getMaximumMegabytes() {
        return maximumMegabytes;
    }

    public void setMaximumMegabytes(Integer maximumMegabytes) {
        this.maximumMegabytes = maximumMegabytes;
    }

    public FormatDescription _default(Boolean _default) {
        this._default = _default;
        return this;
    }

    /**
     * Get _default
     *
     * @return _default
  *
     */
    public Boolean isDefault() {
        return _default;
    }

    public void setDefault(Boolean _default) {
        this._default = _default;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FormatDescription formatDescription = (FormatDescription) o;
        return Objects.equals(this.maximumMegabytes, formatDescription.maximumMegabytes)
                && Objects.equals(this._default, formatDescription._default)
                && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maximumMegabytes, _default, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class FormatDescription {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    maximumMegabytes: ").append(toIndentedString(maximumMegabytes)).append("\n");
        sb.append("    _default: ").append(toIndentedString(_default)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
