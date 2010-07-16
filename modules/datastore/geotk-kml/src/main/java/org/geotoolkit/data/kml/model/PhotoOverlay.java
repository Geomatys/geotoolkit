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
 * <p>This interface maps PhotoOverlay element.</p>
 *
 * <pre>
 * &lt;element name="PhotoOverlay" type="kml:PhotoOverlayType" substitutionGroup="kml:AbstractOverlayGroup"/>
 *
 * &lt;complexType name="PhotoOverlayType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractOverlayType">
 *          &lt;sequence>
 *              &lt;element ref="kml:rotation" minOccurs="0"/>
 *              &lt;element ref="kml:ViewVolume" minOccurs="0"/>
 *              &lt;element ref="kml:ImagePyramid" minOccurs="0"/>
 *              &lt;element ref="kml:Point" minOccurs="0"/>
 *              &lt;element ref="kml:shape" minOccurs="0"/>
 *              &lt;element ref="kml:PhotoOverlaySimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:PhotoOverlayObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="PhotoOverlaySimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="PhotoOverlayObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PhotoOverlay extends AbstractOverlay {

    /**
     *
     * @return
     */
    public double getRotation();

    /**
     *
     * @return
     */
    public ViewVolume getViewVolume();

    /**
     *
     * @return
     */
    public ImagePyramid getImagePyramid();

    /**
     *
     * @return
     */
    public Point getPoint();

    /**
     *
     * @return
     */
    public Shape getShape();

    /**
     *
     * @param rotation
     */
    public void setRotation(double rotation);

    /**
     *
     * @param viewVolume
     */
    public void setViewVolume(ViewVolume viewVolume);

    /**
     *
     * @param imagePyramid
     */
    public void setImagePyramid(ImagePyramid imagePyramid);

    /**
     *
     * @param point
     */
    public void setPoint(Point point);

    /**
     *
     * @param shape
     */
    public void setShape(Shape shape);

}
