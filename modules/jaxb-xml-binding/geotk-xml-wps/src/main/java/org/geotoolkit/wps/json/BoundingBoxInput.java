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
import org.geotoolkit.ows.xml.BoundingBox;

/**
 * BoundingBoxInputType
 */
public class BoundingBoxInput extends InputBase {

    private String crs;

    private List<Double> bbox = new ArrayList<>();

    public BoundingBoxInput() {
    }

    public BoundingBoxInput(BoundingBox bbox) {
        if (bbox != null) {
            this.crs = bbox.getCrs();
            this.bbox = new ArrayList();
            if (bbox.getLowerCorner() != null && bbox.getUpperCorner() != null) {
                this.bbox.addAll(bbox.getLowerCorner());
                this.bbox.addAll(bbox.getUpperCorner());
            }
        }
    }

    public BoundingBoxInput(String crs, List<Double> bbox) {
        this.crs = crs;
        this.bbox = bbox;
    }

    public BoundingBoxInput(BoundingBoxInput that) {
        if (that != null) {
            this.crs = that.crs;
            if (that.bbox != null) {
                this.bbox = new ArrayList(that.bbox);
            }
        }
    }

     /**
     * @return the crs
     */
    public String getCrs() {
        return crs;
    }

    /**
     * @param crs the crs to set
     */
    public void setCrs(String crs) {
        this.crs = crs;
    }

    /**
     * @return the bbox
     */
    public List<Double> getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bbox to set
     */
    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoundingBoxInput that = (BoundingBoxInput) o;
        return Objects.equals(this.crs, that.crs) &&
               Objects.equals(this.bbox, that.crs);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(crs, bbox);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BoundingBoxInput {\n");
        sb.append("    crs: ").append(toIndentedString(crs)).append("\n");
        sb.append("    bbox: ").append(toIndentedString(bbox)).append("\n");
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
