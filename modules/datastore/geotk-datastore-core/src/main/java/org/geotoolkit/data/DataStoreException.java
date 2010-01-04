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
 * Exception used in DataStores, extends Exception.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DataStoreException extends Exception{

    public DataStoreException(){
        super();
    }

    public DataStoreException(String message){
        super(message);
    }

    public DataStoreException(String message, Throwable th){
        super(message, th);
    }

    public DataStoreException(Throwable th){
        super(th);
    }

    public DataStoreException(Exception th){
        super(th.getMessage());
        initCause(th);
    }

}
