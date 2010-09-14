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
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultBoundary implements Boundary {

    private final Extensions extensions = new Extensions();
    private LinearRing linearRing;

    /**
     * 
     */
    public DefaultBoundary() {
    }

    /**
     * 
     * @param linearRing
     * @param boundarySimpleExtensions
     * @param boundaryObjectExtensions
     */
    public DefaultBoundary(LinearRing linearRing,
            List<SimpleTypeContainer> boundarySimpleExtensions,
            List<Object> boundaryObjectExtensions) {
        this.linearRing = linearRing;
        if (boundarySimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.BOUNDARY).addAll(boundarySimpleExtensions);
        }
        if (boundaryObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.BOUNDARY).addAll(boundaryObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LinearRing getLinearRing() {
        return this.linearRing;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLinearRing(LinearRing linearRing) {
        this.linearRing = linearRing;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Extensions extensions() {
        return this.extensions;
    }
}
