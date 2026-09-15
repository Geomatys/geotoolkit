/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.tjs.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Request to a TJS to perform the JoinData operation, joining attribute data to a spatial framework.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JoinDataRequestType", propOrder = {"attributeData", "mapStyling", "classificationURL"})
@XmlRootElement(name = "JoinData", namespace = "http://www.opengis.net/tjs/1.0")
public class JoinDataRequestType extends RequestBaseType {

    @XmlElement(name = "AttributeData", required = true)
    private AttributeDataType attributeData;

    @XmlElement(name = "MapStyling")
    private MapStylingType mapStyling;

    @XmlElement(name = "ClassificationURL")
    private String classificationURL;

    @XmlAttribute
    private String update;

    public AttributeDataType getAttributeData() {
        return attributeData;
    }

    public void setAttributeData(AttributeDataType attributeData) {
        this.attributeData = attributeData;
    }

    public MapStylingType getMapStyling() {
        return mapStyling;
    }

    public void setMapStyling(MapStylingType mapStyling) {
        this.mapStyling = mapStyling;
    }

    public String getClassificationURL() {
        return classificationURL;
    }

    public void setClassificationURL(String classificationURL) {
        this.classificationURL = classificationURL;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }
}
