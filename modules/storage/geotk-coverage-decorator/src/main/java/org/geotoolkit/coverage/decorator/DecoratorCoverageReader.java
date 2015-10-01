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

import java.util.List;
import java.util.concurrent.CancellationException;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DecoratorCoverageReader extends GridCoverageReader{

    private final DecoratorCoverageReference ref;
    private final CoverageReader reader;

    public DecoratorCoverageReader(DecoratorCoverageReference ref) throws CoverageStoreException {
        this.ref = ref;
        this.reader = ref.getDecorated().acquireReader();
    }

    public CoverageReader getDecorated(){
        return reader;
    }

    @Override
    public List<? extends GenericName> getCoverageNames() throws CoverageStoreException, CancellationException {
        return reader.getCoverageNames();
    }

    @Override
    public GeneralGridGeometry getGridGeometry(int index) throws CoverageStoreException, CancellationException {
        return ref.getGridGeometry(index);
    }

    @Override
    public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException, CancellationException {
        return ref.getSampleDimensions(index);
    }

    @Override
    public GridCoverage read(int index, GridCoverageReadParam param) throws CoverageStoreException, CancellationException {

        


        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reset() throws CoverageStoreException {
        reader.reset();
    }

    @Override
    public void dispose() {
        ref.getDecorated().recycle(reader);
    }


}
