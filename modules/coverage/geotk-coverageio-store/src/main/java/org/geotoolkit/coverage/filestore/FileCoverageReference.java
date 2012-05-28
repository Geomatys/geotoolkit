/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.filestore;

import java.io.File;
import java.io.IOException;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.type.Name;

/**
 * Reference to a coverage stored in a single file.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FileCoverageReference implements CoverageReference{

    private final FileCoverageStore store;
    private final Name name;
    private final File file;
    
    FileCoverageReference(FileCoverageStore store, Name name, File file) {
        this.store = store;
        this.name = name;
        this.file = file;
    }
    
    @Override
    public GridCoverageReader createReader() throws DataStoreException{
        final ImageCoverageReader reader = new ImageCoverageReader();
        try {
            reader.setInput(store.createReader(file));
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(),ex);
        }
        return reader;
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public CoverageStore getStore() {
        return store;
    }
    
}
