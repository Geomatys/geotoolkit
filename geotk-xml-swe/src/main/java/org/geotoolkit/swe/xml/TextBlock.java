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


/**
 * Textual encoding of data.
 *
 * @author Guilhem Legal
 * @module
 */
public interface TextBlock extends AbstractEncoding {

    /**
     * Max three characters to use as token separator
     */
    String getTokenSeparator();

    /**
     * Max three characters to use as block separator
     */
    String getBlockSeparator();

    /**
     * One character to use as a decimal separator
     */
    String getDecimalSeparator();

}
