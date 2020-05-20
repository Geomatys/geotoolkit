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
import org.geotoolkit.wps.xml.v200.BoundingBoxData;
import org.geotoolkit.wps.xml.v200.SupportedCRS;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class BoundingBoxInputDescription extends InputDescriptionBase {

    private List<SupportedCrs> supportedCRS;

    public BoundingBoxInputDescription() {

    }

    public BoundingBoxInputDescription(List<SupportedCrs> supportedCRS) {
        this.supportedCRS = supportedCRS;
    }

    public BoundingBoxInputDescription(BoundingBoxInputDescription that) {
        if (that != null && that.supportedCRS != null) {
            this.supportedCRS = new ArrayList<>();
            for (SupportedCrs c : that.supportedCRS) {
                this.supportedCRS.add(new SupportedCrs(c));
            }
        }
    }

    public BoundingBoxInputDescription(BoundingBoxData bbox) {
        if (bbox!= null && bbox.getSupportedCRS() != null && !bbox.getSupportedCRS().isEmpty()) {
            List<SupportedCrs> crs = new ArrayList<>();
            for (SupportedCRS c : bbox.getSupportedCRS()) {
                crs.add(new SupportedCrs(c));
            }
            this.supportedCRS = crs;
        }
    }

    public BoundingBoxInputDescription supportedCRS(List<SupportedCrs> supportedCRS) {
        this.supportedCRS = supportedCRS;
        return this;
    }

    public BoundingBoxInputDescription addSupportedCRSItem(SupportedCrs supportedCRSItem) {
        this.supportedCRS.add(supportedCRSItem);
        return this;
    }

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
        BoundingBoxInputDescription inputType = (BoundingBoxInputDescription) o;
        return Objects.equals(this.supportedCRS, inputType.supportedCRS);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(supportedCRS);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BoundingBoxInputDescription {\n");
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
