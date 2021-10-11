/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.index.quadtree;

import org.geotoolkit.util.Exceptions;


/**
 * DOCUMENT ME!
 *
 * @author Tommaso Nolli
 * @module
 */
public class StoreException extends Exception {

    private static final long serialVersionUID = -3356954193373344773L;

    public StoreException(final String message) {
        super(message);
        assert Exceptions.isValidMessage(message) : message;
    }

    public StoreException(final Throwable cause) {
        super(cause);
    }

    public StoreException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
