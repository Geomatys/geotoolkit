/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.referencing.operation;

import java.io.LineNumberReader;
import org.geotoolkit.console.ReferencingConsole;


/**
 * A console for running test scripts. Most of the work is already done by the subclass.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 */
final strictfp class ScriptRunner3D extends ReferencingConsole {
    /**
     * The first error that occurred, or {@code null} if none.
     */
    Exception firstError;

    /**
     * Creates a new instance using the specified input stream.
     *
     * @param in The input stream.
     */
    ScriptRunner3D(final LineNumberReader in) {
        super(in);
    }

    /**
     * Invoked when an error occurred.
     */
    @Override
    protected void reportError(final Exception exception) {
        super.reportError(exception);
        if (firstError == null) {
            firstError = exception;
        }
    }
}
