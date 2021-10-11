/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

import org.geotoolkit.util.Exceptions;

/**
 * Exceptions thrown by lucene indexing should be instances of this type.
 *
 * @author Adrian Custer (Geomatys)
 * @module
 * @since 0.3
 */
public class IndexingException extends Exception {

    /**
     * Allow for cross-version identity of these exceptions.
     */
    private static final long serialVersionUID = -6424956545282486878L;

    /**
     * Construct an exception from an explanation of the cause.
     *
     * @param message User understandable explanation of the cause of the
     *                  exception.
     */
    public IndexingException(final String message) {
        super(message);
        assert Exceptions.isValidMessage(message) : message;
    }

    /**
     * Construct an indexing exception from a lower level exception.
     *
     * @param message User understandable explanation of the cause of the
     *                  exception.
     * @param cause The preceding exception.
     */
    public IndexingException(final String message, final Throwable cause) {
        super(message, cause);
        assert Exceptions.isValidMessage(message) : message;
    }

}
