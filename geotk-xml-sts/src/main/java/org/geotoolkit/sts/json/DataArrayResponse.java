/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.sts.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class DataArrayResponse implements STSResponse {

    @JsonProperty("value")
    private List<DataArray> value = null;

    public DataArrayResponse() {

    }

    public DataArrayResponse(List<DataArray> value) {
        this.value = value;
    }

     public DataArrayResponse addValueItem(DataArray valueItem) {

        if (this.value == null) {
            this.value = new ArrayList<>();
        }

        this.value.add(valueItem);
        return this;
    }

    /**
     * Get value
     *
     * @return value
  *
     */
    public List<DataArray> getValue() {
        return value;
    }

    public void setValue(List<DataArray> value) {
        this.value = value;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataArrayResponse da = (DataArrayResponse) o;
        return Objects.equals(this.value, da.value);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DataArrayResponse {\n");
        sb.append("    value: ").append(toIndentedString(value)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    protected String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
