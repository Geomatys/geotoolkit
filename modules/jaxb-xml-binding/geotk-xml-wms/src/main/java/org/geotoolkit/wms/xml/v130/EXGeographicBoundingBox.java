/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.wms.xml.v130;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractGeographicBoundingBox;
import org.opengis.metadata.extent.GeographicBoundingBox;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="westBoundLongitude" type="{http://www.opengis.net/wms}longitudeType"/>
 *         &lt;element name="eastBoundLongitude" type="{http://www.opengis.net/wms}longitudeType"/>
 *         &lt;element name="southBoundLatitude" type="{http://www.opengis.net/wms}latitudeType"/>
 *         &lt;element name="northBoundLatitude" type="{http://www.opengis.net/wms}latitudeType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "westBoundLongitude",
    "eastBoundLongitude",
    "southBoundLatitude",
    "northBoundLatitude"
})
@XmlRootElement(name = "EX_GeographicBoundingBox")
public class EXGeographicBoundingBox extends AbstractGeographicBoundingBox {

    private double westBoundLongitude;
    private double eastBoundLongitude;
    private double southBoundLatitude;
    private double northBoundLatitude;

    /**
     * An empty constructor used by JAXB.
     */
    EXGeographicBoundingBox() {
    }

    /**
     * Build a new bounding box.
     *
     */
    public EXGeographicBoundingBox(final double westBoundLongitude, final double southBoundLatitude,
            final double eastBoundLongitude, final double northBoundLatitude) {
        this.eastBoundLongitude = eastBoundLongitude;
        this.northBoundLatitude = northBoundLatitude;
        this.southBoundLatitude = southBoundLatitude;
        this.westBoundLongitude = westBoundLongitude;
        
    }

    /**
     * Build a new bounding box.
     *
     */
    public EXGeographicBoundingBox(final GeographicBoundingBox geoBox) {
        this.westBoundLongitude = geoBox.getWestBoundLongitude();
        this.southBoundLatitude = geoBox.getSouthBoundLatitude();
        this.eastBoundLongitude = geoBox.getEastBoundLongitude();
        this.northBoundLatitude = geoBox.getNorthBoundLatitude();
    }

    /**
     * Gets the value of the westBoundLongitude property.
     * 
     */
    public double getWestBoundLongitude() {
        return westBoundLongitude;
    }

    /**
     * Gets the value of the eastBoundLongitude property.
     * 
     */
    public double getEastBoundLongitude() {
        return eastBoundLongitude;
    }

    /**
     * Gets the value of the southBoundLatitude property.
     * 
     */
    public double getSouthBoundLatitude() {
        return southBoundLatitude;
    }

    /**
     * Gets the value of the northBoundLatitude property.
     * 
     */
    public double getNorthBoundLatitude() {
        return northBoundLatitude;
    }
    
    @Override
    public String toString() {
        return "Env[" + westBoundLongitude + " : " + eastBoundLongitude + 
                 ", " + southBoundLatitude + " : " + northBoundLatitude + "]";
    }
    
}
