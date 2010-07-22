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
package org.geotoolkit.data.gx.model;

/**
 * <p>This interface maps SoundCue element.</p>
 *
 * <pre>
 * &lt;element name="SoundCue" type="gx:SoundCueType" substitutionGroup="gx:AbstractTourPrimitiveGroup"/>
 *
 * &lt;complexType name="SoundCueType">
 *  &lt;complexContent>
 *      &lt;extension base="gx:AbstractTourPrimitiveType">
 *          &lt;sequence>
 *              &lt;element ref="kml:href" minOccurs="0"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * @author Samuel Andrés
 */
public interface SoundCue extends AbstractTourPrimitive {

    /**
     *
     * @return
     */
    String getHref();

    /**
     *
     * @param href
     */
    void setHref(String href);

}
