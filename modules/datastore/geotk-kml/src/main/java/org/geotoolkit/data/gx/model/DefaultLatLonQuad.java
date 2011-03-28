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
package org.geotoolkit.data.gx.model;

import com.vividsolutions.jts.geom.CoordinateSequence;
import java.util.List;
import org.geotoolkit.data.kml.model.DefaultAbstractObject;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultLatLonQuad extends DefaultAbstractObject implements LatLonQuad {

    private CoordinateSequence coordinates;

    public DefaultLatLonQuad(){}

    public DefaultLatLonQuad(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, CoordinateSequence coordinates){
        super(objectSimpleExtensions, idAttributes);
        this.coordinates = coordinates;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public CoordinateSequence getCoordinates() {
        return this.coordinates;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setCoordinates(CoordinateSequence coordinates) {
        this.coordinates = coordinates;
    }

}
