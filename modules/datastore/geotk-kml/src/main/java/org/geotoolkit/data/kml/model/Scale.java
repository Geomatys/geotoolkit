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

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps Scale element.</p>
 *
 * <pre>
 * &lt;element name="Scale" type="kml:ScaleType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="ScaleType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:x" minOccurs="0"/>
 *              &lt;element ref="kml:y" minOccurs="0"/>
 *              &lt;element ref="kml:z" minOccurs="0"/>
 *              &lt;element ref="kml:ScaleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:ScaleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="ScaleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="ScaleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Scale extends AbstractObject {

    /**
     *
     * @return
     */
    public double getX();

    /**
     *
     * @return
     */
    public double getY();

    /**
     *
     * @return
     */
    public double getZ();

    /**
     *
     * @param x
     */
    public void setX(double x);

    /**
     *
     * @param y
     */
    public void setY(double y);

    /**
     *
     * @param z
     */
    public void setZ(double z);

}
