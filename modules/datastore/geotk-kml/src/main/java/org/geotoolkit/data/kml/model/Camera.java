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
 * <p>This interface maps Camera element.</p>
 *
 * <pre>
 * &lt;element name="Camera" type="kml:CameraType" substitutionGroup="kml:AbstractViewGroup"/>
 *
 * &lt;complexType name="CameraType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractViewType">
 *          &lt;sequence>
 *              &lt;element ref="kml:longitude" minOccurs="0"/>
 *              &lt;element ref="kml:latitude" minOccurs="0"/>
 *              &lt;element ref="kml:altitude" minOccurs="0"/>
 *              &lt;element ref="kml:heading" minOccurs="0"/>
 *              &lt;element ref="kml:tilt" minOccurs="0"/>
 *              &lt;element ref="kml:roll" minOccurs="0"/>
 *              &lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 *              &lt;element ref="kml:CameraSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:CameraObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="CameraSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="CameraObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Camera extends AbstractView {

    /**
     *
     * @return the camera longitude position.
     */
    double getLongitude();

    /**
     *
     * @return the camera latitude position.
     */
    double getLatitude();

    /**
     *
     * @return the camera altitude position.
     */
    double getAltitude();

    /**
     *
     * @return the camera heading angle.
     */
    double getHeading();

    /**
     *
     * @return the tilt angle.
     */
    double getTilt();

    /**
     *
     * @return the roll angle.
     */
    double getRoll();

    /**
     *
     * @return the altitude mode.
     */
    EnumAltitudeMode getAltitudeMode();

    /**
     *
     * @param longitude
     */
    void setLongitude(double longitude);

    /**
     *
     * @param latitude
     */
    void setLatitude(double latitude);

    /**
     *
     * @param atitude
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
     *
     * @param roll
     */
    void setRoll(double roll);

    /**
     *
     * @param altitudeMode
     */
    void setAltitudeMode(EnumAltitudeMode altitudeMode);

}
