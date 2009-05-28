/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.csw.xml;

/**
 * An interface containing the common methods to the different version of the operation GetRecords.
 *
 *  * @author Guilhem Legal
 */
public interface AbstractCswRequest {

    /**
     * Gets the value of the outputFormat property.
     *
     */
    public String getOutputFormat();

    /**
     * Sets the value of the outputFormat property.
     *
     */
    public void setOutputFormat(String value);

}
