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
package org.geotoolkit.coverage;

import java.awt.Image;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.type.Name;
import org.opengis.util.GenericName;

/**
 * CoverageReference implementation wrapping a Coveragereader.
 * 
 * @author Johann Sorel
 */
public class DefaultCoverageReference extends AbstractCoverageReference{
    
    private GridCoverageReader reader;
    private int imageIndex;

    public DefaultCoverageReference(final GridCoverageReader reader, final int imageIndex) {
        this.reader = reader;
        this.imageIndex = imageIndex;
    }

    @Override
    public Name getName() {
        try {
            final GenericName gn = reader.getCoverageNames().get(imageIndex);
            return DefaultName.valueOf(gn.toString());
        } catch (CoverageStoreException ex) {
            Logger.getLogger(DefaultCoverageReference.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
        } catch (CancellationException ex) {
            Logger.getLogger(DefaultCoverageReference.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
        }
        return new DefaultName("unnamed");
    }

    @Override
    public int getImageIndex() {
        return imageIndex;
    }

    @Override
    public boolean isWritable() throws DataStoreException {
        return false;
    }

    @Override
    public CoverageStore getStore() {
        return null;
    }

    @Override
    public GridCoverageReader createReader() throws DataStoreException {
        return reader;
    }

    @Override
    public GridCoverageWriter createWriter() throws DataStoreException {
        throw new UnsupportedOperationException("Writting not supported.");
    }

    @Override
    public Image getLegend() throws DataStoreException {
        return null;
    }
    
}
