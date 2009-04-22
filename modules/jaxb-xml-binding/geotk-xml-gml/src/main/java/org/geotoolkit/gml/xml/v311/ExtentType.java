/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gml.xml.v311;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * Information about the spatial, vertical, and/or temporal extent of a reference system object. 
 * Constraints: At least one of the elements "description", "boundingBox", "boundingPolygon", "verticalExtent", 
 * and temporalExtent" must be included, but more that one can be included when appropriate. 
 * Furthermore, more than one "boundingBox", "boundingPolygon", "verticalExtent", and/or temporalExtent" element can be included, 
 * with more than one meaning the union of the individual domains.
 * 
 * <p>Java class for ExtentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExtentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml}boundingBox" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{http://www.opengis.net/gml}boundingPolygon" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/gml}verticalExtent" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}temporalExtent" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtentType", propOrder = {
    "description",
    "boundingBox",
    "boundingPolygon",
    "verticalExtent",
    "temporalExtent"
})
public class ExtentType {

    private StringOrRefType description;
    private List<EnvelopeEntry> boundingBox;
    private List<PolygonType> boundingPolygon;
    private List<EnvelopeEntry> verticalExtent;
    private List<TimePeriodType> temporalExtent;

    /**
     * Description of spatial and/or temporal extent of this object.
     * 
     */
    public StringOrRefType getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
    */
    public void setDescription(StringOrRefType value) {
        this.description = value;
    }

    /**
     * Unordered list of bounding boxes (or envelopes) whose union describes the spatial domain of this object.
     * Gets the value of the boundingBox property.
     * 
     */
    public List<EnvelopeEntry> getBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new ArrayList<EnvelopeEntry>();
        }
        return this.boundingBox;
    }

    /**
     * Unordered list of bounding polygons whose union describes the spatial domain of this object.
     * Gets the value of the boundingPolygon property.
     * 
     */
    public List<PolygonType> getBoundingPolygon() {
        if (boundingPolygon == null) {
            boundingPolygon = new ArrayList<PolygonType>();
        }
        return this.boundingPolygon;
    }

    /**
     * Unordered list of vertical intervals whose union describes the spatial domain of this object.
     * Gets the value of the verticalExtent property.
     * 
     */
    public List<EnvelopeEntry> getVerticalExtent() {
        if (verticalExtent == null) {
            verticalExtent = new ArrayList<EnvelopeEntry>();
        }
        return this.verticalExtent;
    }

    /**
     * Unordered list of time periods whose union describes the spatial domain of this object.
     * Gets the value of the temporalExtent property.
     * 
     */
    public List<TimePeriodType> getTemporalExtent() {
        if (temporalExtent == null) {
            temporalExtent = new ArrayList<TimePeriodType>();
        }
        return this.temporalExtent;
    }

}
