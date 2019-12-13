/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.storage;

import java.util.Collection;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;

/**
 * An asset is anything that exist in binary form but do not fit in common GIS patterns.
 * Videos, musics, user files, photographies, notes, attachments and many more
 * type of objects may be stored is assets.
 *
 * TODO : this is an experimental interface, may change without notice.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Assets extends Resource {

    /**
     * Get collection of available datas.
     *
     * @return collection, not null, can be empty.
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<Data> getDatas() throws DataStoreException;

    /**
     * Store a new data.
     *
     * @param data
     * @throws org.apache.sis.storage.DataStoreException
     */
    void addData(Data data) throws DataStoreException;

    /**
     * Remove an existing data.
     *
     * @param data
     * @throws org.apache.sis.storage.DataStoreException
     */
    void removeData(Data data) throws DataStoreException;

    public interface Data {

        /**
         * The data natural mime-type.
         *
         * @return mime-type, can be null.
         */
        String getMimeType();

        /**
         * List of different representation as java objects of this data.
         *
         * @return Set, contains at least one element.
         */
        Set<Class> getSupportedTypes();

        /**
         * Load data as given type.
         *
         * @param <T>
         * @param expectedType expected result type, if null, natural type should be returned.
         * @return
         */
        <T> T load(Class<T> expectedType) throws DataStoreException;
    }

}
