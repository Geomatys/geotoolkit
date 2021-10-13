/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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

package org.geotoolkit.ows.xml;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface AbstractMetadata {

    Object getAbstractMetaData();

    /**
     * Gets the value of the about property.
     */
    String getAbout();

    /**
     * Gets the value of the type property.
     */
    String getType();

    /**
     * Gets the value of the href property.
     */
    String getHref();

    /**
     * Gets the value of the role property.
     */
    String getRole();

    /**
     * Gets the value of the arcrole property.
     *
     */
    String getArcrole();

    /**
     * Gets the value of the title property.
     *
     */
    String getTitle();

   /**
    * Gets the value of the show property.
    *
    */
    String getShow();

    /**
     * Gets the value of the actuate property.
     */
    String getActuate();
}
