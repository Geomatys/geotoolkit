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

import com.vividsolutions.jts.geom.CoordinateSequence;

/**
 * <p>This interface maps Point element.</p>
 *
 * <pre>
 * &lt;element name="Point" type="kml:PointType" substitutionGroup="kml:AbstractGeometryGroup"/>
 *
 * &lt;complexType name="PointType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractGeometryType">
 *          &lt;sequence>
 *              &lt;element ref="kml:extrude" minOccurs="0"/>
 *              &lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 *              &lt;element ref="kml:coordinates" minOccurs="0"/>
 *              &lt;element ref="kml:PointSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:PointObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="PointSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="PointObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * <h3>OGC Documentation</h3>
 *
 * <p>A geographic location defined by a single geodetic longitude, geodetic latitude, and
 * (optional) altitude coordinate tuple.</p>
 *
 * @author Samuel Andrés
 */
public interface Point extends AbstractGeometry{
    
    /**
     *
     * @return
     */
    CoordinateSequence getCoordinateSequence();

    /**
     *
     * @return
     */
    boolean getExtrude();

    /**
     *
     * @return
     */
    AltitudeMode getAltitudeMode();

    /**
     *
     * @param extrude
     */
    void setExtrude(boolean extrude);

    /**
     *
     * @param altitudeMode
     */
    void setAltitudeMode(AltitudeMode altitudeMode);
}
