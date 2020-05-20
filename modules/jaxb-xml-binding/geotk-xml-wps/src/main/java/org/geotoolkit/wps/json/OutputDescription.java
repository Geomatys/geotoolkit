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

import java.util.List;
import java.util.Objects;
import org.geotoolkit.wps.xml.v200.BoundingBoxData;
import org.geotoolkit.wps.xml.v200.ComplexData;
import org.geotoolkit.wps.xml.v200.LiteralData;

/**
 * OutputDescription
 */
public class OutputDescription extends DescriptionType {

    private String minOccurs = null;

    private String maxOccurs = null;

    private InputDescriptionBase output;

    public OutputDescription() {

    }

    public OutputDescription(org.geotoolkit.wps.xml.v200.OutputDescription out) {
        super(out);
        if (out != null) {
            if (out.getDataDescription() instanceof ComplexData) {
                this.output = new ComplexInputDescription((ComplexData) out.getDataDescription());
            } else if (out.getDataDescription() instanceof LiteralData) {
                this.output = new LiteralInputDescription((LiteralData) out.getDataDescription());
            } else if (out.getDataDescription() instanceof BoundingBoxData) {
                this.output = new BoundingBoxInputDescription((BoundingBoxData) out.getDataDescription());
            }
        }
    }

    public OutputDescription(OutputDescription that) {
        super(that);
        if (that != null) {
            this.maxOccurs = that.maxOccurs;
            this.minOccurs = that.minOccurs;
            if (that.output != null) {
                if (that.output instanceof ComplexInputDescription) {
                    this.output = new ComplexInputDescription((ComplexInputDescription) that.output);
                } else if (that.output instanceof LiteralInputDescription) {
                    this.output = new LiteralInputDescription((LiteralInputDescription) that.output);
                } else if (that.output instanceof BoundingBoxInputDescription) {
                    this.output = new BoundingBoxInputDescription((BoundingBoxInputDescription) that.output);
                }
            }
        }
    }


    public OutputDescription(String id, String title, String _abstract, List<String> keywords,
            List<Metadata> metadata, List<AdditionalParameters> additionalParameters,
            InputDescriptionBase output) {
        super(id, title, _abstract, keywords, metadata, additionalParameters);
        this.output = output;
    }

    public InputDescriptionBase getOutput() {
        return output;
    }

    public void setOutput(InputDescriptionBase output) {
        this.output = output;
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
        OutputDescription dataDescriptionType = (OutputDescription) o;
        return Objects.equals(this.minOccurs, dataDescriptionType.minOccurs)
                && Objects.equals(this.maxOccurs, dataDescriptionType.maxOccurs)
                && Objects.equals(this.output, dataDescriptionType.output)
                && super.equals(o);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(minOccurs, maxOccurs, output, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OutputDescription {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    minOccurs: ").append(toIndentedString(minOccurs)).append("\n");
        sb.append("    maxOccurs: ").append(toIndentedString(maxOccurs)).append("\n");
        sb.append("    output: ").append(toIndentedString(output)).append("\n");
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
