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
import java.util.Collections;


/**
 * Files-related {@linkplain org.apache.sis.storage.DataStoreProvider provider}.
 * This interface provides additional descriptive informations on the supported
 * file types.
 *
 * @author  Johann Sorel (Geomatys)
 * @module
 */
public interface ProviderOnFileSystem {
    /**
     * Get the list of this format mainly used file suffixes.
     * If the provider uses multiple files, this method should return
     * only the entry file suffixes.
     * <p>
     * For example : the shapefile format uses the files shp,shx,dbf,qix,...
     * but this collection only return the shp suffix.
     * </p>
     *
     * @return list of suffix, case insensitive, never null, can be empty.
     *         Suffixes are in lower-case.
     */
    Collection<String> getSuffix();

    /**
     * Binary and sometimes text formats often have a special header at the beginning
     * of the file.
     * This part of the file is call Signature or Magic number and is used by
     * file explorers and applications to identify the file type.
     *
     * <p>Some format may declare multiple different signatures. Such case can
     * happen for various reasons like historical evolution and changes on version updates.</p>
     *
     * <p>Default implementation returns an empty collection.</p>
     *
     * @return collection of signatures, never null, can be empty.
     */
    default Collection<byte[]> getSignature() {
        return Collections.emptyList();
    }

}
