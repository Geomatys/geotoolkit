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
 * ValueType
 */
public class ValueType {

    private String data = null;

    private String href = null;

    private String mimeType = null;

    private String schema = null;

    private String encoding = null;

    public ValueType() {

    }

    public ValueType(ValueType that) {
        if (that != null) {
            this.data = that.data;
            this.encoding = that.encoding;
            this.href = that.href;
            this.mimeType = that.mimeType;
            this.schema = that.schema;
        }
    }

    public ValueType data(String data) {
        this.data = data;
        return this;
    }

    /**
     * Get data
     *
     * @return data
  *
     */
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ValueType href(String href) {
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

    public ValueType mimeType(String mimeType) {
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

    public ValueType schema(String schema) {
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

    public ValueType encoding(String encoding) {
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

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ValueType valueType = (ValueType) o;
        return Objects.equals(this.data, valueType.data)
                && Objects.equals(this.href, valueType.href)
                && Objects.equals(this.mimeType, valueType.mimeType)
                && Objects.equals(this.schema, valueType.schema)
                && Objects.equals(this.encoding, valueType.encoding);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(data, href, mimeType, schema, encoding);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ValueType {\n");

        sb.append("    data: ").append(toIndentedString(data)).append("\n");
        sb.append("    href: ").append(toIndentedString(href)).append("\n");
        sb.append("    mimeType: ").append(toIndentedString(mimeType)).append("\n");
        sb.append("    schema: ").append(toIndentedString(schema)).append("\n");
        sb.append("    encoding: ").append(toIndentedString(encoding)).append("\n");
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
