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
 * <p>This interface maps Orientation element.</p>
 *
 * <pre>
 * &lt;element name="Orientation" type="kml:OrientationType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="OrientationType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:heading" minOccurs="0"/>
 *              &lt;element ref="kml:tilt" minOccurs="0"/>
 *              &lt;element ref="kml:roll" minOccurs="0"/>
 *              &lt;element ref="kml:OrientationSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:OrientationObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="OrientationSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="OrientationObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Orientation extends AbstractObject {

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
    double getRoll();

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

}
