/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.dggal.panama;


/**
 * Status of the native library.
 *
 * @see org.apache.sis.storage.panama.LibraryStatus
 */
public enum LibraryStatus {
    /**
     * The native library is ready for use.
     */
    LOADED,

    /**
     * The native library has not been found or cannot be loaded. Note: this is a merge of
     * {@link org.apache.sis.storage.panama.LibraryStatus#LIBRARY_NOT_FOUND} and
     * {@link org.apache.sis.storage.panama.LibraryStatus#UNAUTHORIZED}.
     */
    CANNOT_LOAD_LIBRARY,

    /**
     * The native library was found, but not symbol that we searched.
     */
    FUNCTION_NOT_FOUND
}
