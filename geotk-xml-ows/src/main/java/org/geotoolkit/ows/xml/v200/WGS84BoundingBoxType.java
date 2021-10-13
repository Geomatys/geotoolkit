/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.ows.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.opengis.metadata.extent.GeographicBoundingBox;


/**
 * This type is adapted from the general BoundingBoxType,
 *       with modified contents and documentation for use with the 2D WGS 84
 *       coordinate reference system.
 *
 * <p>Java class for WGS84BoundingBoxType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="WGS84BoundingBoxType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.opengis.net/ows/2.0}BoundingBoxType">
 *       &lt;sequence>
 *         &lt;element name="LowerCorner" type="{http://www.opengis.net/ows/2.0}PositionType2D"/>
 *         &lt;element name="UpperCorner" type="{http://www.opengis.net/ows/2.0}PositionType2D"/>
 *       &lt;/sequence>
 *       &lt;attribute name="crs" type="{http://www.w3.org/2001/XMLSchema}anyURI" fixed="urn:ogc:def:crs:OGC:2:84" />
 *       &lt;attribute name="dimensions" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" fixed="2" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WGS84BoundingBoxType")
public class WGS84BoundingBoxType extends BoundingBoxType {

    WGS84BoundingBoxType(){

    }

    public WGS84BoundingBoxType(final double minx, final double miny, final double maxx, final double maxy){
        super(null, minx, miny, maxx, maxy);
    }

    public WGS84BoundingBoxType(final String crsName, final double minx, final double miny, final double maxx, final double maxy){
        super(crsName, minx, miny, maxx, maxy);
    }

    public WGS84BoundingBoxType(final GeographicBoundingBox inputGeoBox){
        super(null,
             inputGeoBox.getWestBoundLongitude(),
             inputGeoBox.getSouthBoundLatitude(),
             inputGeoBox.getEastBoundLongitude(),
             inputGeoBox.getNorthBoundLatitude());
    }

}
