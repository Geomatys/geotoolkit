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
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * @author Rohan FERRE (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Spatial {

    private String description;
    private List<BoundingBox> bbox;
    @XmlElement(name = "LowerCorner")
    private String lowerCorner = null;
    @XmlElement(name = "UpperCorner")
    private String upperCorner = null;

    @XmlAttribute
    private String crs;

    public Spatial() {
        bbox = new ArrayList<>();
        crs = "http://www.opengis.net/def/crs/OGC/1.3/CRS84";
    }

    public Spatial(String description, List<BoundingBox> bbox, String crs) {
        this.description = description;
        this.bbox = bbox;
        this.crs = crs;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the bbox : list of the array list of number
     */
    public List<BoundingBox> getBbox() {
        return bbox;
    }

    /**
     * @param bbox the list of the array list of number to set
     */
    public void setBbox(List<BoundingBox> bbox) {
        this.bbox = bbox;
    }

    /**
     * @return the crs.
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
     * @param box the array list of int to add at bbox
     */
    public void addBox(BoundingBox box) {
        bbox.add(box);
    }

    /**
     *
     * @return the lower corner
     */
    public String getLowerCorner() {
        return lowerCorner;
    }

    /**
     *
     * @return the upper corner
     */
    public String getUpperCorner() {
        return upperCorner;
    }

    /**
     * Set the value of LowerCorner the value of lower corner can only be the
     * value of minx and miny
     */
    public void setLowerCorner() {
        if (bbox != null && bbox.size() > 0) {
            lowerCorner = String.valueOf(bbox.get(0).getMinx()) + " " + String.valueOf(bbox.get(0).getMiny());
        }
    }

    /**
     * Set the value of UpperCorner the value of upper corner can only be the
     * value of maxx and maxy
     */
    public void setUpperCorner() {
        if (bbox != null && bbox.size() > 0) {
            upperCorner = String.valueOf(bbox.get(0).getMaxx()) + " " + String.valueOf(bbox.get(0).getMaxy());
        }
    }

    public void setXmlMode() {
        this.setLowerCorner();
        this.setUpperCorner();
        this.getBbox().clear();
    }
}
