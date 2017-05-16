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
package org.geotoolkit.wms.xml;

/**
 *
 * @author Olivier Terral (Geomatys)
 *
 * @module
 */
public interface AbstractBoundingBox {

    /**
     * Gets the value of the crs or srs property.
     */
    String getCRSCode();

    /**
     * Gets the value of the minx property.
     */
    double getMinx();

    /**
     * Gets the value of the miny property.
     */
    double getMiny();

    /**
     * Gets the value of the maxx property.
     */
    double getMaxx();

    /**
     * Gets the value of the maxy property.
     */
    double getMaxy();

    /**
     * Gets the value of the resx property.
     */
    Double getResx();

    /**
     * Gets the value of the resy property.
     */
    Double getResy();

}
