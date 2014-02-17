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
 * <p>This interface maps Lod element.</p>
 *
 * <pre>
 * &lt;element name="Lod" type="kml:LodType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="LodType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:minLodPixels" minOccurs="0"/>
 *              &lt;element ref="kml:maxLodPixels" minOccurs="0"/>
 *              &lt;element ref="kml:minFadeExtent" minOccurs="0"/>
 *              &lt;element ref="kml:maxFadeExtent" minOccurs="0"/>
 *              &lt;element ref="kml:LodSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:LodObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="LodSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="LodObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface Lod extends AbstractObject{

    /**
     *
     * @return
     */
    double getMinLodPixels();

    /**
     *
     * @return
     */
    double getMaxLodPixels();

    /**
     *
     * @return
     */
    double getMinFadeExtent();

    /**
     *
     * @return
     */
    double getMaxFadeExtent();

    /**
     *
     * @param minLodPixels
     */
    void setMinLodPixels(double minLodPixels);

    /**
     *
     * @param maxLodPixels
     */
    void setMaxLodPixels(double maxLodPixels);

    /**
     *
     * @param minFadeExtent
     */
    void setMinFadeExtent(double minFadeExtent);

    /**
     *
     * @param maxFadeExtent
     */
    void setMaxFadeExtent(double maxFadeExtent);

}
