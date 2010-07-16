/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLocation extends DefaultAbstractObject implements Location {

    private double longitude;
    private double latitude;
    private double altitude;

    public DefaultLocation() {
        this.longitude = DEF_LONGITUDE;
        this.latitude = DEF_LATITUDE;
        this.altitude = DEF_ALTITUDE;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param longitude
     * @param latitude
     * @param altitude
     * @param locationSimpleExtensions
     * @param locationObjectExtensions
     */
    public DefaultLocation(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double longitude, double latitude, double altitude,
            List<SimpleType> locationSimpleExtensions,
            List<AbstractObject> locationObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.longitude = KmlUtilities.checkAngle180(longitude);
        this.latitude = KmlUtilities.checkAngle90(latitude);
        this.altitude = altitude;
        if (locationSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.LOCATION).addAll(locationSimpleExtensions);
        }
        if (locationObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.LOCATION).addAll(locationObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getLongitude() {
        return this.longitude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getLatitude() {
        return this.latitude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getAltitude() {
        return this.altitude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLongitude(double longitude) {
        this.longitude = KmlUtilities.checkAngle180(longitude);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLatitude(double latitude) {
        this.latitude = KmlUtilities.checkAngle90(latitude);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
