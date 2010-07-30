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
 * <p>This interface maps Model element.</p>
 *
 * <pre>
 * &lt;element name="Model" type="kml:ModelType" substitutionGroup="kml:AbstractGeometryGroup"/>
 *
 * &lt;complexType name="ModelType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractGeometryType">
 *          &lt;sequence>
 *              &lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 *              &lt;element ref="kml:Location" minOccurs="0"/>
 *              &lt;element ref="kml:Orientation" minOccurs="0"/>
 *              &lt;element ref="kml:Scale" minOccurs="0"/>
 *              &lt;element ref="kml:Link" minOccurs="0"/>
 *              &lt;element ref="kml:ResourceMap" minOccurs="0"/>
 *              &lt;element ref="kml:ModelSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:ModelObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="ModelSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="ModelObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Model extends AbstractGeometry {

    /**
     *
     * @return
     */
    AltitudeMode getAltitudeMode();

    /**
     *
     * @return
     */
    Location getLocation();

    /**
     *
     * @return
     */
    Orientation getOrientation();

    /**
     *
     * @return
     */
    Scale getScale();

    /**
     *
     * @return
     */
    Link getLink();

    /**
     *
     * @return
     */
    ResourceMap getRessourceMap();

    /**
     *
     * @param altitudeMode
     */
    void setAltitudeMode(AltitudeMode altitudeMode);

    /**
     *
     * @param location
     */
    void setLocation(Location location);

    /**
     *
     * @param orientation
     */
    void setOrientation(Orientation orientation);

    /**
     *
     * @param scale
     */
    void setScale(Scale scale);

    /**
     *
     * @param link
     */
    void setLink(Link link);

    /**
     *
     * @param resourceMap
     */
    void setRessourceMap(ResourceMap resourceMap);

}
