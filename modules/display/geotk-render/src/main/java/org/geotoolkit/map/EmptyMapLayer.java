/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.Resource;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.style.MutableStyle;
import org.opengis.geometry.Envelope;

/**
 * This class is a dummy implementation of maplayer, which hold no datas.
 * It can be used for different purposes in applications.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
public class EmptyMapLayer extends AbstractMapLayer{

    private final Envelope bounds;

    EmptyMapLayer(final MutableStyle style){
        this(style, null);
    }

    EmptyMapLayer(final MutableStyle style, Envelope bounds){
        super(style);
        if (bounds == null) {
            bounds = new Envelope2D(CommonCRS.WGS84.normalizedGeographic(), XRectangle2D.INFINITY);
        }
        this.bounds = bounds;
    }

    @Override
    public Resource getResource() {
        return null;
    }

    @Override
    public Envelope getBounds() {
        return bounds;
    }

}
