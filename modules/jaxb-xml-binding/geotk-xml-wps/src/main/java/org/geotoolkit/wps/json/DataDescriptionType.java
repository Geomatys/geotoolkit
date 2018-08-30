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
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.wps.xml.v200.Format;
import org.geotoolkit.wps.xml.v200.InputDescription;
import org.geotoolkit.wps.xml.v200.OutputDescription;

/**
 * DataDescription
 */
public class DataDescriptionType extends DescriptionType {

    private String minOccurs = null;

    private String maxOccurs = null;

    private List<FormatDescription> formats;

    public DataDescriptionType() {

    }

    public DataDescriptionType(String id, String title, String _abstract, List<String> keywords,
            List<Metadata> metadata, List<AdditionalParameters> additionalParameters,
            List<FormatDescription> formats) {
        super(id, title, _abstract, keywords, metadata, additionalParameters);
        this.formats = formats;
    }

    public DataDescriptionType(InputDescription desc) {
        super(desc);
        if (desc != null && desc.getDataDescription() != null) {
            this.formats = new ArrayList<>();
            for (Format format : desc.getDataDescription().getFormat()) {
                this.formats.add(new FormatDescription(format));
            }
        }
    }

    public DataDescriptionType(OutputDescription desc) {
        super(desc);
        if (desc != null && desc.getDataDescription() != null) {
            this.formats = new ArrayList<>();
            for (Format format : desc.getDataDescription().getFormat()) {
                this.formats.add(new FormatDescription(format));
            }
        }
    }

    public DataDescriptionType(DataDescriptionType that) {
        super(that);
        if (that != null) {
            this.maxOccurs = that.maxOccurs;
            this.minOccurs = that.minOccurs;
            if (that.formats != null && !that.formats.isEmpty()) {
                this.formats = new ArrayList<>();
                for (FormatDescription f : that.formats) {
                    this.formats.add(new FormatDescription(f));
                }
            }
        }
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

    public String getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(String minOccurs) {
        this.minOccurs = minOccurs;
    }

    public String getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(String maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataDescriptionType dataDescriptionType = (DataDescriptionType) o;
        return Objects.equals(this.minOccurs, dataDescriptionType.minOccurs)
                && Objects.equals(this.maxOccurs, dataDescriptionType.maxOccurs)
                && Objects.equals(this.formats, dataDescriptionType.formats)
                && super.equals(o);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(minOccurs, maxOccurs, formats, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DataDescriptionType {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    minOccurs: ").append(toIndentedString(minOccurs)).append("\n");
        sb.append("    maxOccurs: ").append(toIndentedString(maxOccurs)).append("\n");
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
