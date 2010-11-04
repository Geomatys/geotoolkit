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
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultLookAt extends DefaultAbstractView implements LookAt {

    private double longitude;
    private double latitude;
    private double altitude;
    private double heading;
    private double tilt;
    private double range;
    private AltitudeMode altitudeMode;

    /**
     * 
     */
    public DefaultLookAt() {
        this.longitude = DEF_LONGITUDE;
        this.latitude = DEF_LATITUDE;
        this.altitude = DEF_ALTITUDE;
        this.heading = DEF_HEADING;
        this.tilt = DEF_TILT;
        this.range = DEF_RANGE;
        this.altitudeMode = DEF_ALTITUDE_MODE;
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractViewSimpleExtensions
     * @param abstractViewObjectExtensions
     * @param longitude
     * @param latitude
     * @param altitude
     * @param heading
     * @param tilt
     * @param range
     * @param lookAtSimpleExtensions
     * @param lookAtObjectExtensions
     */
    public DefaultLookAt(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractViewSimpleExtensions,
            List<Object> abstractViewObjectExtensions,
            double longitude, double latitude, double altitude,
            double heading, double tilt, double range, AltitudeMode altitudeMode,
            List<SimpleTypeContainer> lookAtSimpleExtensions,
            List<Object> lookAtObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions,
                abstractViewObjectExtensions);
        this.longitude = KmlUtilities.checkAngle180(longitude);
        this.latitude = KmlUtilities.checkAngle90(latitude);
        this.altitude = altitude;
        this.heading = KmlUtilities.checkAngle360(heading);
        this.tilt = KmlUtilities.checkAnglePos90(tilt);
        this.range = range;
        this.altitudeMode = altitudeMode;
        if (lookAtSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.LOOK_AT).addAll(lookAtSimpleExtensions);
        }
        if (lookAtObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.LOOK_AT).addAll(lookAtObjectExtensions);
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
    public double getHeading() {
        return this.heading;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getTilt() {
        return this.tilt;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getRange() {
        return this.range;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AltitudeMode getAltitudeMode() {
        return this.altitudeMode;
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

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHeading(double heading) {
        this.heading = KmlUtilities.checkAngle360(heading);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTilt(double tilt) {
        this.tilt = KmlUtilities.checkAnglePos90(tilt);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRange(double range) {
        this.range = range;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitudeMode(AltitudeMode altitudeMode) {
        this.altitudeMode = altitudeMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    @Deprecated
    public void setTilt_v2_1(double tilt) {
        this.tilt = KmlUtilities.checkAnglePos90(tilt);
    }
}
