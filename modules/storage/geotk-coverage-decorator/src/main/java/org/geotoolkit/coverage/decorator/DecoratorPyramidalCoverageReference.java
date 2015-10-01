/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.coverage.decorator;

import java.awt.Image;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.CoverageStore;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DecoratorPyramidalCoverageReference extends DecoratorCoverageReference{

    public DecoratorPyramidalCoverageReference(CoverageReference ref, CoverageStore store) {
        super(ref, store);
    }

    @Override
    public GridCoverageReader acquireReader() throws CoverageStoreException {
        return new DecoratorCoverageReader(this);
    }

    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void recycle(CoverageReader reader) {
        ((DecoratorCoverageReader)reader).dispose();
    }

    @Override
    public void recycle(GridCoverageWriter writer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
