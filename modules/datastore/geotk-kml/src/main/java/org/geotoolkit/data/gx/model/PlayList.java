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

import java.util.List;
import org.geotoolkit.data.kml.model.AbstractObject;

/**
 *
 * <p>This interface maps Playlist element.</p>
 *
 * <pre>
 * &lt;element name="Playlist" type="gx:PlaylistType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="PlaylistType">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="gx:AbstractTourPrimitiveGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PlayList extends AbstractObject{

    /**
     *
     * @return
     */
    List<AbstractTourPrimitive> getTourPrimitives();

    /**
     * 
     * @param tourPrimitives
     */
    void setTourPrimitives(List<AbstractTourPrimitive> tourPrimitives);
}
