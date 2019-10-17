/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

package org.geotoolkit.data;

import org.geotoolkit.util.Exceptions;
import org.apache.sis.util.collection.BackingStoreException;

/**
 * Exception used in FeatureCollection, extends RuntimeException to
 * be useable in standard collection classes.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FeatureStoreRuntimeException extends BackingStoreException{

    public FeatureStoreRuntimeException(final String message){
        super(message);
        assert Exceptions.isValidMessage(message) : message;
    }

    public FeatureStoreRuntimeException(final String message, final Throwable th){
        super(message, th);
        assert Exceptions.isValidMessage(message) : message;
    }

    public FeatureStoreRuntimeException(final Throwable th){
        super(th);
    }

}
