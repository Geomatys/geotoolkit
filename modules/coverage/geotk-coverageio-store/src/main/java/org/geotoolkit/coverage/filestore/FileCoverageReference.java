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

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.AbstractCoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.io.ImageCoverageWriter;
import org.opengis.feature.type.Name;

/**
 * Reference to a coverage stored in a single file.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FileCoverageReference extends AbstractCoverageReference{

    private final FileCoverageStore store;
    private final Name name;
    private final File file;
    private final int imageIndex;

    FileCoverageReference(FileCoverageStore store, Name name, File file, int imageIndex) {
        this.store = store;
        this.name = name;
        this.file = file;
        this.imageIndex = imageIndex;
    }

    @Override
    public boolean isWritable() throws DataStoreException {
        try {
            final ImageWriter writer = store.createWriter(file);
            writer.dispose();
            return true;
        } catch (IOException ex) {
        }
        return false;
    }

    @Override
    public GridCoverageReader acquireReader() throws CoverageStoreException{
        final ImageCoverageReader reader = new ImageCoverageReader();
        try {
            final ImageReader ioreader =store.createReader(file);
            reader.setInput(ioreader);
        } catch (IOException ex) {
            throw new CoverageStoreException(ex.getMessage(),ex);
        }
        return reader;
    }

    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        final ImageCoverageWriter writer = new ImageCoverageWriter();
        try {
            writer.setOutput(store.createWriter(file));
        } catch (IOException ex) {
            throw new CoverageStoreException(ex.getMessage(),ex);
        }
        return writer;
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public int getImageIndex() {
        return imageIndex;
    }

    @Override
    public CoverageStore getStore() {
        return store;
    }

    public Image getLegend() throws DataStoreException {
        return null;
    }

}
