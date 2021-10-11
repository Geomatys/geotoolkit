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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class AdditionalParameter {

    private String name = null;

    private List<String> values = null;

    public AdditionalParameter() {

    }

    public AdditionalParameter(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }

    public AdditionalParameter(String name, String value) {
        this.name = name;
        if (value != null) {
            this.values = Arrays.asList(value);
        }
    }

    public AdditionalParameter(AdditionalParameter that) {
        if (that != null) {
            this.name = that.name;
            if (that.values != null && !that.values.isEmpty()) {
                this.values = new ArrayList<>(that.values);
            }
        }
    }

    /**
     * Get name
     *
     * @return name
  *
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get value
     *
     * @return value
  *
     */
    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdditionalParameter additionalParameter = (AdditionalParameter) o;
        return Objects.equals(this.name, additionalParameter.name)
                && Objects.equals(this.values, additionalParameter.values);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, values);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AdditionalParameter {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    value: ").append(toIndentedString(values)).append("\n");
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
