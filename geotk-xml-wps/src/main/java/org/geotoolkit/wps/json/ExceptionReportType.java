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

/**
 * ExceptionReportType
 */
public class ExceptionReportType implements WPSJSONResponse {

    private String code = null;

    private String description = null;

    public ExceptionReportType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public ExceptionReportType() {

    }
    public ExceptionReportType code(String code) {
        this.code = code;
        return this;
    }

    /**
     * Get code
     *
     * @return code
  *
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ExceptionReportType description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
  *
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExceptionReportType exceptionReportType = (ExceptionReportType) o;
        return Objects.equals(this.code, exceptionReportType.code)
                && Objects.equals(this.description, exceptionReportType.description);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(code, description);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ExceptionReportType {\n");

        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
