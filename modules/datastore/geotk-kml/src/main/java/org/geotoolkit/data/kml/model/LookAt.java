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

/**
 *
 * <p>This interface maps Lod element.</p>
 *
 * <pre>
 * &lt;element name="LookAt" type="kml:LookAtType" substitutionGroup="kml:AbstractViewGroup"/>
 *
 * &lt;complexType name="LookAtType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractViewType">
 *          &lt;sequence>
 *              &lt;element ref="kml:longitude" minOccurs="0"/>
 *              &lt;element ref="kml:latitude" minOccurs="0"/>
 *              &lt;element ref="kml:altitude" minOccurs="0"/>
 *              &lt;element ref="kml:heading" minOccurs="0"/>
 *              &lt;element ref="kml:tilt" minOccurs="0"/>
 *              &lt;element ref="kml:range" minOccurs="0"/>
 *              &lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 *              &lt;element ref="kml:LookAtSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:LookAtObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="LookAtSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="LookAtObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface LookAt extends AbstractView {

    /**
     *
     * @return
     */
    double getLongitude();

    /**
     *
     * @return
     */
    double getLatitude();

    /**
     *
     * @return
     */
    double getAltitude();

    /**
     *
     * @return
     */
    double getHeading();

    /**
     *
     * @return
     */
    double getTilt();

    /**
     *
     * @return
     */
    double getRange();

    /**
     *
     * @return
     */
    AltitudeMode getAltitudeMode();

    /**
     *
     * @param angle
     */
    void setLongitude(double longitude);

    /**
     *
     * @param latitude
     */
    void setLatitude(double latitude);

    /**
     *
     * @param altitude
     */
    void setAltitude(double altitude);

    /**
     *
     * @param heading
     */
    void setHeading(double heading);

    /**
     *
     * @param tilt
     */
    void setTilt(double tilt);

    /**
     * <p>Specific setter for 2.1 Kml version.</p>
     *
     * @param tilt
     * @deprecated
     */
    @Deprecated
    void setTilt_v2_1(double tilt);

    /**
     *
     * @param range
     */
    void setRange(double range);

    /**
     * 
     * @param altitudeMode
     */
    void setAltitudeMode(AltitudeMode altitudeMode);

}
