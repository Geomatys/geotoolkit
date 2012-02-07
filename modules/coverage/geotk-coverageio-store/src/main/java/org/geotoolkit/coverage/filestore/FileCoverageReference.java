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
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.storage.DataStoreException;

/**
 * Reference to a coverage stored in a single file.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FileCoverageReference implements CoverageReference{

    private final File file;
    
    public FileCoverageReference(final File file){
        this.file = file;
    }
    
    @Override
    public GridCoverageReader createReader() throws DataStoreException{
        final ImageCoverageReader reader = new ImageCoverageReader();
        reader.setInput(file);
        return reader;
    }
    
}
