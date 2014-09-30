/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.swe.xml;

import java.util.List;

/**
 *
 * @author Guilhem Legal
 * @module pending
 */
public interface Vector extends AbstractVector {

    public List<? extends Coordinate> getCoordinate();

    /**
     * Returns the coordinate having the {@code "urn:ogc:def:phenomenon:latitude"} definition, or {@code null} if none.
     */
    public Coordinate getLatitude();

    /**
     * Returns the coordinate having the {@code "urn:ogc:def:phenomenon:longitude"} definition, or {@code null} if none.
     */
    public Coordinate getLongitude();
}
