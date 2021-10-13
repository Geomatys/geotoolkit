/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.feature.xml;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Simple DTO that expose coordinates of extent.
 *
 * @author Rohan FERRE (Geomatys).
 */

@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoundingBox implements Serializable {

    private double minx;
    private double miny;
    private double maxx;
    private double maxy;

    private CoordinateReferenceSystem crs;

    public BoundingBox() {

    }

    public BoundingBox(double minx, double miny, double maxx, double maxy, CoordinateReferenceSystem crs) {
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
        this.crs = crs;
    }

    public double getMaxx() {
        return maxx;
    }

    public double getMaxy() {
        return maxy;
    }

    public double getMinx() {
        return minx;
    }

    public double getMiny() {
        return miny;
    }

    public CoordinateReferenceSystem getCrs() {
        return crs;
    }

    public void setCrs(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    public void setMaxx(double maxx) {
        this.maxx = maxx;
    }

    public void setMaxy(double maxy) {
        this.maxy = maxy;
    }

    public void setMinx(double minx) {
        this.minx = minx;
    }

    public void setMiny(double miny) {
        this.miny = miny;
    }
}
