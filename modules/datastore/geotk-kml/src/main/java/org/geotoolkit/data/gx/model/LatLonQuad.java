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
import org.geotoolkit.data.kml.model.AbstractObject;

/**
 * <p>This interface maps LatLonQuad element.</p>
 *
 * <pre>
 * &lt;element name="LatLonQuad" type="gx:LatLonQuadType" substitutionGroup="kml:GroundOverlayObjectExtensionGroup"/>
 *
 * &lt;complexType name="LatLonQuadType">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:coordinates" minOccurs="0"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface LatLonQuad extends AbstractObject {

    /**
     *
     * @return
     */
    CoordinateSequence getCoordinates();

    /**
     * 
     * @param coordinates
     */
    void setCoordinates(CoordinateSequence coordinates);
}
