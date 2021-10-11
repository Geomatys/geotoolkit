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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GeneralAssets extends AbstractResource implements Assets {

    private final AtomicInteger IDINC = new AtomicInteger();
    private final List<Data> datas = new ArrayList<>();

    public GeneralAssets(NamedIdentifier identifier) {
        super(identifier);
    }

    @Override
    public Collection<Data> getDatas() throws DataStoreException {
        return Collections.unmodifiableList(datas);
    }

    @Override
    public String addData(Data data) throws DataStoreException {
        final String newId = "" + IDINC.incrementAndGet();
        data = new ImmutableAssetData(newId, data.getMimeType(), data.load(null));
        this.datas.add(data);
        return newId;
    }

    @Override
    public void removeData(Data data) throws DataStoreException {
        for (int i=0,n=datas.size();i<n;i++) {
            if (datas.get(i).equals(data)) {
                datas.remove(i);
                return;
            }
        }
        throw new DataStoreException("Data not found in asset list");
    }

}
