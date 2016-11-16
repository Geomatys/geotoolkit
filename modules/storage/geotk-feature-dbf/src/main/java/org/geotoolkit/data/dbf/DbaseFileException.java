/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.dbf;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.util.Exceptions;


/**
 * Thrown when an error relating to the shapefile occurs.
 * 
 * @module
 */
public class DbaseFileException extends DataStoreException {

    private static final long serialVersionUID = -6890880438911014652L;

    public DbaseFileException(final String message) {
        super(message);
        assert Exceptions.isValidMessage(message) : message;
    }

    public DbaseFileException(final String message, final Throwable cause) {
        super(message, cause);
        assert Exceptions.isValidMessage(message) : message;
    }
}
