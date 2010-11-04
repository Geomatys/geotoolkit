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

import org.geotoolkit.data.kml.model.AbstractView;

/**
 *
 * <p>This interface maps FlyTo element.</p>
 *
 * <pre>
 * &lt;element name="FlyTo" type="gx:FlyToType" substitutionGroup="gx:AbstractTourPrimitiveGroup"/>
 *
 * &lt;complexType name="FlyToType">
 *  &lt;complexContent>
 *      &lt;extension base="gx:AbstractTourPrimitiveType">
 *          &lt;sequence>
 *              &lt;element ref="gx:duration" minOccurs="0"/>
 *              &lt;element ref="gx:flyToMode" minOccurs="0"/>
 *              &lt;element ref="kml:AbstractViewGroup" minOccurs="0"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface FlyTo extends AbstractTourPrimitive {

    /**
     *
     * @return
     */
    double getDuration();

    /**
     *
     * @return
     */
    EnumFlyToMode getFlyToMode();

    /**
     *
     * @return
     */
    AbstractView getView();

    /**
     *
     * @param duration
     */
    void setDuration(double duration);

    /**
     *
     * @param flyToMode
     */
    void setFlyToMode(EnumFlyToMode flyToMode);

    /**
     * 
     * @param view
     */
    void setView(AbstractView view);

}
