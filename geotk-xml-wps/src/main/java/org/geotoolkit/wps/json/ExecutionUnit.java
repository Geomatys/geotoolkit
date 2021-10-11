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
 * ExecutionUnit
 */
public class ExecutionUnit {

    private String href = null;

    private String mimeType = null;

    private String schema = null;

    private String encoding = null;

    private Object unit = null;

    public ExecutionUnit() {

    }

    public ExecutionUnit(String href) {
        this.href = href;
    }

    public ExecutionUnit href(String href) {
        this.href = href;
        return this;
    }

    /**
     * Get href
     *
     * @return href
     *
     */
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public ExecutionUnit mimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    /**
     * Get mimeType
     *
     * @return mimeType
     *
     */
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public ExecutionUnit schema(String schema) {
        this.schema = schema;
        return this;
    }

    /**
     * Get schema
     *
     * @return schema
     *
     */
    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public ExecutionUnit encoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    /**
     * Get encoding
     *
     * @return encoding
     *
     */
    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public ExecutionUnit unit(Object unit) {
        this.unit = unit;
        return this;
    }

    /**
     * Get unit
     *
     * @return unit
     *
     */
    public Object getUnit() {
        return unit;
    }

    public void setUnit(Object unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExecutionUnit executionUnit = (ExecutionUnit) o;
        return Objects.equals(this.href, executionUnit.href)
                && Objects.equals(this.mimeType, executionUnit.mimeType)
                && Objects.equals(this.schema, executionUnit.schema)
                && Objects.equals(this.encoding, executionUnit.encoding)
                && Objects.equals(this.unit, executionUnit.unit);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(href, mimeType, schema, encoding, unit);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ExecutionUnit {\n");

        sb.append("    href: ").append(toIndentedString(href)).append("\n");
        sb.append("    mimeType: ").append(toIndentedString(mimeType)).append("\n");
        sb.append("    schema: ").append(toIndentedString(schema)).append("\n");
        sb.append("    encoding: ").append(toIndentedString(encoding)).append("\n");
        sb.append("    unit: ").append(toIndentedString(unit)).append("\n");
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
