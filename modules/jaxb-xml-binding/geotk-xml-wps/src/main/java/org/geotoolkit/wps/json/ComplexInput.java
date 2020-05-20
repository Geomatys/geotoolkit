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

import java.util.Objects;

public class ComplexInput extends InputBase {

    private Format format;

    private ValueType value;

    public ComplexInput() {

    }

    public ComplexInput(Format format, ValueType value) {
        this.format = format;
        this.value = value;
    }

    public ComplexInput(ComplexInput that) {
        if (that != null) {
            this.format = new Format(that.format);
            this.value = new ValueType(that.value);
        }
    }

    /**
     * @return the format
     */
    public Format getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(Format format) {
        this.format = format;
    }

    /**
     * @return the value
     */
    public ValueType getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(ValueType value) {
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
        ComplexInput ci = (ComplexInput) o;
        return Objects.equals(this.format, ci.format) &&
               Objects.equals(this.value, ci.value);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ComplexInput {\n");
        sb.append("    format: ").append(toIndentedString(format)).append("\n");
        sb.append("    value: ").append(toIndentedString(value)).append("\n");
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
