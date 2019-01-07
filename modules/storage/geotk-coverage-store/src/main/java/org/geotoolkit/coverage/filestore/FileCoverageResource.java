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
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageReaderSpi;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.io.ImageCoverageWriter;
import org.geotoolkit.storage.coverage.AbstractCoverageResource;
import org.opengis.util.GenericName;

/**
 * Reference to a coverage stored in a single file.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FileCoverageResource extends AbstractCoverageResource {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.coverage.filestore");

    private final Path file;
    private ImageReaderSpi spi;

    @Deprecated
    FileCoverageResource(FileCoverageStore store, GenericName name, File file) {
        this(store, name, file.toPath());
    }

    FileCoverageResource(FileCoverageStore store, GenericName name, Path file) {
        super(store,name);
        this.file = file;
        this.spi = store.spi;
    }

    @Override
    public boolean isWritable() throws DataStoreException {
        try {
            final ImageWriter writer = ((FileCoverageStore)store).createWriter(file);
            writer.dispose();
            return true;
        } catch (IOException ex) {
            //no writer found
            LOGGER.log(Level.FINER, "No writer found for file : "+file.toAbsolutePath().toString());
        }
        return false;
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        final GridCoverageReader reader = acquireReader();
        try {
            return reader.getGridGeometry();
        } finally {
            recycle(reader);
        }
    }

    @Override
    public GridCoverageReader acquireReader() throws CoverageStoreException {
        final ImageCoverageReader reader = new ImageCoverageReader();
        try {
            final ImageReader ioreader = ((FileCoverageStore)store).createReader(file, spi);
            if (spi == null) {
                //format was on AUTO. keep the spi for futur reuse.
                spi = ioreader.getOriginatingProvider();
            }
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
            writer.setOutput( ((FileCoverageStore)store).createWriter(file) );
        } catch (IOException ex) {
            throw new CoverageStoreException(ex.getMessage(),ex);
        }
        return writer;
    }

    /**
     * Get the input image file used for this coverage.
     * @return a {@link Path} object which point to he image file of this coverage, or null if the input has not been
     * initialized.
     */
    public Path getInput() {
        return file;
    }

    public ImageReaderSpi getSpi() {
        return spi;
    }

    public Image getLegend() throws DataStoreException {
        return null;
    }

}
