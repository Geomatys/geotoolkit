/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.mapinfo.mif;

/**
 * An enum to list the header labels we can encounter in MIF file.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 20/02/13
 */
public enum MIFHeaderCategory {

    // The headers label, stored in logical order of encounter( as told in specification.

    /** Mif file version */
    VERSION,
    /** character encoding */
    CHARSET,
    /** (Optional) delimiting character in quotation marks */
    DELIMITER,
    /** (Optional) Numbers indicating database column for eventual identifiers. */
    UNIQUE,
    /** (Optional) Numbers for eventual database index. */
    INDEX,
    /** (Optional) Feature CRS. If no provided, data is long/lat format. */
    COORDSYS,
    /** (Optional) Transform coefficients to apply to geometries. */
    TRANSFORM,
    /** The number and definition of the feature attributes */
    COLUMNS,
    /** The beginning of the real data */
    DATA;
}
