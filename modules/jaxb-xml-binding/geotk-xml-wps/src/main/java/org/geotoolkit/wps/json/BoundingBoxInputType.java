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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * BoundingBoxInputType
 */
public class BoundingBoxInputType {

    private List<SupportedCrs> supportedCRS = new ArrayList<>();

    public BoundingBoxInputType supportedCRS(List<SupportedCrs> supportedCRS) {
        this.supportedCRS = supportedCRS;
        return this;
    }

    public BoundingBoxInputType addSupportedCRSItem(SupportedCrs supportedCRSItem) {

        this.supportedCRS.add(supportedCRSItem);
        return this;
    }

    /**
     * Get supportedCRS
     *
     * @return supportedCRS
  *
     */
    public List<SupportedCrs> getSupportedCRS() {
        return supportedCRS;
    }

    public void setSupportedCRS(List<SupportedCrs> supportedCRS) {
        this.supportedCRS = supportedCRS;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoundingBoxInputType boundingBoxInputType = (BoundingBoxInputType) o;
        return Objects.equals(this.supportedCRS, boundingBoxInputType.supportedCRS);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(supportedCRS);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BoundingBoxInputType {\n");

        sb.append("    supportedCRS: ").append(toIndentedString(supportedCRS)).append("\n");
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
