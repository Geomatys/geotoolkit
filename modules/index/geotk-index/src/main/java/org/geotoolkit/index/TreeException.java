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
package org.geotoolkit.index;

import java.io.IOException;
import org.geotoolkit.util.Exceptions;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @module pending
 */
public class TreeException extends IOException {

    private static final long serialVersionUID = 1988241322009839486L;

    /**
     * DOCUMENT ME!
     * 
     * @param message
     */
    public TreeException(final String message) {
        super(message);
        assert Exceptions.isValidMessage(message) : message;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param message
     * @param cause
     */
    public TreeException(final String message, final Throwable cause) {
        super(message);
        initCause(cause);
        assert Exceptions.isValidMessage(message) : message;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param cause
     */
    public TreeException(final Throwable cause) {
        super(cause);
    }
}
