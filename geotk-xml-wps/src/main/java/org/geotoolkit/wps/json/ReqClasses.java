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

/**
 * ReqClasses
 */
public class ReqClasses {

    private List<String> conformsTo = new ArrayList<String>();

    public ReqClasses conformsTo(List<String> conformsTo) {
        this.conformsTo = conformsTo;
        return this;
    }

    public ReqClasses addConformsToItem(String conformsToItem) {

        this.conformsTo.add(conformsToItem);
        return this;
    }

    /**
     * Get conformsTo
     *
     * @return conformsTo
  *
     */
    public List<String> getConformsTo() {
        return conformsTo;
    }

    public void setConformsTo(List<String> conformsTo) {
        this.conformsTo = conformsTo;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReqClasses reqClasses = (ReqClasses) o;
        return Objects.equals(this.conformsTo, reqClasses.conformsTo);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(conformsTo);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ReqClasses {\n");

        sb.append("    conformsTo: ").append(toIndentedString(conformsTo)).append("\n");
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
