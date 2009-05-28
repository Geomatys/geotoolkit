/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.map;

import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.style.MutableStyle;
import org.opengis.geometry.Envelope;

/**
 * This class is a dummy implementation of maplayer, which hold no datas.
 * It can be used for different purposes in applications.
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class EmptyMapLayer extends AbstractMapLayer{

    EmptyMapLayer(MutableStyle style){
        super(style);
    }

    @Override
    public Envelope getBounds() {
        return new Envelope2D(DefaultGeographicCRS.WGS84, XRectangle2D.INFINITY);
    }

}
