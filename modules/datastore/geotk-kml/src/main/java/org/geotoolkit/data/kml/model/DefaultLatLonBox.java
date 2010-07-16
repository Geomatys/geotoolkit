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
public class DefaultLatLonBox extends DefaultAbstractLatLonBox implements LatLonBox {

    private double rotation;

    /**
     * 
     */
    public DefaultLatLonBox() {
        this.rotation = DEF_ROTATION;
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
     * @param rotation
     * @param latLonBoxSimpleExtensions
     * @param latLonBoxObjectExtensions
     */
    public DefaultLatLonBox(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions,
            List<AbstractObject> abstractLatLonBoxObjectExtensions,
            double rotation,
            List<SimpleType> latLonBoxSimpleExtensions,
            List<AbstractObject> latLonBoxObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                north, south, east, west,
                abstractLatLonBoxSimpleExtensions,
                abstractLatLonBoxObjectExtensions);
        this.rotation = KmlUtilities.checkAngle180(rotation);
        if (latLonBoxSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.LAT_LON_BOX).addAll(latLonBoxSimpleExtensions);
        }
        if (latLonBoxObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.LAT_LON_BOX).addAll(latLonBoxObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getRotation() {
        return this.rotation;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRotation(double rotation) {
        this.rotation = KmlUtilities.checkAngle180(rotation);
    }
}
