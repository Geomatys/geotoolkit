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
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.memory.MemoryCoverageReader;
import org.opengis.feature.type.Name;

/**
 * CoverageReference implementation wrapping a coverage.
 *
 * @author Johann Sorel
 */
public class DefaultCoverageReference extends AbstractCoverageReference{

    private final GridCoverage2D coverage;
    private final Object input;
    private final Name name;
    private final int imageIndex;

    public DefaultCoverageReference(final GridCoverage2D coverage, Name name) {
        this.coverage = coverage;
        this.input = null;
        this.name = name;
        this.imageIndex = 0;
    }

    public DefaultCoverageReference(final Object input, Name name) {
        this.coverage = null;
        this.input = input;
        this.name = name;
        this.imageIndex = 0;
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
    public boolean isWritable() throws DataStoreException {
        return false;
    }

    @Override
    public CoverageStore getStore() {
        return null;
    }

    @Override
    public GridCoverageReader acquireReader() throws CoverageStoreException {
        if(coverage != null){
            return new MemoryCoverageReader(coverage);
        }else if(input instanceof GridCoverageReader){
            return (GridCoverageReader)input;
        }else{
            ImageCoverageReader reader = new ImageCoverageReader();
            reader.setInput(input);
            return reader;
        }
    }

    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        throw new CoverageStoreException("Writing not supported.");
    }

    @Override
    public void recycle(GridCoverageReader reader) {
        if(input instanceof GridCoverageReader){
            //do not dispose it, it will be reused
        }else{
            super.recycle(reader);
        }
    }

    @Override
    public Image getLegend() throws DataStoreException {
        return null;
    }

    @Override
    protected void finalize() throws Throwable {
        if(input instanceof GridCoverageReader){
            dispose((GridCoverageReader)input);
        }
        super.finalize();
    }

}
