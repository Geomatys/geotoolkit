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
import static org.geotoolkit.data.kml.xml.KmlConstants.*;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractLatLonBox extends DefaultAbstractObject implements AbstractLatLonBox {

    protected double north;
    protected double south;
    protected double east;
    protected double west;

    /**
     * 
     */
    protected DefaultAbstractLatLonBox() {
        this.north = DEF_NORTH;
        this.south = DEF_SOUTH;
        this.east = DEF_EAST;
        this.west = DEF_WEST;
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
     */
    protected DefaultAbstractLatLonBox(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions,
            List<AbstractObject> abstractLatLonBoxObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.north = KmlUtilities.checkAngle90(north);
        this.south = KmlUtilities.checkAngle90(south);
        this.east = KmlUtilities.checkAngle180(east);
        this.west = KmlUtilities.checkAngle180(west);
        if (abstractLatLonBoxSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.ABSTRACT_LAT_LON_BOX).addAll(abstractLatLonBoxSimpleExtensions);
        }
        if (abstractLatLonBoxObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.ABSTRACT_LAT_LON_BOX).addAll(abstractLatLonBoxObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getNorth() {
        return this.north;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getSouth() {
        return this.south;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getEast() {
        return this.east;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getWest() {
        return this.west;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setNorth(double north) {
        this.north = KmlUtilities.checkAngle90(north);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSouth(double south) {
        this.south = KmlUtilities.checkAngle90(south);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setEast(double east) {
        this.east = KmlUtilities.checkAngle180(east);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setWest(double west) {
        this.west = KmlUtilities.checkAngle180(west);
    }
}
