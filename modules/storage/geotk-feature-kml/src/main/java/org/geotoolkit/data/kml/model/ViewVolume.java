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
 * <p>This interface maps viewVolume element.</p>
 *
 * <pre>
 * &lt;element name="ViewVolume" type="kml:ViewVolumeType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="ViewVolumeType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:leftFov" minOccurs="0"/>
 *              &lt;element ref="kml:rightFov" minOccurs="0"/>
 *              &lt;element ref="kml:bottomFov" minOccurs="0"/>
 *              &lt;element ref="kml:topFov" minOccurs="0"/>
 *              &lt;element ref="kml:near" minOccurs="0"/>
 *              &lt;element ref="kml:ViewVolumeSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:ViewVolumeObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="ViewVolumeSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="ViewVolumeObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface ViewVolume extends AbstractObject {

    /**
     *
     * @return
     */
    double getLeftFov();

    /**
     *
     * @return
     */
    double getRightFov();

    /**
     *
     * @return
     */
    double getBottomFov();

    /**
     *
     * @return
     */
    double getTopFov();

    /**
     *
     * @return
     */
    double getNear();

    /**
     *
     * @param leftFov
     */
    void setLeftFov(double leftFov);

    /**
     *
     * @param rightFov
     */
    void setRightFov(double rightFov);

    /**
     *
     * @param bottomFov
     */
    void setBottomFov(double bottomFov);

    /**
     *
     * @param topFov
     */
    void setTopFov(double topFov);

    /**
     *
     * @param near
     */
    void setNear(double near);

}
