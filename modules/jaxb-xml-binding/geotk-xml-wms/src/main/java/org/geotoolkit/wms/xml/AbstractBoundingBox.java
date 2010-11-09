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
 * @module pending
 */
public abstract class AbstractBoundingBox {
    
    /**
     * Gets the value of the crs or srs property.
     */
    public abstract String getCRSCode();

    /**
     * Gets the value of the minx property.
     */
    public abstract double getMinx();

    /**
     * Gets the value of the miny property.
     */
    public abstract double getMiny();

    /**
     * Gets the value of the maxx property.
     */
    public abstract double getMaxx();

    /**
     * Gets the value of the maxy property.
     */
    public abstract double getMaxy();

    /**
     * Gets the value of the resx property.
     */
    public abstract Double getResx();

    /**
     * Gets the value of the resy property.
     */
    public abstract Double getResy();

}
