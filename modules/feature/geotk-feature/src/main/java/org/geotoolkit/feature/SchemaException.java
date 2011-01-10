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
package org.geotoolkit.feature;

import java.io.IOException;


/**
 * Indicates client class has attempted to create an invalid schema.
 * 
 * @module pending
 */
public class SchemaException extends IOException {

    private static final String NOT_FOUND = "Feature type could not be found for ";

    public SchemaException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor with message argument.
     *
     * @param message Reason for the exception being thrown
     */
    public SchemaException(final String message) {
        super(message);
    }

    /**
     * Constructor with message argument and cause.
     *
     * @param message Reason for the exception being thrown
     * @param cause Cause of SchemaException
     */
    public SchemaException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public static SchemaException notFound(final String typeName){
        return notFound(typeName,null);
    }

    public static SchemaException notFound(final String typeName, final Throwable th){
        final SchemaException ex = new SchemaException(NOT_FOUND+typeName);
        if(th != null){
            ex.initCause(th);
        }
        return ex;
    }

}
