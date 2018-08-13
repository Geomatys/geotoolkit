/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2018, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.Locale;
import org.geotoolkit.resources.Errors;


/**
 * Thrown when an inconsistency has been found during an update. This exception occurs for example
 * during an {@code INSERT}, {@code DELETE} or {@code UPDATE} statement if we expected a change in
 * only one row but more rows were affected.
 *
 * <p>When such an inconsistent update occurs, the updated {@linkplain Table table} will typically
 * revert to the previous state through a call to {@link java.sql.Connection#rollback()}.</p>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
public class IllegalUpdateException extends CatalogException {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 4475051674927844277L;

    /**
     * Creates an exception with a detail messages created from the specified number
     * of rows that were changed.
     *
     * @param locale  the locale to use for formatting the error message, or {@code null}.
     * @param count   the unexpected number of rows changed.
     */
    IllegalUpdateException(final Locale locale, final int count) {
        this(Errors.getResources(locale).getString(Errors.Keys.UnexpectedUpdates_1, count));
    }

    /**
     * Creates an exception with the specified details message.
     *
     * @param message the detail message.
     */
    public IllegalUpdateException(final String message) {
        super(message);
    }
}
