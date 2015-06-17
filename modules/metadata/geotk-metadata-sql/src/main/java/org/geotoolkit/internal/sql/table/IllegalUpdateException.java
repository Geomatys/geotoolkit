/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.internal.sql.table;

import java.util.Locale;
import org.geotoolkit.resources.Errors;


/**
 * Thrown when an inconsistency has been found during an update. This exception occurs for example
 * during an {@code INSERT}, {@code DELETE} or {@code UPDATE} statement if we expected a change in
 * only one row but more rows were affected.
 * <p>
 * When such an inconsistent update occurs, the updated {@linkplain Table table} will typically
 * revert to the previous state through a call to {@link java.sql.Connection#rollback()}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.13
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
public class IllegalUpdateException extends CatalogException {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 4475051674927844277L;

    /**
     * Creates an exception with no cause and no details message.
     */
    public IllegalUpdateException() {
        super();
    }

    /**
     * Creates an exception with a detail messages created from the specified number
     * of rows that were changed.
     *
     * @param locale The locale to use for formatting the error message, or {@code null}.
     * @param count The unexpected number of rows changed.
     *
     * @since 3.13
     */
    public IllegalUpdateException(final Locale locale, final int count) {
        this(Errors.getResources(locale).getString(Errors.Keys.UnexpectedUpdates_1, count));
    }

    /**
     * Creates an exception with the specified details message.
     *
     * @param message The detail message.
     */
    public IllegalUpdateException(final String message) {
        super(message);
    }

    /**
     * Creates an exception with the specified cause and no details message.
     *
     * @param cause The cause for this exception.
     */
    public IllegalUpdateException(final Exception cause) {
        super(cause);
    }
}
