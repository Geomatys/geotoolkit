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
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLatLonAltBox extends DefaultAbstractLatLonBox implements LatLonAltBox {

    private double minAltitude;
    private double maxAltitude;
    private AltitudeMode altitudeMode;

    /**
     * 
     */
    public DefaultLatLonAltBox() {
        this.minAltitude = DEF_MIN_ALTITUDE;
        this.maxAltitude = DEF_MAX_ALTITUDE;
        this.altitudeMode = DEF_ALTITUDE_MODE;
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param north
     * @param south
     * @param east
     * @param west
     * @param abstractLatLonBoxSimpleExtensions
     * @param abstractLatLonBoxObjectExtensions
     * @param minAltitude
     * @param maxAltitude
     * @param altitudeMode
     * @param latLonAltBoxSimpleExtensions
     * @param latLonAltBoxObjectExtensions
     */
    public DefaultLatLonAltBox(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleTypeContainer> abstractLatLonBoxSimpleExtensions,
            List<Object> abstractLatLonBoxObjectExtensions,
            double minAltitude, double maxAltitude, AltitudeMode altitudeMode,
            List<SimpleTypeContainer> latLonAltBoxSimpleExtensions,
            List<Object> latLonAltBoxObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                north, south, east, west,
                abstractLatLonBoxSimpleExtensions,
                abstractLatLonBoxObjectExtensions);
        this.minAltitude = minAltitude;
        this.maxAltitude = maxAltitude;
        this.altitudeMode = altitudeMode;
        if (latLonAltBoxSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.LAT_LON_ALT_BOX).addAll(latLonAltBoxSimpleExtensions);
        }
        if (latLonAltBoxObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.LAT_LON_ALT_BOX).addAll(latLonAltBoxObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMinAltitude() {
        return this.minAltitude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMaxAltitude() {
        return this.maxAltitude;
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
    public void setMinAltitude(double minAltitude) {
        this.minAltitude = minAltitude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMaxAltitude(double maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitudeMode(AltitudeMode altitudeMode) {
        this.altitudeMode = altitudeMode;
    }
}
