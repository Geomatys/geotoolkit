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
import org.apache.sis.storage.NoSuchDataException;
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
     * Property defined on objects which should be handled as stored assets.
     * Such objects are used multiple times in their context.
     */
    public static final String ASSET_FLAG = "asset";

    /**
     * Get collection of available datas.
     *
     * @return collection, not null, can be empty.
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<Data> getDatas() throws DataStoreException;

    /**
     * Find a resource.
     * @param identifier
     * @return data never null
     * @throws DataStoreException if an error occured or data do not exist
     */
    default Data findData(final String identifier) throws DataStoreException {
        for (Data data : getDatas()) {
            if (identifier.equals(data.getIdentifier())) {
                return data;
            }
        }
        throw new NoSuchDataException();
    }

    /**
     * Store a new data.
     *
     * @param data
     * @return identifier in this Assets
     * @throws org.apache.sis.storage.DataStoreException
     */
    String addData(Data data) throws DataStoreException;

    /**
     * Remove an existing data.
     *
     * @param data
     * @throws org.apache.sis.storage.DataStoreException
     */
    void removeData(Data data) throws DataStoreException;

    public interface Data {

        /**
         * The data identifier.
         *
         * @return identifier of the data in it's parent, not null.
         */
        String getIdentifier();

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
