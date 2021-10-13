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

import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class LiteralInput extends InputBase {

    private NameReferenceType dataType;

    private NameReferenceType uomType;

    private String value;

    public LiteralInput() {

    }

    public LiteralInput(String value, NameReferenceType dataType, NameReferenceType uomType) {
        this.dataType = dataType;
        this.uomType = uomType;
        this.value  = value;
    }

    public LiteralInput(LiteralInput that) {
        if (that != null) {
            this.dataType = new NameReferenceType(that.dataType);
            this.uomType  = new NameReferenceType(that.uomType);
            this.value    = that.value;
        }
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the dataType
     */
    public NameReferenceType getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(NameReferenceType dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the uomType
     */
    public NameReferenceType getUomType() {
        return uomType;
    }

    /**
     * @param uomType the uomType to set
     */
    public void setUomType(NameReferenceType uomType) {
        this.uomType = uomType;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LiteralInput li = (LiteralInput) o;
        return Objects.equals(this.dataType, li.dataType) &&
               Objects.equals(this.value, li.value) &&
               Objects.equals(this.uomType, li.uomType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataType, uomType, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class LiteralInput {\n");
        sb.append("    dataType: ").append(toIndentedString(dataType)).append("\n");
        sb.append("    value: ").append(toIndentedString(value)).append("\n");
        sb.append("    uomType: ").append(toIndentedString(uomType)).append("\n");
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
