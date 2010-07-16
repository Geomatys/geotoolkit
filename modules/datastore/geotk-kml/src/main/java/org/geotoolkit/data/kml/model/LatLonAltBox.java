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
 * <p>This interface maps LatLonAltBox element.</p>
 *
 * <p>Type of the KML element is an extension of AbstractLatLonBoxType type. But there is
 * no substitution group corresponding to this abstract type. LatLonAltBox is member of
 * AbstractObjectSubstitution Group.</p>
 *
 * <p>This interface considers logical to inherit from AbstractLatLonBox interface which both
 * contains AbstractLatLonBoxType mapping and extends AbstractObject.</p>
 * 
 * <pre>
 * &lt;element name="LatLonAltBox" type="kml:LatLonAltBoxType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="LatLonAltBoxType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractLatLonBoxType">
 *          &lt;sequence>
 *              &lt;element ref="kml:minAltitude" minOccurs="0"/>
 *              &lt;element ref="kml:maxAltitude" minOccurs="0"/>
 *              &lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 *              &lt;element ref="kml:LatLonAltBoxSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:LatLonAltBoxObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="LatLonAltBoxSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="LatLonAltBoxObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface LatLonAltBox extends AbstractLatLonBox{

    /**
     *
     * @return
     */
    public double getMinAltitude();

    /**
     *
     * @return
     */
    public double getMaxAltitude();

    /**
     *
     * @return
     */
    public AltitudeMode getAltitudeMode();

    /**
     *
     * @param minAltitude
     */
    public void setMinAltitude(double minAltitude);

    /**
     *
     * @param maxAltitude
     */
    public void setMaxAltitude(double maxAltitude);

    /**
     *
     * @param altitudeMode
     */
    public void setAltitudeMode(AltitudeMode altitudeMode);

}
