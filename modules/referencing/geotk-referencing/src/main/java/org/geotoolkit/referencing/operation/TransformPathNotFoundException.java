/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.OperationNotFoundException;


/**
 * Thrown when a transformation can't be performed because no path from
 * {@linkplain CoordinateOperation#getSourceCRS source CRS} to
 * {@linkplain CoordinateOperation#getTargetCRS target CRS} has been found.
 * This exception usually wraps an {@link OperationNotFoundException} thrown
 * by an {@linkplain CoordinateOperationFactory coordinate operation factory}.
 * This exception is sometime used in order to collapse a
 *
 * {@preformat java
 *     throws FactoryException, TransformException
 * }
 *
 * clause (in method signature) into a single
 *
 * {@preformat java
 *     throws TransformException
 * }
 *
 * clause, i.e. in order to hide the factory step into a more general transformation process
 * from the API point of view.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
public class TransformPathNotFoundException extends TransformException {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 5072333160296464925L;

    /**
     * Constructs an exception with no detail message.
     */
    public TransformPathNotFoundException() {
    }

    /**
     * Constructs an exception with the specified detail message.
     *
     * @param  cause The cause for this exception. The cause is saved
     *         for later retrieval by the {@link #getCause()} method.
     */
    public TransformPathNotFoundException(FactoryException cause) {
        super(cause.getLocalizedMessage(), cause);
    }

    /**
     * Constructs an exception with the specified detail message.
     *
     * @param  message The detail message. The detail message is saved
     *         for later retrieval by the {@link #getMessage()} method.
     */
    public TransformPathNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs an exception with the specified detail message and cause.
     *
     * @param  message The detail message. The detail message is saved
     *         for later retrieval by the {@link #getMessage()} method.
     * @param  cause The cause for this exception. The cause is saved
     *         for later retrieval by the {@link #getCause()} method.
     */
    public TransformPathNotFoundException(String message, FactoryException cause) {
        super(message, cause);
    }
}
