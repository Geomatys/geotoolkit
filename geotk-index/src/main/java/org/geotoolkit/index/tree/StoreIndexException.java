/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.index.tree;

import org.apache.sis.storage.DataStoreException;

/**
 * Exception in relation with RTree exception.
 *
 * @author Remi Marechal (Geomatys).
 */
public class StoreIndexException extends DataStoreException {

    public StoreIndexException(Throwable thrwbl) {
        super(thrwbl);
    }

    public StoreIndexException(String string) {
        super(string);
    }

    public StoreIndexException() {
    }

    public StoreIndexException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
}
