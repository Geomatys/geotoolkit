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
 * <p>This interface maps ScreenOverlay element.</p>
 *
 * <pre>
 * &lt;element name="ScreenOverlay" type="kml:ScreenOverlayType" substitutionGroup="kml:AbstractOverlayGroup"/>
 *
 * &lt;complexType name="ScreenOverlayType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractOverlayType">
 *          &lt;sequence>
 *              &lt;element ref="kml:overlayXY" minOccurs="0"/>
 *              &lt;element ref="kml:screenXY" minOccurs="0"/>
 *              &lt;element ref="kml:rotationXY" minOccurs="0"/>
 *              &lt;element ref="kml:size" minOccurs="0"/>
 *              &lt;element ref="kml:rotation" minOccurs="0"/>
 *              &lt;element ref="kml:ScreenOverlaySimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:ScreenOverlayObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="ScreenOverlaySimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="ScreenOverlayObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface ScreenOverlay extends AbstractOverlay {

    /**
     *
     * @return
     */
    public Vec2 getOverlayXY();

    /**
     *
     * @return
     */
    public Vec2 getScreenXY();

    /**
     *
     * @return
     */
    public Vec2 getRotationXY();

    /**
     *
     * @return
     */
    public Vec2 getSize();

    /**
     *
     * @return
     */
    public double getRotation();

    /**
     *
     * @param overlayXY
     */
    public void setOverlayXY(Vec2 overlayXY);

    /**
     *
     * @param screenXY
     */
    public void setScreenXY(Vec2 screenXY);

    /**
     *
     * @param rotationXY
     */
    public void setRotationXY(Vec2 rotationXY);

    /**
     *
     * @param size
     */
    public void setSize(Vec2 size);

    /**
     *
     * @param rotation
     */
    public void setRotation(double rotation);

}
