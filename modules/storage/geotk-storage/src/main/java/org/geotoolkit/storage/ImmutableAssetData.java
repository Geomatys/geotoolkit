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

import java.util.Collections;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ImmutableAssetData implements Assets.Data {

    private final String mimeType;
    private final Object instance;

    public ImmutableAssetData(String mimeType, Object instance) {
        this.mimeType = mimeType;
        this.instance = instance;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public Set<Class> getSupportedTypes() {
        return Collections.singleton(instance.getClass());
    }

    @Override
    public <T> T load(Class<T> expectedType) throws DataStoreException {
        if (expectedType != null && !expectedType.isInstance(instance)) {
            throw new DataStoreException("Data object can not be mapped to type "+expectedType);
        }
        return expectedType.cast(instance);
    }

}
