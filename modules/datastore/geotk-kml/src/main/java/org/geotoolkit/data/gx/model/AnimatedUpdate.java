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

import org.geotoolkit.data.kml.model.Update;

/**
 *
 * <p>This interface maps AnimatedUpdate element.</p>
 *
 * <pre>
 * &lt;element name="AnimatedUpdate" type="gx:AnimatedUpdateType" substitutionGroup="gx:AbstractTourPrimitiveGroup"/>
 *
 * &lt;complexType name="AnimatedUpdateType">
 *  &lt;complexContent>
 *      &lt;extension base="gx:AbstractTourPrimitiveType">
 *          &lt;sequence>
 *              &lt;element ref="gx:duration" minOccurs="0"/>
 *              &lt;element ref="kml:Update" minOccurs="0"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AnimatedUpdate extends AbstractTourPrimitive {

    /**
     *
     * @return
     */
    double getDuration();

    /**
     *
     * @return
     */
    Update getUpdate();

    /**
     *
     * @param duration
     */
    void setDuration(double duration);

    /**
     * 
     * @param update
     */
    void setUpdate(Update update);
}
