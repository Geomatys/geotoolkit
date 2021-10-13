/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
import java.util.List;
import java.util.Objects;
import org.geotoolkit.wps.xml.v200.ComplexData;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ComplexInputDescription extends InputDescriptionBase {

    private List<FormatDescription> formats = new ArrayList<>();

    public ComplexInputDescription() {

    }

    public ComplexInputDescription(List<FormatDescription> formats) {
        this.formats = formats;
    }

    public ComplexInputDescription(ComplexInputDescription that) {
        if (that != null && that.formats != null) {
            this.formats = new ArrayList<>();
            for (FormatDescription f : that.formats) {
                this.formats.add(new FormatDescription(f));
            }
        }

    }

    public ComplexInputDescription(ComplexData dataDesc) {
        if (dataDesc!= null && dataDesc.getFormat() != null && !dataDesc.getFormat().isEmpty()) {
            this.formats = new ArrayList<>();
            for (org.geotoolkit.wps.xml.v200.Format f : dataDesc.getFormat()) {
                this.formats.add(new FormatDescription(f));
            }
        }
    }

    public ComplexInputDescription formats(List<FormatDescription> formats) {
        this.formats = formats;
        return this;
    }

    public ComplexInputDescription addFormatsItem(FormatDescription formatsItem) {

        this.formats.add(formatsItem);
        return this;
    }

    /**
     * Get formats
     *
     * @return formats
     *
     */
    public List<FormatDescription> getFormats() {
        return formats;
    }

    public void setFormats(List<FormatDescription> formats) {
        this.formats = formats;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ComplexInputDescription that = (ComplexInputDescription) o;
        return Objects.equals(this.formats, that.formats);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(formats);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ComplexInputDescription {\n");
        sb.append("    formats: ").append(toIndentedString(formats)).append("\n");
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
