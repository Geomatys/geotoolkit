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

package org.geotoolkit.data;

/**
 * Exception used in FeatureCollection, extends RuntimeException to
 * be useable in standard collection classes.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DataStoreRuntimeException extends RuntimeException{

    public DataStoreRuntimeException(String message){
        super(message);
    }

    public DataStoreRuntimeException(String message, Throwable th){
        super(message, th);
    }

    public DataStoreRuntimeException(Throwable th){
        super(th);
    }

}
