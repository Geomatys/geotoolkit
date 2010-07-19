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
 * <p>This interface maps LinearRing element.</p>
 *
 * <pre>
 * &lt;element name="LinearRing" type="kml:LinearRingType" substitutionGroup="kml:AbstractGeometryGroup"/>
 *
 * &lt;complexType name="LinearRingType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractGeometryType">
 *          &lt;sequence>
 *              &lt;element ref="kml:extrude" minOccurs="0"/>
 *              &lt;element ref="kml:tessellate" minOccurs="0"/>
 *              &lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 *              &lt;element ref="kml:coordinates" minOccurs="0"/>
 *              &lt;element ref="kml:LinearRingSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:LinearRingObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="LinearRingSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="LinearRingObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface LinearRing extends AbstractGeometry {

    /**
     *
     * @return
     */
    Coordinates getCoordinateSequence();
    
    /**
     *
     * @return
     */
    public boolean getExtrude();

    /**
     *
     * @return
     */
    public boolean getTessellate();

    /**
     *
     * @return
     */
    public EnumAltitudeMode getAltitudeMode();

    /**
     * 
     * @param extrude
     */
    public void setExtrude(boolean extrude);

    /**
     *
     * @param tesselate
     */
    public void setTessellate(boolean tesselate);

    /**
     *
     * @param altitudeMode
     */
    public void setAltitudeMode(EnumAltitudeMode altitudeMode);

}
