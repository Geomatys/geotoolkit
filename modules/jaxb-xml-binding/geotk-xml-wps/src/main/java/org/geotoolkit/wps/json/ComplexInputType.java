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
import org.geotoolkit.wps.xml.v200.InputDescription;

/**
 * ComplexInputType
 */
public class ComplexInputType extends DataDescriptionType {

    private Integer minOccurs = null;

    private Integer maxOccurs = null;

    public ComplexInputType() {

    }

    public ComplexInputType(InputDescription in) {
        super(in);
        if (in != null) {
            this.minOccurs = in.getMinOccurs();
            this.maxOccurs = in.getMaxOccurs();
        }
    }

    public ComplexInputType minOccurs(Integer minOccurs) {
        this.minOccurs = minOccurs;
        return this;
    }

    /**
     * Get minOccurs
     *
     * @return minOccurs
  *
     */
    public Integer getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(Integer minOccurs) {
        this.minOccurs = minOccurs;
    }

    public ComplexInputType maxOccurs(Integer maxOccurs) {
        this.maxOccurs = maxOccurs;
        return this;
    }

    /**
     * Get maxOccurs
     *
     * @return maxOccurs
     *
     */
    public Integer getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(Integer maxOccurs) {
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
        ComplexInputType complexInputType = (ComplexInputType) o;
        return Objects.equals(this.minOccurs, complexInputType.minOccurs)
                && Objects.equals(this.maxOccurs, complexInputType.maxOccurs)
                && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minOccurs, maxOccurs, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ComplexInputType {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    minOccurs: ").append(toIndentedString(minOccurs)).append("\n");
        sb.append("    maxOccurs: ").append(toIndentedString(maxOccurs)).append("\n");
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
