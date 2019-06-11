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
 * Format
 */
public class Format {

    private String mimeType = null;

    private String schema = null;

    private String encoding = null;

    public Format() {

    }

    public Format(Format that) {
        if (that != null) {
            this.encoding = that.encoding;
            this.mimeType = that.mimeType;
            this.encoding = that.encoding;
        }
    }

    public Format(String mimeType, String schema, String encoding) {

        this.encoding = encoding;
        this.mimeType = mimeType;
        this.schema = schema;

    }

    public Format(org.geotoolkit.wps.xml.v200.Format format) {
        if (format != null) {
            this.encoding = format.getEncoding();
            this.mimeType = format.getMimeType();
            this.schema = format.getSchema();
        }
    }

    public Format mimeType(String mimeType) {
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

    public Format schema(String schema) {
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

    public Format encoding(String encoding) {
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
        Format format = (Format) o;
        return Objects.equals(this.mimeType, format.mimeType)
                && Objects.equals(this.schema, format.schema)
                && Objects.equals(this.encoding, format.encoding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mimeType, schema, encoding);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Format {\n");

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
